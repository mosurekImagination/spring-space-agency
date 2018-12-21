package net.mosur.spaceagency.domain.specification;

import lombok.AllArgsConstructor;
import net.mosur.spaceagency.domain.model.ImageryType;
import net.mosur.spaceagency.domain.model.Mission;
import net.mosur.spaceagency.domain.model.Product;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import java.time.Instant;

@AllArgsConstructor

public class ProductSpecification {

    public static Specification<Product> hasMissionName(String missionName) {
        return (Specification<Product>) (root, criteriaQuery, criteriaBuilder) -> {
            if (missionName == null) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }
            Join<Product, Mission> groupJoin = root.join("mission");
            return criteriaBuilder.equal(groupJoin.<String>get("missionName"), missionName);
        };
    }

    public static Specification<Product> hasProductType(String productType) {
        return (Specification<Product>) (root, criteriaQuery, criteriaBuilder) -> {
            if (productType == null) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }
            Join<Product, Mission> groupJoin = root.join("mission");
            return criteriaBuilder.equal(groupJoin.<String>get("imageryType"), ImageryType.valueOf(productType));
        };
    }

    public static Specification<Product> hasAcquisitionDateAfter(String date) {
        return (Specification<Product>) (root, criteriaQuery, criteriaBuilder) -> {
            if (date == null) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }
            return criteriaBuilder.lessThan(root.get("acquisitionDate"), Instant.parse(date));
        };
    }

    public static Specification<Product> hasAcquisitionDateBefore(String date) {
        return (Specification<Product>) (root, criteriaQuery, criteriaBuilder) -> {
            if (date == null) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }
            return criteriaBuilder.greaterThan(root.get("acquisitionDate"), Instant.parse(date));
        };
    }

    public static Specification<Product> hasAcquisitionDateBetween(String takenAfter, String takenBefore) {
        return (Specification<Product>) (root, criteriaQuery, criteriaBuilder) -> {
            if (takenAfter == null || takenBefore == null) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }
            return criteriaBuilder.between(root.get("acquisitionDate"), Instant.parse(takenAfter), Instant.parse(takenBefore));
        };
    }


}
