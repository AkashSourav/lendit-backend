package com.codimen.lendit.security;

import lombok.Getter;

import java.util.HashMap;

public class UserLogInDetailsInMemory {
    //      HashMap<UserId, BlockedTime>
    @Getter
    private HashMap<Long, Long> loginDetails;
    private static UserLogInDetailsInMemory userLogInDetailsInMemory;

    private UserLogInDetailsInMemory(){
        loginDetails = new HashMap<>();
    }

    public static UserLogInDetailsInMemory getInstance(){
        if(userLogInDetailsInMemory == null){
            userLogInDetailsInMemory = new UserLogInDetailsInMemory();
        }
        return userLogInDetailsInMemory;
    }
}