package co.com.crediya.api;

import co.com.crediya.api.dto.CreateUserRequest;
import co.com.crediya.api.dto.UserResponse;
import co.com.crediya.model.user.Role;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exception.BusinessException;
import co.com.crediya.model.user.exception.ErrorCode;
import co.com.crediya.usecase.registeruser.RegisterUserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private static final String EMAIL = "ana.lopez@crediya.com";
    private static final Long GENERATED_ID = 1L;

    @Mock
    private RegisterUserUseCase registerUserUseCase;

    @InjectMocks
    private UserController userController;

    private CreateUserRequest request;

    @BeforeEach
    void setUp() {
        request = new CreateUserRequest(
                "1001234567", "Ana", "López", LocalDate.of(1995, 5, 20),
                "Calle 10 # 5-20", "3001234567", EMAIL, new BigDecimal("3500000"));
    }

    @Test
    @DisplayName("Responde 201 con el usuario creado y sin exponer la contraseña")
    void shouldReturnCreatedWhenUserIsRegistered() {
        User created = User.builder()
                .id(GENERATED_ID)
                .nombres("Ana")
                .apellidos("López")
                .correoElectronico(EMAIL)
                .salarioBase(new BigDecimal("3500000"))
                .password("secreto")
                .rol(Role.CLIENTE)
                .build();
        when(registerUserUseCase.execute(any(User.class))).thenReturn(created);

        ResponseEntity<UserResponse> response = userController.registerNewUser(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(GENERATED_ID, response.getBody().id());
        assertEquals(EMAIL, response.getBody().correoElectronico());
        assertEquals("CLIENTE", response.getBody().rol());
    }

    @Test
    @DisplayName("Propaga la excepción de negocio para que la procese el manejador global")
    void shouldPropagateBusinessException() {
        when(registerUserUseCase.execute(any(User.class)))
                .thenThrow(new BusinessException(ErrorCode.EMAIL_ALREADY_REGISTERED));

        assertThrows(BusinessException.class, () -> userController.registerNewUser(request));
    }
}