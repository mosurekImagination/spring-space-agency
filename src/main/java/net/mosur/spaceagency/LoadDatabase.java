package net.mosur.spaceagency;

import lombok.extern.slf4j.Slf4j;
import net.mosur.spaceagency.domain.model.ImageryType;
import net.mosur.spaceagency.domain.model.Mission;
import net.mosur.spaceagency.domain.model.Product;
import net.mosur.spaceagency.repository.MissionRepository;
import net.mosur.spaceagency.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(MissionRepository missionRepository, ProductRepository productRepository) {
        return args -> {
            Mission mission = new Mission();
            mission.setMissionName("test Mission");
            mission.setImageryType(ImageryType.MULTISPECTRAL);

            Product product = new Product();
            product.setMission(mission);
            log.info("Preloading:" + missionRepository.save(mission));
            log.info("Preloading:" + productRepository.save(product));
        };
    }
}
