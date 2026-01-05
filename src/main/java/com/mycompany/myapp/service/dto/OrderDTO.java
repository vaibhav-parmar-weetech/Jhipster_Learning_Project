package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Order} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderDTO implements Serializable {

    private Long id;

    private LocalDate orderDate;

    private String status;

    private Double totalAmount;

    private UserDTO user;

    private Set<ProductDTO> products = new HashSet<>();

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

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public Set<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(Set<ProductDTO> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderDTO)) {
            return false;
        }

        OrderDTO orderDTO = (OrderDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, orderDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderDTO{" +
            "id=" + getId() +
            ", orderDate='" + getOrderDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", totalAmount=" + getTotalAmount() +
            ", user=" + getUser() +
            ", products=" + getProducts() +
            "}";
    }
}
