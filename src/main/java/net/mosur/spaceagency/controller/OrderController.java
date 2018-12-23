package net.mosur.spaceagency.controller;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.mosur.spaceagency.domain.exception.InvalidRequestException;
import net.mosur.spaceagency.domain.model.Mission;
import net.mosur.spaceagency.domain.model.Product;
import net.mosur.spaceagency.domain.model.ProductsOrder;
import net.mosur.spaceagency.service.OrderService;
import net.mosur.spaceagency.service.ProductService;
import net.mosur.spaceagency.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
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

    @PostMapping
    @RolesAllowed("CUSTOMER")
    public ResponseEntity<?> orderProducts(@Valid @RequestBody OrderProductsParam orderProductsParam,
                                           Principal principal,
                                           BindingResult bindingResult) { //TO-DO
        Long userId = userService.getUserId(principal);
        List<Product> products = productService.getProductsByIds(orderProductsParam.getProductsIds());
        checkInput(userId, products, bindingResult, orderProductsParam);
        checkIfUserOrderedProductsAlready(userId, products, bindingResult);
        orderService.makeOrder(products, userId);
        return ResponseEntity.ok(
                new HashMap<String, Object>() {{
                    put("boughtProducts", products.stream().map(product -> productService.getProductResponseWithUrl(product, userId)));
                }});
    }

    private void checkInput(Long userId, List<Product> products, BindingResult bindingResult, OrderProductsParam params) {
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }
        if (products.isEmpty()) {
            bindingResult.rejectValue("productsIds", "INCORRECT PRODUCTS", "Products not exists");
        }
        if (products.size() != params.getProductsIds().size()) {
            bindingResult.rejectValue("productsIds", "INCORRECT PRODUCTS", "One of selected products not exists");
        }
        checkIfUserOrderedProductsAlready(userId, products, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }
    }

    private void checkIfUserOrderedProductsAlready(Long userId, List<Product> products, BindingResult bindingResult) {
        List<Long> boughtProductsIds = new ArrayList<>();
        products.forEach(product -> {
            if (orderService.hasAccessToProduct(product, userId)) {
                boughtProductsIds.add(product.getId());
            }
        });
        if (!boughtProductsIds.isEmpty()) {
            bindingResult.rejectValue("productsIds", "PRODUCT ORDERED", "Product with Ids bought before: " + boughtProductsIds);
        }
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

@Getter
@Setter
@JsonRootName("product")
@NoArgsConstructor
class OrderProductsParam {
    private List<Long> productsIds;
}
