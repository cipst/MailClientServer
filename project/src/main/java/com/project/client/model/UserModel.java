package com.project.client.model;

import java.util.Objects;

public class UserModel {
    private String address;
    private String password;

    public UserModel(String address, String password) {
        this.address = address;
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public String getPassword() {
        return password;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "address='" + address + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserModel userModel = (UserModel) o;
        return Objects.equals(address, userModel.address) &&
                Objects.equals(password, userModel.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, password);
    }
}
