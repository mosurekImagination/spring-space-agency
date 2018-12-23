package net.mosur.spaceagency.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.mosur.spaceagency.domain.model.enums.ImageryType;

import javax.persistence.*;
import java.time.Instant;


@Data
@EqualsAndHashCode(of = {"id"})
@Entity
@NoArgsConstructor
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String missionName;

    private ImageryType imageryType;
    private Instant startDate;
    private Instant finishDate;


    public Mission(String missionName, ImageryType imageryType, Instant startDate, Instant finishDate) {
        this.missionName = missionName;
        this.imageryType = imageryType;
        this.startDate = startDate;
        this.finishDate = finishDate;
    }

    public void update(String missionName, String imageryType, String startDate, String finishDate){ // TO-DO
        if(notBlank(missionName)){
            this.missionName = missionName;
        }
        if(notBlank(imageryType)){
            this.imageryType = ImageryType.valueOf(imageryType);
        }
        if(notBlank(startDate)){
            this.startDate = Instant.parse(startDate); // TO-DO
        }
        if(notBlank(finishDate)){
            this.finishDate = Instant.parse(finishDate);
        }
    }

    private boolean notBlank(String text) {
        return !"".equals(text);
    }

}
