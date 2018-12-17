package net.mosur.spaceagency.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import net.mosur.spaceagency.service.MissionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(MissionController.class)
public class MissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MissionController missionController;

    @MockBean
    private MissionService missionService;

    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {

    }

    @Test
    @WithMockUser(roles="MANAGER")
    public void createMission() throws Exception {
        NewMissionParam newMission = new NewMissionParam();
        newMission.setMissionName("Test Name7");
        newMission.setImageryType("MULTISPECTRAL");

        String jsonRequest = mapper.writeValueAsString(newMission);
//
//        when(missionService.save(any())).thenReturn(return new Mission("Test Name7",
//                ImageryType.MULTISPECTRAL,
//                Instant.now(),
//                Instant.now()
//                ));

        mockMvc.perform(post("/missions/")
                .content(jsonRequest)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("asdf"));

    }

    @Test
    public void updateMission() {
    }

    @Test
    public void deleteMission() {
    }
}