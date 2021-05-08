package io.aritra.phantomread.service;

import io.aritra.phantomread.entity.Order;
import io.aritra.phantomread.entity.Product;
import io.aritra.phantomread.repository.OrderRepository;
import io.aritra.phantomread.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class OrderService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Optional<Order> placeOrder(Long productId, Long userId) {
        Optional<Product> product = productRepository.findById(productId);
        product.orElseThrow(() -> new RuntimeException("Invalid product id"));

        if (product.get().getAvailableUnits() > 0) {
            productRepository.decrementAvailableUnitsCountBy1(productId);
            Order newOrder = new Order(userId, productId);
            orderRepository.save(newOrder);
            return Optional.of(newOrder);
        }
        return Optional.empty();
    }
}
