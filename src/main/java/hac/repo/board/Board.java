package hac.repo.board;


import hac.repo.player.Player;
import hac.repo.subamrine.Submarine;
import hac.repo.tile.Tile;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;


@Entity
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private long id;

    @Column(unique = true)
    @NotBlank(message="User's id must be inserted.")
    private String username;

    @OneToOne
    private Player player;

    @OneToMany
    private List<Submarine> submarines ;

    @OneToMany
    private List<Tile> boardTiles;
}