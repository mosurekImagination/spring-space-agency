package net.mosur.spaceagency.service;

import net.mosur.spaceagency.domain.model.Mission;
import net.mosur.spaceagency.domain.model.Product;
import net.mosur.spaceagency.domain.model.ProductsOrder;
import net.mosur.spaceagency.domain.model.enums.ImageryType;
import net.mosur.spaceagency.repository.MissionRepository;
import net.mosur.spaceagency.repository.ProductRepository;
import net.mosur.spaceagency.repository.ProductsOrderRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@DataJpaTest
@RunWith(SpringRunner.class)
@Import(OrderService.class)
public class OrderServiceTest {

    @Autowired
    OrderService orderService;
    @Autowired
    MissionRepository missionRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductsOrderRepository productsOrderRepository;

    Mission mission1;
    Mission mission2;
    Product mission1Product;
    Product mission1Product2;
    Product mission2Product;

    @Before
    public void setUp() {
        mission1 = new Mission("testMission", ImageryType.PANCHROMATIC, Instant.now(), Instant.now());
        mission2 = new Mission("testMission2", ImageryType.PANCHROMATIC, Instant.now(), Instant.now());

        missionRepository.save(mission1);
        missionRepository.save(mission2);

        mission1Product = new Product();
        mission1Product.setUrl("url");
        mission1Product.setMission(mission1);
        mission1Product2 = new Product();
        mission1Product2.setMission(mission1);
        mission1Product2.setUrl("url");
        mission2Product = new Product();
        mission2Product.setMission(mission2);
        mission2Product.setUrl("url");

        productRepository.save(mission1Product);
        productRepository.save(mission1Product2);
        productRepository.save(mission2Product);

    }

    @Test
    public void should_make_order_success() {
        orderService.makeOrder(asList(mission1Product, mission2Product), 1L);
        List<ProductsOrder> orders = orderService.getOrdersHistory(1L);

        assertThat(orders.size(), is(1));
        assertTrue(orders.get(0).getProducts().contains(mission1Product));
        assertTrue(orders.get(0).getProducts().contains(mission2Product));
    }

    @Test
    public void should_show_order_history() {
        orderService.makeOrder(singletonList(mission1Product), 1L);
        orderService.makeOrder(singletonList(mission1Product2), 1L);

        List<ProductsOrder> orders = orderService.getOrdersHistory(1L);
        assertThat(orders.size(), is(2));
        assertTrue(orders.get(0).getProducts().contains(mission1Product));
        assertTrue(orders.get(1).getProducts().contains(mission1Product2));
    }

    @Test
    public void should_get_popular_missions() {
        orderService.makeOrder(singletonList(mission1Product), 1L);
        orderService.makeOrder(singletonList(mission1Product), 2L);
        orderService.makeOrder(singletonList(mission2Product), 1L);
        List<Mission> missions = orderService.getMostPopularMissions();
        assertEquals(missions.get(0), mission1);
    }

    @Test
    public void should_get_popular_products() {
        orderService.makeOrder(singletonList(mission1Product), 1L);
        orderService.makeOrder(singletonList(mission1Product), 2L);
        orderService.makeOrder(singletonList(mission2Product), 1L);
        List<Product> mostPopularProducts = orderService.getMostPopularProducts();
        assertEquals(mostPopularProducts.get(0), mission1Product);

        orderService.makeOrder(singletonList(mission2Product), 3L);
        orderService.makeOrder(singletonList(mission2Product), 4L);

        mostPopularProducts = orderService.getMostPopularProducts();
        assertEquals(mostPopularProducts.get(0), mission2Product);
    }

    @Test
    public void popular_mission_should_not_include_missions_withour_orders() {
        orderService.makeOrder(singletonList(mission1Product), 1L);
        orderService.makeOrder(singletonList(mission1Product), 2L);
        List<Mission> orders = orderService.getMostPopularMissions();
        assertThat(orders.size(), is(1));
        assertEquals(orders.get(0), mission1);
    }


}