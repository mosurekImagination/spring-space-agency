package net.mosur.spaceagency.service;

import net.mosur.spaceagency.domain.model.Mission;
import net.mosur.spaceagency.domain.model.Product;
import net.mosur.spaceagency.domain.model.ProductsOrder;
import net.mosur.spaceagency.repository.ProductsOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    ProductsOrderRepository productsOrderRepository;


    public ProductsOrder makeOrder(List<Product> products, long userId) {
        ProductsOrder order = new ProductsOrder(userId, products);
        productsOrderRepository.save(order);
        return order;
    }

    public List<ProductsOrder> getOrdersHistory(Long userId) {
        return productsOrderRepository.findAllByUserIdOrderByCreatedAt(userId);
    }

    public List<Mission> getMostPopularMissions() {
        return productsOrderRepository.getMostOrderedMissions();
    }

    public List<Product> getMostPopularProducts() {
        return productsOrderRepository.getMostOrderedProducts();
    }
}
