package chz.common.exception;


/**
 * 根据sql语句在数据库中查询，但找不到数据的时候，报这个异常
 */
public class NotFoundDataException extends RuntimeException {

	public NotFoundDataException(String message){
		super(message);
	}
	
}
