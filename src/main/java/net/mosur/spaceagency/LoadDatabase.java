package net.mosur.spaceagency;

import lombok.extern.slf4j.Slf4j;
import net.mosur.spaceagency.domain.model.Coordinate;
import net.mosur.spaceagency.domain.model.Mission;
import net.mosur.spaceagency.domain.model.Product;
import net.mosur.spaceagency.domain.model.ProductsOrder;
import net.mosur.spaceagency.domain.model.enums.ImageryType;
import net.mosur.spaceagency.repository.MissionRepository;
import net.mosur.spaceagency.repository.ProductRepository;
import net.mosur.spaceagency.repository.ProductsOrderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

@Configuration
@Slf4j
public class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(MissionRepository missionRepository, ProductRepository productRepository,
                                   ProductsOrderRepository productsOrderRepository) {
        return args -> {
            Mission mission = new Mission();
            mission.setMissionName("test Mission");
            mission.setImageryType(ImageryType.MULTISPECTRAL);
            mission.setStartDate(Instant.parse("2018-12-18T22:21:38.175691600Z"));
            mission.setFinishDate(Instant.parse("2019-12-18T22:21:38.175691600Z"));

            Product product = new Product();

            product.setFootprint(Arrays.asList(
                    new Coordinate(-1, 1),
                    new Coordinate(1, 1),
                    new Coordinate(1, -1),
                    new Coordinate(-1, -1)
                    )
            );
            product.setAcquisitionDate(Instant.now());
            product.setPrice(new BigDecimal(100.99));
            product.setMission(mission);
            product.setUrl("http://mision.pl/product");

            Product product2 = new Product();

            product2.setFootprint(Arrays.asList(
                    new Coordinate(-2, 2),
                    new Coordinate(2, 2),
                    new Coordinate(2, -2),
                    new Coordinate(-2, -2)
                    )
            );
            product2.setAcquisitionDate(Instant.now().plusSeconds(1000));
            product2.setPrice(new BigDecimal(50.99));
            product2.setMission(mission);
            product2.setUrl("http://mision.pl/product2");

            ProductsOrder order = new ProductsOrder(1L, Collections.singletonList(product));
            log.info("Preloading:" + missionRepository.save(mission));
            log.info("Preloading:" + productRepository.save(product));
            log.info("Preloading:" + productRepository.save(product2));
            log.info("Preloading:" + productsOrderRepository.save(order));
        };
    }
}
