package space.shougat.blog.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link ShippingAddressSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class ShippingAddressSearchRepositoryMockConfiguration {

    @MockBean
    private ShippingAddressSearchRepository mockShippingAddressSearchRepository;

}
