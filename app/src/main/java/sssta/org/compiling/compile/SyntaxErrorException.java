package sssta.org.compiling.compile;

/**
 * Created by lint on 16/12/23.
 */
public class SyntaxErrorException extends Exception {
    public SyntaxErrorException() {
    }

    public SyntaxErrorException(String message) {
        super(message);
    }

}
