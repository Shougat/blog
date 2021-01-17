package space.shougat.blog.repository.search;

import space.shougat.blog.domain.CartProduct;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link CartProduct} entity.
 */
public interface CartProductSearchRepository extends ElasticsearchRepository<CartProduct, Long> {
}
