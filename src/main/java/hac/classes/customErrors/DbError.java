package hac.classes.customErrors;

import hac.controllers.Default;

public class DbError extends RuntimeException{

    final static String DEFAULT_ERROR = "We can't find your room or there is a problem in the program. Please try to reconnect to another room.";
    public DbError(String message) {
        super(message);
    }

    public DbError(){
        super(DEFAULT_ERROR);
    }
}
