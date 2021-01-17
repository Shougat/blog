package space.shougat.blog.repository.search;

import space.shougat.blog.domain.ShippingAddress;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link ShippingAddress} entity.
 */
public interface ShippingAddressSearchRepository extends ElasticsearchRepository<ShippingAddress, Long> {
}
