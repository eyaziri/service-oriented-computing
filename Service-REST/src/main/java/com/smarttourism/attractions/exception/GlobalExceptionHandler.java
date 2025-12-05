package com.smarttourism.attractions.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        
        log.error("Validation error: {}", ex.getMessage());
        
        List<ApiError.ApiSubError> subErrors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            subErrors.add(new ApiError.ApiSubError(
                error.getField(),
                error.getDefaultMessage(),
                error.getRejectedValue()
            ));
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            subErrors.add(new ApiError.ApiSubError(
                error.getObjectName(),
                error.getDefaultMessage()
            ));
        }
        
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setCode(ErrorCode.VALIDATION_ERROR.getCode());
        apiError.setMessage("Erreur de validation des données");
        apiError.setSubErrors(subErrors);
        apiError.setPath(((HttpServletRequest) request).getRequestURI());
        
        return handleExceptionInternal(ex, apiError, headers, apiError.getStatus(), request);
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<Object> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        
        log.error("Resource not found: {}", ex.getMessage());
        
        ApiError apiError = new ApiError(
            HttpStatus.NOT_FOUND,
            ex.getErrorCode(),
            ex.getMessage(),
            ex
        );
        apiError.setPath(request.getRequestURI());
        
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<Object> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {
        
        log.error("Business exception: {}", ex.getMessage());
        
        ApiError apiError = new ApiError(
            HttpStatus.BAD_REQUEST,
            ex.getErrorCode(),
            ex.getMessage(),
            ex
        );
        apiError.setPath(request.getRequestURI());
        
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(ValidationException.class)
    protected ResponseEntity<Object> handleValidationException(
            ValidationException ex, HttpServletRequest request) {
        
        log.error("Validation exception: {}", ex.getMessage());
        
        ApiError apiError = new ApiError(
            HttpStatus.BAD_REQUEST,
            ex.getErrorCode(),
            ex.getMessage(),
            ex
        );
        apiError.setPath(request.getRequestURI());
        
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        
        log.error("Constraint violation: {}", ex.getMessage());
        
        List<ApiError.ApiSubError> subErrors = new ArrayList<>();
        ex.getConstraintViolations().forEach(violation -> {
            String field = violation.getPropertyPath().toString();
            // Extraire juste le nom du champ du chemin complet
            if (field.contains(".")) {
                field = field.substring(field.lastIndexOf('.') + 1);
            }
            subErrors.add(new ApiError.ApiSubError(
                field,
                violation.getMessage(),
                violation.getInvalidValue()
            ));
        });
        
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setCode(ErrorCode.CONSTRAINT_VIOLATION.getCode());
        apiError.setMessage("Violation de contraintes de validation");
        apiError.setSubErrors(subErrors);
        apiError.setPath(request.getRequestURI());
        
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<Object> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        
        log.error("Data integrity violation: {}", ex.getMessage(), ex);
        
        String message = "Violation d'intégrité des données";
        if (ex.getRootCause() != null) {
            String rootCause = ex.getRootCause().getMessage();
            if (rootCause.contains("Duplicate entry")) {
                message = "Une ressource avec ces valeurs existe déjà";
            } else if (rootCause.contains("foreign key constraint fails")) {
                message = "Violation de clé étrangère";
            }
        }
        
        ApiError apiError = new ApiError(
            HttpStatus.CONFLICT,
            ErrorCode.DATA_INTEGRITY_VIOLATION,
            message,
            ex
        );
        apiError.setPath(request.getRequestURI());
        
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        
        log.error("Argument type mismatch: {}", ex.getMessage());
        
        String error = String.format(
            "Le paramètre '%s' de valeur '%s' n'est pas valide. Type attendu: %s",
            ex.getName(),
            ex.getValue(),
            ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "inconnu"
        );
        
        ApiError apiError = new ApiError(
            HttpStatus.BAD_REQUEST,
            ErrorCode.VALIDATION_ERROR,
            error,
            ex
        );
        apiError.setPath(request.getRequestURI());
        
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
    
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        
        log.error("HTTP message not readable: {}", ex.getMessage());
        
        String message = "Requête JSON mal formée";
        if (ex.getCause() != null && ex.getCause().getMessage() != null) {
            String cause = ex.getCause().getMessage();
            if (cause.contains("java.util.Date") || cause.contains("java.time")) {
                message = "Format de date invalide. Utilisez le format ISO (yyyy-MM-dd)";
            }
        }
        
        ApiError apiError = new ApiError(
            HttpStatus.BAD_REQUEST,
            ErrorCode.VALIDATION_ERROR,
            message,
            ex
        );
        apiError.setPath(((HttpServletRequest) request).getRequestURI());
        
        return handleExceptionInternal(ex, apiError, headers, apiError.getStatus(), request);
    }
    
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleAllExceptions(
            Exception ex, HttpServletRequest request) {
        
        log.error("Internal server error: {}", ex.getMessage(), ex);
        
        ApiError apiError = new ApiError(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ErrorCode.INTERNAL_SERVER_ERROR,
            "Une erreur interne s'est produite",
            ex
        );
        apiError.setPath(request.getRequestURI());
        
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}