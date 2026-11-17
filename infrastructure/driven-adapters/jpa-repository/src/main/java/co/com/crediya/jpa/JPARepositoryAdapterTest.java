package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.UserEntity;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exception.BusinessException;
import co.com.crediya.model.user.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JPARepositoryAdapterTest {

    private static final String UNIQUE_VIOLATION = "23505";
    private static final String NOT_NULL_VIOLATION = "23502";

    private final JPARepository repository = mock(JPARepository.class);
    private final JPARepositoryAdapter adapter = new JPARepositoryAdapter(repository);

    @Test
    @DisplayName("Guarda el usuario y devuelve el id generado")
    void shouldSaveUserAndReturnGeneratedId() {
        when(repository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity entity = invocation.getArgument(0);
            entity.setId(7L);
            return entity;
        });

        User saved = adapter.save(validUser());

        assertEquals(7L, saved.getId());
        assertEquals("ana@crediya.com", saved.getCorreoElectronico());
    }

    @Test
    @DisplayName("Violación de unicidad se traduce a USER_ALREADY_EXISTS")
    void shouldTranslateUniqueViolationToBusinessException() {
        when(repository.save(any(UserEntity.class))).thenThrow(
                new DataIntegrityViolationException("duplicado",
                        new RuntimeException("envoltorio",
                                new SQLException("duplicado", UNIQUE_VIOLATION)))
        );

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> adapter.save(validUser())
        );

        assertEquals(ErrorCode.USER_ALREADY_EXISTS, exception.getErrorCode());
    }

    @Test
    @DisplayName("Otra violación de integridad no se disfraza de duplicado")
    void shouldRethrowIntegrityViolationThatIsNotUnique() {
        when(repository.save(any(UserEntity.class))).thenThrow(
                new DataIntegrityViolationException("nulo",
                        new SQLException("campo obligatorio", NOT_NULL_VIOLATION))
        );

        assertThrows(
                DataIntegrityViolationException.class,
                () -> adapter.save(validUser())
        );
    }

    @Test
    @DisplayName("Consulta si el correo existe delegando en el repositorio")
    void shouldDelegateEmailExistenceCheck() {
        when(repository.existsByCorreoElectronico("ana@crediya.com")).thenReturn(true);

        assertTrue(adapter.existsByCorreoElectronico("ana@crediya.com"));
    }

    private User validUser() {
        return User.builder()
                .nombres("Ana")
                .apellidos("López")
                .correoElectronico("ana@crediya.com")
                .salarioBase(new BigDecimal("3500000"))
                .build();
    }
}