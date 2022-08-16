package com.toutiao.melon.api.utils;

public class ApiUtil {
    public static void validateId(String id) {
        if (id == null || !id.matches("[a-zA-Z0-9]+")) {
            throw new IllegalArgumentException("Only alphanumeric characters allowed for id");
        }
    }
}
