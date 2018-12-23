package net.mosur.spaceagency.controller;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import net.mosur.spaceagency.domain.model.Coordinate;
import net.mosur.spaceagency.domain.model.Mission;
import net.mosur.spaceagency.domain.model.Product;
import net.mosur.spaceagency.domain.model.enums.ImageryType;
import net.mosur.spaceagency.domain.payload.BoughtProductResponse;
import net.mosur.spaceagency.domain.payload.ProductResponse;
import net.mosur.spaceagency.service.MissionService;
import net.mosur.spaceagency.service.ProductService;
import net.mosur.spaceagency.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(ProductController.class)
@Import({ProductService.class, UserService.class})
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private MissionService missionService;

    @MockBean
    private UserService userService;

    @Before
    public void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    @WithMockUser
    public void should_create_product_success() {
        String missionName = "TestName";
        String imageryType = ImageryType.MULTISPECTRAL.toString();
        String acquisitionDate = "2018-12-18T22:21:38.175691600Z";
        String price = "100.00";
        String url = "http://asdf.pl";

        Product product = new Product();
        Mission mission = new Mission();
        mission.update(missionName, imageryType, "", "");

        product.setId(1L);
        product.setMission(mission);

        when(missionService.findByMissionName(eq(missionName))).thenReturn(Optional.of(mission));

        Map<String, Object> param = prepareCreateProductParameter(missionName, acquisitionDate, price, url, getValidCoords()
        );

        given()
                .contentType("application/json")
                .body(param)
                .when()
                .post("/products/")
                .then()
                .statusCode(201)
                .body("product.missionName", equalTo(missionName));

        verify(productService).save(any());
    }

    @Test
    @WithMockUser
    public void should_create_product_without_missionName_fail() {
        String missionName = "";
        String acquisitionDate = "2018-12-18T22:21:38.175691600Z";
        String price = "100.00";
        String url = "http://asdf.pl";

        Map<String, Object> param = prepareCreateProductParameter(missionName, acquisitionDate, price, url, getValidCoords()
        );

        given()
                .contentType("application/json")
                .body(param)
                .when()
                .post("/products/")
                .then()
                .statusCode(422)
                .body("errors.missionName[0]", equalTo("can't be empty"));
    }

    @Test
    @WithMockUser
    public void should_create_product_with_invalid_coords_fail() {
        String missionName = "testMission2";
        String acquisitionDate = "2018-12-18T22:21:38.175691600Z";
        String price = "100.00";
        String url = "http://asdf.pl";
        List<Coordinate> coords = getValidCoords();
        coords.get(0).setLatitude(null);
        Map<String, Object> param = prepareCreateProductParameter(missionName, acquisitionDate, price, url, coords
        );

        given()
                .contentType("application/json")
                .body(param)
                .when()
                .post("/products/")
                .then()
                .statusCode(422)
                .body("errors.footprint[0]", equalTo("longitude and latitude cannot be null"));
    }

    @Test
    @WithMockUser
    public void should_create_product_without_url_fail() {
        String missionName = "test";
        String acquisitionDate = "2018-12-18T22:21:38.175691600Z";
        String price = "100.00";
        String url = "";

        Map<String, Object> param = prepareCreateProductParameter(missionName, acquisitionDate, price, url, getValidCoords()
        );

        given()
                .contentType("application/json")
                .body(param)
                .when()
                .post("/products/")
                .then()
                .statusCode(422)
                .body("errors.url[0]", equalTo("can't be empty"));
    }

    @Test
    @WithMockUser
    public void should_create_product_without_price_fail() {
        String missionName = "test";
        String acquisitionDate = "2018-12-18T22:21:38.175691600Z";
        String price = "";
        String url = "url:";

        Map<String, Object> param = prepareCreateProductParameter(missionName, acquisitionDate, price, url, getValidCoords()
        );

        given()
                .contentType("application/json")
                .body(param)
                .when()
                .post("/products/")
                .then()
                .statusCode(422)
                .body("errors.price[0]", equalTo("can't be empty"));
    }


    @Test
    @WithMockUser
    public void should_create_product_without_acquisitionDate_fail() {
        String missionName = "test";
        String acquisitionDate = "";
        String price = "100.00";
        String url = "url:";

        Map<String, Object> param = prepareCreateProductParameter(missionName, acquisitionDate, price, url, getValidCoords()
        );

        given()
                .contentType("application/json")
                .body(param)
                .when()
                .post("/products/")
                .then()
                .statusCode(422)
                .body("errors.acquisitionDate[0]", equalTo("can't be empty"));
    }

    @Test
    @WithMockUser
    public void should_create_product_without_footprint_fail() {
        String missionName = "test";
        String acquisitionDate = "2018-12-18T22:21:38.175691600Z";
        String price = "100.00";
        String url = "http://asdf.pl";

        Map<String, Object> param = prepareCreateProductParameter(missionName, acquisitionDate, price, url, new ArrayList<>()
        );

        given()
                .contentType("application/json")
                .body(param)
                .when()
                .post("/products/")
                .then()
                .statusCode(422)
                .body("errors.footprint.size()", equalTo(1));
    }

    @Test
    @WithMockUser
    public void should_create_product_with_2_point_footprint_fail() {
        String missionName = "test";
        String acquisitionDate = "2018-12-18T22:21:38.175691600Z";
        String price = "100.00";
        String url = "http://asdf.pl";

        Map<String, Object> param = prepareCreateProductParameter(missionName, acquisitionDate, price, url,
                Arrays.asList(
                        new Coordinate(1, 1),
                        new Coordinate(1, 2)
                )
        );

        given()
                .contentType("application/json")
                .body(param)
                .when()
                .post("/products/")
                .then()
                .statusCode(422)
                .body("errors.footprint.size()", equalTo(1));
    }

    @Test
    @WithMockUser
    public void should_create_product_with_3_point_footprint_success() {
        String missionName = "TestName";
        String imageryType = ImageryType.MULTISPECTRAL.toString();
        String acquisitionDate = "2018-12-18T22:21:38.175691600Z";
        String price = "100.00";
        String url = "http://asdf.pl";

        Product product = new Product();
        Mission mission = new Mission();
        mission.update(missionName, imageryType, "", "");

        product.setId(1L);
        product.setMission(mission);

        when(missionService.findByMissionName(eq(missionName))).thenReturn(Optional.of(mission));

        Map<String, Object> param = prepareCreateProductParameter(missionName, acquisitionDate, price, url,
                Arrays.asList(
                        new Coordinate(1, 1),
                        new Coordinate(1, 2),
                        new Coordinate(1, 3)
                )
        );

        given()
                .contentType("application/json")
                .body(param)
                .when()
                .post("/products/")
                .then()
                .statusCode(201)
                .body("product.missionName", equalTo(missionName))
                .body("product.price.toString()", equalTo("100.0"));

        verify(productService).save(any());
    }

    @Test
    @WithMockUser
    public void should_delete_product_success() {
        Long productId = 1L;
        Product product = new Product();
        product.setId(1L);

        when(productService.findById(eq(1L))).thenReturn(Optional.of(product));

        given()
                .contentType("application/json")
                .when()
                .delete("/products/{id}", productId)
                .then()
                .statusCode(204);

        verify(productService).deleteById(eq(productId));
    }

    @Test
    @WithMockUser
    public void should_show_error_deleting_product_with_orders() {
        Long productId = 1L;
        Product product = new Product();
        product.setId(1L);

        when(productService.findById(eq(1L))).thenReturn(Optional.of(product));
        when(productService.hasOrders(any())).thenReturn(true);

        given()
                .contentType("application/json")
                .when()
                .delete("/products/{id}", productId)
                .then()
                .statusCode(400)
                .body("errors", equalTo("Cannot delete this products, it has customer orders!"));
    }

    @Test
    @WithMockUser
    public void should_search_product_success() {
        Product product = new Product();
        product.setId(1L);
        Product product1 = new Product();
        product.setId(2L);

        when(productService.findProductsWithCriteria(any(), any(), any(), any(), any(), any()))
                .thenReturn(Arrays.asList(product, product1));
        given()
                .contentType("application/json")
                .when()
                .get("/products/")
                .then()
                .statusCode(200);
    }

    @Test
    @WithMockUser
    public void should_get_bought_product_with_url_success() {
        Product product = new Product(new Mission(), Instant.now(), getValidCoords(), BigDecimal.valueOf(100), "url");
        product.setId(1L);

        when(productService.findById(anyLong())).thenReturn(Optional.of(product));
        when(userService.getUserId(any())).thenReturn(1L);
        when(productService.getProductDetail(any(), anyLong())).thenReturn(new BoughtProductResponse(product));

        given()
                .contentType("application/json")
                .when()
                .get("/products/{id}", 1)
                .then()
                .statusCode(200)
                .body("product.id", equalTo(1))
                .body("product", hasKey("url"))
                .body("product.url", equalTo("url"));
    }

    @Test
    @WithMockUser
    public void should_hide_product_url_success() {
        Product product = new Product(new Mission(), Instant.now(), getValidCoords(), BigDecimal.valueOf(100), "url");
        product.setId(1L);

        when(productService.findById(anyLong())).thenReturn(Optional.of(product));
        when(userService.getUserId(any())).thenReturn(1L);
        when(productService.getProductDetail(any(), anyLong())).thenReturn(new ProductResponse(product));

        given()
                .contentType("application/json")
                .when()
                .get("/products/{id}", 1)
                .then()
                .statusCode(200)
                .body("product.id", equalTo(1))
                .body("product", not(hasKey("url")));
    }

    private Map<String, Object> prepareCreateProductParameter(String missionName, String acquisitionDate, String price, String url, List<Coordinate> coords) {
        return new HashMap<String, Object>() {{
            put("missionName", missionName);
            put("acquisitionDate", acquisitionDate);
            put("price", price);
            put("url", url);
            put("footprint", coords);
        }};
    }

    private List<Coordinate> getValidCoords() {
        return Arrays.asList(
                new Coordinate(1, 1),
                new Coordinate(-1, 1),
                new Coordinate(1, -1),
                new Coordinate(-1, -1)
        );
    }
}