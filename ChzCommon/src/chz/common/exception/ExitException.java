package chz.common.exception;

/**
 * 为了退出一个函数或者代码块而主动抛出一个异常
 */
public class ExitException extends RuntimeException {

	public ExitException(String message){
		super(message);
	}
	
}
