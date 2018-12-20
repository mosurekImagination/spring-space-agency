package net.mosur.spaceagency.domain.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Mission mission;

    private Instant acquisitionDate;

    @OneToMany
    private List<Coordinate> footprint;
    private BigDecimal price;
    private String url;

    @ManyToMany
    @Setter
    private Set<User> usersWithAccess;

    public Product(Mission mission, Instant acquisitionDate, List<Coordinate> footprint, BigDecimal price, String url) {
        this.mission = mission;
        this.acquisitionDate = acquisitionDate;
        this.footprint = footprint;
        this.price = price;
        this.url = url;
        this.usersWithAccess = new HashSet<>();
    }
}
