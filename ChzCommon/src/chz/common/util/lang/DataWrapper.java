package chz.common.util.lang;

public class DataWrapper<T>
{

	T data = null;
	
	public DataWrapper()
	{
	}
	
	public DataWrapper(T data)
	{
		this.data = data;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
	
	public boolean getBoolean()
	{
		return (Boolean)this.data;
	}
	
	public boolean isFalse()
	{
		return getBoolean()==false;
	}
	
	public boolean isTrue()
	{
		return getBoolean()==true;
	}
	
}
