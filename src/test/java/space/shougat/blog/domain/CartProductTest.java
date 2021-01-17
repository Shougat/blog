package space.shougat.blog.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import space.shougat.blog.web.rest.TestUtil;

public class CartProductTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CartProduct.class);
        CartProduct cartProduct1 = new CartProduct();
        cartProduct1.setId(1L);
        CartProduct cartProduct2 = new CartProduct();
        cartProduct2.setId(cartProduct1.getId());
        assertThat(cartProduct1).isEqualTo(cartProduct2);
        cartProduct2.setId(2L);
        assertThat(cartProduct1).isNotEqualTo(cartProduct2);
        cartProduct1.setId(null);
        assertThat(cartProduct1).isNotEqualTo(cartProduct2);
    }
}
