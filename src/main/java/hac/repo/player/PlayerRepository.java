package hac.repo.player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    @Query("SELECT p FROM Player p JOIN p.room r WHERE p.id = :playerId AND r.id = :roomId")
    Optional<Player> findByPlayerIdAndRoomId(@Param("playerId") long playerId, @Param("roomId") long roomId);
}
