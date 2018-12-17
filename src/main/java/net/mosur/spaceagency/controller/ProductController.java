package net.mosur.spaceagency.controller;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.mosur.spaceagency.domain.exception.InvalidRequestException;
import net.mosur.spaceagency.domain.exception.ResourceNotFoundException;
import net.mosur.spaceagency.domain.model.Coordinate;
import net.mosur.spaceagency.domain.model.Product;
import net.mosur.spaceagency.domain.model.User;
import net.mosur.spaceagency.service.MissionService;
import net.mosur.spaceagency.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private MissionService missionService;

    @PostMapping
    @RolesAllowed("MANAGER")
    public ResponseEntity addProduct(@Valid @RequestBody NewProductParam newProductParam,
                      BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new InvalidRequestException(bindingResult);
        }

        Product product = new Product();
        product.setURL(newProductParam.getUrl());
        product.setPrice(new BigDecimal(newProductParam.getPrice()));
        product.setAcquisitionDate(Instant.parse(newProductParam.getAcquisitionDate()));

        product.setMission(missionService.findByMissionName(newProductParam.getMissionName()).get());

        productService.save(product);
        return ResponseEntity.status(201).body(new HashMap<String, Object>() {{
            put("product", product);
        }});
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
                               @RequestParam(value = "acquistionDateTo", required = false) String acquistionDateTo){
        return ResponseEntity.ok(productService.findProductsWithCriteria(missionName, productType, acquisitionDateFrom, acquistionDateTo));

    }

    @PostMapping(path = "/buy")
    @RolesAllowed("CUSTOMER")
    public ResponseEntity<?> buyProducts(@Valid @RequestBody BuyProductsParam buyProductsParam,
                                         @AuthenticationPrincipal User user){
        productService.buyProducts(buyProductsParam.getProductsIds(), user);
        return ResponseEntity.ok(
                new HashMap<String, Object>() {{
                    put("userProducts", productService.getUserProducts(user));
                }});
    }
}

@Getter
@JsonRootName("product")
@NoArgsConstructor
class NewProductParam{
    private String missionName = "";
    private List<Coordinate> footprint = new ArrayList<>();
    private String url = "";
    private String price = "";
    private String acquisitionDate = "";

}

@Getter
@NoArgsConstructor
class BuyProductsParam{
    private List<Long> productsIds;
}

