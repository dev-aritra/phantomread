package io.aritra.phantomread.service;

import io.aritra.phantomread.entity.Order;
import io.aritra.phantomread.entity.Product;
import io.aritra.phantomread.repository.OrderRepository;
import io.aritra.phantomread.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderServiceTest {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        deleteAllData();
    }

    @Test
    void shouldCreateOrder() throws InterruptedException {
        Product iPhone12 = new Product("iPhone12", 1);
        productRepository.saveAndFlush(iPhone12);

        ExecutorService threadPool = Executors.newFixedThreadPool(100);
        List<Callable<Optional<Order>>> orderRequests = getOrderRequests(iPhone12.getId(), 100);
        List<Future<Optional<Order>>> results = threadPool.invokeAll(orderRequests);
        Thread.sleep(3000);
        long successCount = results.stream()
                .filter(Future::isDone)
                .map(result -> {
                    try {
                        return result.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        return Optional.empty();
                    }
                })
                .filter(Optional::isPresent)
                .count();
        assertEquals(1, successCount);
    }

    private List<Callable<Optional<Order>>> getOrderRequests(Long productId, int count) {
        return LongStream.range(0, count)
                .mapToObj(index -> {
                    Callable<Optional<Order>> task = () -> orderService.placeOrder(productId, index);
                    return task;
                }).collect(Collectors.toList());
    }

    @AfterEach
    void tearDown() {
//        deleteAllData();
    }

    private void deleteAllData() {
        productRepository.deleteAll();
        orderRepository.deleteAll();
    }
}