package net.mosur.spaceagency.controller;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import net.mosur.spaceagency.domain.model.ImageryType;
import net.mosur.spaceagency.domain.model.Mission;
import net.mosur.spaceagency.domain.model.Product;
import net.mosur.spaceagency.service.MissionService;
import net.mosur.spaceagency.service.ProductService;
import net.mosur.spaceagency.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(ProductController.class)
@Import({ProductService.class, UserService.class})
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private MissionService missionService;


    @Before
    public void setUp() throws Exception {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    @WithMockUser
    public void should_create_product_success() throws Exception {
        String missionName = "TestName";
        String imageryType = ImageryType.MULTISPECTRAL.toString();
        String acquisitionDate = "2018-12-18T22:21:38.175691600Z";
        String price = "100.00";
        String url = "http://asdf.pl";

        Product product = new Product();
        Mission mission = new Mission();
        mission.update(missionName, imageryType, "", "");

        product.setId(1L);
        product.setMission(mission);

        when(missionService.findByMissionName(eq(missionName))).thenReturn(Optional.of(mission));

        Map<String, Object> param = prepareCreateProductParameter(missionName, acquisitionDate, price, url);

        given()
                .contentType("application/json")
                .body(param)
                .when()
                .post("/products/")
                .then()
                .statusCode(201)
                .body("product.missionName", equalTo(missionName));

        verify(productService).save(any());
    }

    @Test
    @WithMockUser
    public void should_delete_product_success() throws Exception {
        Long productId = 1L;
        Product product = new Product();
        product.setId(1L);

        when(productService.findById(eq(1L))).thenReturn(Optional.of(product));

        given()
                .contentType("application/json")
                .when()
                .delete("/products/{id}", productId)
                .then()
                .statusCode(204);

        verify(productService).deleteById(eq(productId));
    }

    @Test
    @WithMockUser
    public void should_search_product_success() throws Exception {
        Product product = new Product();
        product.setId(1L);
        Product product1 = new Product();
        product.setId(2L);

        when(productService.findProductsWithCriteria(any(), any(), any(), any(), any(), any()))
                .thenReturn(Arrays.asList(product, product1));
        given()
                .contentType("application/json")
                .when()
                .get("/products/")
                .then()
                .statusCode(200);
    }

    private Map<String, Object> prepareCreateProductParameter(String missionName, String acquisitionDate, String price, String url) {
        return new HashMap<String, Object>() {{
            put("missionName", missionName);
            put("acquisitionDate", acquisitionDate);
            put("price", price);
            put("url", url);
        }};
    }
}