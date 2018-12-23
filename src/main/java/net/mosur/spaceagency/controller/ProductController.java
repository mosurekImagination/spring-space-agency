package net.mosur.spaceagency.controller;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.mosur.spaceagency.domain.exception.InvalidRequestException;
import net.mosur.spaceagency.domain.exception.ResourceNotFoundException;
import net.mosur.spaceagency.domain.model.Coordinate;
import net.mosur.spaceagency.domain.model.Product;
import net.mosur.spaceagency.domain.payload.ProductResponse;
import net.mosur.spaceagency.service.MissionService;
import net.mosur.spaceagency.service.ProductService;
import net.mosur.spaceagency.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final MissionService missionService;
    private final UserService userService;

    @Autowired
    public ProductController(ProductService productService, MissionService missionService, UserService userService) {
        this.productService = productService;
        this.missionService = missionService;
        this.userService = userService;
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

    @GetMapping(path = "/")
    @RolesAllowed("CUSTOMER")
    public ResponseEntity<?> searchProducts(@RequestParam(value = "missionName", required = false) String missionName,
                                            @RequestParam(value = "productType", required = false) String productType,
                                            @RequestParam(value = "acquisitionDateFrom ", required = false) String acquisitionDateFrom,
                                            @RequestParam(value = "acquistionDateTo", required = false) String acquisitionDateTo,
                                            @RequestParam(value = "longitude", required = false) Double longitude,
                                            @RequestParam(value = "latitude", required = false) Double latitude) {
        List<Product> products = productService.findProductsWithCriteria(missionName, productType, acquisitionDateFrom, acquisitionDateTo, longitude, latitude);
        return ResponseEntity.ok(new HashMap<String, Object>() {{
            put("products", products.stream().map(productService::getProductResponse));
        }});
    }

    @GetMapping(path = "/{id}")
    @RolesAllowed("CUSTOMER")
    public ResponseEntity<?> getProduct(@PathVariable(name = "id") long productId,
                                        Principal principal) {
        long userId = userService.getUserId(principal);
        return productService.findById(productId).map(product ->
                ResponseEntity.ok(productDetailResponse(product, userId)))
                .orElseThrow(ResourceNotFoundException::new);
    }

    private Map<String, Object> productDetailResponse(Product product, long userId) {
        return new HashMap<String, Object>() {{
            put("product", productService.getProductDetail(product, userId));
        }};
    }
}

@Getter
@JsonRootName("product")
@NoArgsConstructor
class NewProductParam{
    @NotBlank(message = "can't be empty")
    private String missionName = "";
    @Size(min = 3)
    private List<Coordinate> footprint = new ArrayList<>();
    @NotBlank(message = "can't be empty")
    private String url = "";
    @NotBlank(message = "can't be empty")
    private String price = "";
    @NotBlank(message = "can't be empty")
    private String acquisitionDate = "";

}


