package net.mosur.spaceagency.controller;


import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.mosur.spaceagency.domain.exception.InvalidRequestException;
import net.mosur.spaceagency.domain.exception.ResourceNotFoundException;
import net.mosur.spaceagency.domain.model.ImageryType;
import net.mosur.spaceagency.domain.model.Mission;
import net.mosur.spaceagency.service.MissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/missions")
public class MissionController {

    private final MissionService missionService;

    @Autowired
    public MissionController(MissionService missionService) {
        this.missionService = missionService;
    }

    @PostMapping
    @RolesAllowed("MANAGER")
    public ResponseEntity createMission(@Valid @RequestBody NewMissionParam newMissionParam,
                                        BindingResult bindingResult) {
        checkInput(newMissionParam, bindingResult);

        Mission mission = new Mission(
                newMissionParam.getMissionName(),
                ImageryType.valueOf(newMissionParam.getImageryType()),
                newMissionParam.getStartDate(),
                newMissionParam.getFinishDate()
        );

       missionService.save(mission);
        Mission savedMission = missionService.findById(mission.getId()).get();

        return ResponseEntity.status(201).body(new HashMap<String, Object>() {{
            put("mission", savedMission);
        }});
    }

    private void checkInput(NewMissionParam newMissionParam, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }
        if (missionService.findByMissionName(newMissionParam.getMissionName()).isPresent()) {
            bindingResult.rejectValue("missionName", "DUPLICATED", "duplicated mission name");
        }
        if (imageryTypeNotExists(newMissionParam.getImageryType())) {
            bindingResult.rejectValue("imageryType", "BAD_TYPE", "this imagery type doesn't exists");
        }
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }
    }

    private boolean imageryTypeNotExists(String imageryType) {
        return !Arrays.stream(ImageryType.values()).anyMatch(type -> type.name().equals(imageryType));
    }

    @PutMapping(path = "/{name}")
    @RolesAllowed("MANAGER")
    public ResponseEntity<?> updateMission(@PathVariable("name") String missionName,
                                           @Valid @RequestBody UpdateMissionParam updateMissionParam) {
        return missionService.findByMissionName(missionName).map(mission -> {
                    mission.update(updateMissionParam.getMissionName(),
                            updateMissionParam.getImageryType(),
                            updateMissionParam.getStartDate(),
                            updateMissionParam.getFinishDate());
                    missionService.save(mission);
            return ResponseEntity.ok(missionResponse(missionService.findById(mission.getId()).get()));
                }
        ).orElseThrow(ResourceNotFoundException::new);
    }

    @DeleteMapping(path = "/{name}")
    @RolesAllowed("MANAGER")
    public ResponseEntity<?> deleteMission(@PathVariable("name") String missionName) {

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
@Setter
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

