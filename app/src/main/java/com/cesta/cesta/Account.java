package com.cesta.cesta;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

public class Account implements Serializable {
    private String name;
    private String email;
    private Calendar dob;
    private Type t;
    private String imagePath;
    private int age;
    private String gender = "M";
    private List<Pref> p;

    public Account() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Calendar getDob() {
        return dob;
    }

    public void setDob(Calendar dob) {
        this.dob = dob;
    }

    public Type getT() {
        return t;
    }

    public void setT(Type t) {
        this.t = t;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<Pref> getP() {
        return p;
    }

    public void setP(List<Pref> p) {
        this.p = p;
    }

    enum Type {
        FaceBook, Google;
    }
}
