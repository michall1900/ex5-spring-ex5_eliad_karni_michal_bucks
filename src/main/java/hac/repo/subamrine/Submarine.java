package hac.repo.subamrine;

//import hac.repo.tile.Tile;
import hac.repo.board.Board;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

@Entity
public class Submarine {

    final static String MAX_SIZE_ERROR = "Submarine's size can't be greater than 5";
    final static String MIN_SIZE_ERROR = "Submarine's size can't be lower than 1";
    final static String NULL_ERROR = "is mandatory";

    final static String INVALID_DISPLAY = "Invalid submarine's display (The indexes not pointing on the size or the submarine displayed diagonal)";

    final static String MAX_INDEX_ERROR = "size can't be greater than "+ (Board.SIZE-1);
    final static String MIN_INDEX_ERROR = "size can't be greater than 0";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id", nullable = false)
    private long id;


    @Column
    @Max(value= 5, message = MAX_SIZE_ERROR)
    @Min(value = 0, message= MIN_SIZE_ERROR)
    private int size;


    @Column
    private int hits = 0;

    @Column
    @Max(value= Board.SIZE-1, message = "First row " + MAX_INDEX_ERROR)
    @Min(value = 0, message= "First row " + MIN_INDEX_ERROR)
    private int firstRow;

    @Column
    @Max(value= Board.SIZE-1, message = "First column " + MAX_INDEX_ERROR)
    @Min(value = 0, message= "First column " + MIN_INDEX_ERROR)
    private int firstCol;

    @Column
    @Max(value= Board.SIZE-1, message = "Last row " + MAX_INDEX_ERROR)
    @Min(value = 0, message= "Last row " + MIN_INDEX_ERROR)
    private int lastRow;



    @Column
    @Max(value= Board.SIZE-1, message = "Last column " + MAX_INDEX_ERROR)
    @Min(value = 0, message= "Last column " + MIN_INDEX_ERROR)
    private int lastCol;



    public Submarine() {
    }

    public Submarine(long id, int size, int hits, int firstRow, int firstCol, int lastRow, int lastCol) {
        setId(id);
        setSize(size);
        setHits(hits);
        setFirstCol(firstCol);
        setFirstRow(firstRow);
        setLastRow(lastRow);
        setLastCol(lastCol);
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

    public int getFirstRow() {
        return firstRow;
    }

    public void setFirstRow(int first_row) {
        this.firstRow = first_row;
    }

    public int getFirstCol() {
        return firstCol;
    }

    public void setFirstCol(int first_col) {
        this.firstCol = first_col;
    }

    public int getLastRow() {
        return lastRow;
    }

    public void setLastRow(int last_row) {
        this.lastRow = last_row;
    }

    public int getLastCol() {
        return lastCol;
    }

    public void setLastCol(int last_col) {
        this.lastCol = last_col;
    }

    public void validateSubmarine(){
        if (firstRow > lastRow || firstCol > lastCol ||
            !((firstRow == lastRow && lastCol-firstCol+1 ==size)||
                firstCol== lastCol && lastRow-firstRow+1==size)){
            throw new RuntimeException(INVALID_DISPLAY);
        }
    }

    public void hitSubmarine(){
        hits ++ ;
    }

    @Override
    public String toString(){
        return "Submarine{submarine id = " + this.getId() + "}";
    }

}