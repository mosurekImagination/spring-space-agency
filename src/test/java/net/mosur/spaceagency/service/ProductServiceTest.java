package net.mosur.spaceagency.service;

import net.mosur.spaceagency.domain.model.ImageryType;
import net.mosur.spaceagency.domain.model.Mission;
import net.mosur.spaceagency.domain.model.Product;
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
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@DataJpaTest
@RunWith(SpringRunner.class)
@Import(ProductService.class)
public class ProductServiceTest {

    @Autowired
    ProductService productService;
    @Autowired
    ProductRepository productRepository;
    private final String ACQUISITION_DATE = "2018-12-19T23:25:41.880900700Z";
    private Product createdProduct;

    @Before
    public void setUp() throws Exception {
        Mission mission = new Mission("test", ImageryType.MULTISPECTRAL, Instant.now(), Instant.now());
        createdProduct = new Product(mission, Instant.parse(ACQUISITION_DATE), null, new BigDecimal(100), "url");

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