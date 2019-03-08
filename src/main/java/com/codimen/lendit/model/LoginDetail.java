package com.codimen.lendit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class LoginDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;
    private Date lastLogin;
    @JsonIgnore
    private Byte failedAttempt;
    @JsonIgnore
    private Long blockedTime;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String userIp;

    @JsonIgnore
    public LoginDetail updatedCopyOfLoginDetails(String userIp){
        LoginDetail loginDetail = new LoginDetail();
        loginDetail.setId(this.id);
        loginDetail.setFailedAttempt((byte)0);
        loginDetail.setBlockedTime(this.blockedTime);
        loginDetail.setLastLogin(new Date());
        loginDetail.setUser(this.user);
        loginDetail.setUserIp(userIp);
        return loginDetail;
    }
}
