package me.rkomarov.catalog.db;

import me.rkomarov.catalog.db.model.Product;
import me.rkomarov.catalog.db.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = "SELECT * FROM product", nativeQuery = true)
    List<Product> fetchAllProductsWithDeleted();

    @Query(value = "SELECT * FROM product WHERE id = ?1", nativeQuery = true)
    Optional<Product> fetchProduct(long id);

    @Query(value = "SELECT COUNT(*) FROM product p " +
            "LEFT JOIN section s ON p.section_id = s.id " +
            "WHERE s.deleted = true AND p.id = ?1",
            nativeQuery = true)
    int existDeletedSectionByProductId(long id);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.section WHERE p.id = ?1")
    Optional<Product> fetchWithSectionById(long id);

    @Modifying
    @Query("UPDATE Product p SET p.deleted = true WHERE p = ?1")
    void softDelete(Product product);

    @Modifying
    @Query("UPDATE Product p SET p.deleted = false WHERE p = ?1")
    void restoreDeleted(Product product);

    @Modifying
    @Query("UPDATE Product p SET p.deleted = false WHERE p.section IN ?1")
    void restoreBySections(Set<Section> sections);

    @Modifying
    @Query("UPDATE Product p SET p.deleted = true WHERE p.section IN ?1")
    void softDeleteBySections(Set<Section> sections);
}
