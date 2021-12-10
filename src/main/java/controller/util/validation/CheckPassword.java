package controller.util.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Size;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Size(min = 8, max = 128, message = "Password must be 8-128 characters in length.")
@Constraint(validatedBy = {})
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckPassword {
    String message() default "Password is not acceptable.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
