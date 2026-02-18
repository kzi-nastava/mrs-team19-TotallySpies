package rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.User;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.UserRole;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryStopRideTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_returnsUser_whenExists(){
        User u = new User();
        u.setName("Test");
        u.setLastName("Driver");
        u.setEmail("driver@gmail.com");
        u.setPassword("driver123");
        u.setRole(UserRole.DRIVER);
        u.setEnabled(true);
        userRepository.save(u);

        User userFound = userRepository.findByEmail("driver@gmail.com");
        assertNotNull(userFound);
        assertEquals("driver@gmail.com", userFound.getEmail());
        assertEquals(UserRole.DRIVER, userFound.getRole());
    }

    @Test
    void findByEmail_returnsNull_whenNotExists(){
        User userFound = userRepository.findByEmail("missing@gmail.com");
        assertNull(userFound);
    }
}
