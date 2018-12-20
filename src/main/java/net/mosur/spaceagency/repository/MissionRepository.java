package net.mosur.spaceagency.repository;

import net.mosur.spaceagency.domain.model.Mission;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MissionRepository extends CrudRepository<Mission, Long> {

    Optional<Mission> findByMissionName(String name);

}
