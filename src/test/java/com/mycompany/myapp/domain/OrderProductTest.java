package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.OrderProductTestSamples.*;
import static com.mycompany.myapp.domain.OrderTestSamples.*;
import static com.mycompany.myapp.domain.ProductTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderProductTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderProduct.class);
        OrderProduct orderProduct1 = getOrderProductSample1();
        OrderProduct orderProduct2 = new OrderProduct();
        assertThat(orderProduct1).isNotEqualTo(orderProduct2);

        orderProduct2.setId(orderProduct1.getId());
        assertThat(orderProduct1).isEqualTo(orderProduct2);

        orderProduct2 = getOrderProductSample2();
        assertThat(orderProduct1).isNotEqualTo(orderProduct2);
    }

    @Test
    void orderTest() {
        OrderProduct orderProduct = getOrderProductRandomSampleGenerator();
        Order orderBack = getOrderRandomSampleGenerator();

        orderProduct.setOrder(orderBack);
        assertThat(orderProduct.getOrder()).isEqualTo(orderBack);

        orderProduct.order(null);
        assertThat(orderProduct.getOrder()).isNull();
    }

    @Test
    void productTest() {
        OrderProduct orderProduct = getOrderProductRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        orderProduct.setProduct(productBack);
        assertThat(orderProduct.getProduct()).isEqualTo(productBack);

        orderProduct.product(null);
        assertThat(orderProduct.getProduct()).isNull();
    }
}
