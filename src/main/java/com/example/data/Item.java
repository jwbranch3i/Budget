package com.example.data;

public class Item {

    private String type;
    private String category;
    private Double cost;


    public Item(String type, String category, Double cost) {
        this.type = type;
        this.category = category;
        this.cost = cost;
    }


    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getCost() {
        return this.cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }


    @Override
    public String toString() {
        return "{" +
            " type='" + getType() + "'" +
            ", category='" + getCategory() + "'" +
            ", cost='" + getCost() + "'" +
            "}";
    }

}