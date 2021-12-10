package controller.util.exceptionmapper;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@Provider
public class ExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<Exception> {
    private static Logger logger = LogManager.getLogManager().getLogger(ExceptionMapper.class.getName());

    @Override
    public Response toResponse(Exception e) {
        Response response;
        if (e instanceof WebApplicationException) {
            WebApplicationException webEx = (WebApplicationException) e;
            response = webEx.getResponse();
        } else {
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).type(MediaType.APPLICATION_JSON)
                    .build();
        }
        return response;
    }
}
