package hac.repo.player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * The players' repository
 */
public interface PlayerRepository extends JpaRepository<Player, Long> {
    /**
     * Las player find by the player's username.
     * @param username The player's username.
     * @return The found player.
     */
    Player findByUsername(String username);
    
}
