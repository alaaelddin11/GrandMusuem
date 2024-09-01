package com.example.grandmusuemclient;


import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class User {
    private SimpleIntegerProperty userId;
    private SimpleStringProperty username;
    private SimpleStringProperty email;
    private SimpleStringProperty password;
    private SimpleStringProperty nationality;
    private SimpleStringProperty gender;
    private SimpleStringProperty status;

    // Constructor
    public User(int userId, String username, String email, String password, String nationality, String gender, String status) {
        this.userId = new SimpleIntegerProperty(userId);
        this.username = new SimpleStringProperty(username);
        this.email = new SimpleStringProperty(email);
        this.password = new SimpleStringProperty(password);
        this.nationality = new SimpleStringProperty(nationality);
        this.gender = new SimpleStringProperty(gender);
        this.status = new SimpleStringProperty(status);
    }

    // Getters
    public int getUserId() {
        return userId.get();
    }

    public SimpleIntegerProperty userIdProperty() {
        return userId;
    }

    public String getUsername() {
        return username.get();
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public String getEmail() {
        return email.get();
    }

    public SimpleStringProperty emailProperty() {
        return email;
    }

    public String getPassword() {
        return password.get();
    }

    public SimpleStringProperty passwordProperty() {
        return password;
    }

    public String getNationality() {
        return nationality.get();
    }

    public SimpleStringProperty nationalityProperty() {
        return nationality;
    }

    public String getGender() {
        return gender.get();
    }

    public SimpleStringProperty genderProperty() {
        return gender;
    }

    public String getStatus() {
        return status.get();
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    // Setters
    public void setUserId(int userId) {
        this.userId.set(userId);
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public void setNationality(String nationality) {
        this.nationality.set(nationality);
    }

    public void setGender(String gender) {
        this.gender.set(gender);
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    // Optional: Override toString() method for debugging
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId.get() +
                ", username=" + username.get() +
                ", email=" + email.get() +
                ", nationality=" + nationality.get() +
                ", gender=" + gender.get() +
                ", status=" + status.get() +
                '}';
    }
}
