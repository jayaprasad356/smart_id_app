package com.gmwapp.slv_aidi.model;

public class LevelTypeData {
    private String id, name;
    private boolean isSelected; // New field

    public LevelTypeData() {
    }

    public LevelTypeData(String id, String name) {
        this.id = id;
        this.name = name;
        this.isSelected = false; // Default to not selected
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}


