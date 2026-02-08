    package rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories;

    import io.jsonwebtoken.security.Jwks;
    import jakarta.transaction.Transactional;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Modifying;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.stereotype.Repository;
    import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.User;
    import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.UserRole;

    import java.util.Optional;

    @Repository
    public interface UserRepository extends JpaRepository<User, Long> {
        User findByEmail(String email);
        boolean existsByEmail(String email);

        @Transactional
        @Modifying
        @Query("update User u set u.password = ?2 where u.email = ?1")
        void updatePassword(String email, String password);

        @Query("select u.role from User u where u.email = ?1")
        UserRole getRoleByEmail(String email);
    }

