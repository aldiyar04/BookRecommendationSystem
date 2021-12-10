package controller.util.validation;

import repository.entity.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;
import java.util.Objects;

@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class RoleValidator implements ConstraintValidator<CheckRole, String> {
    @Override
    public void initialize(CheckRole checkRole) {
    }

    @Override
    public boolean isValid(String role, ConstraintValidatorContext constraintValidatorContext) {
        return Objects.equals(role, User.ROLE_USER) ||
                Objects.equals(role, User.ROLE_LIBRARIAN) ||
                Objects.equals(role, User.ROLE_ADMIN);
    }
}
