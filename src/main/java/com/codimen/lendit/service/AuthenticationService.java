package com.codimen.lendit.service;

import com.codimen.lendit.dto.request.ConfirmEmailRequest;
import com.codimen.lendit.dto.request.EmailContent;
import com.codimen.lendit.dto.request.UpdatePasswordRequest;
import com.codimen.lendit.exception.AuthorizationException;
import com.codimen.lendit.exception.DataFoundNullException;
import com.codimen.lendit.exception.EntityNotFoundException;
import com.codimen.lendit.model.LoginDetail;
import com.codimen.lendit.model.User;
import com.codimen.lendit.model.constant.Constant;
import com.codimen.lendit.model.enumeration.UserRoles;
import com.codimen.lendit.repository.LoginDetailRepository;
import com.codimen.lendit.repository.UserRepository;
import com.codimen.lendit.security.SecurityConfiguration;
import com.codimen.lendit.security.UserInfo;
import com.codimen.lendit.security.UserLogInDetailsInMemory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Component
public class AuthenticationService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    @Value("${app.mail.forgot.password.url}")
    private String forgotPasswordURL;

    @Value("${app.mail.forgot.password.subject}")
    private String forgotPasswordSubject;

    @Value("${app.lendit.ui.host}")
    private String host;

    @Value("${app.origin.subDomain}")
    private String subDomain;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginDetailRepository loginDetailRepository;

    private UserLogInDetailsInMemory userLogInDetailsInMemory = UserLogInDetailsInMemory.getInstance();

    private boolean accountNonExpired = true;
    private boolean credentialsNonExpired = true;
    private boolean accountNonLocked = true;

    private final Long FIVE_MIN_TIMEOUT_MILLI_SEC = 1000*60*5l;
    private final Long FIFTEEN_MIN_TIMEOUT_MILLI_SEC = 1000*60*15l;
    private final Short MAX_FAILED_SIGN_IN_ATTEMPT = 5;

    @Override
    public UserInfo loadUserByUsername(String email) throws UsernameNotFoundException {

        log.info("<====== Started loadUserByUsername(String email) ======>");
        User user = this.userRepository.findOneByEmail(email);

        if (user == null) {
            log.error("User not found for email: " + email);
            throw new UsernameNotFoundException("User with email: " + email + " not found");
        }
        if (user.getAuthorised() == null || !user.getAuthorised()) {
            log.error("User not Authorised for email: " + email);
            throw new UsernameNotFoundException("User with email: " + email + " not Authorised!");
        }

        log.info("Found user with id: " + user.getId() + " ;type: " + user.getUserRole().toString() + " ;type-name: " + user.getUserRole().name() + " ;");

        UserInfo authenticatedUser = new UserInfo(user.getUserRole(), user.getEmail(), user.getPassword(), true,
                accountNonExpired, credentialsNonExpired, accountNonLocked, getAuthorities(user.getUserRole()));
        log.info("authenticated user: " + authenticatedUser.toString());
        log.info("<====== Ended loadUserByUsername(String email) ======>");
        return authenticatedUser;
    }

    private Collection<? extends GrantedAuthority> getAuthorities(UserRoles userRoles) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userRoles.toString());
        authorities.add(authority);
        return authorities;
    }

    public Map<String, Object> login(String email, String password, HttpServletRequest request,
                                     HttpServletResponse response)
            throws AuthenticationServiceException, AuthorizationException, EntityNotFoundException {
        log.info("<====== Started login(String email, String password, HttpServletRequest request, HttpServletResponse response) ======>");
        LoginDetail loginDetail = loginDetailRepository.findOneByUserEmail(email);
        String x_real_ip = "test";//request.getHeader("x-real-ip");
        if(x_real_ip == null){
            throw new AuthorizationException(" User x-real-ip not Found ");
        }
        validateLoginDetails(loginDetail, email);

        UserInfo user = this.loadUserByUsername(email);
        if (SecurityConfiguration.getPasswordEncoder().matches(password, user.getPassword())) {
            loginDetailRepository.save(loginDetail.updatedCopyOfLoginDetails(x_real_ip));
            userLogInDetailsInMemory.getLoginDetails().put(
                    loginDetail.getUser().getId(), getLongTimeOut(FIFTEEN_MIN_TIMEOUT_MILLI_SEC));

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put(Constant.AUTHENTICATED_USER, user);

            loginDetail.getUser().setPassword(null);
            user.setLoginDetail(loginDetail);
            user.eraseCredentials();
            Authentication authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);
            HttpSession httpSession = request.getSession(true);
            httpSession.setAttribute(SPRING_SECURITY_CONTEXT_KEY, securityContext);
            httpSession.setAttribute("x-real-ip",x_real_ip);
            log.info("<====== Ended login(String email, String password, HttpServletRequest request, HttpServletResponse response) ======>");
            return responseMap;
        } else {
            throw new AuthenticationServiceException("Invalid Credentials. "+
                    (MAX_FAILED_SIGN_IN_ATTEMPT-loginDetail.getFailedAttempt())+" attempts left");
        }
    }


    //Validate validateLoginDetails, whether Blocked
    private void validateLoginDetails(LoginDetail loginDetail, String email)
            throws AuthorizationException, EntityNotFoundException {
        if (loginDetail == null) {
            log.error("LoginDetail not found for email: " + email);
            throw new EntityNotFoundException(LoginDetail.class,"LoginDetails " + Constant.NOT_FOUND,
                    "EmailId : "+email);
        }
    }

    public void checkDoesUserHasPermissionToGetUserRelatedDataByUserId(Long userId)
            throws AuthorizationException, DataFoundNullException {
        if(userId == null || userId <= 0){
            throw new DataFoundNullException("UserId");
        }
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(userInfo == null){
            throw new AuthorizationException(Constant.AUTHORIZATION_FAILED);
        }else if(userInfo.getRole().equals(UserRoles.USER)){
            if(!userInfo.getLoginDetail().getUser().getId().equals(userId)){
                throw new AuthorizationException("Insufficient Privilege!");
            }
        }
    }
    public void checkDoesUserHasPermissionToGetUserRelatedDataByEmailId(String emilId)
            throws AuthorizationException, DataFoundNullException {
        if(emilId == null || emilId.trim().isEmpty()){
            throw new DataFoundNullException("emilId");
        }
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(userInfo == null){
            throw new AuthorizationException(Constant.AUTHORIZATION_FAILED);
        }else if(userInfo.getRole().equals(UserRoles.USER)){
            if(!userInfo.getLoginDetail().getUser().getEmail().equals(emilId)){
                throw new AuthorizationException("Insufficient Privilege!");
            }
        }
    }
    public boolean doesUserHasRightToSeeAdminData() throws AuthorizationException {
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(userInfo == null){
            throw new AuthorizationException(Constant.AUTHORIZATION_FAILED);
        }else if(userInfo.getRole().name().contains(UserRoles.ADMIN.name())){
            return true;
        }
        return false;
    }
    private Long getLongTimeOut(Long TIMEOUT_IN_MILLI_SEC){
        return new Date().getTime() + TIMEOUT_IN_MILLI_SEC;
    }

    public void forgotPassword(String emailId) throws EntityNotFoundException, AuthorizationException {
        log.info("<====== started forgotPassword(String emailId) ======>");
        User user = this.userRepository.findOneByEmail(emailId);
        if (user == null) {
            log.error("User not found with email:");
            throw new EntityNotFoundException(User.class, "User "+Constant.NOT_FOUND, emailId);
        }
        if(!user.getAuthorised()){
            throw new AuthorizationException(Constant.AUTHORIZATION_FAILED);
        }
        // Creating a random UUID (Universally unique identifier)
        UUID uuidOne = UUID.randomUUID();
        String randomUUIDOne = uuidOne.toString().replaceAll("-", "");
        user.setUuid(randomUUIDOne);
        this.userRepository.save(user);
        EmailContent emailContent = this.getAccountContent();
        String activationLink = emailContent.getHost().concat(forgotPasswordURL);
        log.info("activation link - " + activationLink);

        this.emailService.sendForgotPasswordEmail(user.getFirstName(), user.getEmail(),
                activationLink + "emailId="+user.getEmail()+"&token=" + randomUUIDOne,
                forgotPasswordSubject, emailContent);
        log.info("<====== Ended forgotPassword(String emailId) ======>");
    }

    public EmailContent getAccountContent(){
        EmailContent emailContent = null;
        try {
            emailContent = fillContentFromEnterprise(host, subDomain);
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
        return emailContent;
    }

    private EmailContent fillContentFromEnterprise(String host, String subDomain){
        EmailContent emailContent = new EmailContent();
        emailContent.setSupportContactName("Support");
        emailContent.setSupportContactNo("8109813712");
        emailContent.setSupportEmailId("info@lendit.com");
        emailContent.setHost(host);
        emailContent.setSubDomain(subDomain);
        return emailContent;
    }

    public void resetPassword(UpdatePasswordRequest updatePasswordRequest) throws EntityNotFoundException, AuthorizationException {
        log.info("<====== Started resetPassword(UpdatePasswordRequest updatePasswordRequest) ======>");
        User user = this.userRepository.findByEmailAndUuid(updatePasswordRequest.getEmail(), updatePasswordRequest.getToken());
        if (user == null) {
            log.error("User not found with id:"+updatePasswordRequest.getEmail());
            throw new EntityNotFoundException(User.class,"User "+Constant.NOT_FOUND + updatePasswordRequest.getEmail());
        }
        if(!user.getAuthorised()){
            throw new AuthorizationException(Constant.AUTHORIZATION_FAILED);
        }
        user.setPassword(SecurityConfiguration.getPasswordEncoder().encode(updatePasswordRequest.getPassword()));
        user.setUpdatedDate(new Date());
        //setting uuid token to null
        user.setUuid(null);
        this.userRepository.save(user);
        log.info("<====== Ended resetPassword(UpdatePasswordRequest updatePasswordRequest) ======>");
    }

    public boolean confirmEmail(ConfirmEmailRequest confirmEmailRequest) throws EntityNotFoundException {
        log.info("<====== Started confirmEmail(ConfirmEmailRequest confirmEmailRequest) ======>");
        User user = this.userRepository.findByEmailAndUuid(confirmEmailRequest.getEmail(), confirmEmailRequest.getToken());
        if (user == null) {
            log.error("User not found:");
            throw new EntityNotFoundException(User.class,"User "+Constant.NOT_FOUND + confirmEmailRequest.getEmail());
        }
        user.setUuid(null);
        user.setAuthorised(true);
        user.setUpdatedDate(new Date());
        this.userRepository.save(user);
        log.info("<====== Ended confirmEmail(ConfirmEmailRequest confirmEmailRequest) ======>");
        return true;
    }
}