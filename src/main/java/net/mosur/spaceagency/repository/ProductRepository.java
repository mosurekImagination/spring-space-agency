package net.mosur.spaceagency.repository;

import net.mosur.spaceagency.domain.model.Product;
import net.mosur.spaceagency.domain.model.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {

    @Override
    Optional<Product> findById(Long aLong);

    @Override
    List<Product> findAllById(Iterable<Long> iterable);

    @Override
    void deleteById(Long aLong);

    List<Product> findByUsersWithAccessContains(User user);


    List<Product> findAll(Specification<Product> spec);

//    List<Product> findAllOrOrderByUsersWithAccess

}
