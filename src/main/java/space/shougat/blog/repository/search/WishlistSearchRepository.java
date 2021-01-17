package space.shougat.blog.repository.search;

import space.shougat.blog.domain.Wishlist;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link Wishlist} entity.
 */
public interface WishlistSearchRepository extends ElasticsearchRepository<Wishlist, Long> {
}
