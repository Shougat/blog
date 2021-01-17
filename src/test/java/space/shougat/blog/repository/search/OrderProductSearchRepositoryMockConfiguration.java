package space.shougat.blog.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link OrderProductSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class OrderProductSearchRepositoryMockConfiguration {

    @MockBean
    private OrderProductSearchRepository mockOrderProductSearchRepository;

}
