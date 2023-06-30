package hac.classes.customErrors;

/**
 * The exception thrown in case there is attempt to preform an invalid action at the game.
 */
public class InvalidChoiceError extends RuntimeException {
    /**
     * The Ctor of the exception
     * @param message The exception's error message.
     */
    public InvalidChoiceError(String message) {
        super(message);
    }


}
