package com.mycompany.myapp.service.dto;

public class UserProductResponseDTO {

    private Long id;

    private String name;

    private Double price;

    private Integer qty;

    public UserProductResponseDTO(Long id, String name, Double price, Integer qty) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.qty = qty;
    }

    public UserProductResponseDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
}
