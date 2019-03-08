package com.codimen.lendit.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class UserProfilePicResponse implements Serializable {

    private static final long serialVersionUID = 4219113406398434445L;

    private Long useId;
    private String profilePic;
}
