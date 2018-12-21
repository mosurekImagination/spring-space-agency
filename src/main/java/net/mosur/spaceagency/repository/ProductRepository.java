package net.mosur.spaceagency.repository;

import net.mosur.spaceagency.domain.model.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {

    @Override
    Optional<Product> findById(Long aLong);

    @Override
    List<Product> findAllById(Iterable<Long> iterable);

    @Override
    void deleteById(Long aLong);

    List<Product> findAll(Specification<Product> spec);
}
