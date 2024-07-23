package com.example.data;

public class LineItemCSV {
    private int id = 0;
    private int type = 0;
    private String parent = "";
    private String category = "";
    private Double amount = 0.0;

    public LineItemCSV() {
    }


    public LineItemCSV(int type, String parent, String category, Double amount ) {
        this.type = type;
        this.parent = parent;
        this.category = category;
    }


    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getParent() {
        return this.parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getAmount() {
        return this.amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }   

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", type='" + getType() + "'" +
            ", parent='" + getParent() + "'" +
            ", category='" + getCategory() + "'" +
            ", amount='" + getAmount() + "'" +
            "}";
    }
}