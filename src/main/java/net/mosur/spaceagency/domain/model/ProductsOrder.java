package net.mosur.spaceagency.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@EqualsAndHashCode(of = {"id"})
@Data
public class ProductsOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long userId;

    @CreatedDate
    Instant createTime;

    @ManyToMany
    List<Product> products;

    public ProductsOrder(long userId, List<Product> products) {
        this.userId = userId;
        this.products = products;
    }
}
