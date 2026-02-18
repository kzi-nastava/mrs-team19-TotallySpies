package rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>{
    List<ChatMessage> findBySenderIdOrReceiverIdOrderByTimestampAsc(Long userId, Long userId2);
    @Query("SELECT DISTINCT c.senderId FROM ChatMessage c WHERE c.receiverId IS NULL")
    List<Long> findDistinctSenderIdsByReceiverIdIsNull();
}
