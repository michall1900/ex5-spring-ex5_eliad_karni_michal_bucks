package hac.repo.subamrine;

import hac.classes.GameBoard;
import hac.repo.tile.Tile;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
public class Submarine {

    final static String MAX_ERROR = "Submarine's size can't be greater than 5";
    final static String MIN_ERROR = "Submarine's size can't be lower than 1";
    final static String NULL_ERROR = "is mandatory";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id", nullable = false)
    private long id;


    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @NotNull(message = "First index "+NULL_ERROR)
    private Tile firstIndex;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @NotNull(message = "Last index "+NULL_ERROR)
    private Tile lastIndex;

    @Column
    @Max(value= 5, message = MAX_ERROR)
    @Min(value = 0, message= MIN_ERROR)
    @NotNull(message = "Size "+ NULL_ERROR)
    private int size;

    @Column
    private int hitsNum=0;
}