package com.codimen.lendit.service;

import com.codimen.lendit.dto.request.EmailContent;
import com.codimen.lendit.dto.request.RegisterUserRequest;
import com.codimen.lendit.dto.request.UpdateUserDetailsRequest;
import com.codimen.lendit.dto.response.UserProfilePicResponse;
import com.codimen.lendit.exception.AuthorizationException;
import com.codimen.lendit.exception.DataFoundNullException;
import com.codimen.lendit.exception.EntityNotFoundException;
import com.codimen.lendit.model.LoginDetail;
import com.codimen.lendit.model.User;
import com.codimen.lendit.model.constant.Constant;
import com.codimen.lendit.model.enumeration.UserRoles;
import com.codimen.lendit.repository.LoginDetailRepository;
import com.codimen.lendit.repository.UserRepository;
import com.codimen.lendit.security.ContextData;
import com.codimen.lendit.security.ContextStorage;
import com.codimen.lendit.security.SecurityConfiguration;
import com.codimen.lendit.utils.FileUploadUtil;
import com.codimen.lendit.utils.ResponseJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@SuppressWarnings({"rawtypes", "unchecked"})
public class UserService {

    @Value("${app.codimen.bankName}")
    private String bankName;

    @Value("${app.codimen.branchName}")
    private String branchName;

    @Value("${app.codimen.bankCode}")
    private String bankCode;

    @Value("${app.mail.activate.registereduser}")
    private String activateUserLink;

    @Value("${app.mail.activateUserMail.subject}")
    private String activationAccountSubject;

    private final byte INCREMENTAL = 1;

    private final long BASE_ACCOUNT_NUMBER = 50110000000000L;

    private final String SEMI_COLON_SEPARATOR = " : ";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private LoginDetailRepository loginDetailRepository;

    @Autowired
    private FileUploadUtil fileUploadUtil;


    @Autowired
    private AuthenticationService authenticationService;

    public boolean validateEmail(String email) {
        User user = this.userRepository.findOneByEmail(email);
        return user == null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public Map registerUser(RegisterUserRequest addUserObj) throws EntityNotFoundException {
        log.info("<====== Started registerUser(RegisterUserRequest addUserObj) ======>"+addUserObj);
        User user = this.userRepository.findOneByEmail(addUserObj.getEmail());
        if (user != null) {
            log.info("User already register with lendit application email :: "+addUserObj.getEmail());
            throw new DuplicateKeyException(addUserObj.getEmail() +
                    SEMI_COLON_SEPARATOR + Constant.EMAIL_ID_ALREADY_EXIST);
        } else {
            //Saving User
            user = new User();
            user.setFirstName(addUserObj.getFirstName());
            user.setLastName(addUserObj.getLastName());
            user.setEmail(addUserObj.getEmail());
            user.setMobile(addUserObj.getMobile());
            user.setAddress1(addUserObj.getAddress2());
            user.setAddress2(addUserObj.getAddress2());
            user.setCity(addUserObj.getCityName());
            user.setPinCode(addUserObj.getPinCode());

            user.setPassword(SecurityConfiguration.getPasswordEncoder().encode(addUserObj.getPassword()));
            user.setUserRole(UserRoles.USER);
            user.setAuthorised(true);
            Date modifiedDate = new Date();
            user.setCreatedDate(modifiedDate);
            user.setCreatedDate(modifiedDate);
            // Creating a random UUID (Universally unique identifier).
            UUID uuidOne = UUID.randomUUID();
            String randomUUIDOne = uuidOne.toString().replaceAll("-", "");
            user.setUuid(randomUUIDOne);
            this.userRepository.save(user);
            log.info("user data saved successfully"+user);

            LoginDetail loginDetail = new LoginDetail();
            loginDetail.setBlockedTime(0l);
            loginDetail.setFailedAttempt((byte)0);
            loginDetail.setLastLogin(new Date());
            loginDetail.setUser(user);
            ContextData contextData  = ContextStorage.get();
            loginDetail.setUserIp(contextData.getX_real_ip());
            this.loginDetailRepository.save(loginDetail);
            log.info("user loginDetails saved successfully" +loginDetail);

            EmailContent emailContent = this.authenticationService.getAccountContent();
            String activationLink = emailContent.getHost().concat(activateUserLink);
            log.info("activation link - " + activationLink);

//            this.emailService.sendActivationEmail(user.getFirstName(), user.getEmail(),
//                    activationLink + "emailId="+user.getEmail()+ "&token=" + randomUUIDOne,
//                    activationAccountSubject, emailContent);
            log.info("Email sent successfully");

            log.info("<====== ended registerUser(RegisterUserRequest addUserObj) ======>");
            return ResponseJsonUtil.getSuccessResponseJson();
        }
    }

    public Map searchUsersByEmail(String email)
            throws EntityNotFoundException, AuthorizationException, DataFoundNullException {
        log.info("<====== Started searchUsersByEmail(String email) ======>");
        User user = null;
        //Checking Authorization
        authenticationService.checkDoesUserHasPermissionToGetUserRelatedDataByEmailId(email);
        user = this.userRepository.findOneByEmail(email);
        user.setPassword(null);
        if (user == null) {
            log.error("user not found for emailId :: "+email);
            throw new EntityNotFoundException(User.class, "EmailId "+Constant.NOT_FOUND,
                    " emailId : "+email);
        } else {
            log.info("successfully found user with emailId ::"+email);
        }
        log.info("<====== ended searchUsersByEmail(String email) ======>");
        return ResponseJsonUtil.getSuccessResponseJson(user);
    }

    public User getUsersById(Long userId)
            throws EntityNotFoundException, AuthorizationException, DataFoundNullException {
        //Checking Authorization
        log.info("<====== Started getUsersById(Long userId) ======>");
        authenticationService.checkDoesUserHasPermissionToGetUserRelatedDataByUserId(userId);
        User user = userRepository.findOne(userId);
        user.setPassword(null);
        if (user == null) {
            log.error("user found null for the userId"+userId);
            throw new EntityNotFoundException(User.class, "User "+Constant.NOT_FOUND,
                    " userId : "+userId.toString());
        } else {
            log.info("user found for the userId"+userId);
        }
        log.info("<====== ended getUsersById(Long userId) ======>");
        return user;
    }

    public Map userRolesList() throws AuthorizationException {
        //Checking Authorization
        if(authenticationService.doesUserHasRightToSeeAdminData()){
            return ResponseJsonUtil.getSuccessResponseJson(EnumSet.allOf(UserRoles.class));
        }else {
            throw new AuthorizationException("Insufficient Privilege!");
        }
    }

    public Map listAllUsers() throws AuthorizationException{
        //Checking Authorization
        log.info("<====== Started listAllUsers() ======>");
        List<User> users = null;
        if(authenticationService.doesUserHasRightToSeeAdminData()){
            users = this.userRepository.findAllUsersExcludePassword();
            log.info("List of users");
        }else {
            log.error("Insufficient privilege");
            throw new AuthorizationException("Insufficient Privilege!");
        }
        log.info("<====== Ended listAllUsers() ======>");
        return ResponseJsonUtil.getSuccessResponseJson(users);
    }

    public Map updateUser(UpdateUserDetailsRequest updatedUser)
            throws DataFoundNullException, AuthorizationException, EntityNotFoundException {
        log.info("<====== Started updateUser(UpdateUserDetailsRequest updatedUser) ======>");
        authenticationService.checkDoesUserHasPermissionToGetUserRelatedDataByUserId(updatedUser.getUserId());
        User user = this.userRepository.findOne(updatedUser.getUserId());
        if (user == null) {
            log.error("User found null for the userId"+updatedUser.getUserId());
            throw new EntityNotFoundException(User.class,"User "+Constant.NOT_FOUND,
                    " userId : "+updatedUser.getUserId().toString());
        }else {
            //Updating User
            boolean userUpdated = false;
            log.info("started the updating the userData");
            if(updatedUser.getFirstName() != null && !updatedUser.getFirstName().trim().isEmpty()){
                user.setFirstName(updatedUser.getFirstName());
                userUpdated = true;
            }
            if(updatedUser.getLastName() != null && !updatedUser.getLastName().trim().isEmpty()){
                user.setLastName(updatedUser.getLastName());
                userUpdated = true;
            }
            if(updatedUser.getMobile() != null && !updatedUser.getMobile().trim().isEmpty()){
                user.setMobile(updatedUser.getMobile());
                userUpdated = true;
            }
            if(updatedUser.getPassword() != null && !updatedUser.getPassword().trim().isEmpty()){
                user.setPassword(SecurityConfiguration.getPasswordEncoder().encode(updatedUser.getPassword()));
                userUpdated = true;
            }
            if(updatedUser.getMobile() != null && !updatedUser.getMobile().trim().isEmpty()){
                user.setMobile(updatedUser.getMobile());
                userUpdated = true;
            }

            if(updatedUser.getAddress1() != null && !updatedUser.getAddress1().trim().isEmpty()){
                user.setAddress1(updatedUser.getAddress1());
                userUpdated = true;
            }
            if(updatedUser.getAddress2() != null && !updatedUser.getAddress2().trim().isEmpty()){
                user.setAddress2(updatedUser.getAddress2());
                userUpdated = true;
            }
            if(updatedUser.getCityName() != null && !updatedUser.getCityName().trim().isEmpty()){
                user.setCity(updatedUser.getCityName());
                userUpdated = true;
            }
            if(updatedUser.getPinCode() != null && !updatedUser.getPinCode().trim().isEmpty()){
                user.setPinCode(updatedUser.getPinCode());
                userUpdated = true;
            }

            if(userUpdated){
                user.setUpdatedDate(new Date());
                this.userRepository.save(user);
                log.info("User data Updated successfully:"+user.getId());
            }

            //validation
            if(!userUpdated){
                log.error("Update User Data Found Empty");
                throw new DataFoundNullException("Update User Data Found Empty");
            }
            log.info("<====== Ended updateUser(UpdateUserDetailsRequest updatedUser) ======>");
            return ResponseJsonUtil.getSuccessResponseJson();
        }
    }

    public Map disableUserAccount(Long userId) throws DataFoundNullException,
            EntityNotFoundException, AuthorizationException {
        log.info("<====== Started disableUserAccount(Long userId) ======>");
        authenticationService.checkDoesUserHasPermissionToGetUserRelatedDataByUserId(userId);
        User user = userRepository.findOne(userId);
        if (user ==  null) {
            log.error("User not Found with userId"+userId);
            throw new EntityNotFoundException(User.class, "User "+Constant.NOT_FOUND,
                    " userId : "+userId.toString());
        }
        user.setAuthorised(false);
        userRepository.save(user);
        log.info("user disabled successfully");
        log.info("<====== Ended disableUserAccount(Long userId) ======>");
        return ResponseJsonUtil.getSuccessResponseJson();
    }

    public Map saveUserProfile(MultipartFile file, Long userId)
            throws DataFoundNullException, IOException, EntityNotFoundException, AuthorizationException {
        log.info("<====== Started saveUserProfile(MultipartFile file, Long userId) ======>");
        authenticationService.checkDoesUserHasPermissionToGetUserRelatedDataByUserId(userId);
        // Get the filename and build the local file path
        if(file == null || file.isEmpty()){
            log.error("file found null/empty file");
            throw new DataFoundNullException("file");
        }
        String receivedFilename = file.getOriginalFilename();
        String extension = fileUploadUtil.getExtension(receivedFilename);
        if(extension == null){
            log.error("File extension not supported");
            throw new MultipartException("File extension not supported");
        }
        boolean doesExtMatched = fileUploadUtil.matchProfilePicExtension(extension);
        if(!doesExtMatched){
            log.error("File extension not supported");
            throw new MultipartException("File extension not supported");
        }

        if (!fileUploadUtil.doesProfileFileSizeLessThenMaxSize(file)) {
            log.info("Size of the file - " + String.valueOf(file.getSize()) +
                    " and maxFileSize allowed - " + fileUploadUtil.getProfilePicMaxFileSize());
            throw new MultipartException("File larger than maximum size limit!");
        }
        User user = userRepository.findOne(userId);
        if (user == null) {
            log.error("User not found");
            throw new EntityNotFoundException(User.class, "User "+Constant.NOT_FOUND,
                    " userId : "+userId);
        }

        String finalUserProfilePicFileName =
                fileUploadUtil.getUserProfilePicFileName(receivedFilename, user.getFirstName(), userId);

        String profilePicUploadPath = fileUploadUtil.getProfilePicUploadPath(finalUserProfilePicFileName);

        // deleting old file
        //fileUploadUtil.deleteFile(profilePicUploadPath);

        // Save the file locally
        fileUploadUtil.saveUserProfilePic(file,profilePicUploadPath);

        // deleting old file
        fileUploadUtil.deleteFile(fileUploadUtil.getProfilePicUploadPath(
                fileUploadUtil.getUserOldProfilePicFileName(user.getProfilePic())));

        //updating user table profile pic column
        user.setProfilePic(fileUploadUtil.getProfilePicUrl(finalUserProfilePicFileName));
        user.setUpdatedDate(new Date());
        this.userRepository.save(user);
        log.info("User table updated successfully");

        UserProfilePicResponse  userProfilePicResponse = new UserProfilePicResponse();
        userProfilePicResponse.setUseId(userId);
        userProfilePicResponse.setProfilePic(user.getProfilePic());
        log.info("<====== Ended saveUserProfile(MultipartFile file, Long userId) ======>");
        return ResponseJsonUtil.getSuccessResponseJson(userProfilePicResponse);
    }
}
