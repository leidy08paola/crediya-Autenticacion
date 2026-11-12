package co.com.crediya.usecase.registeruser;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exception.BusinessException;
import co.com.crediya.model.user.exception.ErrorCode;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class RegisterUserUseCase {

    private static final BigDecimal MIN_SALARY = BigDecimal.ZERO;
    private static final BigDecimal MAX_SALARY = new BigDecimal("15000000");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final String FIELD_NOMBRES = "nombres";
    private static final String FIELD_APELLIDOS = "apellidos";
    private static final String FIELD_CORREO = "correo_electronico";
    private static final String FIELD_SALARIO = "salario_base";

    private final UserRepository userRepository;

    public User execute(User user) {
        validateRequiredFields(user);
        validateEmailFormat(user.getCorreoElectronico());
        validateSalaryRange(user.getSalarioBase());
        validateEmailNotRegistered(user.getCorreoElectronico());
        return userRepository.save(user);
    }

    private void validateRequiredFields(User user) {
        requireText(user.getNombres(), FIELD_NOMBRES);
        requireText(user.getApellidos(), FIELD_APELLIDOS);
        requireText(user.getCorreoElectronico(), FIELD_CORREO);
        if (user.getSalarioBase() == null) {
            throw new BusinessException(ErrorCode.REQUIRED_FIELD, FIELD_SALARIO);
        }
    }

    private void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(ErrorCode.REQUIRED_FIELD, fieldName);
        }
    }

    private void validateEmailFormat(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL);
        }
    }

    private void validateSalaryRange(BigDecimal salary) {
        if (salary.compareTo(MIN_SALARY) < 0 || salary.compareTo(MAX_SALARY) > 0) {
            throw new BusinessException(ErrorCode.INVALID_SALARY);
        }
    }

    private void validateEmailNotRegistered(String email) {
        if (userRepository.existsByCorreoElectronico(email)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_REGISTERED);
        }
    }
}