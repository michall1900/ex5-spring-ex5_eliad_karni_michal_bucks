package hac.services;

import hac.repo.board.Board;
import hac.repo.board.BoardRepository;
import hac.repo.player.Player;
import hac.repo.player.PlayerRepository;
import hac.repo.room.Room;
import hac.repo.tile.Tile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

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
        //TODO order the code.
        Player p = playerService.getPlayerByUsername(username,true);
        Room r = playerService.getRoomByUsername(username);
        if (p.getBoard()!=null)
            throw new RuntimeException(ALREADY_HAVE_BOARD);
        board.makeBoard(r.getOption());
        p.setBoard(board);
        p.setStatus(Player.PlayerStatus.READY);
        board.setPlayer(p);
        boardRepository.save(board);
    }

    @Transactional(readOnly = true)
    public ArrayList<ArrayList<String>> getTwoDimensionalArrayByPlayer(Player player, Boolean getSubmarine){

        Board board = player.getBoard();
        List<Tile> tiles= board.getBoardTiles();
        ArrayList<ArrayList<String>> boardToSend = new ArrayList<>();
        for (int row = 0; row<Board.SIZE; row++){
            ArrayList<String> rowToSend = new ArrayList<>();
            for(int col =0; col<Board.SIZE; col++){
                Tile currentTile = tiles.get(row*Board.SIZE + col);
                Tile.TileStatus status = currentTile.getStatus();
                if (status == Tile.TileStatus.Hit) {
                    //System.out.println(status);
                    //System.out.println(row);
                    //System.out.println(col);
                    //System.out.println(player.getUsername());
                    //System.out.println(getSubmarine);
                }
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

    @Transactional(readOnly = true)
    public HashMap<String, ArrayList<ArrayList<String>>>  getOpponentBoardsByUsername(String username){
        HashMap<String,  ArrayList<ArrayList<String>>> allBoards = new HashMap<>();
        Room room = playerService.getRoomByUsername(username);
        List<Player> players = room.getPlayers();
        players.forEach((player)->{
            String playerName = player.getUsername();
            if (!Objects.equals(playerName, username))
                allBoards.put(playerName,getTwoDimensionalArrayByPlayer(player, false));
        });
        return allBoards;
    }

    @Transactional(readOnly = true)
    public ArrayList<ArrayList<String>> getUserTwoDimensionalArrayBoardByUsername(String username){
        Player player = playerService.getPlayerByUsername(username,false);
        return getTwoDimensionalArrayByPlayer(player,true);
    }

    @Transactional(readOnly = true)
    public Board getUserBoardByUserName(String username){
        return playerService.getPlayerByUsername(username,false).getBoard();
    }
}
