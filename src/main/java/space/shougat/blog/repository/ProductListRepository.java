package space.shougat.blog.repository;

import space.shougat.blog.domain.ProductList;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the ProductList entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductListRepository extends JpaRepository<ProductList, Long> {

    @Query("select productList from ProductList productList where productList.user.login = ?#{principal.username}")
    List<ProductList> findByUserIsCurrentUser();
}
