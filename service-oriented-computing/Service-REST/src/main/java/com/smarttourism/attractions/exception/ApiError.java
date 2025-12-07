package com.smarttourism.attractions.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    
    private HttpStatus status;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String code;
    private String message;
    private String debugMessage;
    private String path;
    private List<ApiSubError> subErrors;
    
    private ApiError() {
        timestamp = LocalDateTime.now();
    }
    
    public ApiError(HttpStatus status) {
        this();
        this.status = status;
    }
    
    public ApiError(HttpStatus status, Throwable ex) {
        this();
        this.status = status;
        this.message = "Une erreur inattendue s'est produite";
        this.debugMessage = ex.getLocalizedMessage();
    }
    
    public ApiError(HttpStatus status, String message, Throwable ex) {
        this();
        this.status = status;
        this.message = message;
        this.debugMessage = ex.getLocalizedMessage();
    }
    
    public ApiError(HttpStatus status, ErrorCode errorCode, Throwable ex) {
        this();
        this.status = status;
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.debugMessage = ex.getLocalizedMessage();
    }
    
    public ApiError(HttpStatus status, ErrorCode errorCode, String customMessage, Throwable ex) {
        this();
        this.status = status;
        this.code = errorCode.getCode();
        this.message = customMessage != null ? customMessage : errorCode.getMessage();
        this.debugMessage = ex.getLocalizedMessage();
    }
    
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ApiSubError {
        private String field;
        private String message;
        private Object rejectedValue;
        private String errorCode;
        
        public ApiSubError(String field, String message, Object rejectedValue) {
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
        }
        
        public ApiSubError(String field, String message) {
            this.field = field;
            this.message = message;
        }
        
        public ApiSubError(String field, String message, String errorCode) {
            this.field = field;
            this.message = message;
            this.errorCode = errorCode;
        }
    }
}