import repository.entity.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class CreateAdminAndLibrarian {
    static EntityManager em;

    static {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
        em = emf.createEntityManager();
    }

    public static void main(String[] args) {
        String salt = PasswordUtil.getNewSalt();
        String password = PasswordUtil.getEncryptedPassword("password", salt);
        User admin = User.builder()
                .role(User.ROLE_ADMIN)
                .email("n.urmanov@iitu.edu.kz")
                .username("Admin")
                .password(password)
                .salt(salt)
                .build();

        String salt2 = PasswordUtil.getNewSalt();
        String password2 = PasswordUtil.getEncryptedPassword("password", salt2);
        User librarian = User.builder()
                .role(User.ROLE_LIBRARIAN)
                .email("lib@library.com")
                .username("Librarian")
                .password(password2)
                .salt(salt2)
                .build();

        em.getTransaction().begin();
        em.persist(admin);
        em.persist(librarian);
        em.getTransaction().commit();
        em.close();
    }
}
