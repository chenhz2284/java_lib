package chz.common.util.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ��ʱ���й�
 */
public class DateUtil {

	public final static int YEAR         = Calendar.YEAR;
	public final static int MONTH        = Calendar.MONTH;
	public final static int DAY_OF_MONTH = Calendar.DAY_OF_MONTH;
	public final static int DATE         = Calendar.DATE;
	public final static int HOUR         = Calendar.HOUR;
	public final static int MINUTE       = Calendar.MINUTE;
	public final static int SECOND       = Calendar.SECOND;
	
	public final static long MILLISECOND_PER_SECOND = (long)1000;
	public final static long MILLISECOND_PER_MINUTE = (long)1000*60;
	public final static long MILLISECOND_PER_HOUR 	= (long)1000*60*60;
	public final static long MILLISECOND_PER_DAY 	= (long)1000*60*60*24;
	public final static long MILLISECOND_PER_MONTH 	= (long)1000*60*60*24*30;
	public final static long MILLISECOND_PER_YEAR 	= (long)1000*60*60*24*365;
	
	/**
	 * �����ڼ���һ��
	 * 
	 * @param date
	 * @param amount 
	 * @return
	 */
	public static Date addDateByDay(Date date, int amount)
	{
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		gc.add(Calendar.DATE, amount);
		return gc.getTime();
	}
	
	/**
	 * ��ʱ����� ĳ��ʱ���
	 * 
	 * @param date
	 * @param type		ʱ�䵥λ������ΪCalendar.DATE
	 * @param amount 
	 * @return
	 */
	public static Date addTime(Date date, int type, int amount){
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		gc.add(type, amount);
		return gc.getTime();
	}
	
	/**
	 * 
	 */
	public static Date cut(Date date, int type){
		long ts = date.getTime();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		int y = cal.get(Calendar.YEAR);
		int m = cal.get(Calendar.MONTH);
		int d = cal.get(Calendar.DAY_OF_MONTH);
		int h = cal.get(Calendar.HOUR_OF_DAY);
		int mi = cal.get(Calendar.MINUTE);
		int s = cal.get(Calendar.SECOND);
		
		if( type==Calendar.YEAR ){
			cal.setTimeInMillis(ts/1000*1000);
			cal.set(y, 0, 1, 0, 0, 0);
			return cal.getTime();
		}
		if( type==Calendar.MONTH ){
			cal.setTimeInMillis(ts/1000*1000);
			cal.set(y, m, 1, 0, 0, 0);
			return cal.getTime();
		}
		if( type==Calendar.DAY_OF_MONTH || type==Calendar.DATE ){
			cal.setTimeInMillis(ts/1000*1000);
			cal.set(y, m, d, 0, 0, 0);
			return cal.getTime();
		}
		if( type==Calendar.HOUR_OF_DAY || type==Calendar.HOUR ){
			cal.setTimeInMillis(ts/1000*1000);
			cal.set(y, m, d, h, 0, 0);
			return cal.getTime();
		}
		if( type==Calendar.MINUTE ){
			cal.setTimeInMillis(ts/1000*1000);
			cal.set(y, m, d, h, mi, 0);
			return cal.getTime();
		}
		if( type==Calendar.SECOND ){
			cal.setTimeInMillis(ts/1000*1000);
			cal.set(y, m, d, h, mi, s);
			return cal.getTime();
		}
		return cal.getTime();
	}
	
	/**
	 * ���ַ���ת��ΪDate
	 * �����ʽ: "2001-12-11 22:59:00"
	 * @throws ParseException 
	 */
	public static Date strToDate(String str) throws ParseException{
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str);
	}
	
	/**
	 * ���ַ���ת��ΪDate
	 * @param str		Ҫת�����ַ���
	 * @param format	ת����ʽ,��"yyyy-MM-dd HH:mm:ss.SSS"
	 */
	public static Date strToDate(String str, String format) throws ParseException{
		if( str==null ){
			return null;
		}
		format = (format==null) ? "yyyy-MM-dd HH:mm:ss.SSS" : format;
		return new SimpleDateFormat(format).parse(str);
	}
	
	/**
	 * ��һDate���͵Ķ���ת��Ϊһ�� "1998-01-01 01:01:01" �������ַ���
	 */
	public static String dateToString(Date date){
		return dateToString(date, "yyyy-MM-dd HH:mm:ss.SSS");
	}
	
	public static String dateToString(long date){
		return dateToString(new Date(date));
	}
	
	/**
	 * ��һDate���͵Ķ���ת��Ϊһ���ַ���
	 * @param format Ĭ��"yyyy-MM-dd HH:mm:ss.SSS"
	 */
	public static String dateToString(Date date, String formate){
		if( date==null ){
			return "";
		}
		formate = (formate==null) ? "yyyy-MM-dd HH:mm:ss.SSS" : formate;
		return new SimpleDateFormat(formate).format(date);
	}
	
	public static String dateToString(long date, String formate){
		return dateToString(new Date(date), formate);
	}
	
	/**
	 * ��һ���ַ�����ʽ�����ڴ�һ�ָ�ʽת��Ϊ��һ�ָ�ʽ
	 * @param format1: ת��ǰ�ĸ�ʽ
	 * @param format2: ת����ĸ�ʽ
	 */
	public static String dateStringToString(String dateString, String format1, String format2) throws ParseException{
		Date date = strToDate(dateString, format1);
		return dateToString(date, format2);
	}
	
	/**
	 * isDateTimeString("2001-01-01 10:03:22")
	 */
	public static boolean isDateTimeString(String str){
		Pattern pattern = Pattern.compile("^([0-9]{4})-([0-9]{2})-([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2})$");
		Matcher matcher = pattern.matcher(str);
		if( matcher.find() ){
			int d = 0;
			d = Integer.parseInt(matcher.group(2));
			if( d<1 || d > 12 ){
				return false;
			}
			d = Integer.parseInt(matcher.group(3));
			if( d<1 || d > 31 ){
				return false;
			}
			d = Integer.parseInt(matcher.group(4));
			if( d > 23 ){
				return false;
			}
			d = Integer.parseInt(matcher.group(5));
			if( d > 59 ){
				return false;
			}
			d = Integer.parseInt(matcher.group(6));
			if( d > 59 ){
				return false;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * isDateString("2001-12-01")
	 */
	public static boolean isDateString(String str){
		Pattern pattern = Pattern.compile("^([0-9]{4})-([0-9]{2})-([0-9]{2})$");
		Matcher matcher = pattern.matcher(str);
		if( matcher.find() ){
			int d = 0;
			d = Integer.parseInt(matcher.group(2));
			if( d<1 || d > 12 ){
				return false;
			}
			d = Integer.parseInt(matcher.group(3));
			if( d<1 || d > 31 ){
				return false;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * isTimeString("23:34:22")
	 */
	public static boolean isTimeString(String str){
		Pattern pattern = Pattern.compile("^([0-9]{2}):([0-9]{2}):([0-9]{2})$");
		Matcher matcher = pattern.matcher(str);
		if( matcher.find() ){
			int d = 0;
			d = Integer.parseInt(matcher.group(1));
			if( d > 23 ){
				return false;
			}
			d = Integer.parseInt(matcher.group(2));
			if( d > 59 ){
				return false;
			}
			d = Integer.parseInt(matcher.group(3));
			if( d > 59 ){
				return false;
			}
			return true;
		}
		return false;
	}
	
	//----------
	
	/**
	 * 
	 */
	public static Calendar createCalendar(Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}
	
	public static int getChineseDayOfWeek(Calendar calendar)
	{
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);	// ���ա���һ...���� -> 1��2...7
		if( dayOfWeek==1 )	// ����
		{
			return 7;
		}
		else	// ����������
		{
			return dayOfWeek - 1;
		}
	}
	
	//----------
	
	/**
	 * @throws ParseException 
	 * 
	 */
	public static void main(String[] args) throws Exception {
		long d1 = System.currentTimeMillis();
		long d2 = DateUtil.strToDate("2011-07-24 04:23:09").getTime();
		System.out.println(d1-d2);
	}
	
}
