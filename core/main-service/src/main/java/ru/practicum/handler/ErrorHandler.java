package ru.practicum.handler;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.AccessException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.IncorrectValueException;
import ru.practicum.exception.NotFoundException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final MethodArgumentNotValidException e) {
        log.warn("Validation error", e);
        return createApiError(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleValidationException(final ConstraintViolationException e) {
        log.warn("Conflict", e);
        return createApiError(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleValidationException(final NotFoundException e) {
        log.warn("NOT FOUND", e);
        return createApiError(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({DataAccessException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleIllegalArgumentException(final Exception e) {
        log.warn("Conflict", e);
        return createApiError(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({ConflictException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleIllegalArgumentException(final ConflictException e) {
        log.warn("Conflict", e);
        return createApiError(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleAccessException(final AccessException e) {
        log.info("Access denied: {}", e.getMessage());
        return createApiError(e, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(final Exception e) {
        log.warn("Error", e);
        return createApiError(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleException(final MissingServletRequestParameterException e) {
        log.warn("Error", e);
        return createApiError(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({IncorrectValueException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleException(final IncorrectValueException e) {
        log.warn("Error", e);
        return createApiError(e, HttpStatus.BAD_REQUEST);
    }

    private ApiError createApiError(Exception e, HttpStatus status) {
        return new ApiError(
                e.getStackTrace(),
                e.getMessage(),
                (e.getCause() != null) ? e.getCause().toString() : "",
                status
        );
    }
}
