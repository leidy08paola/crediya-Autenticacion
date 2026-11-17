package co.com.crediya.usecase.registeruser;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exception.BusinessException;
import co.com.crediya.model.user.exception.ErrorCode;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

@RequiredArgsConstructor
public class RegisterUserUseCase {

    private static final BigDecimal MIN_SALARY = BigDecimal.ZERO;
    private static final BigDecimal MAX_SALARY = new BigDecimal("15000000");

    private final UserRepository userRepository;

    public User execute(User user) {
        Objects.requireNonNull(user, "El usuario no puede ser nulo");
        validateSalaryRange(user.getSalarioBase());
        validateEmailNotRegistered(user.getCorreoElectronico());
        return userRepository.save(user);
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