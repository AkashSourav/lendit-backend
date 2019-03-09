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
public class ValidateEmailRequest implements Serializable {

    private static final long serialVersionUID = 3609225587793419524L;

    @NotNull
    @NotBlank
    @NotEmpty
    @Size(min = 5, max = 30)
    @Pattern(regexp = RegexValidation.REGEX_EMAIL, message = RegexValidation.MESSAGE_EMAIL)
    private String email;
}
