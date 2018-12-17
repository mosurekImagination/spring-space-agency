package net.mosur.spaceagency.repository;

import net.mosur.spaceagency.domain.model.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long> {

    @Override
    Optional<Product> findById(Long aLong);

    @Override
    void deleteById(Long aLong);

}
