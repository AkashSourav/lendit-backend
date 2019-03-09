package com.codimen.lendit.exception;

import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class DuplicateDataException extends Exception {


    public DuplicateDataException(String clazz) {
        super(DuplicateDataException.generateMessage(clazz));
    }

    private static String generateMessage(String entity) {
        return StringUtils.capitalize(entity);
    }
}
