package space.shougat.blog.repository;

import space.shougat.blog.domain.Wishlist;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Wishlist entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    @Query("select wishlist from Wishlist wishlist where wishlist.user.login = ?#{principal.username}")
    List<Wishlist> findByUserIsCurrentUser();
}
