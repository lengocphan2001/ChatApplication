package com.example.chatapplication;

public class User {
    private String userName, email, password, imgProfile;
    public User(){
        this.password = "";
        this.email = "";
        this.userName = "";
        this.imgProfile = "@drawable/img";
    }
    public User(String userName, String email, String password){
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.imgProfile = "@drawable/img";
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImgProfile() {
        return imgProfile;
    }

    public void setImgProfile(String imgProfile) {
        this.imgProfile = imgProfile;
    }
}
