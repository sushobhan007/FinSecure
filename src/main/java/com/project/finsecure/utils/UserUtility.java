package com.project.finsecure.utils;

import com.project.finsecure.entity.User;

public class UserUtility {
    public static String getUserFullName(User user) {
        return String.join(" ",
                user.getFirstName().trim(),
                user.getMiddleName().trim(),
                user.getLastName().trim());
    }
}
