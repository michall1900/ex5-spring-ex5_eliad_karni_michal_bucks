package hac.repo.board;


import hac.repo.player.Player;
import hac.repo.subamrine.Submarine;
import hac.repo.tile.Tile;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.*;


@Entity
public class Board {
    final static String SUBMARINE_OPTION_ERROR="The submarines you are invalid.";
    public enum Options{
        BASIC,
        ALTERNATIVE
    }
    public final static int SIZE = 10;
    public final static Map<String, String> imgType = new HashMap<String, String>(){{
        put(String.valueOf(Tile.TileStatus.Miss), "noShip.png");
        put(String.valueOf(Tile.TileStatus.Submarine), "submarineCell.png");
        put(String.valueOf(Tile.TileStatus.Hit), "explodeShip.jpg");
        put(String.valueOf(Tile.TileStatus.Empty), "empty.png");
    }};

    public final static Map <Integer,HashMap<Integer,Integer>> options = new HashMap<Integer, HashMap<Integer,Integer>>(){{
        put(Options.BASIC.ordinal(),new HashMap<Integer, Integer>(){{
            put(5,1);
            put(4,1);
            put(3,2);
            put(2,1);
        }});
        put(Options.ALTERNATIVE.ordinal(),new HashMap<Integer, Integer>(){{
            put(4,1);
            put(3,2);
            put(2,3);
            put(1,4);
        }});
    }};

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id",nullable = false)
    private long id;

    @OneToOne
    @JoinColumn(name = "player_id", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Player player;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "board_id")
    private List<Submarine> submarines ;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "board_id")
    private List<Tile> boardTiles = new ArrayList<>();

    @Column
    private int explodedSubmarine = 0;

    public Board(long id, Player player, List<Submarine> submarines, List<Tile> boardTiles) {
        setId(id);
        setBoardTiles(boardTiles);
        setPlayer(player);
        setSubmarines(submarines);
    }

    public Board() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public List<Submarine> getSubmarines() {
        return submarines;
    }

    public void setSubmarines(List<Submarine> submarines) {
        this.submarines = submarines;
    }

    public List<Tile> getBoardTiles() {
        return boardTiles;
    }

    public void setBoardTiles(List<Tile> boardTiles) {
        this.boardTiles = boardTiles;
    }


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
            throw new RuntimeException(SUBMARINE_OPTION_ERROR);
        for(Map.Entry<Integer, Integer> entry: options.get(option.ordinal()).entrySet()){
            Integer currentCounter = numberOfSubmarines.get(entry.getKey());
            if(currentCounter==null || !currentCounter.equals(entry.getValue()))
                throw new RuntimeException(SUBMARINE_OPTION_ERROR);
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
                        throw new RuntimeException(SUBMARINE_OPTION_ERROR);
                }
            }
        }));
    }

}