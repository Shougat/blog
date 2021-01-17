package space.shougat.blog.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link ProductListProductSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class ProductListProductSearchRepositoryMockConfiguration {

    @MockBean
    private ProductListProductSearchRepository mockProductListProductSearchRepository;

}
