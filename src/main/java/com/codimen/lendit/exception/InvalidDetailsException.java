package com.codimen.lendit.exception;

import org.springframework.util.StringUtils;

public class InvalidDetailsException extends Exception {

    public InvalidDetailsException(String clazz) {
        super(InvalidDetailsException.generateMessage(clazz));
    }

    private static String generateMessage(String entity) {
        return StringUtils.capitalize(entity);
    }
}


