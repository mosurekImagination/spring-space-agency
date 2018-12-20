package net.mosur.spaceagency.domain.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.mosur.spaceagency.domain.model.Coordinate;
import net.mosur.spaceagency.domain.model.Product;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
public class ProductResponse {

    private long id;
    private String missionName;
    private Instant acquisitionDate;
    private List<Coordinate> footPrint;
    private BigDecimal price;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String url;

    public ProductResponse(Product product) {
        this.id = product.getId();
        if (product.getMission() != null) {
            this.missionName = product.getMission().getMissionName();
        }
        this.acquisitionDate = product.getAcquisitionDate();
        this.footPrint = product.getFootprint();
        this.price = product.getPrice();
    }

    public ProductResponse(Product product, Long userId) {
        this(product);
//        if (product.getUsersWithAccess() != null && product.getUsersWithAccess().contains(userId)) {
//            this.url = product.getUrl();
//        } /TO-DO
    }

}
