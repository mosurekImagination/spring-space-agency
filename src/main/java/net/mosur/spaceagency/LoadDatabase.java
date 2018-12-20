package net.mosur.spaceagency;

import lombok.extern.slf4j.Slf4j;
import net.mosur.spaceagency.domain.model.ImageryType;
import net.mosur.spaceagency.domain.model.Mission;
import net.mosur.spaceagency.domain.model.Product;
import net.mosur.spaceagency.domain.model.ProductsOrder;
import net.mosur.spaceagency.repository.MissionRepository;
import net.mosur.spaceagency.repository.ProductRepository;
import net.mosur.spaceagency.repository.ProductsOrderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

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

            Product product = new Product();
            product.setMission(mission);
            ProductsOrder order = new ProductsOrder(1L, Arrays.asList(product));
            log.info("Preloading:" + missionRepository.save(mission));
            log.info("Preloading:" + productRepository.save(product));
            log.info("Preloading:" + productsOrderRepository.save(order));
        };
    }
}
