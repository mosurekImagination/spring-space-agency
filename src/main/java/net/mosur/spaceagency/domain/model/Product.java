package net.mosur.spaceagency.domain.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
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
    private List <Coordinate> footPrint;
    private BigDecimal price;
    private String URL;

    @ManyToMany
    @Setter
    private Set<User> usersWithAccess;
}
