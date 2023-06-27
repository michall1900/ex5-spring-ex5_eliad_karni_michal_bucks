package hac.classes.forGame;

import hac.repo.board.Board;
import hac.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import hac.classes.customErrors.InvalidChoiceError;
import java.io.Serializable;

@Component
public class UserTurn implements Serializable {
    final static String ERROR_MESSAGE = "Your choice is invalid";
    int row;
    int col;
    String opponentName;

    public UserTurn() {
    }

    public UserTurn(int row, int col, String opponentName) {
        setRow(row);
        setCol(col);
        setOpponentName(opponentName);
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        validateIndex(row);
        this.row = row;
    }

    public int getCol() {
        validateIndex(col);
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }


    private void validateIndex(int index){
        if (index<0 || index>= Board.SIZE)
            throw new InvalidChoiceError(ERROR_MESSAGE);
    }
}
