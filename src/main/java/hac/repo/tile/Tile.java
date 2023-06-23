package hac.repo.tile;

import hac.classes.GameBoard;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Formatter;

@Entity
public class Tile {

    public enum TileStatus{
        Miss,
        Hit,
        Empty
    }
    final static String MAX_ERROR = "value can't be bigger than " + (GameBoard.SIZE-1);
    final static String MIN_ERROR = "value can't be less than 0";
    final static String NULL_ERROR = "is mandatory";
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id", nullable = false)
    private long id;

    @Column
    @Max(value= GameBoard.SIZE-1, message = "Row's " + MAX_ERROR)
    @Min(value = 0, message= "Row's "+ MIN_ERROR)
    @NotNull(message = "Row "+ NULL_ERROR)
    @NotEmpty(message = "Row "+ NULL_ERROR)
    private int row;

    @Column
    @Max(value= GameBoard.SIZE-1, message = "Column's " + MAX_ERROR)
    @Min(value = 0, message= "Column's "+ MIN_ERROR)
    @NotNull(message = "Column "+ NULL_ERROR)
    @NotEmpty(message = "Column "+ NULL_ERROR)
    private int col;


    @Enumerated (EnumType.ORDINAL)
    private TileStatus status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public TileStatus getStatus() {
        return status;
    }

    public void setStatus(TileStatus status) {
        this.status = status;
    }

    public Tile(){
    }

    public Tile(TileStatus status,int row, int col){
        setStatus(status);
        setCol(col);
        setRow(row);
    }
}
