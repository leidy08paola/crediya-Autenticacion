package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Datos para registrar un usuario")
public record CreateUserRequest(
        @Size(max = 20, message = "El campo documento_identidad no puede superar 20 caracteres")
        @JsonProperty("documento_identidad") String documentoIdentidad,

        @NotBlank(message = "El campo nombres es obligatorio")
        @Size(max = 100, message = "El campo nombres no puede superar 100 caracteres")
        @JsonProperty("nombres") String nombres,

        @NotBlank(message = "El campo apellidos es obligatorio")
        @Size(max = 100, message = "El campo apellidos no puede superar 100 caracteres")
        @JsonProperty("apellidos") String apellidos,

        @Past(message = "El campo fecha_nacimiento debe ser una fecha anterior a hoy")
        @JsonProperty("fecha_nacimiento") LocalDate fechaNacimiento,

        @Size(max = 255, message = "El campo direccion no puede superar 255 caracteres")
        @JsonProperty("direccion") String direccion,

        @Size(max = 20, message = "El campo telefono no puede superar 20 caracteres")
        @JsonProperty("telefono") String telefono,

        @NotBlank(message = "El campo correo_electronico es obligatorio")
        @Pattern(regexp = CreateUserRequest.EMAIL_REGEX,
                message = "El formato del correo electrónico no es válido")
        @Size(max = 150, message = "El campo correo_electronico no puede superar 150 caracteres")
        @JsonProperty("correo_electronico") String correoElectronico,

        @NotNull(message = "El campo salario_base es obligatorio")
        @Digits(integer = 10, fraction = 2,
                message = "El campo salario_base admite máximo 2 decimales")
        @JsonProperty("salario_base") BigDecimal salarioBase
) {

        public static final String EMAIL_REGEX =
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
}