package hac.repo.board;


import hac.repo.player.Player;
import hac.repo.subamrine.Submarine;
import hac.repo.tile.Tile;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Entity
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id",nullable = false)
    private long id;

    @OneToOne
    private Player player;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "board_id")
    private Set<Submarine> submarines ;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "board_id")
    private List<Tile> boardTiles;

    public Board(long id, Player player, Set<Submarine> submarines, List<Tile> boardTiles) {
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

    public Set<Submarine> getSubmarines() {
        return submarines;
    }

    public void setSubmarines(Set<Submarine> submarines) {
        this.submarines = submarines;
    }

    public List<Tile> getBoardTiles() {
        return boardTiles;
    }

    public void setBoardTiles(List<Tile> boardTiles) {
        this.boardTiles = boardTiles;
    }
}