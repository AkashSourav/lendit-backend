package com.codimen.lendit.exception;

import org.springframework.util.StringUtils;

public class AuthorizationException extends Exception {

    public AuthorizationException(String clazz) {
        super(AuthorizationException.generateMessage(clazz));
    }

    private static String generateMessage(String entity) {
        return "Access denied "
                + StringUtils.capitalize(entity) + "! ";
    }

}
