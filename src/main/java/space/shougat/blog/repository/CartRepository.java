package space.shougat.blog.repository;

import space.shougat.blog.domain.Cart;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Cart entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("select cart from Cart cart where cart.user.login = ?#{principal.username}")
    List<Cart> findByUserIsCurrentUser();
}
