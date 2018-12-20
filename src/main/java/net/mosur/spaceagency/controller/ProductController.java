package net.mosur.spaceagency.controller;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.mosur.spaceagency.domain.exception.InvalidRequestException;
import net.mosur.spaceagency.domain.exception.ResourceNotFoundException;
import net.mosur.spaceagency.domain.model.Coordinate;
import net.mosur.spaceagency.domain.model.Product;
import net.mosur.spaceagency.domain.model.User;
import net.mosur.spaceagency.domain.payload.ProductResponse;
import net.mosur.spaceagency.service.MissionService;
import net.mosur.spaceagency.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    private final MissionService missionService;

    @Autowired
    public ProductController(ProductService productService, MissionService missionService) {
        this.productService = productService;
        this.missionService = missionService;
    }

    @PostMapping
    @RolesAllowed("MANAGER")
    public ResponseEntity createProduct(@Valid @RequestBody NewProductParam newProductParam,
                                        BindingResult bindingResult) {
        checkInput(newProductParam, bindingResult);

        Product product = new Product();
        product.setUrl(newProductParam.getUrl());
        product.setPrice(new BigDecimal(newProductParam.getPrice()));
        product.setAcquisitionDate(Instant.parse(newProductParam.getAcquisitionDate()));

        product.setMission(missionService.findByMissionName(newProductParam.getMissionName()).get());

        productService.save(product);
        return ResponseEntity.status(201).body(new HashMap<String, Object>() {{
            put("product", new ProductResponse(product));
        }});
    }

    private void checkInput(NewProductParam newProductParam, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }
        if (!missionService.findByMissionName(newProductParam.getMissionName()).isPresent()) {
            bindingResult.rejectValue("missionName", "NOT EXISTS", "mission with that name doesnt exists");
        }
        if (new BigDecimal(newProductParam.getPrice()).compareTo(BigDecimal.ZERO) < 0) {
            bindingResult.rejectValue("price", "LESS THAN ZERO", "price must be 0 or more");
        }
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }
    }

    @DeleteMapping(path = "/{id}")
    @RolesAllowed("MANAGER")
    public ResponseEntity deleteProduct(@PathVariable("id") long id){
        return productService.findById(id).map(product -> {
            productService.deleteById(id);
            return ResponseEntity.noContent().build();
        }).orElseThrow(ResourceNotFoundException::new);
    }

    @GetMapping(path = "/search")
    @RolesAllowed("CUSTOMER")
    public ResponseEntity<?> searchProducts(@RequestParam(value = "missionName", required = false) String missionName,
                                            @RequestParam(value = "productType", required = false) String productType,
                                            @RequestParam(value = "acquisitionDateFrom ", required = false) String acquisitionDateFrom,
                                            @RequestParam(value = "acquistionDateTo", required = false) String acquisitionDateTo) {
        List<Product> products = productService.findProductsWithCriteria(missionName, productType, acquisitionDateFrom, acquisitionDateTo);
        return ResponseEntity.ok(products.stream().map(productService::getProductResponse));
    }

    @PostMapping(path = "/buy")
    @RolesAllowed("CUSTOMER")
    public ResponseEntity<?> buyProducts(@Valid @RequestBody BuyProductsParam buyProductsParam,
                                         @AuthenticationPrincipal User user){
        List<Product> products = productService.getProductsByIds(buyProductsParam.getProductsIds());
        productService.buyProducts(products, user);
        return ResponseEntity.ok(
                new HashMap<String, Object>() {{
                    put("boughtProducts", products.stream().map(product -> productService.getProductResponse(product, user)));
                }});
    }
}

@Getter
@JsonRootName("product")
@NoArgsConstructor
class NewProductParam{
    @NotBlank(message = "can't be empty")
    private String missionName = "";
    private List<Coordinate> footprint = new ArrayList<>();
    @NotBlank(message = "can't be empty")
    private String url = "";
    @NotBlank(message = "can't be empty")
    private String price = "";
    private String acquisitionDate = "";

}

@Getter
@JsonRootName("products")
@NoArgsConstructor
class BuyProductsParam{
    private List<Long> productsIds;
}

