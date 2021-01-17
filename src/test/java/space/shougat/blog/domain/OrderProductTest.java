package space.shougat.blog.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import space.shougat.blog.web.rest.TestUtil;

public class OrderProductTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderProduct.class);
        OrderProduct orderProduct1 = new OrderProduct();
        orderProduct1.setId(1L);
        OrderProduct orderProduct2 = new OrderProduct();
        orderProduct2.setId(orderProduct1.getId());
        assertThat(orderProduct1).isEqualTo(orderProduct2);
        orderProduct2.setId(2L);
        assertThat(orderProduct1).isNotEqualTo(orderProduct2);
        orderProduct1.setId(null);
        assertThat(orderProduct1).isNotEqualTo(orderProduct2);
    }
}
