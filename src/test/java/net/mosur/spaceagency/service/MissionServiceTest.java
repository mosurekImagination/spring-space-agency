package net.mosur.spaceagency.service;

import net.mosur.spaceagency.domain.model.Mission;
import net.mosur.spaceagency.domain.model.enums.ImageryType;
import net.mosur.spaceagency.repository.MissionRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@DataJpaTest
@RunWith(SpringRunner.class)
@Import({MissionService.class})
public class MissionServiceTest {

    @MockBean
    ProductService productService;
    @Autowired
    MissionRepository missionRepository;

    @Autowired
    MissionService missionService;

    private final String MISSION_NAME = "mission_name";
    private final ImageryType IMAGERY_TYPE = ImageryType.MULTISPECTRAL;
    private final String START_DATE = "2018-12-19T23:25:41.880900700Z";
    private final String FINISH_DATE = "2018-12-19T23:25:41.880900700Z";
    private Mission createdMission;

    @Before
    public void setUp() {
        createdMission = new Mission(MISSION_NAME, IMAGERY_TYPE, Instant.parse(START_DATE), Instant.parse(FINISH_DATE));
        missionRepository.save(createdMission);
    }

    @Test
    public void should_get_mission_by_name_success() {
        Optional<Mission> optional = missionService.findByMissionName(MISSION_NAME);
        assertThat(optional.isPresent(), is(true));

        Mission mission = optional.get();

        assertEquals(mission.getMissionName(), MISSION_NAME);
        assertEquals(mission.getImageryType(), IMAGERY_TYPE);
        assertThat(mission.getStartDate(), notNullValue());
        assertThat(mission.getFinishDate(), notNullValue());
    }

    @Test
    public void should_get_mission_by_name_fail() {
        Optional<Mission> optional = missionService.findByMissionName(MISSION_NAME + "asdf");
        assertThat(optional.isPresent(), is(false));
    }

    @Test
    public void should_get_mission_by_id_success() {
        Optional<Mission> optional = missionService.findById(createdMission.getId());
        assertThat(optional.isPresent(), is(true));

        Mission mission = optional.get();

        assertEquals(mission.getMissionName(), MISSION_NAME);
        assertEquals(mission.getImageryType(), IMAGERY_TYPE);
        assertThat(mission.getStartDate(), notNullValue());
        assertThat(mission.getFinishDate(), notNullValue());
    }

    @Test
    public void should_get_mission_by_id_fail() {
        Optional<Mission> optional = missionService.findById(-1L);
        assertThat(optional.isPresent(), is(false));
    }

    @Test
    public void should_save_mission_success() {
        String newMissionName = MISSION_NAME + "2";
        Mission toSave = new Mission(newMissionName, ImageryType.PANCHROMATIC, Instant.now(), Instant.now());
        missionService.save(toSave);
        Optional<Mission> optional = missionService.findByMissionName(newMissionName);
        assertThat(optional.isPresent(), is(true));

        Mission mission = optional.get();
        assertEquals(mission.getMissionName(), newMissionName);
        assertEquals(mission.getImageryType(), ImageryType.PANCHROMATIC);
        assertThat(mission.getStartDate(), notNullValue());
        assertThat(mission.getFinishDate(), notNullValue());
    }

    @Test
    public void should_delete_mission_success() {
        long id = createdMission.getId();
        missionService.delete(createdMission);

        Optional<Mission> optional = missionService.findById(id);

        assertThat(optional.isPresent(), is(false));
    }
}