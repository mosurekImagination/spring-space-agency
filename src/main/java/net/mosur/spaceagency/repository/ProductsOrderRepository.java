package net.mosur.spaceagency.repository;

import net.mosur.spaceagency.domain.model.Mission;
import net.mosur.spaceagency.domain.model.Product;
import net.mosur.spaceagency.domain.model.ProductsOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductsOrderRepository extends JpaRepository<ProductsOrder, Long> {

    List<ProductsOrder> findAllByUserIdOrderByCreatedAt(long userId);

    @Query("Select products.mission from ProductsOrder o inner join o.products as products group by products.mission order by count(products.mission.id)")
    List<Mission> getMostOrderedMissions();

    @Query("Select products from ProductsOrder o inner join o.products as products group by products.id order by count(products.id) desc")
    List<Product> getMostOrderedProducts();

    Optional<ProductsOrder> productsContainsAndAndUserId(Product product, long userId);


}
