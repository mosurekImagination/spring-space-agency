package net.mosur.spaceagency.service;

import net.mosur.spaceagency.domain.model.Product;
import net.mosur.spaceagency.domain.payload.ProductResponse;
import net.mosur.spaceagency.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static net.mosur.spaceagency.domain.specification.ProductSpecification.*;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;


    public void save(Product product) {
        productRepository.save(product);
    }

    public Optional<Product> findById(Long id){
        return productRepository.findById(id);
    }

    public void deleteById(Long id){
        productRepository.deleteById(id);
    }

    public List<Product> findProductsWithCriteria(String missionName, String productType, String acquisitionDateFrom, String acquistionDateTo) {
        Specification<Product> specification = Specification.where(
                hasMissionName(missionName)
                        .and(hasProductType(productType)
                                .and(hasAcquisitionDateAfter(acquisitionDateFrom)
                                        .and(hasAcquisitionDateBefore(acquistionDateTo)))));
        return productRepository.findAll(specification);
    }

    public List<Product> getUserProducts(long userId) {
        return null;
        // return productRepository.findByUsersWithAccessContains(user);
    }

    public List<Product> getProductsByIds(List<Long> productsIds) {
        return productRepository.findAllById(productsIds);
    }

    public ProductResponse getProductResponse(Product product, Long userId) {
        return new ProductResponse(product, userId);
    }

    public ProductResponse getProductResponse(Product product) {
        return new ProductResponse(product);
    }
}
