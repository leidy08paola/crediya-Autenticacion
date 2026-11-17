package co.com.crediya.api.exception;

import co.com.crediya.model.user.exception.BusinessException;
import co.com.crediya.model.user.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Correo ya registrado responde 409")
    void shouldReturnConflictWhenEmailAlreadyRegistered() {
        ResponseEntity<ErrorResponse> response =
                handler.handleBusiness(new BusinessException(ErrorCode.EMAIL_ALREADY_REGISTERED));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("EMAIL_ALREADY_REGISTERED", response.getBody().code());
    }

    @Test
    @DisplayName("Usuario duplicado por restricción de base de datos responde 409")
    void shouldReturnConflictWhenUserAlreadyExists() {
        ResponseEntity<ErrorResponse> response =
                handler.handleBusiness(new BusinessException(ErrorCode.USER_ALREADY_EXISTS));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    @DisplayName("Error de negocio por salario inválido responde 400")
    void shouldReturnBadRequestWhenSalaryIsInvalid() {
        ResponseEntity<ErrorResponse> response =
                handler.handleBusiness(new BusinessException(ErrorCode.INVALID_SALARY));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("INVALID_SALARY", response.getBody().code());
    }

    @Test
    @DisplayName("Error de validación responde 400 con los mensajes ordenados")
    void shouldReturnSortedMessagesWhenValidationFails() {
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "createUserRequest");
        bindingResult.addError(new FieldError("createUserRequest", "salarioBase",
                "El campo salario_base es obligatorio"));
        bindingResult.addError(new FieldError("createUserRequest", "apellidos",
                "El campo apellidos es obligatorio"));
        bindingResult.addError(new FieldError("createUserRequest", "nombres",
                "El campo nombres es obligatorio"));

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALIDATION_ERROR", response.getBody().code());
        assertEquals(
                "El campo apellidos es obligatorio; El campo nombres es obligatorio; "
                        + "El campo salario_base es obligatorio",
                response.getBody().message());
    }

    @Test
    @DisplayName("JSON mal formado responde 400")
    void shouldReturnBadRequestWhenBodyIsMalformed() {
        HttpMessageNotReadableException exception =
                new HttpMessageNotReadableException("malformed", mock(HttpInputMessage.class));

        ResponseEntity<ErrorResponse> response = handler.handleMalformedRequest(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("MALFORMED_REQUEST", response.getBody().code());
    }

    @Test
    @DisplayName("Método HTTP no soportado conserva el 405")
    void shouldKeepFrameworkStatusForHttpErrors() {
        ResponseEntity<ErrorResponse> response =
                handler.handleUnexpected(new HttpRequestMethodNotSupportedException("GET"));

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
    }

    @Test
    @DisplayName("Error inesperado responde 500 sin exponer el detalle")
    void shouldReturnGenericMessageWhenUnexpectedErrorOccurs() {
        ResponseEntity<ErrorResponse> response =
                handler.handleUnexpected(new IllegalStateException("detalle técnico interno"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_ERROR", response.getBody().code());
        assertEquals(false, response.getBody().message().contains("detalle técnico"));
    }
}