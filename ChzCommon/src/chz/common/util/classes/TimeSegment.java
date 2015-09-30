package chz.common.util.classes;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chz.common.util.common.DateUtil;
import chz.common.util.common.StringUtil;

/*
 * ����ʱ��ε��࣬���ԱȽ�һ��ʱ���ǲ�����һ��ʱ�����
 */
public class TimeSegment
{
	public static final String TYPE_PER_DAY       = "ÿ��";
	public static final String TYPE_PER_WEEK      = "ÿ��";
	public static final String TYPE_PER_MONTH     = "ÿ��";
	public static final String TYPE_CONCRETE_TIME = "����ʱ��";
	
	// ÿ��(HH:mm ~ HH:mm)                           -> ÿ��(08:00 ~ 09:00)
	public static final Pattern PATTERN_PER_DAY       = Pattern.compile("^\\s*"+TYPE_PER_DAY + "\\s*\\(\\s*(\\d{2}:\\d{2})\\s*~\\s*(\\d{2}:\\d{2})\\s*\\)\\s*$");
	
	// ÿ��(DayOfWeek_HH:mm ~ DayOfWeek_HH:mm)       -> ÿ��(1_08:00 ~ 2_09:00)
	public static final Pattern PATTERN_PER_WEEK      = Pattern.compile("^\\s*"+TYPE_PER_WEEK + "\\s*\\(\\s*(\\d_\\d{2}:\\d{2})\\s*~\\s*(\\d_\\d{2}:\\d{2})\\s*\\)\\s*$");
	
	// ÿ��(DayOfMonth_HH:mm ~ DayOfMonth_HH:mm)     -> ÿ��(01_08:00 ~ 02_09:00)
	public static final Pattern PATTERN_PER_MONTH     = Pattern.compile("^\\s*"+TYPE_PER_MONTH + "\\s*\\(\\s*(\\d{2}_\\d{2}:\\d{2})\\s*~\\s*(\\d{2}_\\d{2}:\\d{2})\\s*\\)\\s*$");
	
	// ����ʱ��(yyyy-MM-dd HH:mm ~ yyyy-MM-dd HH:mm) -> ����ʱ��(2012-12-21 08:00 ~ 2012-12-24 09:00)
	public static final Pattern PATTERN_CONCRETE_TIME = Pattern.compile("^\\s*"+TYPE_CONCRETE_TIME + "\\s*\\(\\s*(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2})\\s*~\\s*(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2})\\s*\\)\\s*$");

	//--------------
	
	private String type;
	private String sTime1;
	private String sTime2;
	
	private TimeSegment(String type, String sTime1, String sTime2)
	{
		this.type = type;
		this.sTime1 = sTime1;
		this.sTime2 = sTime2;
	}
	
	public static TimeSegment parse(String sTime)
	{
		Matcher matcher = null;
		// ÿ��
		matcher = PATTERN_PER_DAY.matcher(sTime);
		if( matcher.find() )
		{
			String time1 = matcher.group(1);
			String time2 = matcher.group(2);
			return new TimeSegment(TYPE_PER_DAY, time1, time2);
		}
		// ÿ��
		matcher = PATTERN_PER_WEEK.matcher(sTime);
		if( matcher.find() )
		{
			String time1 = matcher.group(1);
			String time2 = matcher.group(2);
			return new TimeSegment(TYPE_PER_WEEK, time1, time2);
		}
		// ÿ��
		matcher = PATTERN_PER_MONTH.matcher(sTime);
		if( matcher.find() )
		{
			String time1 = matcher.group(1);
			String time2 = matcher.group(2);
			return new TimeSegment(TYPE_PER_MONTH, time1, time2);
		}
		// ����ʱ��
		matcher = PATTERN_CONCRETE_TIME.matcher(sTime);
		if( matcher.find() )
		{
			String time1 = matcher.group(1);
			String time2 = matcher.group(2);
			return new TimeSegment(TYPE_CONCRETE_TIME, time1, time2);
		}
		return null;
	}
	
	public boolean between(Date date)
	{
		String timeStr = "";
		if( TYPE_PER_DAY.equals(type) )
		{
			timeStr = DateUtil.dateToString(date, "HH:mm");
		}
		else if( TYPE_PER_WEEK.equals(type) )
		{
			Calendar calendar = DateUtil.createCalendar(date);
			int dayOfWeek = DateUtil.getChineseDayOfWeek(calendar);
			timeStr = dayOfWeek + "_" + DateUtil.dateToString(date, "HH:mm");
		}
		else if( TYPE_PER_MONTH.equals(type) )
		{
			Calendar calendar = DateUtil.createCalendar(date);
			int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
			timeStr = StringUtil.numToString(dayOfMonth, 2) + "_" + DateUtil.dateToString(date, "HH:mm");
		}
		else if( TYPE_CONCRETE_TIME.equals(type) )
		{
			timeStr = DateUtil.dateToString(date, "yyyy-MM-dd HH:mm");
		}
		//
		if( timeStr.compareTo(this.sTime1)>=0 && timeStr.compareTo(this.sTime2)<=0 )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public String toString()
	{
		return this.type+"("+sTime1+" ~ "+sTime2+")";
	}
	
	//---------
	
	public static void main(String[] args) throws ParseException
	{
		String s1 = " ÿ��(08:00 ~ 09:00) ";
		String s2 = " ÿ��(1_08:00 ~ 5_09:00) ";
		String s3 = " ÿ��(01_08:00 ~ 21_09:00) ";
		String s4 = " ����ʱ��(2012-12-21 08:00 ~ 2012-12-24 09:00) ";
		
		TimeSegment timeSegment = TimeSegment.parse(s4);
		System.out.println(timeSegment);
		
		Date date = DateUtil.strToDate("2012-12-24 08:01", "yyyy-MM-dd HH:mm");
		System.out.println(timeSegment.between(date));
		
	}
	
}





