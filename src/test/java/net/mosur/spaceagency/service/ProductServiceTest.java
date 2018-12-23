package net.mosur.spaceagency.service;

import net.mosur.spaceagency.domain.model.Coordinate;
import net.mosur.spaceagency.domain.model.ImageryType;
import net.mosur.spaceagency.domain.model.Mission;
import net.mosur.spaceagency.domain.model.Product;
import net.mosur.spaceagency.repository.MissionRepository;
import net.mosur.spaceagency.repository.ProductRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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

    private final String ACQUISITION_DATE = "2018-12-19T23:25:41.880900700Z";
    private Product createdProduct;

    @Before
    public void setUp() {
        Mission mission = new Mission("test", ImageryType.MULTISPECTRAL, Instant.now(), Instant.now());
        List<Coordinate> coords = Arrays.asList(new Coordinate(-1, 1),
                new Coordinate(1, 1),
                new Coordinate(1, -1),
                new Coordinate(-1, -1));
        createdProduct = new Product(mission, Instant.parse(ACQUISITION_DATE), coords, new BigDecimal(100), "url");

        missionRepository.save(mission);
        productRepository.save(createdProduct);
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

}