package hac.repo.subamrine;

import hac.repo.board.Board;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * The `Submarine` class represents a submarine entity in a game. It contains attributes and behaviors related to the submarine's size, hits, position, and validation.
 */
@Entity
public class Submarine {

    /**
     * Error message: Submarine's size can't be greater than 5
     */
    final static String MAX_SIZE_ERROR = "Submarine's size can't be greater than 5";

    /**
     * Error message: Submarine's size can't be lower than 1
     */
    final static String MIN_SIZE_ERROR = "Submarine's size can't be lower than 1";

    /**
     * Error message: is mandatory
     */
    final static String NULL_ERROR = "is mandatory";

    /**
     * Error message: Invalid submarine's display (The indexes not pointing on the size or the submarine displayed diagonal)
     */
    final static String INVALID_DISPLAY = "Invalid submarine's display (The indexes not pointing on the size or the submarine displayed diagonal)";

    /**
     * Error message: size can't be greater than {Board.SIZE - 1}
     */
    final static String MAX_INDEX_ERROR = "size can't be greater than " + (Board.SIZE - 1);

    /**
     * Error message: size can't be greater than 0
     */
    final static String MIN_INDEX_ERROR = "size can't be greater than 0";


    /**
     * Database mapping
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private long id;

    /**
     * The size of the submarine.
     */
    @Column
    @Max(value = 5, message = MAX_SIZE_ERROR)
    @Min(value = 0, message = MIN_SIZE_ERROR)
    private int size;

    /**
     * The number of hits the submarine has taken.
     */
    @Column
    private int hits = 0;

    /**
     * The index of the first row where the submarine is located on the board.
     */
    @Column
    @Max(value = Board.SIZE - 1, message = "First row " + MAX_INDEX_ERROR)
    @Min(value = 0, message = "First row " + MIN_INDEX_ERROR)
    private int firstRow;

    /**
     * The index of the first column where the submarine is located on the board.
     */
    @Column
    @Max(value = Board.SIZE - 1, message = "First column " + MAX_INDEX_ERROR)
    @Min(value = 0, message = "First column " + MIN_INDEX_ERROR)
    private int firstCol;

    /**
     * The index of the last row where the submarine is located on the board.
     */
    @Column
    @Max(value = Board.SIZE - 1, message = "Last row " + MAX_INDEX_ERROR)
    @Min(value = 0, message = "Last row " + MIN_INDEX_ERROR)
    private int lastRow;

    /**
     * The index of the last column where the submarine is located on the board.
     */
    @Column
    @Max(value = Board.SIZE - 1, message = "Last column " + MAX_INDEX_ERROR)
    @Min(value = 0, message = "Last column " + MIN_INDEX_ERROR)
    private int lastCol;
    /**
     * Default constructor for the `Submarine` class.
     */
    public Submarine() {
    }

    /**
     * Parameterized constructor for the `Submarine` class.
     *
     * @param id        The unique identifier of the submarine.
     * @param size      The size of the submarine.
     * @param hits      The number of hits the submarine has received.
     * @param firstRow  The row index of the first cell occupied by the submarine.
     * @param firstCol  The column index of the first cell occupied by the submarine.
     * @param lastRow   The row index of the last cell occupied by the submarine.
     * @param lastCol   The column index of the last cell occupied by the submarine.
     */
    public Submarine(long id, int size, int hits, int firstRow, int firstCol, int lastRow, int lastCol) {
        setId(id);
        setSize(size);
        setHits(hits);
        setFirstCol(firstCol);
        setFirstRow(firstRow);
        setLastRow(lastRow);
        setLastCol(lastCol);
    }

    /**
     * Retrieves the unique identifier of the submarine.
     * @return The unique identifier of the submarine.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the submarine.
     * @param id The unique identifier of the submarine.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Retrieves the size of the submarine.
     * @return The size of the submarine.
     */
    public int getSize() {
        return size;
    }

    /**
     * Sets the size of the submarine.
     * @param size The size of the submarine.
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Retrieves the number of hits the submarine has received.
     * @return The number of hits the submarine has received.
     */
    public int getHits() {
        return hits;
    }

    /**
     * Sets the number of hits the submarine has received.
     * @param hits The number of hits the submarine has received.
     */
    public void setHits(int hits) {
        this.hits = hits;
    }

    /**
     * Retrieves the row index of the first cell occupied by the submarine.
     * @return The row index of the first cell occupied by the submarine.
     */
    public int getFirstRow() {
        return firstRow;
    }

    /**
     * Sets the row index of the first cell occupied by the submarine.
     *
     * @param firstRow The row index of the first cell occupied by the submarine.
     */
    public void setFirstRow(int firstRow) {
        this.firstRow = firstRow;
    }

    /**
     * Retrieves the column index of the first cell occupied by the submarine.
     *
     * @return The column index of the first cell occupied by the submarine.
     */
    public int getFirstCol() {
        return firstCol;
    }

    /**
     * Sets the column index of the first cell occupied by the submarine.
     *
     * @param firstCol The column index of the first cell occupied by the submarine.
     */
    public void setFirstCol(int firstCol) {
        this.firstCol = firstCol;
    }

    /**
     * Retrieves the row index of the last cell occupied by the submarine.
     *
     * @return The row index of the last cell occupied by the submarine.
     */
    public int getLastRow() {
        return lastRow;
    }

    /**
     * Sets the row index of the last cell occupied by the submarine.
     *
     * @param lastRow The row index of the last cell occupied by the submarine.
     */
    public void setLastRow(int lastRow) {
        this.lastRow = lastRow;
    }

    /**
     * Retrieves the column index of the last cell occupied by the submarine.
     *
     * @return The column index of the last cell occupied by the submarine.
     */
    public int getLastCol() {
        return lastCol;
    }

    /**
     * Sets the column index of the last cell occupied by the submarine.
     *
     * @param lastCol The column index of the last cell occupied by the submarine.
     */
    public void setLastCol(int lastCol) {
        this.lastCol = lastCol;
    }

    /**
     * Validates the submarine's position and display.
     * Throws a RuntimeException if the submarine's display is invalid.
     */
    public void validateSubmarine() {
        if (firstRow > lastRow || firstCol > lastCol ||
                !((firstRow == lastRow && lastCol - firstCol + 1 == size) ||
                        (firstCol == lastCol && lastRow - firstRow + 1 == size))) {
            throw new RuntimeException(INVALID_DISPLAY);
        }
    }

    /**
     * Increments the number of hits the submarine has received by one.
     */
    public void hitSubmarine() {
        hits++;
    }

    /**
     * Returns a string representation of the submarine.
     *
     * @return A string representation of the submarine.
     */
    @Override
    public String toString() {
        return "Submarine{submarine id = " + this.getId() + "}";
    }
}