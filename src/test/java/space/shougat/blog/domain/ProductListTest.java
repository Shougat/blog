package space.shougat.blog.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import space.shougat.blog.web.rest.TestUtil;

public class ProductListTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductList.class);
        ProductList productList1 = new ProductList();
        productList1.setId(1L);
        ProductList productList2 = new ProductList();
        productList2.setId(productList1.getId());
        assertThat(productList1).isEqualTo(productList2);
        productList2.setId(2L);
        assertThat(productList1).isNotEqualTo(productList2);
        productList1.setId(null);
        assertThat(productList1).isNotEqualTo(productList2);
    }
}
