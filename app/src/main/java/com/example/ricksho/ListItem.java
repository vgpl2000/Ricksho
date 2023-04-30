package com.example.ricksho;

public class ListItem {
    private String name;
    private String distance;

    public ListItem(String name, String distance) {
        this.name = name;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String head) {
        this.name = head;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String desc) {
        this.distance = distance;
    }
}
