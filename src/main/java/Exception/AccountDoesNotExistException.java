package Exception;

public class AccountDoesNotExistException extends Exception{
    public AccountDoesNotExistException(String message){
        super(message);
    }
}
