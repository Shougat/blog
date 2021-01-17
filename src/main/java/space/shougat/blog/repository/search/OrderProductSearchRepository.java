package space.shougat.blog.repository.search;

import space.shougat.blog.domain.OrderProduct;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link OrderProduct} entity.
 */
public interface OrderProductSearchRepository extends ElasticsearchRepository<OrderProduct, Long> {
}
