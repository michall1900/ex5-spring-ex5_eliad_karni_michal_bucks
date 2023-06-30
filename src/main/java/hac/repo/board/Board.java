package hac.repo.board;
import hac.classes.customErrors.InvalidChoiceError;
import hac.repo.player.Player;
import hac.repo.subamrine.Submarine;
import hac.repo.tile.Tile;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.util.*;

/**
 * A Board entity. Saves a submarines' list and a tiles' list.
 * Also saves a pointer to a player and some constants that are in use in the program.
 * Relations:
 * Board with Tile is OneToMany.
 * Board with Submarine is OneToMany.
 * Board with Player is OneToOne mapped by player's id.
 */
@Entity
public class Board {

    /**
     * An error message of invalid submarine.
     */
    final static String SUBMARINE_OPTION_ERROR="The submarines you chose are invalid.";

    /**
     * Options for the board type.
     */
    public enum Options{
        BASIC,
        ALTERNATIVE
    }

    /**
     * Bord's size.
     */
    public final static int SIZE = 10;

    /**
     * A map holds the tile's names + image's path in game.
     */
    public final static Map<String, String> imgType = new HashMap<>(){{
        put(String.valueOf(Tile.TileStatus.Miss), "noShip.png");
        put(String.valueOf(Tile.TileStatus.Submarine), "submarineCell.png");
        put(String.valueOf(Tile.TileStatus.Hit), "explodeShip.jpg");
        put(String.valueOf(Tile.TileStatus.Empty), "empty.png");
    }};

    /**
     * Holds the game's options (the number and the size of submarines).
     */
    public final static Map <Integer,HashMap<Integer,Integer>> options = new HashMap<>(){{
        put(Options.BASIC.ordinal(),new HashMap<>(){{
            put(5,1);
            put(4,1);
            put(3,2);
            put(2,1);
        }});
        put(Options.ALTERNATIVE.ordinal(),new HashMap<>(){{
            put(4,1);
            put(3,2);
            put(2,3);
            put(1,4);
        }});
    }};

    /**
     * Holds the board's id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id",nullable = false)
    private long id;

    /**
     * Holds the player record.
     */
    @OneToOne
    @JoinColumn(name = "player_id", referencedColumnName = "id", nullable = false)
    private Player player;

    /**
     * Holds the Submarine records in a list.
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "board_id")
    @NotEmpty
    private List<Submarine> submarines ;


    /**
     * Holds the tile's records in a list.
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "board_id")
    @NotEmpty
    private List<Tile> boardTiles = new ArrayList<>();

    /**
     * Saves the number of exploded submarines.
     */
    @Column
    private int explodedSubmarine = 0;

    /**
     * A c-tor receives board's id, the player record and both lists.
     * @param id - board's id
     * @param player - player record
     * @param submarines - List of submarines' record.
     * @param boardTiles - List of tiles.
     */
    public Board(long id, Player player, List<Submarine> submarines, List<Tile> boardTiles) {
        setId(id);
        setBoardTiles(boardTiles);
        setPlayer(player);
        setSubmarines(submarines);
    }

    /**
     * Default c-tor.
     */
    public Board() {
    }

    /**
     *
     * @return board's id.
     */
    public long getId() {
        return id;
    }

    /**
     *
     * @param id sets the board's id.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     *
     * @return return the players'
     */
    public Player getPlayer() {
        return player;
    }

    /**
     *
     * @param player sets the player record.
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     *
     * @return list of submarine's records.
     */
    public List<Submarine> getSubmarines() {
        return submarines;
    }

    /**
     *
     * @param submarines list of submarine's records.
     */
    public void setSubmarines(List<Submarine> submarines) {
        this.submarines = submarines;
    }

    /**
     *
     * @return get tile's list of records
     */
    public List<Tile> getBoardTiles() {
        return boardTiles;
    }

    /**
     * Set the list of tiles to represent the game board.
     *
     * @param boardTiles List of Tiles to represent the game board.
     */
    public void setBoardTiles(List<Tile> boardTiles) {
        this.boardTiles = boardTiles;
    }

    /**
     * Get the number of submarines that have exploded.
     *
     * @return int number of exploded submarines.
     */
    public int getExplodedSubmarine() {
        return explodedSubmarine;
    }

    /**
     * Set the number of exploded submarines.
     *
     * @param explodedSubmarine number of exploded submarines.
     */
    public void setExplodedSubmarine(int explodedSubmarine) {
        this.explodedSubmarine = explodedSubmarine;
    }
    /**
     * Construct the game board based on options selected.
     *
     * @param option The selected option for creating the board.
     * @throws InvalidChoiceError if the number and sizes of submarines don't match the selected option.
     */
    public void makeBoard(Options option){
        Map<Integer,Integer> numberOfSubmarines = new HashMap<>();

        submarines.forEach((submarine)->{
            submarine.validateSubmarine();
            if (numberOfSubmarines.get(submarine.getSize())==null){
                numberOfSubmarines.put(submarine.getSize(),1);
            }
            else{
                numberOfSubmarines.put(submarine.getSize(), numberOfSubmarines.get(submarine.getSize())+1);
            }
        });
        if (numberOfSubmarines.size()!= options.get(option.ordinal()).size())
            throw new InvalidChoiceError(SUBMARINE_OPTION_ERROR);
        for(Map.Entry<Integer, Integer> entry: options.get(option.ordinal()).entrySet()){
            Integer currentCounter = numberOfSubmarines.get(entry.getKey());
            if(currentCounter==null || !currentCounter.equals(entry.getValue()))
                throw new InvalidChoiceError(SUBMARINE_OPTION_ERROR);
        }
        for (int i=0; i< Board.SIZE*Board.SIZE; i++){
            Tile tile= new Tile();
            tile.setStatus(Tile.TileStatus.Empty);
            boardTiles.add(tile);
        }
        submarines.forEach((submarine -> {
            for(int row = submarine.getFirstRow(); row<= submarine.getLastRow(); row++){
                for(int col = submarine.getFirstCol(); col<=submarine.getLastCol(); col++){
                    boardTiles.get(Board.SIZE*row + col).setSubmarine(submarine);
                    boardTiles.get(Board.SIZE*row + col).setStatus(Tile.TileStatus.Submarine);
                }
            }
        }));
        submarines.forEach((submarine -> {
            for(int row = Math.max(submarine.getFirstRow()-1, 0); row<= Math.min(submarine.getLastRow()+1, SIZE-1); row++){
                for(int col = Math.max(submarine.getFirstCol()-1, 0); col<=Math.min(submarine.getLastCol()+1, SIZE-1); col++){
                    Submarine submarineToCompare = boardTiles.get(Board.SIZE*row + col).getSubmarine();
                    if (submarineToCompare!=null && submarineToCompare!=submarine)
                        throw new InvalidChoiceError(SUBMARINE_OPTION_ERROR);
                }
            }
        }));
    }

    /**
     * Get the status changes in the board after a hit.
     *
     * @param row the row number of the hit location.
     * @param col the column number of the hit location.
     * @return ArrayList of HashMaps, each containing row, column and status of hit tiles.
     */
    public ArrayList<HashMap<String,String>> getHitChanges(int row, int col){
        Tile tile = this.boardTiles.get(row*SIZE + col);
        tile.hitTile();
        if (tile.getStatus() == Tile.TileStatus.Hit){
            return getListAfterHit(tile, tile.getSubmarine(), row, col);
        }
        ArrayList<HashMap<String,String>> hits = new ArrayList<>();
        hits.add(makeHit(tile, row, col));
        return hits;
    }
    /**
     * Generate a list of hit statuses after a tile has been hit.
     *
     * @param tile The tile that has been hit.
     * @param submarine The submarine located on the hit tile.
     * @param hitRow The row number of the hit location.
     * @param hitCol The column number of the hit location.
     * @return ArrayList of HashMaps, each containing row, column and status of hit tiles.
     */
    private ArrayList<HashMap<String,String>> getListAfterHit(Tile tile, Submarine submarine, int hitRow, int hitCol){
        ArrayList<HashMap<String,String>> hits = new ArrayList<>();
        if (submarine.getHits() == submarine.getSize()) {
            this.explodedSubmarine ++;
            for (int row = Math.max(submarine.getFirstRow() - 1, 0); row <= Math.min(submarine.getLastRow() + 1, SIZE - 1); row++) {
                for (int col = Math.max(submarine.getFirstCol() - 1, 0); col <= Math.min(submarine.getLastCol() + 1, SIZE - 1); col++) {
                    Tile currentTile = this.boardTiles.get(row * SIZE + col);
                    hits.add(makeHit(currentTile, row, col));
                }
            }
        }
        else{
            hits.add(makeHit(tile, hitRow, hitCol));
        }
        return hits;
    }

    /**
     * Create a map of hit information.
     *
     * @param tile The tile that has been hit.
     * @param row The row number of the hit location.
     * @param col The column number of the hit location.
     * @return HashMap containing the row, column and status of a hit tile.
     */
    private HashMap<String,String> makeHit(Tile tile, int row, int col){
        HashMap<String, String> hitsMap = new HashMap<>();
        tile.setStatusWithoutChangeTheSubmarine();
        hitsMap.put("row", Integer.toString(row));
        hitsMap.put("col", Integer.toString(col));
        hitsMap.put("status", String.valueOf(tile.getStatus()));
        return hitsMap;
    }

    /**
     * Returns a string representation of the Board object.
     *
     * @return String representation of the Board.
     */
    @Override
    public String toString(){
        return "Board{\n" + "board id = \n" +getId()+ "\nplayer's name = " + this.player.getUsername() +
                "\ntiles = \n{"+ this.getBoardTiles().stream().map((tile)->" " + tile.toString() + ", \n").toString() +
                "} submarines = \n{" + this.getSubmarines().stream().map((submarine -> " "+ submarine.toString()+ ", \n")).toString()+"}\n}";
    }

}