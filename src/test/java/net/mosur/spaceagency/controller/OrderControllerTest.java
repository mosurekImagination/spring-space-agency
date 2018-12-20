package net.mosur.spaceagency.controller;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import net.mosur.spaceagency.domain.model.Mission;
import net.mosur.spaceagency.domain.model.Product;
import net.mosur.spaceagency.domain.model.ProductsOrder;
import net.mosur.spaceagency.domain.payload.ProductResponse;
import net.mosur.spaceagency.service.OrderService;
import net.mosur.spaceagency.service.ProductService;
import net.mosur.spaceagency.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private ProductService productService;
    @MockBean
    private OrderService orderService;

    @Before
    public void setUp() throws Exception {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    @WithMockUser
    public void should_return_order_history_success() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setUrl("url");
        Product product1 = new Product();
        product.setId(2L);
        product.setUrl("url");
        ProductsOrder order = new ProductsOrder(1L, Arrays.asList(product, product1));

        when(userService.getUserId(any())).thenReturn(1L);
        when(orderService.getOrdersHistory(1L)).thenReturn(Arrays.asList(order));

        given()
                .contentType("application/json")
                .when()
                .get("/orders/history")
                .then()
                .statusCode(200)
                .body("orders.size()", equalTo(1))
                .body("orders[0].products.size()", equalTo(2))
                .body("orders[0].products[0].url", equalTo("url"));

        verify(orderService).getOrdersHistory(anyLong());
    }

    @Test
    @WithMockUser
    public void should_buy_product_success() throws Exception {
        List<Long> productIds = Arrays.asList(1L, 2L);
        Product product = new Product();
        product.setId(1L);
        Product product1 = new Product();
        product.setId(2L);
        Mission mission = new Mission();
        mission.setMissionName("test");
        product.setMission(mission);
        product1.setMission(mission);
        product.setUrl("url");
        product1.setUrl("url");

        ProductResponse productResponse = new ProductResponse();
        productResponse.setMissionName("test");
        productResponse.setUrl("url");

        HashMap<String, Object> params = new HashMap<String, Object>() {{
            put("productsIds", productIds);
        }};
        when(productService.getProductsByIds(any())).thenReturn(Arrays.asList(product, product1));
        when(productService.getProductResponse(any(), any())).thenReturn(productResponse);
        when(userService.getUserId(any())).thenReturn(1L);

        given()
                .contentType("application/json")
                .body(params)
                .when()
                .post("/orders/")
                .then()
                .statusCode(200)
                .body("boughtProducts.size()", equalTo(2))
                .body("boughtProducts[0].missionName", equalTo("test"))
                .body("boughtProducts[0].url", equalTo("url"));

        verify(orderService).makeOrder(any(), anyLong());
    }

    @Test
    @WithMockUser
    public void should_return_most_ordered_products_success() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setUrl("url");
        Product product1 = new Product();
        product.setId(2L);
        product.setUrl("url");

        when(orderService.getMostPopularProducts()).thenReturn(Arrays.asList(product, product1));
        given()
                .contentType("application/json")
                .when()
                .get("/orders/popular/products/")
                .then()
                .statusCode(200)
                .body("products.size()", equalTo(2));

        verify(orderService).getMostPopularProducts();
    }

    @Test
    @WithMockUser
    public void should_return_most_ordered_missions_success() throws Exception {
        Mission mission = new Mission();
        mission.setMissionName("test");
        Mission mission1 = new Mission();
        mission1.setMissionName("test2");

        when(orderService.getMostPopularMissions()).thenReturn(Arrays.asList(mission, mission1));
        given()
                .contentType("application/json")
                .when()
                .get("/orders/popular/missions/")
                .then()
                .statusCode(200)
                .body("missions.size()", equalTo(2));

        verify(orderService).getMostPopularMissions();
    }
}