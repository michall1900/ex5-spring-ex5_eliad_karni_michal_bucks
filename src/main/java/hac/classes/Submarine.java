package hac.classes;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;

public class Submarine implements Serializable {
    final static String MAX_SIZE_ERROR = "Submarine's size can't be greater than 5";
    final static String MIN_SIZE_ERROR = "Submarine's size can't be lower than 1";
    final static String NULL_ERROR = "is mandatory";
    private Index firstIndex;
    private Index lastIndex;

    @Column
    @NotEmpty(message = "Size "+ NULL_ERROR)
    @Max(value= 5, message = MAX_SIZE_ERROR)
    @Min(value = 0, message= MIN_SIZE_ERROR)
    private int size;

    Submarine(){

    }

    public Submarine(Index firstIndex, Index lastIndex, int size) {
        setFirstIndex(firstIndex);
        setLastIndex(lastIndex);
        setSize(size);
    }

    public Index getFirstIndex() {
        return firstIndex;
    }

    public void setFirstIndex(Index firstIndex) {
        this.firstIndex = firstIndex;
    }

    public Index getLastIndex() {
        return lastIndex;
    }

    public void setLastIndex(Index lastIndex) {
        this.lastIndex = lastIndex;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
