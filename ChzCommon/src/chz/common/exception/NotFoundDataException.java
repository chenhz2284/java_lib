package chz.common.exception;


/**
 * ����sql��������ݿ��в�ѯ�����Ҳ������ݵ�ʱ�򣬱�����쳣
 */
public class NotFoundDataException extends RuntimeException {

	public NotFoundDataException(String message){
		super(message);
	}
	
}
