package Exception;

public class TableDoesNotExistException extends Exception{
    public TableDoesNotExistException(String message){
        super(message);
    }
}
