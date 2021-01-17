package space.shougat.blog.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link ProductListSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class ProductListSearchRepositoryMockConfiguration {

    @MockBean
    private ProductListSearchRepository mockProductListSearchRepository;

}
