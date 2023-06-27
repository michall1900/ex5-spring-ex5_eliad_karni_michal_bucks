package hac.classes.customErrors;

public class GameOver extends RuntimeException{
    public GameOver(String message) {
        super(message);
    }
}
