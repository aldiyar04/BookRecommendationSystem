package controller.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import repository.entity.User;
import service.UserService;
import service.exception.user.NoSuchUserException;

import javax.annotation.ManagedBean;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ManagedBean
@Provider
public class AuthenticationFilter implements ContainerRequestFilter {
    @Context
    private ResourceInfo resourceInfo;

    @EJB
    private UserService userService;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        Method method = resourceInfo.getResourceMethod();

        if (!method.isAnnotationPresent(PermitAll.class)) {
            // Get the HTTP Authorization header from the request
            final String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

            if(authorizationHeader == null) {
                if(method.isAnnotationPresent(RolesAllowed.class) ||
                        // Pass only not logged-in users when @DenyAll is present:
                        !method.isAnnotationPresent(DenyAll.class)) {

                    requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                            .entity("You cannot access this resource.").build());
                }

                return;
            }

            // Extract the token from the HTTP Authorization header
            final String token = authorizationHeader.substring("Bearer".length()).trim();


            // Validate the token
            Claims claims = null;
            try {
                claims =  Jwts.parser()
                        .setSigningKey("MYSECRET".getBytes(StandardCharsets.UTF_8))
                        .parseClaimsJws(token)
                        .getBody();

            } catch(Exception e) {
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                        .entity("Invalid JWT token.").build());
                return;
            }

            // Fetch username and password
            final String username = claims.get("username", String.class);
            final String password = claims.get("password", String.class);

            // Verify user access
            if (method.isAnnotationPresent(RolesAllowed.class)) {
                RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
                Set<String> roleSet = new HashSet<>(Arrays.asList(rolesAnnotation.value()));

                // Is user valid?
                if (!isUserAllowed(username, password, roleSet)) {
                    requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                            .entity("You cannot access this resource.").build());
                    //return;
                }
            }
        }
    }

    private boolean isUserAllowed(String username, String password, Set<String> roleSet) {
        Optional<User> userOptional = userService.getUserByUsername(username);

        if(!userOptional.isPresent())
            return false;

        String userRole = userOptional.get().getRole();

        try {
            if(userService.validateUserPassword(username, password) && roleSet.contains(userRole))
                return true;

        } catch (NoSuchUserException ignored) {
            // This case is handled above
        }

        return false;
    }
}


