package net.mosur.spaceagency.domain.payload;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.mosur.spaceagency.domain.model.Product;

@Data
@NoArgsConstructor
public class BoughtProductResponse extends ProductResponse {

    String url;

    public BoughtProductResponse(Product product) {
        super(product);
        this.url = product.getUrl();
    }
}
