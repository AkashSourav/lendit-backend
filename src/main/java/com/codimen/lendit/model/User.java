package com.codimen.lendit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.codimen.lendit.model.enumeration.UserRoles;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class User extends Traceable implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    @JsonIgnore
    private String password;
    private String profilePic;
    private String mobile;
    private String address1;
    private String address2;
    private String city;
    private String pinCode;
    @JsonIgnore
    private String uuid;
    @JsonIgnore
    private UserRoles userRole;
    @JsonIgnore
    private Boolean authorised = false;

    public User(Long id, String firstName, String lastName, String email, String profilePic,
                String mobile, String address1, String address2, String pinCode, String city, UserRoles userRole, Boolean authorised) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profilePic = profilePic;
        this.mobile = mobile;
        this.address1 = address1;
        this.address2 = address2;
        this.pinCode = pinCode;
        this.city = city;
        this.userRole = userRole;
        this.authorised = authorised;
    }
}
