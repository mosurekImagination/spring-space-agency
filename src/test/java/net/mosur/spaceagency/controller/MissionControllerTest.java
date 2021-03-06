package net.mosur.spaceagency.controller;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import net.mosur.spaceagency.domain.model.Mission;
import net.mosur.spaceagency.domain.model.enums.ImageryType;
import net.mosur.spaceagency.service.MissionService;
import net.mosur.spaceagency.service.ProductService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(MissionController.class)
public class MissionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProductService productService;
    @MockBean
    private MissionService missionService;

    @Before
    public void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    @WithMockUser
    public void should_create_mission_success_all_parameters() {
        String missionName = "TestName";
        String imageryType = ImageryType.MULTISPECTRAL.toString();
        String startDate = "2018-12-16T22:21:38.175691600Z";
        String endDate = "2018-12-18T22:21:38.175691600Z";

        Mission mission = new Mission();
        mission.setId(1L);
        mission.setMissionName(missionName);
        mission.setImageryType(ImageryType.MULTISPECTRAL);
        mission.setStartDate(Instant.parse(startDate));
        mission.setFinishDate(Instant.parse(endDate));

        when(missionService.findByMissionName(eq(missionName))).thenReturn(Optional.empty());
        when(missionService.findById(any())).thenReturn(Optional.of(mission));

        Map<String, Object> param = prepareCreateMissionParameter(missionName, imageryType, startDate, endDate);

        given()
                .contentType("application/json")
                .body(param)
                .when()
                .post("/missions/")
                .then()
                .statusCode(201)
                .body("mission.id", equalTo(1))
                .body("mission.missionName", equalTo(missionName))
                .body("mission.imageryType", equalTo(imageryType))
                .body("mission.startDate", equalTo(startDate))
                .body("mission.finishDate", equalTo(endDate));

        verify(missionService).save(any());
    }

    @Test
    @WithMockUser
    public void should_create_mission_without_dates() {
        String missionName = "TestName";
        String imageryType = ImageryType.MULTISPECTRAL.toString();
        String startDate = "";
        String endDate = "";

        Mission mission = new Mission();
        mission.setId(1L);
        mission.setMissionName(missionName);
        mission.setImageryType(ImageryType.MULTISPECTRAL);


        when(missionService.findByMissionName(eq(missionName))).thenReturn(Optional.empty());
        when(missionService.findById(any())).thenReturn(Optional.of(mission));

        Map<String, Object> param = prepareCreateMissionParameter(missionName, imageryType, startDate, endDate);

        given()
                .contentType("application/json")
                .body(param)
                .when()
                .post("/missions/")
                .then()
                .statusCode(201)
                .body("mission.id", equalTo(1))
                .body("mission.missionName", equalTo(missionName))
                .body("mission.imageryType", equalTo(imageryType));

        verify(missionService).save(any());
    }

    @Test
    @WithMockUser
    public void should_show_error_message_for_blank_mission_name() {
        String missionName = "";
        String imageryType = ImageryType.MULTISPECTRAL.toString();
        String startDate = "2018-12-16T22:21:38.175691600Z";
        String endDate = "2018-12-18T22:21:38.175691600Z";

        Map<String, Object> param = prepareCreateMissionParameter(missionName, imageryType, startDate, endDate);

        given()
                .contentType("application/json")
                .body(param)
                .when()
                .post("/missions/")
                .then()
                .statusCode(422)
                .body("errors.missionName[0]", equalTo("can't be empty"));
    }

    @Test
    @WithMockUser
    public void should_show_error_deleting_mission_with_products() {
        String missionName = "test mission";
        ImageryType imageryType = ImageryType.MULTISPECTRAL;
        String startDate = "2018-12-16T22:21:38.175691600Z";
        String endDate = "2018-12-18T22:21:38.175691600Z";
        Mission mission = new Mission(missionName, imageryType, Instant.parse(startDate), Instant.parse(endDate));
        when(missionService.findById(anyLong())).thenReturn(Optional.of(mission));
        when(missionService.hasProducts(any())).thenReturn(true);

        given()
                .contentType("application/json")
                .when()
                .delete("/missions/{id}", 1)
                .then()
                .statusCode(400)
                .body("errors", equalTo("Mission has product, delete its products first"));
    }

    @Test
    @WithMockUser
    public void should_show_error_updating_mission_with_exising_name() {
        String missionName = "test mission";
        ImageryType imageryType = ImageryType.MULTISPECTRAL;
        String startDate = "2018-12-16T22:21:38.175691600Z";
        String endDate = "2018-12-18T22:21:38.175691600Z";
        Mission mission = new Mission(missionName, imageryType, Instant.parse(startDate), Instant.parse(endDate));

        Map<String, Object> param = prepareCreateMissionParameter(missionName, imageryType.toString(), startDate, endDate);

        when(missionService.findById(anyLong())).thenReturn(Optional.of(mission));
        when(missionService.findByMissionName(any())).thenReturn(Optional.of(mission));

        given()
                .contentType("application/json")
                .body(param)
                .when()
                .put("/missions/{id}", 1)
                .then()
                .statusCode(422)
                .body("errors.missionName[0]", equalTo("Mission with that name already exists!"));
    }

    @Test
    @WithMockUser
    public void should_show_error_message_for_blank_imagery_type() {
        String missionName = "testName";
        String imageryType = "";
        String startDate = "2018-12-16T22:21:38.175691600Z";
        String endDate = "2018-12-18T22:21:38.175691600Z";

        Map<String, Object> param = prepareCreateMissionParameter(missionName, imageryType, startDate, endDate);

        given()
                .contentType("application/json")
                .body(param)
                .when()
                .post("/missions/")
                .then()
                .statusCode(422)
                .body("errors.imageryType[0]", equalTo("can't be empty"));
    }

    @Test
    @WithMockUser
    public void should_show_error_message_for_bad_imagery_type() {
        String missionName = "testName";
        String imageryType = "asdf";
        String startDate = "2018-12-16T22:21:38.175691600Z";
        String endDate = "2018-12-18T22:21:38.175691600Z";

        Map<String, Object> param = prepareCreateMissionParameter(missionName, imageryType, startDate, endDate);

        given()
                .contentType("application/json")
                .body(param)
                .when()
                .post("/missions/")
                .then()
                .statusCode(422)
                .body("errors.imageryType[0]", equalTo("this imagery type doesn't exists"));
    }

    @Test
    @WithMockUser
    public void should_show_error_message_for_duplicated_mission_name() {
        String missionName = "TestMission";
        String imageryType = ImageryType.MULTISPECTRAL.toString();
        String startDate = "2018-12-16T22:21:38.175691600Z";
        String endDate = "2018-12-18T22:21:38.175691600Z";

        Mission mission = new Mission();
        mission.setId(1L);
        mission.setMissionName(missionName);
        mission.setImageryType(ImageryType.MULTISPECTRAL);
        mission.setStartDate(Instant.parse(startDate));
        mission.setFinishDate(Instant.parse(endDate));

        when(missionService.findByMissionName(eq(missionName))).thenReturn(Optional.of(mission));

        Map<String, Object> param = prepareCreateMissionParameter(missionName, imageryType, startDate, endDate);

        given()
                .contentType("application/json")
                .body(param)
                .when()
                .post("/missions/")
                .then()
                .statusCode(422)
                .body("errors.missionName[0]", equalTo("duplicated mission name"));
    }

    @Test
    public void should_return_unauthorized() {
        String missionName = "TestMission";
        String imageryType = ImageryType.MULTISPECTRAL.toString();
        String startDate = "2018-12-16T22:21:38.175691600Z";
        String endDate = "2018-12-18T22:21:38.175691600Z";

        Mission mission = new Mission();
        mission.setId(1L);
        mission.setMissionName(missionName);
        mission.setImageryType(ImageryType.MULTISPECTRAL);
        mission.setStartDate(Instant.parse(startDate));
        mission.setFinishDate(Instant.parse(endDate));

        Map<String, Object> param = prepareCreateMissionParameter(missionName, imageryType, startDate, endDate);

        given()
                .contentType("application/json")
                .body(param)
                .when()
                .post("/missions/")
                .then()
                .statusCode(401);
    }

    @Test
    @WithMockUser
    public void should_update_mission_success_all_parameters() {
        String missionName = "TestName";
        String imageryType = ImageryType.MULTISPECTRAL.toString();
        String startDate = "2018-12-16T22:21:38.175691600Z";
        String endDate = "2018-12-18T22:21:38.175691600Z";

        Mission mission = new Mission();
        mission.setId(1L);
        mission.setMissionName(missionName);
        mission.setImageryType(ImageryType.MULTISPECTRAL);
        mission.setStartDate(Instant.parse(startDate));
        mission.setFinishDate(Instant.parse(endDate));

        when(missionService.findByMissionName(eq(missionName))).thenReturn(Optional.of(mission));
        when(missionService.findById(any())).thenReturn(Optional.of(mission));

        Map<String, Object> param = prepareCreateMissionParameter(missionName + "new", imageryType, startDate, endDate);

        given()
                .contentType("application/json")
                .body(param)
                .when()
                .put("/missions/{id}", 1L)
                .then()
                .statusCode(200)
                .body("mission.id", equalTo(1))
                .body("mission.missionName", equalTo(missionName + "new"))
                .body("mission.imageryType", equalTo(imageryType))
                .body("mission.startDate", equalTo(startDate))
                .body("mission.finishDate", equalTo(endDate));

        verify(missionService).save(any());
    }

    @Test
    @WithMockUser
    public void should_get_all_missions() {
        String missionName = "TestName";
        String imageryType = ImageryType.MULTISPECTRAL.toString();
        String startDate = "2018-12-16T22:21:38.175691600Z";
        String endDate = "2018-12-18T22:21:38.175691600Z";

        Mission mission = new Mission();
        mission.setId(1L);
        mission.setMissionName(missionName);
        mission.setImageryType(ImageryType.MULTISPECTRAL);
        mission.setStartDate(Instant.parse(startDate));


        Mission mission2 = new Mission();
        mission2.setId(1L);
        mission2.setMissionName(missionName + "2");
        mission2.setImageryType(ImageryType.MULTISPECTRAL);
        mission2.setStartDate(Instant.parse(startDate));
        mission2.setFinishDate(Instant.parse(endDate));

        when(missionService.findAll()).thenReturn(Arrays.asList(mission, mission2));

        given()
                .contentType("application/json")
                .when()
                .get("/missions/")
                .then()
                .statusCode(200)
                .body("missions.size()", equalTo(2))
                .body("missions[0].imageryType", equalTo(imageryType));
    }

    @Test
    @WithMockUser
    public void should_show_error_updated_mission_doesnt_exists() {
        String missionName = "TestName";
        String imageryType = ImageryType.MULTISPECTRAL.toString();
        String startDate = "2018-12-16T22:21:38.175691600Z";
        String endDate = "2018-12-18T22:21:38.175691600Z";

        Map<String, Object> param = prepareCreateMissionParameter(missionName + "new", imageryType, startDate, endDate);

        given()
                .contentType("application/json")
                .body(param)
                .when()
                .put("/missions/{id}", 1L)
                .then()
                .statusCode(404);
    }

    private Map<String, Object> prepareCreateMissionParameter(String missionName, String imageryType, String startDate, String endDate) {
        return new HashMap<String, Object>() {{
            put("missionName", missionName);
            put("imageryType", imageryType);
            put("startDate", startDate);
            put("endDate", endDate);
        }};
    }
}