package co.com.crediya.api.exception;

import co.com.crediya.model.user.exception.BusinessException;
import co.com.crediya.model.user.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String MALFORMED_REQUEST_CODE = "MALFORMED_REQUEST";
    private static final String MALFORMED_REQUEST_MESSAGE = "El cuerpo de la solicitud tiene un formato inválido";
    private static final String VALIDATION_ERROR_CODE = "VALIDATION_ERROR";
    private static final String MESSAGE_SEPARATOR = "; ";
    private static final String FIELD_SEPARATOR = ",";
    private static final String REQUEST_ERROR_CODE = "REQUEST_ERROR";
    private static final String REQUEST_ERROR_MESSAGE = "La solicitud no pudo ser procesada";
    private static final String INTERNAL_ERROR_CODE = "INTERNAL_ERROR";
    private static final String INTERNAL_ERROR_MESSAGE = "Ocurrió un error inesperado. Intenta nuevamente más tarde";

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        log.warn("Error de negocio: {}", ex.getErrorCode());
        return build(resolveBusinessStatus(ex.getErrorCode()), ex.getErrorCode().name(), ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedRequest(HttpMessageNotReadableException ex) {
        log.warn("Solicitud con formato inválido");
        return build(HttpStatus.BAD_REQUEST, MALFORMED_REQUEST_CODE, MALFORMED_REQUEST_MESSAGE);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        String message = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.joining(MESSAGE_SEPARATOR));

        String invalidFields = fieldErrors.stream()
                .map(FieldError::getField)
                .sorted()
                .collect(Collectors.joining(FIELD_SEPARATOR));

        log.warn("Solicitud con campos inválidos: {}", invalidFields);
        return build(HttpStatus.BAD_REQUEST, VALIDATION_ERROR_CODE, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        if (ex instanceof org.springframework.web.ErrorResponse frameworkError) {
            HttpStatus status = HttpStatus.valueOf(frameworkError.getStatusCode().value());
            log.warn("Error HTTP de la solicitud: {}", status.value());
            return build(status, REQUEST_ERROR_CODE, REQUEST_ERROR_MESSAGE);
        }
        log.error("Error inesperado", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_ERROR_CODE, INTERNAL_ERROR_MESSAGE);
    }

    private HttpStatus resolveBusinessStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case EMAIL_ALREADY_REGISTERED, USER_ALREADY_EXISTS -> HttpStatus.CONFLICT;
            default -> HttpStatus.BAD_REQUEST;
        };
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(code, message, LocalDateTime.now()));
    }
}