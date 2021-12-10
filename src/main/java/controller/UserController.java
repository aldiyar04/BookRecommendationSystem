package controller;

import controller.util.jsonformatting.MyObjectMapper;
import controller.util.validation.CheckEmail;
import controller.util.validation.CheckPassword;
import controller.util.validation.CheckUsername;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import repository.entity.Book;
import repository.entity.User;
import service.UserService;
import service.exception.role.InvalidRoleException;
import service.exception.user.NoSuchUserException;
import service.exception.user.UserAlreadyExistsException;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.transaction.Transactional;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserController {
    @EJB
    private UserService userService;
    @EJB
    private MyObjectMapper objectMapper;
    @Context
    private UriInfo uriInfo;

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @DenyAll
    public Response registerUser(@CheckEmail @FormParam("email") String email,
                                 @CheckUsername @FormParam("username") String username,
                                 @CheckPassword @FormParam("password") String password) {

        try {
            userService.registerUser(email, username, password);
        } catch(UserAlreadyExistsException e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity(e.getMessage())
                    .build();
        }

        return Response.ok("You registered successfully!").build();
    }

    @POST
    @Transactional
    @DenyAll
    @Path("/login")
    public Response authenticateUser(@CheckUsername @FormParam("username") String username,
                                     @CheckPassword @FormParam("password") String password) {
        try {
            if(!userService.validateUserPassword(username, password)) {
                return Response.status(Response.Status.NOT_ACCEPTABLE)
                        .entity("Invalid password.")
                        .build();
            }

        } catch (NoSuchUserException e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity(e.getMessage())
                    .build();
        }



        // Issue a token for the user
        String token = issueToken(username, password);
        // Return the token on the response
        return Response.ok("Success. Your token is in the response authorization header.")
                .header(AUTHORIZATION, "Bearer " + token)
                .build();
    }

    private String issueToken(String username, String password) {
//        Key key = keyGenerator.generateKey();
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("password", password);

        String jwtToken = Jwts.builder()
                .setSubject(username)
                .setIssuer(uriInfo.getAbsolutePath().toString())
                .setIssuedAt(new Date())
                .setExpiration(toDate(LocalDateTime.now().plusMinutes(99999999L)))
                .signWith(SignatureAlgorithm.HS512, "MYSECRET".getBytes(StandardCharsets.UTF_8)) // here was key instead of "MYSECRET"
                .setClaims(claims)
                .compact();
        return jwtToken;
    }

    private Date toDate(LocalDateTime localDateTime) {
        LocalDateTime ldt = LocalDateTime.ofInstant(localDateTime.toInstant(ZoneOffset.UTC), ZoneId.systemDefault());
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

    @GET
    @RolesAllowed({User.ROLE_USER, User.ROLE_LIBRARIAN, User.ROLE_ADMIN})
    public Response getAllUsers() {
        return Response.ok(formatUserJson(userService.getAllUsers())).build();
    }

    @GET
    @Path("/{username}")
    @RolesAllowed({User.ROLE_USER, User.ROLE_LIBRARIAN, User.ROLE_ADMIN})
    public Response getUserByUsername(@CheckUsername @PathParam("username") String username) {
        Optional<User> userOptional = userService.getUserByUsername(username);
        if(!userOptional.isPresent())
            return Response.ok(String.format("User %s does not exist.", username)).build();
        return Response.ok(formatUserJson(userOptional.get())).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/{username}/password")
    @RolesAllowed({User.ROLE_USER, User.ROLE_LIBRARIAN, User.ROLE_ADMIN})
    public Response updateUserPassword(@CheckUsername @PathParam("username") String username,
                                       @CheckPassword @FormParam("oldPassword") String oldPassword,
                                       @CheckPassword @FormParam("newPassword") String newPassword) {
        try {
            boolean isPasswordValid = userService.validateUserPassword(username, oldPassword);
            if(!isPasswordValid)
                return Response.status(Response.Status.NOT_ACCEPTABLE)
                        .entity("Invalid password.")
                        .build();

            userService.updateUserPassword(username, newPassword);

        } catch (NoSuchUserException e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity(e.getMessage())
                    .build();
        }

        return Response.ok("Password updated.").build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/{username}/role")
    @RolesAllowed(User.ROLE_ADMIN)
    public Response updateRole(@CheckUsername @PathParam("username") String username,
                               @NotBlank @FormParam("newRole") String newRole) {
        try {
            userService.updateUserRole(username, newRole);
        } catch (NoSuchUserException | InvalidRoleException e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity(e.getMessage())
                    .build();
        }

        return Response.ok("Role updated.").build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/{username}/username")
    @RolesAllowed(User.ROLE_ADMIN)
    public Response updateUsername(@CheckUsername @PathParam("username") String username,
                               @CheckUsername @FormParam("newUsername") String newUsername) {
        try {
            userService.updateUsername(username, newUsername);
        } catch (NoSuchUserException | UserAlreadyExistsException e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity(e.getMessage())
                    .build();
        }

        return Response.ok("Username updated.").build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/{username}/email")
    @RolesAllowed(User.ROLE_ADMIN)
    public Response updateUserEmail(@CheckUsername @PathParam("username") String username,
                               @CheckEmail @FormParam("newEmail") String newEmail) {
        try {
            userService.updateUserEmail(username, newEmail);
        } catch (NoSuchUserException | UserAlreadyExistsException e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity(e.getMessage())
                    .build();
        }

        return Response.ok("Email updated.").build();
    }

    @DELETE
    @Path("/{username}")
    @RolesAllowed(User.ROLE_ADMIN)
    public Response deleteUser(@CheckUsername @PathParam("username") String username) {
        try {
            userService.deleteUser(username);
        } catch (NoSuchUserException e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity(e.getMessage())
                    .build();
        }

        return Response.ok("User deleted.").build();
    }

    @GET
    @Path("/{username}/recs")
    @RolesAllowed({User.ROLE_USER, User.ROLE_LIBRARIAN, User.ROLE_ADMIN})
    public Response getBookRecommendations(@CheckUsername @PathParam("username") String username) {
        try {
            List<Book> recommendations = userService.getBookRecommendations(username);
            return Response.ok(recommendations).build();
        } catch (NoSuchUserException e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity(e.getMessage())
                    .build();
        }
    }

    private String formatUserJson(Object object) {
        return objectMapper.map(object);
    }
}
