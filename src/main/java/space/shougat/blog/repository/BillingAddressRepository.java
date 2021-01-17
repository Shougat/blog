package space.shougat.blog.repository;

import space.shougat.blog.domain.BillingAddress;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the BillingAddress entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BillingAddressRepository extends JpaRepository<BillingAddress, Long> {
}
