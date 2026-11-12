package co.com.crediya.jpa.mapper;

import co.com.crediya.jpa.entity.UserEntity;
import co.com.crediya.model.user.Role;
import co.com.crediya.model.user.User;

public final class UserEntityMapper {

    private static final Role DEFAULT_ROLE = Role.CLIENTE;

    private UserEntityMapper() {
    }

    public static UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .documentoIdentidad(user.getDocumentoIdentidad())
                .nombres(user.getNombres())
                .apellidos(user.getApellidos())
                .fechaNacimiento(user.getFechaNacimiento())
                .direccion(user.getDireccion())
                .telefono(user.getTelefono())
                .correoElectronico(user.getCorreoElectronico())
                .salarioBase(user.getSalarioBase())
                .password(user.getPassword())
                .rol(user.getRol() != null ? user.getRol() : DEFAULT_ROLE)
                .build();
    }

    public static User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .documentoIdentidad(entity.getDocumentoIdentidad())
                .nombres(entity.getNombres())
                .apellidos(entity.getApellidos())
                .fechaNacimiento(entity.getFechaNacimiento())
                .direccion(entity.getDireccion())
                .telefono(entity.getTelefono())
                .correoElectronico(entity.getCorreoElectronico())
                .salarioBase(entity.getSalarioBase())
                .password(entity.getPassword())
                .rol(entity.getRol())
                .build();
    }
}