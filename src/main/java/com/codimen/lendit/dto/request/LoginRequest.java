package com.codimen.lendit.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class LoginRequest implements Serializable {

    private static final long serialVersionUID = 3609225587793419525L;

    @NotNull
    @NotEmpty
    @NotBlank
    private String emailId;
    @NotNull
    @NotEmpty
    @NotBlank
    private String password;
}
