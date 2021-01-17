package space.shougat.blog.repository;

import space.shougat.blog.domain.CartProduct;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the CartProduct entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, Long> {
}
