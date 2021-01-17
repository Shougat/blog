package space.shougat.blog.repository.search;

import space.shougat.blog.domain.BillingAddress;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link BillingAddress} entity.
 */
public interface BillingAddressSearchRepository extends ElasticsearchRepository<BillingAddress, Long> {
}
