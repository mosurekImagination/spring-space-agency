package net.mosur.spaceagency.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.mosur.spaceagency.domain.audits.DateAudit;

import javax.persistence.*;
import java.util.List;

@Entity
@EqualsAndHashCode(of = {"id"})
@Data
@NoArgsConstructor
public class ProductsOrder extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long userId;

    @ManyToMany
    List<Product> products;

    public ProductsOrder(long userId, List<Product> products) {
        this.userId = userId;
        this.products = products;
    }
}
