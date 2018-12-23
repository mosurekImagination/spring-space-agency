package net.mosur.spaceagency.service;

import net.mosur.spaceagency.domain.model.Mission;
import net.mosur.spaceagency.repository.MissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MissionService {

    private final MissionRepository missionRepository;

    @Autowired
    public MissionService(MissionRepository missionRepository) {
        this.missionRepository = missionRepository;
    }

    public Optional<Mission> findByMissionName(String name){
        return missionRepository.findByMissionName(name);
    }

    public Optional<Mission> findById(Long id) {
        return missionRepository.findById(id);
    }

    public void save(Mission mission){
        missionRepository.save(mission);
    }

    public void delete(Mission mission){
        missionRepository.delete(mission);
    }

    public Iterable<Mission> findAll() {
        return missionRepository.findAll();
    }
}
