package com.myproject.manageyourschedule.DTO;

import java.io.Serializable;

public class User implements Serializable {

    private String userName;
    private String dateOfBirth;
    private String userEmail;
    private String userId;
    private String userPassword;

    public User(){

    }

    public User(String userName, String dateOfBirth, String userEmail, String userId, String userPassword) {
        this.userName = userName;
        this.dateOfBirth = dateOfBirth;
        this.userEmail = userEmail;
        this.userId = userId;
        this.userPassword = userPassword;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userId='" + userId + '\'' +
                ", userPassword='" + userPassword + '\'' +
                '}';
    }
}
