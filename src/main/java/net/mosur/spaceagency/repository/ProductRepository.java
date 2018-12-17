package net.mosur.spaceagency.repository;

import net.mosur.spaceagency.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Override
    Optional<Product> findById(Long aLong);

    @Override
    void deleteById(Long aLong);

//    @Override
//    @Query(value = "Select product from Product where missionName")

//   Iterable<Product> findAllByMission_MissionNameAndMission_ImageryTypeAAndAcquisitionDateBetween(String missionName, ImageryType imageryType, Instant from, Instant to);
}
