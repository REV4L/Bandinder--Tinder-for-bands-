package com.example;

import java.sql.Timestamp;

public class Band {
    public int id;
    public String name;
    public String bio;
    public String email;
    public String phone;
    public Timestamp dt;
    public int krajId;

    public Band(int id, String name, String bio, String email, String phone, Timestamp dt, int krajId) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.email = email;
        this.phone = phone;
        this.dt = dt;
        this.krajId = krajId;
    }
}
