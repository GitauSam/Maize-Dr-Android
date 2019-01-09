package org.tensorflow.demo.models;

public class User {
    private int userID, phoneNo;
    private String firstName, lastName, email;
    private User user;

    public User() {
    }

    public User(User user) {
        this.user = user;
    }

    public User(int userID, String firstName, String lastName, int phoneNo, String email) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNo = phoneNo;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getPhoneNo() {
        return phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public int getUserID() {
        return userID;
    }
}
