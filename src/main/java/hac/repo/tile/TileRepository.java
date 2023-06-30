package hac.repo.tile;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The repository of the tiles.
 */
public interface TileRepository  extends JpaRepository<Tile, Long> {
}
