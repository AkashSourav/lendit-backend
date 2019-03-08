package com.codimen.lendit.repository;

import com.codimen.lendit.model.LoginDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginDetailRepository  extends JpaRepository<LoginDetail, Long> {

    LoginDetail findOneByUserId(Long userId);
    LoginDetail findOneByUserEmail(String email);
}
