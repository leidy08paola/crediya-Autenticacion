
package co.com.crediya.model.user.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    REQUIRED_FIELD("El campo %s es obligatorio"),
    INVALID_EMAIL("El formato del correo electrónico no es válido"),
    INVALID_SALARY("El salario base debe estar entre 0 y 15000000"),
    EMAIL_ALREADY_REGISTERED("El correo electrónico ya está registrado"),
    USER_ALREADY_EXISTS("Ya existe un usuario con los datos proporcionados");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}