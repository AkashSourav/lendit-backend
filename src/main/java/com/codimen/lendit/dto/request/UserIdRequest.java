package com.codimen.lendit.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class UserIdRequest implements Serializable {

    private static final long serialVersionUID = 3609225587793419524L;

    @NotNull
    @Min(value = 1, message = "User Id should be greater than Zero!" )
    private Long userId;
}
