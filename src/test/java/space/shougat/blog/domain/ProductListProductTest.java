package space.shougat.blog.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import space.shougat.blog.web.rest.TestUtil;

public class ProductListProductTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductListProduct.class);
        ProductListProduct productListProduct1 = new ProductListProduct();
        productListProduct1.setId(1L);
        ProductListProduct productListProduct2 = new ProductListProduct();
        productListProduct2.setId(productListProduct1.getId());
        assertThat(productListProduct1).isEqualTo(productListProduct2);
        productListProduct2.setId(2L);
        assertThat(productListProduct1).isNotEqualTo(productListProduct2);
        productListProduct1.setId(null);
        assertThat(productListProduct1).isNotEqualTo(productListProduct2);
    }
}
