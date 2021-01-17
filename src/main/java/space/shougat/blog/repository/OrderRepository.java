package space.shougat.blog.repository;

import space.shougat.blog.domain.Order;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Order entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select order from Order order where order.user.login = ?#{principal.username}")
    List<Order> findByUserIsCurrentUser();
}
