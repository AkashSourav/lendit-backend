package com.codimen.lendit.controller;

import com.codimen.lendit.dto.request.ConfirmEmailRequest;
import com.codimen.lendit.dto.request.LoginRequest;
import com.codimen.lendit.dto.request.UpdatePasswordRequest;
import com.codimen.lendit.exception.AuthorizationException;
import com.codimen.lendit.exception.EntityNotFoundException;
import com.codimen.lendit.repository.LoginDetailRepository;
import com.codimen.lendit.security.UserInfo;
import com.codimen.lendit.security.UserLogInDetailsInMemory;
import com.codimen.lendit.service.AuthenticationService;
import com.codimen.lendit.utils.ResponseJsonUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController()
@RequestMapping(value = "/api/auth/")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private LoginDetailRepository loginDetailRepository;

    private UserLogInDetailsInMemory userLogInDetailsInMemory = UserLogInDetailsInMemory.getInstance();

    @RequestMapping(value = "is-authenticated", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Map response = new HashMap();
        if(authentication != null) {
            response.put("isAuthenticated",authentication.getPrincipal() instanceof UserInfo);
            return response;
        }else {
            response.put("isAuthenticated",false);
            return response;
        }
    }

    @RequestMapping(value = "sign-in", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@Validated @RequestBody LoginRequest loginUser, HttpServletRequest request,
                                   HttpServletResponse response)
            throws AuthorizationException, EntityNotFoundException {
        log.info("Authenticating user: " + loginUser.getEmailId());

        Map<String, Object> responseMap = this.authenticationService.login(
                loginUser.getEmailId(), loginUser.getPassword(), request,response);

        Cookie cookie [] = request.getCookies();
        String XSRF_TOKEN = null;
        if(cookie!=null){
            for(Cookie c : cookie){
                c.setMaxAge(-1);
                if(c.getName().equals("XSRF-TOKEN")){
                    XSRF_TOKEN = c.getValue();
                    break;
                }
            }
        }

        Collection<String> headers = response.getHeaders("Set-Cookie");
        if(XSRF_TOKEN == null){
            for (String head : headers) {
                if (head.contains("XSRF-TOKEN")) {
                    XSRF_TOKEN = head.split("=|\\;")[1];
                    break;
                }
            }
        }
        responseMap.put("X-TOKEN", XSRF_TOKEN);

        return new ResponseEntity<>(responseMap, HttpStatus.OK);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-XSRF-TOKEN", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")
    })
    public ResponseEntity logoutPage(HttpServletRequest request, HttpServletResponse response) {
        HttpSession httpSession = request.getSession(false);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) auth.getPrincipal();

        Long userId = userInfo.getLoginDetail().getUser().getId();
        if(userLogInDetailsInMemory.getLoginDetails().containsKey(userId)){
            userLogInDetailsInMemory.getLoginDetails().remove(userId);
            log.info("SuccessFully User logged out emilId : "+userInfo.getLoginDetail().getUser().getEmail());
        }
        if (auth != null && httpSession != null ) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
            return new ResponseEntity<>(ResponseJsonUtil.getSuccessResponseJson(),HttpStatus.OK);
        }
        throw new AuthenticationServiceException("Authorization Failed!");
    }
    @RequestMapping(value = "/login-details", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-XSRF-TOKEN", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")
    })
    public ResponseEntity getLoginDetails() {
        UserInfo userInfo = (UserInfo)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userInfo == null) {
            throw new AuthenticationServiceException("Authorization Failed!");
        }
        return new ResponseEntity<>(ResponseJsonUtil.getSuccessResponseJson(
                loginDetailRepository.findOneByUserId(userInfo.getUserId())),HttpStatus.OK);
    }

    @RequestMapping(value = "/forgot-password", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity forgotPassword(@RequestParam("email") String email) throws InterruptedException,
            EntityNotFoundException, AuthorizationException {
        this.authenticationService.forgotPassword(email);
        return new ResponseEntity<>(ResponseJsonUtil.getSuccessResponseJson(), HttpStatus.OK);
    }

    @RequestMapping(value = "/reset-password", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity resetPassword(@Validated @RequestBody UpdatePasswordRequest updatePasswordRequest)
            throws EntityNotFoundException, AuthorizationException {
        this.authenticationService.resetPassword(updatePasswordRequest);
        return new ResponseEntity<>(ResponseJsonUtil.getSuccessResponseJson(), HttpStatus.OK);
    }

    @RequestMapping(value = "/confirm-email", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity confirmEmail(@Validated @RequestBody ConfirmEmailRequest confirmEmailRequest)
            throws EntityNotFoundException {
        return new ResponseEntity<>(ResponseJsonUtil.getSuccessResponseJson(this.authenticationService.confirmEmail
                (confirmEmailRequest)), HttpStatus.OK);
    }
}

