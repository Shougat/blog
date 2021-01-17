package space.shougat.blog.repository.search;

import space.shougat.blog.domain.ProductListProduct;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link ProductListProduct} entity.
 */
public interface ProductListProductSearchRepository extends ElasticsearchRepository<ProductListProduct, Long> {
}
