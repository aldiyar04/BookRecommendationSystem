package controller.util.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Size;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Size(min = 4, max = 32, message = "Username must be 4-32 characters in length.")
@Constraint(validatedBy = {})
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckUsername {
    String message() default "Username is not acceptable.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
