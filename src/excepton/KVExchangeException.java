package excepton;

public class KVExchangeException extends RuntimeException {

    public KVExchangeException() {
    }

    public KVExchangeException(String message) {
        super(message);
    }

    public KVExchangeException(String message, Throwable cause) {
        super(message, cause);
    }

    public KVExchangeException(Throwable cause) {
        super(cause);
    }

    public KVExchangeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
