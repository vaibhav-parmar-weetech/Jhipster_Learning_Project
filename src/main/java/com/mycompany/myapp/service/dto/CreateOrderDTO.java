package com.mycompany.myapp.service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public class CreateOrderDTO {

    private Set<OrderProductDTO> products;

    public CreateOrderDTO() {}

    public CreateOrderDTO(Set<OrderProductDTO> products) {
        this.products = products;
    }

    public Set<OrderProductDTO> getProducts() {
        return products;
    }

    public void setProducts(Set<OrderProductDTO> products) {
        this.products = products;
    }
}
