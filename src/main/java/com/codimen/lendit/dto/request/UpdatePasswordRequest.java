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
public class UpdatePasswordRequest implements Serializable {

    @NotNull
    @NotEmpty
    @NotBlank
    @Size(min = 5, max = 30)
    @Pattern(regexp = RegexValidation.REGEX_EMAIL, message = RegexValidation.MESSAGE_EMAIL)
    private String email;

    @NotNull
    @NotEmpty
    @NotBlank
    private String token;

    @Size(min = 8, max = 20)
    @Pattern(regexp = RegexValidation.REGEX_PASSWORD, message =RegexValidation.MESSAGE_PASSWORD)
    private String password;
}
