package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;

/**
 * A OrderProduct.
 */
@Entity
@Table(name = "order_product")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "order_qty")
    private Integer orderQty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "products" }, allowSetters = true)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "orders" }, allowSetters = true)
    private Product product;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OrderProduct id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOrderQty() {
        return this.orderQty;
    }

    public OrderProduct orderQty(Integer orderQty) {
        this.setOrderQty(orderQty);
        return this;
    }

    public void setOrderQty(Integer orderQty) {
        this.orderQty = orderQty;
    }

    public Order getOrder() {
        return this.order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderProduct order(Order order) {
        this.setOrder(order);
        return this;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public OrderProduct product(Product product) {
        this.setProduct(product);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderProduct)) {
            return false;
        }
        return getId() != null && getId().equals(((OrderProduct) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderProduct{" +
            "id=" + getId() +
            ", orderQty=" + getOrderQty() +
            "}";
    }
}
