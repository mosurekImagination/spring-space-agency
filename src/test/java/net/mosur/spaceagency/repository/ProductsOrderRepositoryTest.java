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

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

@DataJpaTest
@RunWith(SpringRunner.class)
public class ProductsOrderRepositoryTest {

    @Autowired
    MissionRepository missionRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductsOrderRepository productsOrderRepository;

    private Product mission1Product;
    private Product mission2Product;
    private Mission mission1;
    private Mission mission2;

    @Before
    public void setUp() {
        mission2 = new Mission("test1", ImageryType.MULTISPECTRAL, Instant.now(), Instant.now());
        mission1 = new Mission("test2", ImageryType.MULTISPECTRAL, Instant.now(), Instant.now());
        mission1Product = new Product(mission1, Instant.parse("2018-12-19T23:25:41.880900700Z"), null, BigDecimal.valueOf(100), "url");
        mission2Product = new Product(mission2, Instant.parse("2018-12-19T23:25:41.880900700Z"), null, BigDecimal.valueOf(100), "url");

        missionRepository.save(mission1);
        productRepository.save(mission1Product);
        missionRepository.save(mission2);
        productRepository.save(mission2Product);
    }

    @Test
    public void should_find_most_ordered_missions() {
        ProductsOrder productMission1Order = new ProductsOrder(1, Collections.singletonList(mission1Product));
        ProductsOrder productMission2Order = new ProductsOrder(2, Collections.singletonList(mission2Product));
        ProductsOrder productMission2Order2 = new ProductsOrder(3, Collections.singletonList(mission2Product));
        productsOrderRepository.save(productMission1Order);
        productsOrderRepository.save(productMission2Order);
        productsOrderRepository.save(productMission2Order2);

        List<Mission> missionList = productsOrderRepository.getMostOrderedMissions();

        assertEquals(missionList.size(), 2);
        assertEquals(missionList.get(0), mission2);

        ProductsOrder productMission1Order2 = new ProductsOrder(2, Collections.singletonList(mission1Product));
        ProductsOrder productMission1Order3 = new ProductsOrder(3, Collections.singletonList(mission1Product));

        productsOrderRepository.save(productMission1Order2);
        productsOrderRepository.save(productMission1Order3);
        List<ProductsOrder> all = productsOrderRepository.findAll();
        missionList = productsOrderRepository.getMostOrderedMissions();
        assertEquals(missionList.size(), 2);
        assertEquals(missionList.get(0), mission1);
    }


    @Test
    public void should_find_most_ordered_products() {
        ProductsOrder product1order = new ProductsOrder(1, Collections.singletonList(mission1Product));
        ProductsOrder product1order2 = new ProductsOrder(2, Collections.singletonList(mission1Product));

        ProductsOrder product2order = new ProductsOrder(2, Collections.singletonList(mission2Product));

        productsOrderRepository.save(product1order);
        productsOrderRepository.save(product1order2);
        productsOrderRepository.save(product2order);

        List<Product> productsList = productsOrderRepository.getMostOrderedProducts();

        assertEquals(productsList.size(), 2);
        assertEquals(productsList.get(0), mission1Product);

        ProductsOrder product2order2 = new ProductsOrder(4, Collections.singletonList(mission2Product));
        ProductsOrder product2order3 = new ProductsOrder(5, Collections.singletonList(mission2Product));

        productsOrderRepository.save(product2order2);
        productsOrderRepository.save(product2order3);

        productsList = productsOrderRepository.getMostOrderedProducts();
        assertEquals(productsList.size(), 2);
        assertEquals(productsList.get(0), mission2Product);
    }

    @Test
    public void should_check_user_access_to_product() {
        ProductsOrder product1order = new ProductsOrder(1, Collections.singletonList(mission1Product));
        ProductsOrder product1order2 = new ProductsOrder(2, Collections.singletonList(mission1Product));


        productsOrderRepository.save(product1order);
        productsOrderRepository.save(product1order2);

        Optional<ProductsOrder> productsOrder = productsOrderRepository.productsContainsAndAndUserId(mission1Product, 1L);

        assertTrue(productsOrder.isPresent());
    }

    @Test
    public void should_check_user_access_to_product_fail() {
        ProductsOrder product1order = new ProductsOrder(1, Collections.singletonList(mission1Product));
        ProductsOrder product1order2 = new ProductsOrder(2, Collections.singletonList(mission2Product));


        productsOrderRepository.save(product1order);
        productsOrderRepository.save(product1order2);

        Optional<ProductsOrder> productsOrder = productsOrderRepository.productsContainsAndAndUserId(mission1Product, 2L);

        assertFalse(productsOrder.isPresent());
    }

    @Test
    public void should_generate_create_date() {
        ProductsOrder product2order = new ProductsOrder(2, Collections.singletonList(mission2Product));

        productsOrderRepository.save(product2order);
        Optional<ProductsOrder> order = productsOrderRepository.findById(product2order.getId());
        ProductsOrder o = order.get();
        assertNotNull(o.getCreatedAt());
    }
}