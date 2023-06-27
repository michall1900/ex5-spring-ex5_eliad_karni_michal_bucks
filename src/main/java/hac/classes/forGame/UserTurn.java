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
    String row;
    String col;
    String opponentName;

    public UserTurn() {
    }

    public UserTurn(String row, String col, String opponentName) {
        setRow(row);
        setCol(col);
        setOpponentName(opponentName);
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        validateIndex(row);
        this.row = row;
    }

    public String getCol() {
        validateIndex(col);
        return col;
    }

    public void setCol(String col) {
        this.col = col;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }


    private void validateIndex(String index){

        if (Integer.parseInt(index)<0 || Integer.parseInt(index)>= Board.SIZE)
            throw new InvalidChoiceError(ERROR_MESSAGE);
    }
}
