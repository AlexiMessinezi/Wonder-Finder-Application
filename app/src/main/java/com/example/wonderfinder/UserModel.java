package com.example.wonderfinder;

public class UserModel {
    String email, username;
    boolean checkNotification;

    public UserModel() {
    }

    public UserModel(String email, String username) {
        this.email = email;
        this.username = username;
        this.checkNotification = checkNotification;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

//    public String getImage() {
//        return image;
//    }
//
//    public void setImage(String image) {
//        this.image = image;
//    }

    public boolean isCheckNotification() {
        return checkNotification;
    }

    public void setCheckNotification(boolean checkNotification) {
        this.checkNotification = checkNotification;
    }

}
