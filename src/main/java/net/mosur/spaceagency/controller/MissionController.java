package net.mosur.spaceagency.controller;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.mosur.spaceagency.domain.exception.InvalidRequestException;
import net.mosur.spaceagency.domain.exception.ResourceNotFoundException;
import net.mosur.spaceagency.domain.model.ImageryType;
import net.mosur.spaceagency.domain.model.Mission;
import net.mosur.spaceagency.domain.model.User;
import net.mosur.spaceagency.service.MissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/missions")
public class MissionController {

    @Autowired
    private MissionService missionService;

    @PostMapping
    @RolesAllowed("MANAGER")
    public ResponseEntity createMission(@Valid @RequestBody NewMissionParam newMissionParam,
                                        BindingResult bindingResult,
                                        @AuthenticationPrincipal User user){
        if(bindingResult.hasErrors()){
            throw new InvalidRequestException(bindingResult);
        }

        Mission mission = new Mission(
                newMissionParam.getMissionName(),
                ImageryType.valueOf(newMissionParam.getImageryType()),
                newMissionParam.getStartDate(),
                newMissionParam.getFinishDate()
        );


        missionService.save(mission);
        return ResponseEntity.ok(new HashMap<String, Object>() {{
            put("mission", mission);
        }});
    }

    @PutMapping(path = "/{name}")
    @RolesAllowed("MANAGER")
    public ResponseEntity<?> updateMission(@PathVariable("name") String missionName,
                                           @Valid @RequestBody UpdateMissionParam updateMissionParam,
                                           @AuthenticationPrincipal User user) {
        Instant.now().toString();
        return missionService.findByMissionName(missionName).map(mission -> {
                    mission.update(updateMissionParam.getMissionName(),
                            updateMissionParam.getImageryType(),
                            updateMissionParam.getStartDate(),
                            updateMissionParam.getFinishDate());
                    missionService.save(mission);
                    return ResponseEntity.ok(missionResponse(missionService.findByMissionName(updateMissionParam.getMissionName()).get()));
                }
        ).orElseThrow(ResourceNotFoundException::new);
    }

    @DeleteMapping(path = "/{name}")
    @RolesAllowed("MANAGER")
    public ResponseEntity<?> deleteMission(@PathVariable("name") String missionName,
                                           @AuthenticationPrincipal User user){

        return missionService.findByMissionName(missionName).map(mission ->{
            missionService.delete(mission);
            return ResponseEntity.noContent().build();
        }).orElseThrow(ResourceNotFoundException::new);
    }

    private Map<String, Object> missionResponse(Mission mission){
        return new HashMap<String, Object>() {{
            put("mission", mission);
        }};
    }
}

@Getter
@JsonRootName("mission")
@NoArgsConstructor
class NewMissionParam{
    @NotBlank(message = "can't be empty")
    private String missionName;
    @NotBlank(message = "can't be empty")
    private String imageryType;

    private Instant startDate;

    private Instant finishDate;
}

@Getter
@JsonRootName("mission")
@NoArgsConstructor
class UpdateMissionParam{
    private String missionName = "";
    private String imageryType = "";
    private String startDate = "";
    private String finishDate = "";
}

