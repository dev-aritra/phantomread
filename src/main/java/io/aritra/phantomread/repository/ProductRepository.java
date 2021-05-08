package io.aritra.phantomread.repository;

import io.aritra.phantomread.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("update products set available_units = available_units - 1 where id = ?1")
    @Modifying
    void decrementAvailableUnitsCountBy1(Long productId);
}
