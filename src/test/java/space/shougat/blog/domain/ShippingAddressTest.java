package space.shougat.blog.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import space.shougat.blog.web.rest.TestUtil;

public class ShippingAddressTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShippingAddress.class);
        ShippingAddress shippingAddress1 = new ShippingAddress();
        shippingAddress1.setId(1L);
        ShippingAddress shippingAddress2 = new ShippingAddress();
        shippingAddress2.setId(shippingAddress1.getId());
        assertThat(shippingAddress1).isEqualTo(shippingAddress2);
        shippingAddress2.setId(2L);
        assertThat(shippingAddress1).isNotEqualTo(shippingAddress2);
        shippingAddress1.setId(null);
        assertThat(shippingAddress1).isNotEqualTo(shippingAddress2);
    }
}
