package exceptions;


public class DatabaseException extends Exception{
    String message;

    public DatabaseException(String message) {
        this.message = message;
    }
    @Override
    public String getMessage() {
        return message;
    }
}
