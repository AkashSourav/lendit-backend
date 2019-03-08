package com.codimen.lendit.utils;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class ResponseJsonUtil {

    public static Map<String, String> getSuccessResponseJson() {
        Map<String, String> responseBuilder = new HashMap<>();
        responseBuilder.put(ResponseConstants.STATUS, ResponseConstants.SUCCESS);
        return responseBuilder;
    }

    public static Map<String, Object> getSuccessResponseJson(Object data) {
        Map<String, Object> responseBuilder = new HashMap<>();
        responseBuilder.put(ResponseConstants.STATUS, ResponseConstants.SUCCESS);
        responseBuilder.put(ResponseConstants.DATA, data);
        return responseBuilder;
    }

    public static Map<String, Object> getFailedResponseJson(Object apiError) {
        Map<String, Object> responseBuilder = new HashMap<>();
        responseBuilder.put(ResponseConstants.STATUS, ResponseConstants.FAILURE);
        responseBuilder.put(ResponseConstants.ERROR, apiError);
        return responseBuilder;
    }

    public static Map getFailedResponseJson(String errorCode, String error) {
        Map responseBuilder = new HashMap();
        responseBuilder.put(ResponseConstants.STATUS, ResponseConstants.FAILURE);
        Map errors = new HashMap();
        errors.put(errorCode, error);
        responseBuilder.put(ResponseConstants.ERROR, errors);
        return responseBuilder;
    }

    public static Map getSuccessResponseJsonForId(Long id) {
        Map data = new HashMap();
        data.put("id", id);
        return getSuccessResponseJson(data);
    }
}
