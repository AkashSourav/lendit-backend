package com.codimen.lendit.controller;

import com.codimen.lendit.dto.request.RegisterUserRequest;
import com.codimen.lendit.dto.request.UpdateUserDetailsRequest;
import com.codimen.lendit.exception.AuthorizationException;
import com.codimen.lendit.exception.DataFoundNullException;
import com.codimen.lendit.exception.EntityNotFoundException;
import com.codimen.lendit.model.User;
import com.codimen.lendit.service.UserService;
import com.codimen.lendit.utils.ResponseJsonUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/api/user/")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "validate-email",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> validateEmail(@RequestParam("email") String email) {
        Map<String, Boolean> data = new HashMap<>();
        data.put("canUseEmail", this.userService.validateEmail(email));
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @RequestMapping(value = "register-user",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> registerUser(@Validated @RequestBody RegisterUserRequest registerUserRequest)
            throws EntityNotFoundException {
        return new ResponseEntity<>(this.userService.registerUser(registerUserRequest), HttpStatus.OK);
    }

    @RequestMapping(value = "update-user", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-XSRF-TOKEN", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")
    })
    public ResponseEntity<Object> updateUser(@Validated @RequestBody UpdateUserDetailsRequest updateUser)
            throws DataFoundNullException, AuthorizationException, EntityNotFoundException {
        return new ResponseEntity(this.userService.updateUser(updateUser), HttpStatus.OK);
    }

    @RequestMapping(value = "/search-user",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-XSRF-TOKEN", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")
    })
    public ResponseEntity<Object> getAutocompleteUser(@RequestParam("startsWith") String startsWith)
            throws DataFoundNullException, AuthorizationException, EntityNotFoundException {
        return new ResponseEntity<>(ResponseJsonUtil.getSuccessResponseJson(
                this.userService.searchUsersByName(startsWith)), HttpStatus.OK);
    }

    @RequestMapping(value = "/search-by-email",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-XSRF-TOKEN", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")
    })
    public ResponseEntity<Object> searchUsersByEmail(@RequestParam("email") String email)
            throws EntityNotFoundException, AuthorizationException, DataFoundNullException {
        return new ResponseEntity<>(ResponseJsonUtil.getSuccessResponseJson(
                this.userService.searchUsersByEmail(email)), HttpStatus.OK);
    }

    @RequestMapping(value = "get-user",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-XSRF-TOKEN", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")
    })
    public ResponseEntity<Object> getUserById(@RequestParam("userId") long userId)
            throws EntityNotFoundException, AuthorizationException, DataFoundNullException {
        User user = this.userService.getUsersById(userId);
        user.setPassword(null);
        return new ResponseEntity(user, HttpStatus.OK);
    }

    @RequestMapping(value = "users-list",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-XSRF-TOKEN", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")
    })
    public ResponseEntity<Object> usersList() throws AuthorizationException {
       return new ResponseEntity(this.userService.listAllUsers(), HttpStatus.OK);
    }

    @RequestMapping(value = "user-role-list",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-XSRF-TOKEN", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")
    })
    public ResponseEntity<Object> userRolesList() throws AuthorizationException {
        return new ResponseEntity(this.userService.userRolesList(), HttpStatus.OK);
    }
    @RequestMapping(value = "disable-user",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-XSRF-TOKEN", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")
    })
    public ResponseEntity<Object> disableUserAccount(
            @NotNull @Min(value = 1, message = "User Id should be greater than Zero!" )
            @RequestParam("userId")Long userId)
            throws DataFoundNullException, EntityNotFoundException, AuthorizationException {
        return new ResponseEntity(this.userService.disableUserAccount(userId), HttpStatus.OK);
    }

    @RequestMapping(value = "upload-file", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-XSRF-TOKEN", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")
    })
    public ResponseEntity<?> saveUserProfile(@RequestPart MultipartFile file,
                                             @NotNull @Min(value = 1, message = "User Id should be greater than Zero!" )
                                             @RequestParam("userId") Long userId) throws Exception {
        log.info("Signed Document Upload Started");
        return new ResponseEntity(this.userService.saveUserProfile(file, userId), HttpStatus.OK);
    }
}
