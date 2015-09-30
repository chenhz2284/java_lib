package chz.common.exception;

/**
 * 不完整数据异常
 */
public class DuplicateDataException extends RuntimeException {

	public DuplicateDataException(String message){
		super(message);
	}
	
}
