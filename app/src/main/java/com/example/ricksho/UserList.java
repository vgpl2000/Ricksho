package com.example.ricksho;

public class UserList {
    private String uname;
    private String address;
    private Double u_latitude;
    private Double u_longitude;

    public UserList(String uname, String address, Double u_latitude,Double u_longitude) {
        this.uname = uname;
        this.address = address;
        this.u_latitude=u_latitude;
        this.u_longitude=u_longitude;
    }

    public String getUName() {
        return uname;
    }

    public void setUName(String head) {
        this.uname = head;
    }

    public String getAddress() {
        return address;
    }

    public Double getU_latitude(){return u_latitude;}
    public Double getU_longitude(){return u_longitude;}

    public void setAddress(String desc) {
        this.address = address;
    }
}
