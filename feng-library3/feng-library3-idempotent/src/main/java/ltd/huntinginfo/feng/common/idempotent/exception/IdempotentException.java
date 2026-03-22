package ltd.huntinginfo.feng.common.idempotent.exception;

/*
 *
 * @Description: IdempotentException
 * @author edison
 * @date 2022/5/23
 */
public class IdempotentException extends RuntimeException {

	public IdempotentException() {
		super();
	}

	public IdempotentException(String message) {
		super(message);
	}

	public IdempotentException(String message, Throwable cause) {
		super(message, cause);
	}

	public IdempotentException(Throwable cause) {
		super(cause);
	}

	protected IdempotentException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
