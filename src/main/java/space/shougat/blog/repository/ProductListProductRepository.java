package space.shougat.blog.repository;

import space.shougat.blog.domain.ProductListProduct;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the ProductListProduct entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductListProductRepository extends JpaRepository<ProductListProduct, Long> {
}
