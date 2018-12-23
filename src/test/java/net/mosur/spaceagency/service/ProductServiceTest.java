package net.mosur.spaceagency.service;

import net.mosur.spaceagency.domain.exception.UnauthorizedException;
import net.mosur.spaceagency.domain.model.Coordinate;
import net.mosur.spaceagency.domain.model.Mission;
import net.mosur.spaceagency.domain.model.Product;
import net.mosur.spaceagency.domain.model.enums.ImageryType;
import net.mosur.spaceagency.domain.payload.BoughtProductResponse;
import net.mosur.spaceagency.domain.payload.ProductResponse;
import net.mosur.spaceagency.repository.MissionRepository;
import net.mosur.spaceagency.repository.ProductRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@DataJpaTest
@RunWith(SpringRunner.class)
@Import({ProductService.class, UserService.class, OrderService.class})
public class ProductServiceTest {

    @Autowired
    ProductService productService;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    MissionRepository missionRepository;

    @MockBean
    OrderService orderService;

    private final String ACQUISITION_DATE = "2018-12-19T23:25:41.880900700Z";
    private Product createdProduct;
    private Product createdProduct2;

    @Before
    public void setUp() {
        Mission mission = new Mission("test", ImageryType.MULTISPECTRAL, Instant.now(), Instant.now());
        Mission mission2 = new Mission("test2", ImageryType.PANCHROMATIC, Instant.now(), Instant.now());
        List<Coordinate> coords = Arrays.asList(
                new Coordinate(-1, 1),
                new Coordinate(1, 1),
                new Coordinate(1, -1),
                new Coordinate(-1, -1));
        createdProduct = new Product(mission, Instant.parse(ACQUISITION_DATE), coords, new BigDecimal(100), "url");
        List<Coordinate> coords2 = Arrays.asList(
                new Coordinate(-2, 2),
                new Coordinate(2, 2),
                new Coordinate(2, -2),
                new Coordinate(-2, -2));
        createdProduct2 = new Product(mission2, Instant.parse(ACQUISITION_DATE), coords2, new BigDecimal(100), "url");

        missionRepository.save(mission);
        missionRepository.save(mission2);
        productRepository.save(createdProduct);
        productRepository.save(createdProduct2);
    }

    @Test
    public void should_delete_product() {
        long id = createdProduct.getId();
        productService.deleteById(id);

        Optional<Product> optional = productService.findById(id);
        assertThat(optional.isPresent(), is(false));
    }

    @Test
    public void should_find_by_id() {
        Optional<Product> optional = productService.findById(createdProduct.getId());
        assertThat(optional.isPresent(), is(true));

        Product product = optional.get();

        assertEquals(product.getId(), createdProduct.getId());
        assertEquals(product.getMission(), createdProduct.getMission());
    }

    @Test
    public void should_find_by_id_fail() {
        Optional<Product> optional = productService.findById(createdProduct.getId() + 10000);
        assertThat(optional.isPresent(), is(false));
    }

    @Test
    public void should_get_products_response_without_url() {
        when(orderService.hasAccessToProduct(eq(createdProduct), anyLong())).thenReturn(true);
        BoughtProductResponse response = productService.getProductResponseWithUrl(createdProduct, 1L);
        assertThat(response.getUrl(), equalTo(createdProduct.getUrl()));
    }

    @Test(expected = UnauthorizedException.class)
    public void should_get_products_response_without_url_fail() {
        when(orderService.hasAccessToProduct(eq(createdProduct), anyLong())).thenReturn(false);
        productService.getProductResponseWithUrl(createdProduct, 1L);
    }

    @Test
    public void should_find_product_by_missionName() {
        List<Product> products = productService.findProductsWithCriteria(
                "test", null, null, null, null, null);
        assertThat(products.size(), equalTo(1));
        assertThat(products.get(0), equalTo(createdProduct));
    }

    @Test
    public void should_find_product_by_imageryType() {
        List<Product> products = productService.findProductsWithCriteria(
                null, createdProduct.getMission().getImageryType().toString(),
                null, null, null, null);
        assertThat(products.size(), equalTo(1));
        assertThat(products.get(0), equalTo(createdProduct));
    }

    @Test
    public void should_not_find_product_combined_criteria() {
        List<Product> products = productService.findProductsWithCriteria(
                "test2", createdProduct.getMission().getImageryType().toString(),
                null, null, null, null);
        assertTrue(products.isEmpty());
    }

    @Test
    public void should_find_product_combined_criteria() {
        List<Product> products = productService.findProductsWithCriteria(
                "test", createdProduct.getMission().getImageryType().toString(),
                null, null, null, null);
        assertThat(products.size(), equalTo(1));
        assertThat(products.get(0), equalTo(createdProduct));
    }

    @Test
    public void should_find_product_by_covering_point() {
        List<Product> products = productService.findProductsWithCriteria(
                null, null,
                null, null, 1.5, 1.5);
        assertThat(products.size(), equalTo(1));
        assertThat(products.get(0), equalTo(createdProduct2));
    }

    @Test
    public void should_find_product_by_covering_point_fail() {
        List<Product> products = productService.findProductsWithCriteria(
                "test", createdProduct.getMission().getImageryType().toString(),
                null, null, 3.0, 0.5);
        assertTrue(products.isEmpty());
    }

    @Test
    public void should_find_product_by_date() {
        List<Product> products = productService.findProductsWithCriteria(
                null, null,
                null, Instant.now().toString(), null, null);
        assertTrue(products.isEmpty());
    }

    @Test
    public void should_get_bought_product_detail_with_url() {
        when(orderService.hasAccessToProduct(eq(createdProduct), eq(1L))).thenReturn(true);
        BoughtProductResponse response = (BoughtProductResponse) productService.getProductDetail(createdProduct, 1L);
        assertThat(response.getUrl(), equalTo(createdProduct.getUrl()));
    }

    @Test
    public void should_get_product_detail_without_url() {
        when(orderService.hasAccessToProduct(eq(createdProduct), eq(1L))).thenReturn(false);
        ProductResponse response = productService.getProductDetail(createdProduct, 1L);
        assertThat(response.getId(), equalTo(createdProduct.getId()));
    }
}