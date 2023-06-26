package hac.repo.subamrine;

import hac.classes.GameBoard;
//import hac.repo.tile.Tile;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
public class Submarine {

    final static String MAX_SIZE_ERROR = "Submarine's size can't be greater than 5";
    final static String MIN_SIZE_ERROR = "Submarine's size can't be lower than 1";
    final static String NULL_ERROR = "is mandatory";

    final static String MAX_INDEX_ERROR = "size can't be greater than "+ (GameBoard.SIZE-1);
    final static String MIN_INDEX_ERROR = "size can't be greater than 0";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id", nullable = false)
    private long id;


    @Column
    @NotEmpty(message = "Size "+ NULL_ERROR)
    @Max(value= 5, message = MAX_SIZE_ERROR)
    @Min(value = 0, message= MIN_SIZE_ERROR)
    private int size;


    @Column
    private int hits = 0;

    @Column
    @NotEmpty(message = "First row "+ NULL_ERROR)
    @Max(value= GameBoard.SIZE-1, message = "First row " + MAX_INDEX_ERROR)
    @Min(value = 0, message= "First row " + MIN_INDEX_ERROR)
    private int first_row;

    @Column
    @NotEmpty(message = "First column "+ NULL_ERROR)
    @Max(value= GameBoard.SIZE-1, message = "First column " + MAX_INDEX_ERROR)
    @Min(value = 0, message= "First column " + MIN_INDEX_ERROR)
    private int first_col;

    @Column
    @NotEmpty(message = "Last row "+ NULL_ERROR)
    @Max(value= GameBoard.SIZE-1, message = "Last row " + MAX_INDEX_ERROR)
    @Min(value = 0, message= "Last row " + MIN_INDEX_ERROR)
    private int last_row;



    @Column
    @NotEmpty(message = "Last column "+ NULL_ERROR)
    @Max(value= GameBoard.SIZE-1, message = "Last column " + MAX_INDEX_ERROR)
    @Min(value = 0, message= "Last column " + MIN_INDEX_ERROR)
    private int last_col;



    public Submarine() {
    }

    public Submarine(long id, int size, int hits, int first_row, int first_col, int last_row, int last_col) {
        this.id = id;
        this.size = size;
        this.hits = hits;
        this.first_row = first_row;
        this.first_col = first_col;
        this.last_row = last_row;
        this.last_col = last_col;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int drownSubmarines) {
        this.hits = drownSubmarines;
    }

    public int getFirst_row() {
        return first_row;
    }

    public void setFirst_row(int first_row) {
        this.first_row = first_row;
    }

    public int getFirst_col() {
        return first_col;
    }

    public void setFirst_col(int first_col) {
        this.first_col = first_col;
    }

    public int getLast_row() {
        return last_row;
    }

    public void setLast_row(int last_row) {
        this.last_row = last_row;
    }

    public int getLast_col() {
        return last_col;
    }

    public void setLast_col(int last_col) {
        this.last_col = last_col;
    }

}