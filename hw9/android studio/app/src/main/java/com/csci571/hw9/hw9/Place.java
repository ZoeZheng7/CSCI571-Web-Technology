package com.csci571.hw9.hw9;

import com.google.gson.annotations.Expose;

public class Place {

    @Expose
    private String name;

    @Expose
    private String vicinity;

    @Expose
    private String icon;

    @Expose
    private String id;

    public Place(String name, String vicinity, String icon, String id) {
        this.name = name;
        this.vicinity = vicinity;
        this.icon = icon;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getVicinity() {
        return vicinity;
    }
}
