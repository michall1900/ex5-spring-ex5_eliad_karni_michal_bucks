package hac.repo.board;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The boards' repository
 */
public interface BoardRepository extends JpaRepository<Board, Long> {
}
