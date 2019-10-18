package com.studio.classlog;

/**
 * Created by Studio on 9/10/2017.
 */

public class Users {

    private String email, profile_picture, username, department, year;

    public Users() {
    }

    public Users(String email, String profile_picture, String username, String department, String year) {
        this.email = email;
        this.profile_picture = profile_picture;
        this.username = username;
        this.department = department;
        this.year = year;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "Users{" +
                "email='" + email + '\'' +
                ", profile_picture='" + profile_picture + '\'' +
                ", username='" + username + '\'' +
                ", department='" + department + '\'' +
                ", year='" + year + '\'' +
                '}';
    }
}
