package com.codimen.lendit.repository;

import com.codimen.lendit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findOneByEmail(String email);
    List<User> findByFirstNameContaining(String firstName);

    @Query(value = "Select new User(u.id, u.firstName,u.lastName,u.email,u.profilePic,u.mobile," +
            "u.address1, u.address2, u.pinCode, u.city, u.userRole,u.authorised) from User u")
    List<User> findAllUsersExcludePassword();

    @Query(value = "Select new User(u.id, u.firstName,u.lastName,u.email,u.profilePic,u.mobile," +
            "u.address1, u.address2, u.pinCode, u.city, u.userRole,u.authorised) from User u")
    List<User> findByUserId(Long userId);

    User findByEmailAndUuid(String emailId, String uuid);
    User findByEmail(String emailId);
}
