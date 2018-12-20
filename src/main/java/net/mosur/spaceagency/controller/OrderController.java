package net.mosur.spaceagency.controller;

import net.mosur.spaceagency.domain.model.Mission;
import net.mosur.spaceagency.domain.model.Product;
import net.mosur.spaceagency.domain.model.ProductsOrder;
import net.mosur.spaceagency.service.OrderService;
import net.mosur.spaceagency.service.ProductService;
import net.mosur.spaceagency.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;

    @Autowired
    public OrderController(UserService userService, ProductService productService, OrderService orderService) {
        this.userService = userService;
        this.productService = productService;
        this.orderService = orderService;
    }


    @GetMapping("/history")
    @RolesAllowed("CUSTOMER")
    public ResponseEntity<?> getOrdersHistory(Principal principal) {
        Long userId = userService.getUserId(principal);
        List<ProductsOrder> ordersHistory = orderService.getOrdersHistory(userId);
        return ResponseEntity.ok(
                new HashMap<String, Object>() {{
                    put("orders", ordersHistory);
                }});
    }

    @PostMapping
    @RolesAllowed("CUSTOMER")
    public ResponseEntity<?> buyProducts(@Valid @RequestBody BuyProductsParam buyProductsParam,
                                         Principal principal) {
        Long userId = userService.getUserId(principal);
        List<Product> products = productService.getProductsByIds(buyProductsParam.getProductsIds());
        orderService.makeOrder(products, userId);
        return ResponseEntity.ok(
                new HashMap<String, Object>() {{
                    put("boughtProducts", products.stream().map(product -> productService.getProductResponseWithUrl(product, userId)));
                }});
    }


    @GetMapping("/popular/missions")
    @RolesAllowed("MANAGER")
    public ResponseEntity<?> getMostPopularMissions() {
        List<Mission> missions = orderService.getMostPopularMissions();
        return ResponseEntity.ok(
                new HashMap<String, Object>() {{
                    put("missions", missions);
                }});
    }

    @GetMapping("/popular/products")
    @RolesAllowed("MANAGER")
    public ResponseEntity<?> getMostPopularProduct() {
        List<Product> products = orderService.getMostPopularProducts();
        return ResponseEntity.ok(
                new HashMap<String, Object>() {{
                    put("products", products);
                }});
    }
}
