package net.mosur.spaceagency.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

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

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Coordinate> footprint;

    private BigDecimal price;
    private String url;

    public Product(Mission mission, Instant acquisitionDate, List<Coordinate> footprint, BigDecimal price, String url) {
        this.mission = mission;
        this.acquisitionDate = acquisitionDate;
        this.footprint = footprint;
        this.price = price;
        this.url = url;
    }
}
