package hac.repo.subamrine;

import hac.repo.tile.Tile;
import jakarta.persistence.*;

@Entity
public class Submarine {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id", nullable = false)
    private long id;


    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Tile firstIndex;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Tile lastIndex;


}