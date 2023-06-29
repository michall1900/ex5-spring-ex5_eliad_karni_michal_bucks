package hac.services;

import hac.beans.RoomLockHandler;
import hac.repo.board.Board;
import hac.repo.board.BoardRepository;
import hac.repo.player.Player;
import hac.repo.player.PlayerRepository;
import hac.repo.room.Room;
import hac.repo.tile.Tile;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;


@Service
public class BoardService {
    final static String ALREADY_HAVE_BOARD = "You already have a board";

    @Autowired
    private PlayerService playerService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private RoomService roomService;

    @Resource(name = "getLockForAllDb")
    private ReentrantReadWriteLock DBLock;

    @Resource(name = "getRoomLock")
    private RoomLockHandler roomsLock;

    public BoardService() {
    }

    public BoardService(PlayerService playersRepo, BoardRepository boardRepository) {
        this.playerService = playersRepo;
        this.boardRepository = boardRepository;
    }


    @Transactional
    public void saveNewBoard(Board board, String username){
        try {
            DBLock.writeLock().lock();
            //TODO order the code.
            Player p = playerService.getPlayerByUsername(username,false);
            Room r = p.getRoom();
            if (p.getBoard()!=null)
                throw new RuntimeException(ALREADY_HAVE_BOARD);
            board.makeBoard(r.getOption());
            p.setBoard(board);
            p.setStatus(Player.PlayerStatus.READY);
            board.setPlayer(p);
            boardRepository.save(board);
            roomService.updateRoomStatusByUsername(username, false);
        }
        finally {
            DBLock.writeLock().unlock();
        }

    }


    private ArrayList<ArrayList<String>> getTwoDimensionalArrayByPlayer(Player player, Boolean getSubmarine){

        Board board = player.getBoard();
        List<Tile> tiles= board.getBoardTiles();
        ArrayList<ArrayList<String>> boardToSend = new ArrayList<>();
        for (int row = 0; row<Board.SIZE; row++){
            ArrayList<String> rowToSend = new ArrayList<>();
            for(int col =0; col<Board.SIZE; col++){
                Tile currentTile = tiles.get(row*Board.SIZE + col);
                Tile.TileStatus status = currentTile.getStatus();
                if (status == Tile.TileStatus.Submarine){
                    if (getSubmarine){
                        rowToSend.add(Board.imgType.get(String.valueOf(status)));
                    }
                    else
                        rowToSend.add(Board.imgType.get(String.valueOf(Tile.TileStatus.Empty)));
                }
                else
                    rowToSend.add(Board.imgType.get(String.valueOf(status)));

            }
            boardToSend.add(rowToSend);
        }
        return boardToSend;
    }

    public HashMap<String, ArrayList<ArrayList<String>>>  getOpponentBoardsByUsername(String username){
        try{
            DBLock.readLock().lock();
            Room room = playerService.getRoomByUsername(username);
            roomsLock.getRoomLock(room.getId()).readLock().lock();
            try {

                HashMap<String, ArrayList<ArrayList<String>>> allBoards = new HashMap<>();
                List<Player> players = room.getPlayers();
                players.forEach((player) -> {
                    String playerName = player.getUsername();
                    if (!Objects.equals(playerName, username))
                        allBoards.put(playerName, getTwoDimensionalArrayByPlayer(player, false));
                });
                return allBoards;
            }
            finally {
                roomsLock.getRoomLock(room.getId()).readLock().unlock();
            }
        }
        finally {
            DBLock.readLock().unlock();
        }
    }

    public ArrayList<ArrayList<String>> getUserTwoDimensionalArrayBoardByUsername(String username){
        try {
            DBLock.readLock().lock();
            Player player = playerService.getPlayerByUsername(username, false);
            roomsLock.getRoomLock(player.getRoom().getId()).readLock().lock();
            try {
                return getTwoDimensionalArrayByPlayer(player, true);
            }
            finally {
                roomsLock.getRoomLock(player.getRoom().getId()).readLock().unlock();
            }
        }
        finally {
            DBLock.readLock().unlock();
        }
    }

}
