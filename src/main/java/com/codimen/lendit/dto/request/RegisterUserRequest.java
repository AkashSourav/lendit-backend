package com.codimen.lendit.dto.request;

import com.codimen.lendit.model.constant.RegexValidation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class RegisterUserRequest implements Serializable {

    private static final long serialVersionUID = -7800268607544023291L;

    @NotNull
    @NotBlank
    @NotEmpty
    @Size(min = 1, max = 30)
    @Pattern(regexp = RegexValidation.REGEX_ONLY_ALPHABETS, message = RegexValidation.MESSAGE_ONLY_ALPHABETS)
    private String firstName;

    @NotNull
    @NotBlank
    @NotEmpty
    @Size(min = 1, max = 30)
    @Pattern(regexp = RegexValidation.REGEX_ONLY_ALPHABETS, message = RegexValidation.MESSAGE_ONLY_ALPHABETS)
    private String lastName;

    @NotNull
    @NotBlank
    @NotEmpty
    @Size(min = 5, max = 50)
    @Pattern(regexp = RegexValidation.REGEX_EMAIL, message = RegexValidation.MESSAGE_EMAIL)
    private String email;

    @NotNull
    @NotBlank
    @NotEmpty
    @Size(min = 10, max = 13)
    @Pattern(regexp = RegexValidation.REGEX_ONLY_DIGIT, message = RegexValidation.MESSAGE_ONLY_DIGIT)
    private String mobile;

    @NotNull
    @NotBlank
    @NotEmpty
    @Size(min = 8, max = 100)
    @Pattern(regexp = RegexValidation.REGEX_PASSWORD, message =RegexValidation.MESSAGE_PASSWORD)
    private String password;

    @NotNull
    @NotBlank
    @NotEmpty
    @Size(min = 5, max = 200)
    private String address1;

    @NotNull
    @NotBlank
    @NotEmpty
    @Size(min = 5, max = 200)
    private String address2;

    @NotNull
    @NotBlank
    @NotEmpty
    @Size(min = 5, max = 50)
    @Pattern(regexp = RegexValidation.REGEX_ONLY_ALPHABETS, message = RegexValidation.MESSAGE_ONLY_ALPHABETS)
    private String cityName;

    @NotNull
    @NotBlank
    @NotEmpty
    @Size(min = 6, max = 6)
    @Pattern(regexp = RegexValidation.REGEX_ONLY_DIGIT, message = RegexValidation.MESSAGE_ONLY_DIGIT)
    private String pinCode;

}
