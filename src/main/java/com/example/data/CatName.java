package com.example.data;

public class CatName {

    Integer type;
    String parent;
    String category;

    public CatName() {
    }

    public CatName(Integer type, String parent, String category) {
        this.parent = parent;
        this.type = type;
        this.category = category;
    }

    public String getParent() {
        return this.parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
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
            " type='" + getType() + "'" +
            ", parent='" + getParent() + "'" +
            ", category='" + getCategory() + "'" +
            "}";
    }


}
