package chz.common.util.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtil
{

	public static byte[] sendGet(URL url) throws MalformedURLException, IOException
	{
		HttpURLConnection conn = null;
		try
		{
			conn = (HttpURLConnection)url.openConnection();
			conn.setDoInput(true);
			InputStream in = conn.getInputStream();
			byte[] buf = StreamUtil.inputStreamToByteArray(in);
			return buf;
		}
		finally
		{
			if( conn!=null )
			{
				conn.disconnect();
			}
		}
	}
	
	public static byte[] sendGet(String url) throws MalformedURLException, IOException
	{
		return sendGet(new URL(url));
	}
	
	public static byte[] sendPost(URL url, byte[] sendContent) throws MalformedURLException, IOException
	{
		HttpURLConnection conn = null;
		try
		{
			conn = (HttpURLConnection)url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			
			OutputStream out = conn.getOutputStream();
			out.write(sendContent);
			
			InputStream in = conn.getInputStream();
			byte[] buf = StreamUtil.inputStreamToByteArray(in);
			return buf;
		}
		finally
		{
			if( conn!=null )
			{
				conn.disconnect();
			}
		}
	}
	
	public static byte[] sendPost(String url, byte[] sendContent) throws MalformedURLException, IOException
	{
		return sendPost(new URL(url), sendContent);
	}

	public static void main(String[] args) throws MalformedURLException, IOException
	{
		byte[] bytes = sendGet("http://localhost/chsp/applet/jar/keepSession.jsp");
		System.out.println("["+new String(bytes)+"]");
	}
}
