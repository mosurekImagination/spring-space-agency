package net.mosur.spaceagency.domain.payload;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.mosur.spaceagency.domain.model.Product;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BoughtProductResponse extends ProductResponse {

    String url;

    public BoughtProductResponse(Product product) {
        super(product);
        this.url = product.getUrl();
    }
}
