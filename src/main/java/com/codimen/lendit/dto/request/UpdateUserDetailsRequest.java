package com.codimen.lendit.dto.request;

import com.codimen.lendit.model.constant.RegexValidation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class UpdateUserDetailsRequest implements Serializable {

    private static final long serialVersionUID = 3326510168095898029L;

    @NotNull
    @Min(value = 1, message = "User Id should be greater than Zero!" )
    private Long userId;

    @Size(min = 1, max = 30)
    @Pattern(regexp = RegexValidation.REGEX_ONLY_ALPHABETS, message = RegexValidation.MESSAGE_ONLY_ALPHABETS)
    private String firstName;

    @Size(min = 1, max = 30)
    @Pattern(regexp = RegexValidation.REGEX_ONLY_ALPHABETS, message = RegexValidation.MESSAGE_ONLY_ALPHABETS)
    private String lastName;

    @Size(min = 10, max = 13)
    @Pattern(regexp = RegexValidation.REGEX_ONLY_DIGIT, message = RegexValidation.MESSAGE_ONLY_DIGIT)
    private String mobile;

    @Size(min = 8, max = 100)
    @Pattern(regexp = RegexValidation.REGEX_PASSWORD, message =RegexValidation.MESSAGE_PASSWORD)
    private String password;

    @Size(min = 1, max = 200)
    private String address1;

    @Size(min = 1, max = 200)
    private String address2;

    @Size(min = 1, max = 30)
    @Pattern(regexp = RegexValidation.REGEX_ONLY_ALPHABETS, message = RegexValidation.MESSAGE_ONLY_ALPHABETS)
    private String cityName;

    @Size(min = 6, max = 6)
    @Pattern(regexp = RegexValidation.REGEX_ONLY_DIGIT, message = RegexValidation.MESSAGE_ONLY_DIGIT)
    private String pinCode;

}
