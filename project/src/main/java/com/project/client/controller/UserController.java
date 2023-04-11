package com.project.client.controller;

import com.project.client.model.User;

/**
 * User controller exists in order to have a single instance of user that is globally accessible.
 * This instance is created upon login and never changed
 *
 * @see com.project.client.controller.LoginGUIController login()
 */
public class UserController {
    private static User user;

    public static User getUser() {
        return user;
    }
    public static void setUser(User user) {
        UserController.user = user;
    }
}
