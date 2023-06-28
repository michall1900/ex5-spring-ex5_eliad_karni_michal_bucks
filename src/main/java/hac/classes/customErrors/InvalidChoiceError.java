package hac.classes.customErrors;

public class InvalidChoiceError extends RuntimeException {
    public InvalidChoiceError(String message) {
        super(message);
    }


}
