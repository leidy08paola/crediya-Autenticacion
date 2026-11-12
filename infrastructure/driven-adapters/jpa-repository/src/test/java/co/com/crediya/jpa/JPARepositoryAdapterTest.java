package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.UserEntity;
import co.com.crediya.model.user.Role;
import co.com.crediya.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JPARepositoryAdapterTest {

    private static final String EMAIL = "ana.lopez@crediya.com";
    private static final Long GENERATED_ID = 1L;

    @Mock
    private JPARepository repository;

    @InjectMocks
    private JPARepositoryAdapter adapter;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .nombres("Ana")
                .apellidos("López")
                .correoElectronico(EMAIL)
                .salarioBase(new BigDecimal("3500000"))
                .build();
    }

    @Test
    @DisplayName("Guarda el usuario y devuelve el id generado")
    void shouldSaveUserAndReturnGeneratedId() {
        when(repository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity entity = invocation.getArgument(0);
            entity.setId(GENERATED_ID);
            return entity;
        });

        User result = adapter.save(user);

        assertEquals(GENERATED_ID, result.getId());
        assertEquals(EMAIL, result.getCorreoElectronico());
    }

    @Test
    @DisplayName("Asigna rol CLIENTE cuando el usuario no trae rol")
    void shouldAssignDefaultRoleWhenRoleIsNull() {
        when(repository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        adapter.save(user);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(repository).save(captor.capture());
        assertEquals(Role.CLIENTE, captor.getValue().getRol());
    }

    @Test
    @DisplayName("Devuelve true cuando el correo ya existe")
    void shouldReturnTrueWhenEmailExists() {
        when(repository.existsByCorreoElectronico(EMAIL)).thenReturn(true);

        assertTrue(adapter.existsByCorreoElectronico(EMAIL));
    }

    @Test
    @DisplayName("Devuelve false cuando el correo no existe")
    void shouldReturnFalseWhenEmailDoesNotExist() {
        when(repository.existsByCorreoElectronico(EMAIL)).thenReturn(false);

        assertFalse(adapter.existsByCorreoElectronico(EMAIL));
    }

    @Test
    @DisplayName("Traduce la violación de unicidad a una excepción de negocio")
    void shouldThrowBusinessExceptionWhenUniqueConstraintIsViolated() {
        when(repository.save(any(UserEntity.class)))
                .thenThrow(new org.springframework.dao.DataIntegrityViolationException("duplicado"));

        co.com.crediya.model.user.exception.BusinessException exception = org.junit.jupiter.api.Assertions.assertThrows(
                co.com.crediya.model.user.exception.BusinessException.class, () -> adapter.save(user));

        assertEquals(co.com.crediya.model.user.exception.ErrorCode.USER_ALREADY_EXISTS, exception.getErrorCode());
    }
}