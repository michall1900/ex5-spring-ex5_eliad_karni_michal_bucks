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


/**
 * The service is an api to the boards DB.
 */
@Service
public class BoardService {

    /**
     * The member service acts like an api to the players DB.
     */
    @Autowired
    private PlayerService playerService;

    /**
     * The resource is a lock to all the DB.
     */
    @Resource(name = "getLockForAllDb")
    private ReentrantReadWriteLock DBLock;

    /**
     * The resource is an object of all the rooms locks.
     */
    @Resource(name = "getRoomLock")
    private RoomLockHandler roomsLock;

    /**
     * Default Ctor.
     */
    public BoardService() {
    }


    /**
     * The function returns the board of the received player's as 2D array.
     * The boolean parameter says if the return value should fill the submarines or not.
     * @param player The player id.
     * @param getSubmarine If the submarines should be displayed in the response.
     * @return The board of the received player's as 2D array
     */
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

    /**
     * The function returns all the received player's opponents' boards.
     * Assumption - The function who used this method locked the dbLock + room's lock for reading.
     * @param username The player.
     * @return All the player opponents' boards.
     */
    public HashMap<String, ArrayList<ArrayList<String>>>  getOpponentBoardsByUsername(String username){
        Room room = playerService.getRoomByUsername(username, false);
        HashMap<String, ArrayList<ArrayList<String>>> allBoards = new HashMap<>();
        List<Player> players = room.getPlayers();
        players.forEach((player) -> {
            String playerName = player.getUsername();
            if (!Objects.equals(playerName, username))
                allBoards.put(playerName, getTwoDimensionalArrayByPlayer(player, false));
        });
        return allBoards;
    }

    /**
     * The function gets the user's 2D array board by the player's username.
     * Assumption - the function who used this method already locked db + room's lock for reading.
     * @param username A string includes the username
     * @return 2D array board by the player's username
     */
    public ArrayList<ArrayList<String>> getUserTwoDimensionalArrayBoardByUsername(String username){
        Player player = playerService.getPlayerByUsername(username, false);
        return getTwoDimensionalArrayByPlayer(player, true);

    }

}
