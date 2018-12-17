package net.mosur.spaceagency.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JsonIgnore
    private Mission mission;

    private String missionName;

    private Instant acquisitionDate;

    @OneToMany
    private List<Coordinate> footprint;
    private BigDecimal price;
    private String URL;

    @ManyToMany
    @Setter
    private Set<User> usersWithAccess;

    public void setMission(Mission m) { // TO-DO
        this.mission = m;
        missionName = m.getMissionName();
    }

}
