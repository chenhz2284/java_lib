package chz.common.constant;

public class ContentType
{

	public final static String content_type_txt        = "text/plain";
	public final static String content_type_excel_2003 = "application/vnd.ms-excel";
	public final static String content_type_excel_2007 = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	public final static String content_type_png        = "image/x-png";
	public final static String content_type_avi        = "video/avi";
	public final static String content_type_wmv        = "video/x-ms-wmv";
	
	public static String getContentTypeByExt(String ext)
	{
		if( ext==null )
		{
			return null;
		}
		ext = ext.toLowerCase();
		if( "txt".equals(ext) )
		{
			return content_type_txt;
		}
		else if( "avi".equals(ext) )
		{
			return content_type_avi;
		}
		else if( "wmv".equals(ext) )
		{
			return content_type_wmv;
		}
		else
		{
			return null;
		}
	}
	
	
}
