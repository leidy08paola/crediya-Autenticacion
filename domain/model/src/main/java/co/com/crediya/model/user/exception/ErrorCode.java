package co.com.crediya.model.user.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INVALID_SALARY("El salario base debe estar entre %s y %s"),
    EMAIL_ALREADY_REGISTERED("El correo electrónico ya está registrado"),
    USER_ALREADY_EXISTS("Ya existe un usuario con los datos proporcionados");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}