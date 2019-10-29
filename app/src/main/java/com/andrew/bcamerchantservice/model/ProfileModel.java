package com.andrew.bcamerchantservice.model;

public class ProfileModel {
    private String parent, child;
    private int icon;

    public String getParent() {
        return parent;
    }

    public String getChild() {
        return child;
    }

    public int getIcon() {
        return icon;
    }

    public ProfileModel(String parent, String child, int icon) {
        this.icon = icon;
        this.parent = parent;
        this.child = child;
    }
}
