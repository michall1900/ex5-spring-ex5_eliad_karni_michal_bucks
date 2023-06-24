package hac.repo.board;


import hac.repo.player.Player;
import hac.repo.subamrine.Submarine;
import hac.repo.tile.Tile;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.*;


@Entity
public class Board {
    public enum Options{
        BASIC,
        ALTERNATIVE
    }
    public final static int SIZE = 10;
    public final static Map<String, String> imgType = new HashMap<String, String>(){{
        put("noShip", "noShip.png");
        put("submarineCell", "submarineCell.png");
        put("explodeShip", "explodeShip.jpg");
        put("empty", "empty.png");
    }};

    public final static Map <Integer,HashMap<Integer,Integer>> options = new HashMap<Integer, HashMap<Integer,Integer>>(){{
        put(Options.BASIC.ordinal(),new HashMap<Integer, Integer>(){{
            put (5,1);
            put(4,1);
            put(3,2);
            put(2,1);
        }});
        put(Options.ALTERNATIVE.ordinal(),new HashMap<Integer, Integer>(){{
            put (4,1);
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
    @JoinColumn(name="player_id", nullable = false)
    private Player player;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "board_id")
    private List<Submarine> submarines ;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "board_id")
    private List<Tile> boardTiles;

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
}