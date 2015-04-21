package com.rmathur.noted.data.model;

public class DrawerItem {

    private int iconId;
    private String label;

    public DrawerItem(int iconId, String label) {
        this.iconId = iconId;
        this.label = label;
    }

    public int getIconId() {
        return iconId;
    }

    public String getLabel() {
        return label;
    }
}
