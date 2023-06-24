package hac.classes;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.stereotype.Component;

import java.io.Serializable;

import static hac.classes.Submarine.NULL_ERROR;


@Component
public class Index implements Serializable {
    final static String MAX_INDEX_ERROR = "size can't be greater than "+ (GameBoard.SIZE-1);
    final static String MIN_INDEX_ERROR = "size can't be greater than 0";
    @NotEmpty(message = "Row "+ NULL_ERROR)
    @Max(value= GameBoard.SIZE-1, message = "Row " + MAX_INDEX_ERROR)
    @Min(value = 0, message= "Row " + MIN_INDEX_ERROR)
    private int row;

    @NotEmpty(message = "Column "+ NULL_ERROR)
    @Max(value= GameBoard.SIZE-1, message = "Column " + MAX_INDEX_ERROR)
    @Min(value = 0, message= "Column " + MIN_INDEX_ERROR)
    private int col;

    public Index(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Index() {
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
}
