package co.com.crediya.usecase.registeruser;

import co.com.crediya.model.user.Role;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exception.BusinessException;
import co.com.crediya.model.user.exception.ErrorCode;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class RegisterUserUseCaseTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final RegisterUserUseCase useCase = new RegisterUserUseCase(userRepository);

    @Test
    @DisplayName("Registra al usuario con rol por defecto y correo normalizado")
    void shouldRegisterUserWithDefaultRoleAndNormalizedEmail() {
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = useCase.execute(validUser("  Ana@Crediya.COM ", new BigDecimal("3500000")));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).existsByCorreoElectronico("ana@crediya.com");
        verify(userRepository).save(captor.capture());
        assertEquals("ana@crediya.com", captor.getValue().getCorreoElectronico());
        assertEquals(Role.CLIENTE, captor.getValue().getRol());
        assertEquals(Role.CLIENTE, result.getRol());
    }

    @Test
    @DisplayName("Acepta el salario mínimo permitido (0)")
    void shouldAcceptMinimumSalary() {
        useCase.execute(validUser("ana@crediya.com", BigDecimal.ZERO));

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Acepta el salario máximo permitido (15000000)")
    void shouldAcceptMaximumSalary() {
        useCase.execute(validUser("ana@crediya.com", new BigDecimal("15000000")));

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Rechaza un salario apenas por debajo del mínimo")
    void shouldRejectSalaryBelowMinimum() {
        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCase.execute(validUser("ana@crediya.com", new BigDecimal("-0.01"))));

        assertEquals(ErrorCode.INVALID_SALARY, exception.getErrorCode());
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Rechaza un salario apenas por encima del máximo")
    void shouldRejectSalaryAboveMaximum() {
        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCase.execute(validUser("ana@crediya.com", new BigDecimal("15000000.01"))));

        assertEquals(ErrorCode.INVALID_SALARY, exception.getErrorCode());
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Rechaza un correo ya registrado y no guarda nada")
    void shouldRejectAlreadyRegisteredEmail() {
        when(userRepository.existsByCorreoElectronico("ana@crediya.com")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCase.execute(validUser("ana@crediya.com", new BigDecimal("3500000"))));

        assertEquals(ErrorCode.EMAIL_ALREADY_REGISTERED, exception.getErrorCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Un usuario nulo es un error de programación")
    void shouldFailWhenUserIsNull() {
        assertThrows(NullPointerException.class, () -> useCase.execute(null));
    }

    private User validUser(String email, BigDecimal salary) {
        return User.builder()
                .nombres("Ana")
                .apellidos("López")
                .correoElectronico(email)
                .salarioBase(salary)
                .build();
    }
}