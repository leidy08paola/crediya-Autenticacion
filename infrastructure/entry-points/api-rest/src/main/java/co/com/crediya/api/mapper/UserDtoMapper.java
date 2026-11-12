package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.CreateUserRequest;
import co.com.crediya.api.dto.UserResponse;
import co.com.crediya.model.user.User;

public final class UserDtoMapper {

    private UserDtoMapper() {
    }

    public static User toDomain(CreateUserRequest request) {
        return User.builder()
                .documentoIdentidad(request.documentoIdentidad())
                .nombres(request.nombres())
                .apellidos(request.apellidos())
                .fechaNacimiento(request.fechaNacimiento())
                .direccion(request.direccion())
                .telefono(request.telefono())
                .correoElectronico(request.correoElectronico())
                .salarioBase(request.salarioBase())
                .build();
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getDocumentoIdentidad(),
                user.getNombres(),
                user.getApellidos(),
                user.getFechaNacimiento(),
                user.getDireccion(),
                user.getTelefono(),
                user.getCorreoElectronico(),
                user.getSalarioBase(),
                user.getRol() != null ? user.getRol().name() : null
        );
    }
}