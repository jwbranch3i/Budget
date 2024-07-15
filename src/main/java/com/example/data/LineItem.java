package com.example.data;

public class LineItem {
    int id = 0;
    private String parent;
    private String type;
    private String category;

    public LineItem() {
    }

    public LineItem(String parent, String type, String category) {
        this.parent = parent;
        this.type = type;
        this.category = category;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getParent() {
        return this.parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
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

    @Override
    public String toString() {
        return "{" +
                " id='" + getId() + "'" +
                ", type='" + getType() + "'" +
                ", parent='" + getParent() + "'" +
                ", category='" + getCategory() + "'" +
                "}";
    }

}