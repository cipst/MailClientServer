package com.project.client.controller;

import com.project.client.model.UserModel;

public class UserController {
    private static UserModel user;

    public static UserModel getUser() {
        return user;
    }

    public static void setUser(UserModel user) {
        UserController.user = user;
    }

    public static void setAddress(String address) {
        user.setAddress(address);
    }

    public static void setPassword(String password) {
        user.setPassword(password);
    }
}
