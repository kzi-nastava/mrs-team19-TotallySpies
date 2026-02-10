package rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Notification;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.User;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
}
