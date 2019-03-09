package com.codimen.lendit.model.constant;

public final class RegexValidation {

    public static final String REGEX_ONLY_ALPHA_NUMERIC = "^[a-zA-Z0-9]*$";
    public static final String MESSAGE_ONLY_ALPHA_NUMERIC = "Only alphabets and numeric values allowed";
    public static final String REGEX_ONLY_ALPHABETS_SPACE = "^[a-zA-Z\\s]+$";
    public static final String MESSAGE_ONLY_ALPHABETS_SAPCE = "Only alphabets and space allowed";
    public static final String REGEX_ONLY_ALPHABETS = "^[a-zA-Z]+$";
    public static final String MESSAGE_ONLY_ALPHABETS = "Only alphabets allowed";
    public static final String REGEX_ONLY_DIGIT = "\\d+";
    public static final String MESSAGE_ONLY_DIGIT = "Only numeric values allowed";
    public static final String REGEX_PASSWORD = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20})";
    public static final String MESSAGE_PASSWORD = "Password must contain min 8 characters, including upper & lower case letters and numbers";
    public static final String REGEX_EMAIL = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
    public static final String MESSAGE_EMAIL = "Please Enter valid Email!";

}