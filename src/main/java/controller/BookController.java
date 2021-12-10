package controller;

import controller.aop.BeforeAdvice;
import controller.util.validation.CheckUsername;
import repository.entity.Book;
import repository.entity.User;
import service.BookService;
import service.exception.book.BookAlreadyExistsException;
import service.exception.book.NoSuchBookException;
import service.exception.rating.NoSuchRatingException;
import service.exception.user.NoSuchUserException;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Optional;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Interceptors(BeforeAdvice.class)
@Local
public class BookController {
    @EJB
    private BookService bookService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({User.ROLE_LIBRARIAN, User.ROLE_ADMIN})
    public Response addBook(Book book) {
        try {
            bookService.addBook(book);
        } catch (BookAlreadyExistsException e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity(e.getMessage())
                    .build();
        }
        return Response.ok("Book added.").build();
    }

    @GET
    @PermitAll
    public Response getAllBooks() {
        return Response.ok(bookService.getAllBooks()).build();
    }

    @GET
    @Path("/{isbn}")
    @PermitAll
    public Response getBookByIsbn(@PathParam("isbn") String isbn) {
        try {
            return Response.ok(getBook(isbn)).build();
        } catch (NoSuchBookException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{isbn}")
    @RolesAllowed({User.ROLE_LIBRARIAN, User.ROLE_ADMIN})
    public Response updateBook(@PathParam("isbn") String isbn, Book replacingBook) {
        try {
            bookService.updateBook(isbn, replacingBook);
        } catch (NoSuchBookException | BookAlreadyExistsException e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity(e.getMessage())
                    .build();
        }
        return Response.ok("Book updated.").build();
    }

    @DELETE
    @Path("/{isbn}")
    @RolesAllowed({User.ROLE_LIBRARIAN, User.ROLE_ADMIN})
    public Response deleteBook(@PathParam("isbn") String isbn) {
        try {
            bookService.deleteBook(isbn);
        } catch (NoSuchBookException e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity(e.getMessage())
                    .build();
        }
        return Response.ok("Book deleted.").build();
    }

    @PUT
    @Path("/{isbn}/rating")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @RolesAllowed({User.ROLE_USER, User.ROLE_LIBRARIAN, User.ROLE_ADMIN})
    public Response rateBook(@PathParam("isbn") String isbn,
                             @CheckUsername @FormParam("username") String username,
                             @FormParam("rating") Double rating) {

        try {
            bookService.rateBook(isbn, username, new BigDecimal(rating));
        } catch (NoSuchBookException | NoSuchUserException e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity(e.getMessage())
                    .build();
        }

        return Response.ok("Book rated.").build();
    }

    @DELETE
    @Path("/{isbn}/rating")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @RolesAllowed({User.ROLE_USER, User.ROLE_LIBRARIAN, User.ROLE_ADMIN})
    public Response unrateBook(@PathParam("isbn") String isbn,
                             @CheckUsername @FormParam("username") String username) {

        try {
            bookService.unrateBook(isbn, username);
        } catch (NoSuchBookException | NoSuchUserException | NoSuchRatingException e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity(e.getMessage())
                    .build();
        }

        return Response.ok("Book unrated.").build();
    }

    private Book getBook(String isbn) throws NoSuchBookException {
        Optional<Book> bookOptional = bookService.getBookByIsbn(isbn);
        if(!bookOptional.isPresent())
            throw new NoSuchBookException(isbn);
        return bookOptional.get();
    }
}
