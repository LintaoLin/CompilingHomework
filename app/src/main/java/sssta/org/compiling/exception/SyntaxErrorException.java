package sssta.org.compiling.exception;

public class SyntaxErrorException extends Exception {
    public SyntaxErrorException() {
    }

    public SyntaxErrorException(String message) {
        super(message);
    }
}
