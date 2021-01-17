package space.shougat.blog.repository.search;

import space.shougat.blog.domain.ProductList;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link ProductList} entity.
 */
public interface ProductListSearchRepository extends ElasticsearchRepository<ProductList, Long> {
}
