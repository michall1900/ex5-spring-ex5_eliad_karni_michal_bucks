package hac.classes.customErrors;

/**
 * The exception that thrown when there is a problem to find the user's room and the game's generic exception.
 */
public class DbError extends RuntimeException{

    /**
     * The default exception's error message.
     */
    final static String DEFAULT_ERROR = "Seems like we can't find your room or we faced a problem in the program." +
            " Please try to reconnect another room.";

    /**
     * The Ctor of the exception.
     * @param message The error message.
     */
    public DbError(String message) {
        super(message);
    }

    /**
     * The default Ctor of the DbError exception.
     */
    public DbError(){
        super(DEFAULT_ERROR);
    }
}
