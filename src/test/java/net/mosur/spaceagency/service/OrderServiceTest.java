package net.mosur.spaceagency.service;

import net.mosur.spaceagency.domain.model.Product;
import net.mosur.spaceagency.domain.model.ProductsOrder;
import net.mosur.spaceagency.repository.ProductsOrderRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@DataJpaTest
@RunWith(SpringRunner.class)
@Import(OrderService.class)
public class OrderServiceTest {

    @Autowired
    OrderService orderService;
    @Autowired
    ProductsOrderRepository productsOrderRepository;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void should_make_order_success() {
        Product product = new Product();
        product.setUrl("url");
        Product product1 = new Product();
        product1.setUrl("url");

        orderService.makeOrder(asList(product, product1), 1L);
        List<ProductsOrder> orders = orderService.getOrdersHistory(1L);

        assertThat(orders.size(), is(1));
        assertTrue(orders.get(0).getProducts().contains(product));
        assertTrue(orders.get(0).getProducts().contains(product1));
    }

    @Test
    public void should_show_order_history() {
        Product product = new Product();
        product.setUrl("url");
        Product product1 = new Product();
        product1.setUrl("url");
        orderService.makeOrder(singletonList(product), 1L);
        orderService.makeOrder(singletonList(product1), 1L);

        List<ProductsOrder> orders = orderService.getOrdersHistory(1L);
        assertThat(orders.size(), is(2));
        assertTrue(orders.get(0).getProducts().contains(product));
        assertTrue(orders.get(1).getProducts().contains(product1));
    }


}