package space.shougat.blog.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import space.shougat.blog.web.rest.TestUtil;

public class BillingAddressTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BillingAddress.class);
        BillingAddress billingAddress1 = new BillingAddress();
        billingAddress1.setId(1L);
        BillingAddress billingAddress2 = new BillingAddress();
        billingAddress2.setId(billingAddress1.getId());
        assertThat(billingAddress1).isEqualTo(billingAddress2);
        billingAddress2.setId(2L);
        assertThat(billingAddress1).isNotEqualTo(billingAddress2);
        billingAddress1.setId(null);
        assertThat(billingAddress1).isNotEqualTo(billingAddress2);
    }
}
