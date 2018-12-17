package net.mosur.spaceagency.service;

import net.mosur.spaceagency.domain.model.Product;
import net.mosur.spaceagency.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        List<Product> results = new ArrayList<>();

        return results;
    }
}
