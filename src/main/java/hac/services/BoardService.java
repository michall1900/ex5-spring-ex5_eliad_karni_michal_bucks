package hac.services;

import hac.repo.board.Board;
import hac.repo.board.BoardRepository;
import hac.repo.player.Player;
import hac.repo.player.PlayerRepository;
import hac.repo.room.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BoardService {
    final static String ALREADY_HAVE_BOARD = "You already have a board";

    @Autowired
    private PlayerService playerService;

    @Autowired
    private BoardRepository boardRepository;


    public BoardService() {
    }

    public BoardService(PlayerService playersRepo, BoardRepository boardRepository) {
        this.playerService = playersRepo;
        this.boardRepository = boardRepository;
    }

    public PlayerService getPlayersRepo() {
        return playerService;
    }

    public void setPlayersRepo(PlayerService playersRepo) {
        this.playerService = playersRepo;
    }

    public BoardRepository getBoardRepository() {
        return boardRepository;
    }

    public void setBoardRepository(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Transactional
    public void saveNewBoard(Board board, String username){
        Player p = playerService.getPlayerByUsername(username,true);
        Room r = playerService.getRoomByUsername(username);
        if (p.getBoard()!=null)
            throw new RuntimeException(ALREADY_HAVE_BOARD);
        board.makeBoard(r.getOption());
        p.setBoard(board);
        p.setStatus(Player.PlayerStatus.READY);
        board.setPlayer(p);
        //check this later;
        boardRepository.save(board);

    }

}
