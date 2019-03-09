package com.codimen.lendit.dto.request;

import com.codimen.lendit.model.constant.RegexValidation;
import lombok.Data;

import javax.validation.constraints.*;

@Data
public class ConfirmEmailRequest {

    @NotNull
    @NotEmpty
    @NotBlank
    @Size(min = 5, max = 50)
    @Pattern(regexp = RegexValidation.REGEX_EMAIL, message = RegexValidation.MESSAGE_EMAIL)
    private String email;

    @NotNull
    @NotEmpty
    @NotBlank
    private String token;
}
