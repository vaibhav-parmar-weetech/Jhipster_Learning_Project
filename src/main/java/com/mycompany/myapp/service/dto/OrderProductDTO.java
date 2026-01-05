package com.mycompany.myapp.service.dto;

public class OrderProductDTO {

    private Long productId;
    private Integer qty;

    public OrderProductDTO(Long productId, Integer qty) {
        this.productId = productId;
        this.qty = qty;
    }

    public OrderProductDTO() {}

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
    // getters & setters
}
