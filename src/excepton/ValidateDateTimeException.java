package excepton;

public class ValidateDateTimeException extends RuntimeException{

    public ValidateDateTimeException(String message) {
        super(message);
    }

    public ValidateDateTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidateDateTimeException(Throwable cause) {
        super(cause);
    }

    public ValidateDateTimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ValidateDateTimeException() {
    }
}
