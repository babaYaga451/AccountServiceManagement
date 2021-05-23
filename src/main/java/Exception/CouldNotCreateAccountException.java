package Exception;

public class CouldNotCreateAccountException extends IllegalStateException{
    public CouldNotCreateAccountException(String message){
        super(message);
    }
}
