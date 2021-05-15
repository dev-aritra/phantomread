package io.aritra.phantomread.repository;

import io.aritra.phantomread.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(value = "select * from products where id = ?1 for update", nativeQuery = true)
    Optional<Product> findByIdWithWriteLock(Long productId);

    @Query("update products set available_units = available_units - 1 where id = ?1")
    @Modifying
    void decrementAvailableUnitsCountBy1(Long productId);
}
