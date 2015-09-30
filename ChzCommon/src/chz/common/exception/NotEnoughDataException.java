package chz.common.exception;

/**
 * 数据准备不足够
 */
public class NotEnoughDataException extends RuntimeException {

	public NotEnoughDataException(String message){
		super(message);
	}
}
