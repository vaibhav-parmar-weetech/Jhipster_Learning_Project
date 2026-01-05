package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.Product;
import jakarta.persistence.Column;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class UserOrderResponseDTO {

    private Long id;

    private LocalDate orderDate;

    private String status;

    private Double totalAmount;

    private Set<OrderProductDTO> products = new HashSet<>();

    public UserOrderResponseDTO(Long id, LocalDate orderDate, String status, Double totalAmount, Set<OrderProductDTO> products) {
        this.id = id;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.products = products;
    }

    public UserOrderResponseDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Set<OrderProductDTO> getProducts() {
        return products;
    }

    public void setProducts(Set<OrderProductDTO> products) {
        this.products = products;
    }
}
