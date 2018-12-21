package net.mosur.spaceagency.service;

import net.mosur.raycasting.Point;
import net.mosur.raycasting.Polygon;
import net.mosur.spaceagency.domain.model.Coordinate;
import net.mosur.spaceagency.domain.model.Product;
import net.mosur.spaceagency.domain.payload.BoughtProductResponse;
import net.mosur.spaceagency.domain.payload.ProductResponse;
import net.mosur.spaceagency.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.mosur.spaceagency.domain.specification.ProductSpecification.*;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderService orderService;

    @Autowired
    public ProductService(ProductRepository productRepository, OrderService orderService) {
        this.productRepository = productRepository;
        this.orderService = orderService;
    }

    public void save(Product product) {
        productRepository.save(product);
    }

    public Optional<Product> findById(Long id){
        return productRepository.findById(id);
    }

    public void deleteById(Long id){
        productRepository.deleteById(id);
    }

    public List<Product> findProductsWithCriteria(String missionName, String productType, String acquisitionDateFrom, String acquistionDateTo, Double longitude, Double latitude) {
        Specification<Product> specification = Specification.where(
                hasMissionName(missionName)
                        .and(hasProductType(productType)
                                .and(hasAcquisitionDateAfter(acquisitionDateFrom)
                                        .and(hasAcquisitionDateBefore(acquistionDateTo)))));
        List<Product> products = productRepository.findAll(specification);

        if (longitude == null || latitude == null) {
            return products;
        } else {
            Point searchPoint = new Point(longitude, latitude);
            return products.stream().filter(product -> hasPointInside(searchPoint, product)).collect(Collectors.toList());
        }
    }

    private boolean hasPointInside(Point searchPoint, Product product) {
        List<Coordinate> coords = product.getFootprint();
        List<Point> points = coords.stream().map(coord -> new Point(coord.getLongitude(), coord.getLatitude())).collect(Collectors.toList());
        Polygon polygon = new Polygon(points);
        return polygon.contains(searchPoint);
    }

    public List<Product> getProductsByIds(List<Long> productsIds) {
        return productRepository.findAllById(productsIds);
    }

    public ProductResponse getProductResponseWithUrl(Product product, Long userId) {
        if (orderService.hasAccessToProduct(product, userId)) {
            return new BoughtProductResponse(product);
        }
        return new ProductResponse(product);
    }

    public ProductResponse getProductResponse(Product product) {
        return new ProductResponse(product);
    }
}
