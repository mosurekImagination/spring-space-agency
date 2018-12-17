package net.mosur.spaceagency.repository;

import net.mosur.spaceagency.domain.model.Mission;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MissionRepository extends CrudRepository<Mission, Long> {

    Optional<Mission> findByMissionName(String name);


}
