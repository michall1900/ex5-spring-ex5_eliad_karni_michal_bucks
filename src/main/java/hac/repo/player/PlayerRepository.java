package hac.repo.player;

import org.springframework.data.jpa.repository.JpaRepository;

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
