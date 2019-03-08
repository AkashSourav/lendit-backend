package com.codimen.lendit.security;

import com.codimen.lendit.model.LoginDetail;
import com.codimen.lendit.model.enumeration.UserRoles;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Objects;

public class UserInfo extends User {

    @Getter
    @Setter
    private Long userId;

    @Getter
    @Setter
    private UserRoles role;

    @Getter
    @Setter
    private LoginDetail loginDetail;

    private static final long serialVersionUID = -1253893203016699563L;

    public UserInfo(UserRoles role, String username, String password, boolean enabled, boolean accountNonExpired,
                    boolean credentialsNonExpired, boolean accountNonLocked,
                    Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserInfo)) return false;
        if (!super.equals(o)) return false;
        UserInfo userInfo = (UserInfo) o;
        return Objects.equals(userId, userInfo.userId) &&
                role == userInfo.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userId, role);
    }
}
