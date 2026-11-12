package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.UserEntity;
import co.com.crediya.jpa.mapper.UserEntityMapper;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exception.BusinessException;
import co.com.crediya.model.user.exception.ErrorCode;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JPARepositoryAdapter implements UserRepository {

    private final JPARepository repository;

    @Override
    @Transactional
    public User save(User user) {
        log.info("Guardando usuario en base de datos");
        try {
            UserEntity saved = repository.save(UserEntityMapper.toEntity(user));
            log.info("Usuario guardado con id {}", saved.getId());
            return UserEntityMapper.toDomain(saved);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Violación de unicidad al guardar el usuario");
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCorreoElectronico(String correoElectronico) {
        log.debug("Verificando si el correo ya está registrado");
        return repository.existsByCorreoElectronico(correoElectronico);
    }
}