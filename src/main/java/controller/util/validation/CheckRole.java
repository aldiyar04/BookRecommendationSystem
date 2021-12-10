package controller.util.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = {})
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckRole {
    String message() default "Role is not acceptable.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
