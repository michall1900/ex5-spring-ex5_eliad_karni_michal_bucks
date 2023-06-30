package hac.classes.customErrors;

/**
 * The exception thrown in case there is attempt to reach any of the rooms rest and the game been finished.
 */
public class GameOver extends RuntimeException{
    /**
     * The Ctor of the exception
     * @param message The exception's error message.
     */
    public GameOver(String message) {
        super(message);
    }
}
