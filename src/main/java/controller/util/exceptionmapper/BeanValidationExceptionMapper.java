package controller.util.exceptionmapper;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@Provider
public class BeanValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    private static final Logger logger = LogManager.getLogManager()
            .getLogger(BeanValidationExceptionMapper.class.getName());

    @Override
    public Response toResponse(ConstraintViolationException e) {
        String messages = "";
        for(ConstraintViolation violation: e.getConstraintViolations())
            messages += violation.getMessage() + "\n";
        return Response.status(Response.Status.NOT_ACCEPTABLE)
                .entity(messages)
                .build();
    }

//    private static <T> String getValidationMessage(ConstraintViolation<T> violation) {
//        String className = violation.getRootBeanClass().getSimpleName();
//        String property = violation.getPropertyPath().toString();
//        //Object invalidValue = violation.getInvalidValue();
//        String message = violation.getMessage();
//        return String.format("%s.%s %s", className, property, message);
//    }
}
