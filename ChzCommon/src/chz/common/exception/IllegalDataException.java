package chz.common.exception;

/**
 * 数据不合法的异常
 */
public class IllegalDataException extends RuntimeException {

	public IllegalDataException(String message){
		super(message);
	}
}
