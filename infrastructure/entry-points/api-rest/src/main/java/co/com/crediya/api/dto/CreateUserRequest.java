package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Datos para registrar un usuario")
public record CreateUserRequest(
        @JsonProperty("documento_identidad") String documentoIdentidad,

        @NotBlank(message = "El campo nombres es obligatorio")
        @JsonProperty("nombres") String nombres,

        @NotBlank(message = "El campo apellidos es obligatorio")
        @JsonProperty("apellidos") String apellidos,

        @JsonProperty("fecha_nacimiento") LocalDate fechaNacimiento,

        @JsonProperty("direccion") String direccion,

        @JsonProperty("telefono") String telefono,

        @NotBlank(message = "El campo correo_electronico es obligatorio")
        @Pattern(regexp = CreateUserRequest.EMAIL_REGEX,
                message = "El formato del correo electrónico no es válido")
        @JsonProperty("correo_electronico") String correoElectronico,

        @NotNull(message = "El campo salario_base es obligatorio")
        @JsonProperty("salario_base") BigDecimal salarioBase
) {

    public static final String EMAIL_REGEX =
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
}