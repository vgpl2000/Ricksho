package com.example.ricksho;

public class UserList {
    private String uname;
    private String address;

    public UserList(String uname, String address) {
        this.uname = uname;
        this.address = address;
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

    public void setAddress(String desc) {
        this.address = address;
    }
}
