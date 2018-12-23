package net.mosur.spaceagency.controller;


import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.mosur.spaceagency.domain.exception.InvalidRequestException;
import net.mosur.spaceagency.domain.exception.ResourceNotFoundException;
import net.mosur.spaceagency.domain.model.Mission;
import net.mosur.spaceagency.domain.model.enums.ImageryType;
import net.mosur.spaceagency.service.MissionService;
import net.mosur.spaceagency.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/missions")
public class MissionController {

    private final MissionService missionService;
    private final ProductService productService;

    @Autowired
    public MissionController(MissionService missionService, ProductService productService) {
        this.missionService = missionService;
        this.productService = productService;
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

    @GetMapping
    @RolesAllowed("MANAGER")
    public ResponseEntity getMissions() {
        List<Mission> missions = new ArrayList<>();
        missionService.findAll().forEach(missions::add);
        return ResponseEntity.status(200).body(new HashMap<String, Object>() {{
            put("missions", missions);
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
        return Arrays.stream(ImageryType.values()).noneMatch(type -> type.name().equals(imageryType));
    }

    @PutMapping(path = "/{id}")
    @RolesAllowed("MANAGER")
    public ResponseEntity<?> updateMission(@PathVariable("id") long missionId,
                                           @Valid @RequestBody UpdateMissionParam updateMissionParam,
                                           BindingResult bindingResult) {
        return missionService.findById(missionId).map(mission -> {
                    if (missionService.findByMissionName(updateMissionParam.getMissionName()).isPresent()) {
                        bindingResult.rejectValue("missionName", "BAD NAME", "Mission with that name already exists!");
                        throw new InvalidRequestException(bindingResult);
                    }
                    mission.update(updateMissionParam.getMissionName(),
                            updateMissionParam.getImageryType(),
                            updateMissionParam.getStartDate(),
                            updateMissionParam.getFinishDate());
                    missionService.save(mission);
            return ResponseEntity.ok(missionResponse(missionService.findById(mission.getId()).get()));
                }
        ).orElseThrow(ResourceNotFoundException::new);
    }

    @DeleteMapping(path = "/{id}")
    @RolesAllowed("MANAGER")
    public ResponseEntity<?> deleteMission(@PathVariable("id") long missionId) {
        return missionService.findById(missionId).map(mission -> {
            if (missionService.hasProducts(mission)) {
                return ResponseEntity.badRequest().body(new HashMap<String, Object>() {{
                    put("errors", "Mission has product, delete its products first");
                }});
            }
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

