package ru.practicum.handler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class ApiError {

    private List<StackTraceElement> errors;
    private String message;
    private String reason;
    private HttpStatus status;
    private LocalDateTime timestamp;

    public ApiError(
            StackTraceElement[] errors, String message, String reason, HttpStatus status) {
        this.errors = Arrays.stream(errors).toList();
        this.message = message;
        this.reason = reason;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }


}
