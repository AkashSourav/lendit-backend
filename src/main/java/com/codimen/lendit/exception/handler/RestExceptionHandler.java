package com.codimen.lendit.exception.handler;

import com.codimen.lendit.exception.*;
import com.codimen.lendit.utils.ResponseJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handle MissingServletRequestParameterException. Triggered when a 'required' request parameter is missing.
     *
     * @param ex      MissingServletRequestParameterException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        String error = ex.getParameterName() + " parameter is missing";
        log.error(ex.getMessage(), ex);
        return buildResponseEntity(new ApiError(BAD_REQUEST, error, ex));
    }


    /**
     * Handle HttpMediaTypeNotSupportedException. This one triggers when JSON is invalid as well.
     *
     * @param ex      HttpMediaTypeNotSupportedException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
        log.error(ex.getMessage(), ex);
        return buildResponseEntity(new ApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE, builder.substring(0, builder.length() - 2), ex));
    }

    /**
     * Handle MethodArgumentNotValidException. Triggered when an object fails @Valid validation.
     *
     * @param ex      the MethodArgumentNotValidException that is thrown when @Valid validation fails
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage("Validation error");
        apiError.addValidationErrors(ex.getBindingResult().getFieldErrors());
        apiError.addValidationError(ex.getBindingResult().getGlobalErrors());
        log.error(ex.getMessage(), ex);
        return buildResponseEntity(apiError);
    }

    /**
     * Handles javax.validation.ConstraintViolationException. Thrown when @Validated fails.
     *
     * @param ex the ConstraintViolationException
     * @return the ApiError object
     */
    @ExceptionHandler(javax.validation.ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(
            javax.validation.ConstraintViolationException ex) {
        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage("Validation error");
        apiError.addValidationErrors(ex.getConstraintViolations());
        log.error(ex.getMessage(), ex);
        return buildResponseEntity(apiError);
    }

    /**
     * Handles EntityNotFoundException. Created to encapsulate errors with more detail than javax.persistence.EntityNotFoundException.
     *
     * @param ex the EntityNotFoundException
     * @return the ApiError object
     */
    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
        ApiError apiError = new ApiError(NOT_FOUND);
        apiError.setMessage(ex.getMessage());
        log.error(ex.getMessage(), ex);
        return buildResponseEntity(apiError);
    }

    /**
     * Handle HttpMessageNotReadableException. Happens when request JSON is malformed.
     *
     * @param ex      HttpMessageNotReadableException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ServletWebRequest servletWebRequest = (ServletWebRequest) request;
        log.info("{} to {}", servletWebRequest.getHttpMethod(), servletWebRequest.getRequest().getServletPath());
        String error = "Malformed JSON request";
        log.error(ex.getMessage(), ex);
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
    }

    /**
     * Handle HttpMessageNotWritableException.
     *
     * @param ex      HttpMessageNotWritableException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = "Error writing JSON output";
        log.error(ex.getMessage(), ex);
        return buildResponseEntity(new ApiError(SERVICE_UNAVAILABLE, error, ex));
    }

    /**
     * Handle javax.persistence.EntityNotFoundException
     */
    @ExceptionHandler(javax.persistence.EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(javax.persistence.EntityNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        return buildResponseEntity(new ApiError(HttpStatus.EXPECTATION_FAILED, ex));
    }

    /**
     * Handle DataIntegrityViolationException, inspects the cause for different DB causes.
     *
     * @param ex the DataIntegrityViolationException
     * @return the ApiError object
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex,
                                                                  WebRequest request) {
        log.error(ex.getMessage(), ex);
        if (ex.getCause() instanceof ConstraintViolationException) {
            return buildResponseEntity(new ApiError(HttpStatus.CONFLICT,
                    ex.getCause().getMessage(), ex.getRootCause()));
        }
        return buildResponseEntity(new ApiError(SERVICE_UNAVAILABLE, ex));
    }

    /**
     * Handle DataIntegrityViolationException, inspects the cause for different DB causes.
     *
     * @param ex the InvalidDataAccessApiUsageException
     * @return the ApiError object
     */
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    protected ResponseEntity<Object> handleInvalidDataAccessApiUsage(InvalidDataAccessApiUsageException ex) {
        log.error(ex.getMessage(), ex);
        return buildResponseEntity(new ApiError(HttpStatus.EXPECTATION_FAILED,
                ex.getCause().getMessage(), ex.getRootCause()));

    }

    /**
     * Handle Exception, handle generic Exception.class
     *
     * @param ex the Exception
     * @return the ApiError object
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                                      WebRequest request) {
        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage(String.format("The parameter '%s' of value '%s' could not be converted to type '%s'", ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName()));
        apiError.setDebugMessage(ex.getMessage());
        log.error(ex.getMessage(), ex);
        return buildResponseEntity(apiError);
    }

    /**
     * Handle DataFoundNullException. Happens when request JSON is null.
     *
     * @param ex      DataFoundNullException
     * @return the ApiError object
     */
    @ExceptionHandler(DataFoundNullException.class)
    protected ResponseEntity<Object> handleHttpMessageNotReadable(DataFoundNullException ex) {
        String error = "Request JSON found Null/Empty";
        log.error(ex.getMessage(), ex);
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
    }

    /**
     * Handle AuthorizationException. Happens when request JSON is null.
     *
     * @param ex      AuthorizationException
     * @return the ApiError object
     */
    @ExceptionHandler(AuthorizationException.class)
    protected ResponseEntity<Object> handleAuthorizationException(AuthorizationException ex) {
        String error = "Access denied!";
        log.error(ex.getMessage(), ex);
        return buildResponseEntity(new ApiError(HttpStatus.UNAUTHORIZED, error, ex));
    }

    /**
     * Handle DuplicateKeyException. Happens when duplicate data found.
     *
     * @param ex      DuplicateKeyException
     * @return the ApiError object
     */
    @ExceptionHandler(DuplicateKeyException.class)
    protected ResponseEntity<Object> handleDuplicateKeyException(DuplicateKeyException ex) {
        String error = "Duplicate Found!";
        log.error(ex.getMessage(), ex);
        return buildResponseEntity(new ApiError(HttpStatus.NOT_ACCEPTABLE, error, ex));
    }

    /**
     * Handle UsernameNotFoundException. Happens when user not found.
     *
     * @param ex UsernameNotFoundException
     * @return the ApiError object
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    protected ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        String error = "User Not Found!";
        log.error(ex.getMessage(), ex);
        return buildResponseEntity(new ApiError(HttpStatus.EXPECTATION_FAILED, error, ex));
    }

    /**
     * Handle AuthenticationServiceException. Happens when user not authenticated.
     *
     * @param ex AuthenticationServiceException
     * @return the ApiError object
     */
    @ExceptionHandler(AuthenticationServiceException.class)
    protected ResponseEntity<Object> handleAuthenticationServiceException(AuthenticationServiceException ex) {
        String error = "Access denied!";
        log.error(ex.getMessage(), ex);
        return buildResponseEntity(new ApiError(HttpStatus.UNAUTHORIZED, error, ex));
    }

    @ExceptionHandler(DuplicateDataException.class)
    protected ResponseEntity<Object> handleDuplicateDataException(DuplicateDataException ex) {
        String error = ex.getMessage();
        log.error(ex.getMessage(), ex);
        return buildResponseEntity(new ApiError(BAD_REQUEST, error, ex));
    }


    @ExceptionHandler(InvalidDetailsException.class)
    protected ResponseEntity<Object> handleInvalidDetailsException(InvalidDetailsException ex) {
        String error = ex.getMessage();
        log.error(ex.getMessage(), ex);
        return buildResponseEntity(new ApiError(BAD_REQUEST, error, ex));
    }


    /**
     * Handle Exception, handle generic Exception.class
     *
     * @param ex the Exception
     * @return the ApiError object
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleExceptions(Exception ex) {
        log.error(ex.getMessage(), ex);
        return buildResponseEntity(new ApiError(SERVICE_UNAVAILABLE, ex));
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(ResponseJsonUtil.getFailedResponseJson(apiError), apiError.getStatus());
    }

}
