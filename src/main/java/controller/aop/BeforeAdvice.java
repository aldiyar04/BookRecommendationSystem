package controller.aop;

import lombok.extern.java.Log;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Entered
@Interceptor
@Log
public class BeforeAdvice {
    @AroundInvoke
    protected Object protocolInvocation(final InvocationContext invocationContext) throws Exception {
        final String methodName = invocationContext.getMethod().getName();
        if(methodName.equals("addBook") || methodName.equals("updateBook") || methodName.equals("deleteBook")) {
            for(String key: invocationContext.getContextData().keySet()) {
                String value = (String) invocationContext.getContextData().get(key);
                log.info(String.format("%s: %s %n", key, value));
            }
        }
        return invocationContext.proceed();
    }
}
