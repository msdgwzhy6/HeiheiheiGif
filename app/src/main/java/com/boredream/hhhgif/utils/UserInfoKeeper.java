package com.boredream.hhhgif.utils;

import com.boredream.hhhgif.entity.User;

public class UserInfoKeeper {
    public static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }
}
