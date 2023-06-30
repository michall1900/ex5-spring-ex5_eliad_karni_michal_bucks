package hac.classes.forGame;

import hac.repo.board.Board;
import hac.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import hac.classes.customErrors.InvalidChoiceError;
import java.io.Serializable;

/**
 * The class is the user's turn's action.
 */
@Component
public class UserTurn implements Serializable {
    /**
     * The error message thrown when the user's action is invalid.
     */
    final static String ERROR_MESSAGE = "Your choice is invalid";
    /**
     * The row where the action at.
     */
    int row;
    /**
     * The col where the action at.
     */
    int col;
    /**
     * The player the action on.
     */
    String opponentName;

    /**
     * Default Ctor (for bean).
     */
    public UserTurn() {
    }

    /**
     * The Ctor of the turn
     * @param row The row where the action at.
     * @param col The col where the action at.
     * @param opponentName The player the action on.
     */
    public UserTurn(int row, int col, String opponentName) {
        setRow(row);
        setCol(col);
        setOpponentName(opponentName);
    }

    /**
     * row's parameter getter.
     * @return The row's member value.
     */
    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        validateIndex(row);
        this.row = row;
    }

    /**
     * col's parameter getter.
     * @return The col's member value.
     */
    public int getCol() {
        validateIndex(col);
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    /**
     * Opponent's parameter getter.
     * @return The opponent's member value.
     */
    public String getOpponentName() {
        return opponentName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    /**
     * The method validates that the location of the turn's action is valid.
     * @param index The index where the turn's action at.
     */
    private void validateIndex(int index){
        if (index<0 || index>= Board.SIZE)
            throw new InvalidChoiceError(ERROR_MESSAGE);
    }
}
