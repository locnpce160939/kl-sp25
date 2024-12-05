package com.ftcs.common.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.ftcs.common.dto.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalException {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> handleAppException(AppException e) {
        ApiResponse<?> response = new ApiResponse<>(e.getCode(), e.getMessage(), null);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<?> handleNotFoundException(NotFoundException e) {
        return new ApiResponse<>(404, e.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(new ApiResponse<>("Validation failed", errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> badRequestException(BadRequestException e) {
        log.info(e.getMessage());
        if (e.errorFieldMap != null) {
            return new ApiResponse<>(400, e.getMessage(), e.errorFieldMap);
        } else if (e.errorFieldList != null) {
            return new ApiResponse<>(400, e.getMessage(), e.errorFieldList);
        }
        return new ApiResponse<>(400, e.getMessage(), null);
    }


    @ExceptionHandler(FormValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> formValidException(FormValidException e) {
        log.info(e.getMessage());
        return new ApiResponse<>(400, e.getMessage(), e.getCaughtErr());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<?> unauthorizedException(UnauthorizedException e) {
        log.info(e.getMessage());
        return new ApiResponse<>(401, e.getMessage(), null);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> missingRequestPartException(MissingServletRequestPartException e) {
        log.info(e.getMessage());
        return new ApiResponse<>(400, e.getMessage(), null);
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<?> conflictException(ConflictException e) {
        log.info(e.getMessage());
        return new ApiResponse<>(409, e.getMessage(), null);
    }

    @ExceptionHandler(UnprocessableEntityException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ApiResponse<?> conflictException(UnprocessableEntityException e) {
        return new ApiResponse<>(422, e.getMessage(), null);
    }

    @ExceptionHandler({SQLExceptionResponse.class, SQLException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<?> sqlException(Exception e) {
        return new ApiResponse<>(409, e.getMessage(), null);
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<?> forbiddenExceptionExceptionHandle(ForbiddenException ex) {
        log.info(ex.getMessage());
        return new ApiResponse<>(403, ex.getMessage(), null);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Object>> handleBindException(BindException e) {
        var exMsg = e.getAllErrors().get(0).getDefaultMessage();
        var response = new ApiResponse<>(400, exMsg, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler({PresentationException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<ApiResponse<Object>> handleAppException(Exception e) {
        log.error(e.getMessage());
        var response = new ApiResponse<>(400, e.getMessage(), null);
        return ResponseEntity.status(400).body(response);
    }

//    @ExceptionHandler({AuthenticationException.class})
//    public ResponseEntity<ApiResponse<Object>> handleAuthentication(AuthenticationException e) {
//        log.error(e.getMessage());
//        var response = new ApiResponse<>(400, MessageConst.USERNAME_OR_PASSWORD_NOT_CORRECT, null);
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//    }
//
//    @ExceptionHandler({HttpMessageNotReadableException.class, JsonParseException.class, JsonMappingException.class})
//    public ResponseEntity<ApiResponse<Object>> handleJsonParseException(Exception e) {
//        log.error(e.getMessage());
//        var response = new ApiResponse<>(400, e.getMessage(), null);
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//    }
//
//    @ExceptionHandler({ExpiredJwtException.class, AccessDeniedException.class})
//    @Order(value = Ordered.HIGHEST_PRECEDENCE)
//    public ResponseEntity<ApiResponse<Object>> handleException(Exception exception) {
//        log.error(exception.getMessage());
//        var response = new ApiResponse<>(403, exception.getMessage(), null);
//        return ResponseEntity.status(400).body(response);
//    }

    @ExceptionHandler({RateLimitExceededException.class})
    public ResponseEntity<ApiResponse<Object>> handleTooManyRequest(RateLimitExceededException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }
}
