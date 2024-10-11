package com.billing_software.billing_software.utils.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.billing_software.billing_software.models.common.DataResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CustomException.class)
    private ResponseEntity<?> handleCustomException(CustomException ex) {
        String message = ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "An unexpected error occurred";
        logger.error(message);
        return new ResponseEntity<>(DataResponse.builder().error(message).build(), ex.getStatus());
    }

    @ExceptionHandler(CustomExceptionWithData.class)
    private ResponseEntity<?> handleCustomExceptionWithData(CustomExceptionWithData ex) {
        String message = ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "An unexpected error occurred";
        logger.error(message);
        return new ResponseEntity<>(ex.data, ex.getStatus());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    private ResponseEntity<?> handleNotFoundException(NoResourceFoundException ex) {
        String message = ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "An unexpected error occurred";
        logger.error(message);
        return new ResponseEntity<>(DataResponse.builder().error("url not found").build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { MethodArgumentNotValidException.class, MissingRequestHeaderException.class,
            MissingPathVariableException.class, MissingServletRequestParameterException.class })
    private ResponseEntity<?> handleClientValidationException(Exception ex) {
        String message = null;
        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) ex;
            if (exception.getFieldError() != null)
                message = exception.getFieldError().getDefaultMessage();
        } else {
            message = ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "An unexpected error occurred";
        }
        logger.error(message);
        return new ResponseEntity<>(DataResponse.builder().error(message).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    private ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        String message = ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "An unexpected error occurred";
        logger.error(message);
        return new ResponseEntity<>(DataResponse.builder().error(message).build(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity<?> handleException(Exception ex) {
        String message = ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "An unexpected error occurred";
        logger.error(message);
        return new ResponseEntity<>(DataResponse.builder().error(message).build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}