package net.mosur.spaceagency.repository;

import net.mosur.spaceagency.domain.model.Mission;
import net.mosur.spaceagency.domain.model.Product;
import net.mosur.spaceagency.domain.model.ProductsOrder;
import net.mosur.spaceagency.domain.model.enums.ImageryType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@DataJpaTest
@RunWith(SpringRunner.class)
public class ProductsOrderRepositoryTest {

    @Autowired
    MissionRepository missionRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductsOrderRepository productsOrderRepository;

    private Product productMission1;
    private Product productMission2;
    private Mission mission1;
    private Mission mission2;

    @Before
    public void setUp() {
        mission2 = new Mission("test1", ImageryType.MULTISPECTRAL, Instant.now(), Instant.now());
        mission1 = new Mission("test2", ImageryType.MULTISPECTRAL, Instant.now(), Instant.now());
        productMission1 = new Product(mission2, Instant.parse("2018-12-19T23:25:41.880900700Z"), null, BigDecimal.valueOf(100), "url");
        productMission2 = new Product(mission1, Instant.parse("2018-12-19T23:25:41.880900700Z"), null, BigDecimal.valueOf(100), "url");

        missionRepository.save(mission1);
        productRepository.save(productMission1);
        missionRepository.save(mission2);
        productRepository.save(productMission2);
    }

    @Test
    public void should_find_most_ordered_missions() {
        ProductsOrder productMission1Order = new ProductsOrder(1, Collections.singletonList(productMission1));
        ProductsOrder productMission2Order = new ProductsOrder(2, Collections.singletonList(productMission2));
        ProductsOrder productMission2Order2 = new ProductsOrder(3, Collections.singletonList(productMission2));
        productsOrderRepository.save(productMission1Order);
        productsOrderRepository.save(productMission2Order);
        productsOrderRepository.save(productMission2Order2);

        List<Mission> missionList = productsOrderRepository.getMostOrderedMissions();

        assertEquals(missionList.size(), 2);
        assertEquals(missionList.get(0), mission2);

        ProductsOrder productMission1Order2 = new ProductsOrder(2, Collections.singletonList(productMission1));
        ProductsOrder productMission1Order3 = new ProductsOrder(3, Collections.singletonList(productMission1));

        productsOrderRepository.save(productMission1Order2);
        productsOrderRepository.save(productMission1Order3);

        missionList = productsOrderRepository.getMostOrderedMissions();
        assertEquals(missionList.size(), 2);
        assertEquals(missionList.get(0), mission1);
    }


    @Test
    public void should_find_most_ordered_products() {
        ProductsOrder product1order = new ProductsOrder(1, Collections.singletonList(productMission1));
        ProductsOrder product1order2 = new ProductsOrder(2, Collections.singletonList(productMission1));

        ProductsOrder product2order = new ProductsOrder(2, Collections.singletonList(productMission2));

        productsOrderRepository.save(product1order);
        productsOrderRepository.save(product1order2);
        productsOrderRepository.save(product2order);

        List<Product> productsList = productsOrderRepository.getMostOrderedProducts();

        assertEquals(productsList.size(), 2);
        assertEquals(productsList.get(0), productMission1);

        ProductsOrder product2order2 = new ProductsOrder(4, Collections.singletonList(productMission2));
        ProductsOrder product2order3 = new ProductsOrder(5, Collections.singletonList(productMission2));

        productsOrderRepository.save(product2order2);
        productsOrderRepository.save(product2order3);

        productsList = productsOrderRepository.getMostOrderedProducts();
        assertEquals(productsList.size(), 2);
        assertEquals(productsList.get(0), productMission2);
    }

    @Test
    public void should_generate_create_date() {
        ProductsOrder product2order = new ProductsOrder(2, Collections.singletonList(productMission2));

        productsOrderRepository.save(product2order);
        Optional<ProductsOrder> order = productsOrderRepository.findById(product2order.getId());
        ProductsOrder o = order.get();
        assertNotNull(o.getCreatedAt());
    }
}