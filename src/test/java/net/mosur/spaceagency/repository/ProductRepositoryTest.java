package net.mosur.spaceagency.repository;

import net.mosur.spaceagency.domain.model.ImageryType;
import net.mosur.spaceagency.domain.model.Mission;
import net.mosur.spaceagency.domain.model.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static net.mosur.spaceagency.domain.specification.ProductSpecification.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsIn.isIn;
import static org.junit.Assert.*;

@DataJpaTest
@RunWith(SpringRunner.class)
public class ProductRepositoryTest {

    @Autowired
    MissionRepository missionRepository;

    @Autowired
    ProductRepository productRepository;

    Product product;

    public final String MISSION_NAME = "testMission";
    public final ImageryType IMAGERY_TYPE = ImageryType.MULTISPECTRAL;
    public final String ACQUISITION_DATE = "2018-12-19T23:25:41.880900700Z";

    @Before
    public void setUp() {
        Mission mission = new Mission(MISSION_NAME, IMAGERY_TYPE, Instant.now(), Instant.now());
        product = new Product(mission, Instant.parse(ACQUISITION_DATE), null, BigDecimal.valueOf(100), "url");

        missionRepository.save(mission);
    }

    @Test
    public void findAll() {
    }

    @Test
    public void should_create_and_fetch_product_success() {
        productRepository.save(product);
        Optional<Product> optional = productRepository.findById(product.getId());
        assertThat(optional.isPresent(), is(true));
    }

    @Test
    public void should_find_products_via_mission_name_success() {
        productRepository.save(product);
        Specification<Product> specification = hasMissionName(MISSION_NAME);
        List<Product> products = productRepository.findAll(Specification.where(specification));
        assertThat(products.isEmpty(), is(false));
        assertThat(product, isIn(products));

    }

    @Test
    public void should_find_products_via_product_type_success() {
        productRepository.save(product);
        Specification<Product> specification = hasProductType(IMAGERY_TYPE.toString());
        List<Product> products = productRepository.findAll(Specification.where(specification));
        assertThat(products.isEmpty(), is(false));
        assertThat(product, isIn(products));

    }

    @Test
    public void should_find_products_via_acquisition_date_before_success() {
        productRepository.save(product);
        Instant time = Instant.parse(ACQUISITION_DATE);
        Instant beforeTime = time.plusSeconds(-1);
        Specification<Product> specification = hasAcquisitionDateBefore(beforeTime.toString());
        List<Product> products = productRepository.findAll(Specification.where(specification));
        assertThat(products.isEmpty(), is(false));
        assertThat(product, isIn(products));
    }

    @Test
    public void should_find_products_via_acquisition_date_after_success() {
        productRepository.save(product);
        Instant time = Instant.parse(ACQUISITION_DATE);
        Instant aftertime = time.plusSeconds(1);
        Specification<Product> specification = hasAcquisitionDateAfter(aftertime.toString());
        List<Product> products = productRepository.findAll(Specification.where(specification));
        assertThat(products.isEmpty(), is(false));
        assertThat(product, isIn(products));
    }

    @Test
    public void should_find_products_via_acquisition_date_between_success() {
        productRepository.save(product);
        Instant time = Instant.parse(ACQUISITION_DATE);
        Instant timeAfter = time.plusSeconds(1);
        Instant timeBefore = time.minusSeconds(1);
        Specification<Product> specification = hasAcquisitionDateBetween(timeBefore.toString(), timeAfter.toString());
        List<Product> products = productRepository.findAll(Specification.where(specification));
        assertThat(products.isEmpty(), is(false));
        assertThat(product, isIn(products));
    }

    @Test
    public void should_find_products_via_two_criteria_success() {
        productRepository.save(product);
        Product product1 = new Product(product.getMission(), product.getAcquisitionDate().plusSeconds(10), null, BigDecimal.valueOf(150), "url");
        productRepository.save(product1);
        Instant time = Instant.parse(ACQUISITION_DATE);
        Instant takenAfter = time.minusSeconds(1);
        Instant takenBefore = time.plusSeconds(1);
        Specification<Product> specificationTime = hasAcquisitionDateBetween(takenAfter.toString(), takenBefore.toString());
        Specification<Product> specificationMissionName = hasMissionName(MISSION_NAME);
        List<Product> products = productRepository.findAll(Specification.where(specificationMissionName).and(specificationTime));
        assertThat(product, isIn(products));
        assertEquals(1, products.size());
    }

    @Test
    public void should_find_products_via_two_criteria_failed() {
        productRepository.save(product);
        Product product1 = new Product(product.getMission(), product.getAcquisitionDate().plusSeconds(10), null, BigDecimal.valueOf(150), "url");
        productRepository.save(product1);
        Instant time = Instant.parse(ACQUISITION_DATE);
        Instant takenAfter = time.minusSeconds(0);
        Instant takenBefore = time.plusSeconds(1);
        Specification<Product> specificationTime = hasAcquisitionDateBetween(takenAfter.toString(), takenBefore.toString());
        Specification<Product> specificationMissionName = hasMissionName(MISSION_NAME);
        List<Product> products = productRepository.findAll(Specification.where(specificationMissionName).and(specificationTime));
        assertThat(product, isIn(products));
        assertEquals(1, products.size());
    }

    @Test
    public void should_find_products_via_two_or_criteria_success() {
        productRepository.save(product);
        Product product1 = new Product(product.getMission(), product.getAcquisitionDate().plusSeconds(10), null, BigDecimal.valueOf(150), "url");
        productRepository.save(product1);
        Instant time = Instant.parse(ACQUISITION_DATE);
        Instant takenAfter = time.minusSeconds(0);
        Instant takenBefore = time.plusSeconds(1);
        Specification<Product> specificationTime = hasAcquisitionDateBetween(takenAfter.toString(), takenBefore.toString());
        Specification<Product> specificationMissionName = hasMissionName(MISSION_NAME);
        List<Product> products = productRepository.findAll(Specification.where(specificationMissionName).or(specificationTime));
        assertThat(product, isIn(products));
        assertEquals(2, products.size());
    }

    @Test
    public void should_find_products_via_acquisition_date_between_same_date_failed() {
        productRepository.save(product);
        Instant time = Instant.parse(ACQUISITION_DATE);
        Instant timeAfter = time.plusSeconds(0);
        Instant timeBefore = time.minusSeconds(1);
        Specification<Product> specification = hasAcquisitionDateBetween(timeBefore.toString(), timeAfter.toString());
        List<Product> products = productRepository.findAll(Specification.where(specification));
        assertFalse(products.contains(product));
    }
}