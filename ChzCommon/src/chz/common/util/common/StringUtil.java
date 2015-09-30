package chz.common.util.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * 跟字符串有关
 * 
 * @author chenhz
 */
public class StringUtil {
	
	public static String forNull(String str, String forNull){
		return (str==null) ? forNull : str;
	}
	
	public static String forNull(String str){
		return forNull(str, "");
	}
	
	public static String forEmpty(String str, String forEmpty){
		return ("".equals(str)) ? forEmpty : str;
	}
	
	public static String forEmpty(String str){
		return forEmpty(str, "");
	}

	/**
	 * 说明:
	 * 		若 numToString( 123456789, 4 )
	 * 		将 125369 转化为 5369
	 * 
	 * 		若 numToString( 89, 4 )
	 * 		将 89 转化为 0089
	 * 
	 * 		获得最右边的4位,不足的用0来补.
	 * 
	 * @param num
	 * 		要进行转化的数
	 * @param len 
	 * 		要输出的数的位数
	 * @return
	 */
	public static String numToString(String num, int len) {
		if(num.length()>len) {
			return num.substring(num.length()-len);
		}
		if(num.length()<len) {
			int count = len-num.length();
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<count;i++) {
				sb.append("0");
			}
			num = sb.toString() + num;
			return num;
		}
		return num;
	}
	
	/**
	 * 
	 */
	public static String numToString(int num, int len, int radix){
		return numToString(Integer.toString(num, radix), len);
	}
	
	/**
	 * 
	 */
	public static String numToString(Number num, int len) {
		return numToString(num.toString(), len);
	}
	
	/**
	 * 判断是否是空串
	 */
	public static boolean isEmpty(String input)
	{
		boolean isEmpty = true;		//默认是空串
		if( input!=null ) {
			for( int i=0; i<input.length(); i++ ){	//只要有一个字符>' '，那它就是非空的
				char c = input.charAt(i); 
				if( c>' ' ) {
					return false;			
				}
			}
		}
		return isEmpty;
	}
	
	/*
	 * 判断是否是整数
	 */
	public static boolean isInt(String str)
	{
		if( str==null || str.length()==0 )
		{
			return false;
		}
		char c1 = str.charAt(0);
		int i=0;
		if( c1=='-' )
		{
			i++;
		}
		for( ; i<str.length(); i++ )
		{
			char c = str.charAt(i);
			if( c>='0' && c<='9' )
			{
				continue;
			}
			else
			{
				return false;
			}
		}
		return true;
	}
	
	/*
	 * 判断是否是整数
	 */
	public static boolean isUnsignedInt(String str){
		for( int i=0; i<str.length(); i++ ){
			char c = str.charAt(i);
			if( c>='0' && c<='9' ){
				continue;
			} else {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 判断字符串是否为浮点数
	 * @param str
	 * @return
	 */
	public static boolean isFloat(String str) {
		String pattern = "^(-?\\d+)(\\.\\d+)?$";
		Pattern p = Pattern.compile(pattern); 
        Matcher m = p.matcher(str); 
        return m.find();
	}
	
	/*
	 * 判断是否是ip
	 */
	public static boolean isIP(String str){
		String[] arr = split(str, "\\.");
		if(arr.length!=4) {
			return false;
		}
		for(int i=0;i<arr.length;i++) {
			try {
				int num = Integer.parseInt(arr[i]);
				if(num<0||num>255) {
					return false;
				}
			} catch (NumberFormatException e) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * 说明: 将
	 * 			"20080612"
	 * 		这样的字符串变为
	 * 			"2008-06-12"
	 * 		只是加 "-" 而已
	 */
	public static String strToDate(String input)
	{
		String rs = input.substring(0, 4)+"-"+input.substring(4, 6)+"-"+input.substring(6, 8);
		return rs; 
	}
	
	/**
	 * 说明: 将
	 * 			"210338"
	 * 		这样的字符串变为
	 * 			"21:03:38"
	 * 		只是加 ":" 而已
	 */
	public static String strToTime(String input)
	{
		String rs = input.substring(0, 2)+":"+input.substring(2, 4)+":"+input.substring(4, 6);
		return rs; 
	}
	
	/**
	 * 
	 */
	public static String showLimitedString(String input, int len)
	{
		if(input==null){
			return "";
		} else if(input.length() > len) {
			return input.substring(0, len-1)+"..";
		} else {
			return input;
		}
	}
	
	/**
	 * 
	 */
	public static String substring(String str, int start, int end){
		if( str==null ){
			return "";
		}
		if( start>str.length() ){
			return "";
		}
		if( end>str.length() ){
			return str.substring(start);
		}else{
			return str.substring(start, end);
		}
	}
	
	/**
	 * 
	 */
	public static String substring(String str, int start){
		if( str==null ){
			return "";
		}
		if( start>str.length() ){
			return "";
		}
		return str.substring(start);
	}
	
	/**
	 * 说明:
	 * 		将ss里面的所有字符串连接起来
	 * 		连接符就是s
	 * 
	 * @param ss	字符数组，如 ["a","b","c"]
	 * @param s		连接字符串，如 "-"
	 * @return		连接后的字符串,如 "a-b-c"
	 */
	public static String join(String[] ss, String s) {
		StringBuffer sb = new StringBuffer();
		if(ss.length>0) {
			sb.append(ss[0]);
		}
		for(int i=1;i<ss.length;i++) {
			sb.append(s).append(ss[i]);
		}
		return sb.toString();
	}
	
	/**
	 * 说明: 将ss里面的所有字符串连接起来
	 */
	public static String join(String[] ss) {
		return join(ss,",");
	}
	
	/**
	 * 说明:
	 * 		将col里面的所有对象当成字符串连接起来
	 * 		连接符就是s
	 * 
	 * @param iterable	迭代器
	 * @param s		连接字符串，如 "-"
	 * @return		连接后的字符串,如 "a-b-c"
	 */
	public static String join(Iterable iterable, String sep, String wrap) {
		StringBuffer sb = new StringBuffer();
		Iterator iter = iterable.iterator();
		if( iter.hasNext() ){
			sb.append( "" + wrap + iter.next() + wrap );
		}
		while( iter.hasNext() ){
			sb.append( sep + wrap + iter.next() + wrap );
		}
		return sb.toString();
	}
	
	/**
	 * 说明:
	 * 		将col里面的所有对象当成字符串连接起来
	 * 		连接符就是s
	 * 
	 * @param iterable	迭代器
	 * @param sep		连接字符串，如 "-"
	 * @return		连接后的字符串,如 "a-b-c"
	 */
	public static String join(Iterable iterable, String sep) {
		return join( iterable, sep, "" );
	}
	
	/**
	 * 说明: 将col里面的所有对象当成字符串连接起来
	 */
	public static String join(Iterable iterable) {
		return join( iterable, "," );
	}
	
//----------------
	
	public static long JOIN_WRAP_PREPROCESS_FLAG_REPLACE_NULL				= 0x01;
	public static long JOIN_WRAP_PREPROCESS_FLAG_TRIM						= 0x01 << 1;
	public static long JOIN_WRAP_PREPROCESS_FLAG_DOUBLE_SINGLE_QUOTE_MARK	= 0x01 << 2;
	
	public static String joinWrapPreprocess(String str, long flag)
	{
		String result = str;
		if( ByteUtil.isMask(flag, JOIN_WRAP_PREPROCESS_FLAG_REPLACE_NULL) )
		{
			result = StringUtil.forNull(str);
		}
		if( result==null ){
			return result;
		}
		if( ByteUtil.isMask(flag, JOIN_WRAP_PREPROCESS_FLAG_TRIM) )
		{
			result = str.trim();
		}
		if( ByteUtil.isMask(flag, JOIN_WRAP_PREPROCESS_FLAG_DOUBLE_SINGLE_QUOTE_MARK) )
		{
			result = str.replace("'", "''");
		}
		return result;
	}
	
	public static String joinWrap(Iterable<?> iterable, String seperator, String wrapper, long flag){
		StringBuffer sb = new StringBuffer();
		Iterator<?> iter = iterable.iterator();
		if( iter.hasNext() ){
			String str = null;
			str = joinWrapPreprocess(String.valueOf(iter.next()), flag);
			sb.append(wrapper).append(str).append(wrapper);
			while( iter.hasNext() ){
				str = joinWrapPreprocess(String.valueOf(iter.next()), flag);
				sb.append(seperator).append(wrapper).append(str).append(wrapper);
			}
		}
		return sb.toString();
	}
	
	public static String joinWrap(Iterable<?> iterable, String seperator, String wrapper){
		return joinWrap(iterable, seperator, wrapper, 0);
	}
	
	public static String joinWrap(Iterable<?> iterable, String seperator) {
		return joinWrap( iterable, seperator, "", 0 );
	}
	
	public static String joinWrap(Iterable<?> iterable) {
		return joinWrap( iterable, ",", "", 0 );
	}
	
	public static String joinWrap(String[] arr, String seperator, String wrapper, long flag){
		return joinWrap(Arrays.asList(arr), seperator, wrapper, flag);
	}
	
	public static String joinWrap(String[] arr, String seperator, String wrapper){
		return joinWrap(arr, seperator, wrapper, 0);
	}
	
	public static String joinWrap(String[] arr, String seperator){
		return joinWrap(arr, seperator, "", 0);
	}
	
	public static String joinWrap(String[] arr){
		return joinWrap(arr, ",", "", 0);
	}
	
	//----------------
	
	/**
	 * 
	 */
	public static String[] split(String str, String seperator)
	{
		if( str==null )
		{
			return new String[0];
		}
		List<String> result = new ArrayList<String>();
		int start = 0;
		int sepLen = seperator.length();
		while(true)
		{
			int index = str.indexOf(seperator, start);
			if( index!=-1 )
			{
				result.add(str.substring(start, index));
				start = index + sepLen;
			}
			else
			{
				result.add(str.substring(start));
				break;
			}
		}
		return result.toArray(new String[0]);
	}
	
	/**
	 * 将str切分以后,每个字符串trim以后放到一个String[]里面去,空字符串会被过滤
	 */
	public static String[] splitTrim(String str, String seperator)
	{
		String[] arr = split(str, seperator);
		List<String> list = new ArrayList<String>();
		for( String s : arr ){
			s = StringUtil.trim(s);
			if( "".equals(s)==false ){
				list.add(s);
			}
		}
		return list.toArray(new String[0]);
	}
	
	/**
	 * 将str切分以后,放到一个String[]里面去
	 */
	public static String[] splitByRegex(String str, String seperator){
		if( "".equals(str) ){
			return new String[0];
		} else {
			return str.split(seperator);
		}
	}
	
	
	
	/**
	 * 
	 */
	public static String[] fillInArray(String[] ss, String s) {
		for( int i=0; i<ss.length; i++ ){
			ss[i] = s;
		}
		return ss;
	}
	
	/**
	 * 将一Date类型的对象，转换为一个 "1998-01-01 01:01:01" 这样的字符串
	 */
	public static String dateToString(Date date){
		return DateUtil.dateToString(date);
	}
	
	/**
	 * 将一Date类型的对象，转换为一个字符串
	 * @param format 默认"yyyy-MM-dd HH:mm:ss.SSS"
	 */
	public static String dateToString(Date date, String format){
		return DateUtil.dateToString(date, format);
	}
	
	/**
	 * 若输入为字符为null,则输出forNull
	 */
	public static String trim(String str, String forNull) {
		if(str==null) {
			return forNull;
		} else {
			return str.trim();
		}
	}
	
	/**
	 * 若输入为字符为null,则输出""
	 */
	public static String trim(String str) {
		return trim(str, "");
	}
	
	/**
	 * 若输入为字符为null,则输出forNull
	 */
	public static String trim2(String str, String forNull) {
		if(str==null || str.trim().equals("")) {
			return forNull;
		} else {
			return str.trim();
		}
	}
	
	/**
	 * 若输入为字符为null,则输出forNull
	 */
	public static String trim2(String str) {
		return trim2(str, "");
	}
	
	/**
	 * 若输入为字符为null,则输出forNull
	 */
	public static String escapeTrim(String str, String forNull) {
		if(str==null) {
			return forNull;
		} else {
			return escape(str);
		}
	}
	
	/**
	 * 
	 */
	public static String escapeTrim2(String str, String forNull){
		if( str==null || str.trim().equals("") ){
			return forNull;
		} else {
			return escape(str);
		}
	}
	
	/**
	 * 若输入为字符为null,则输出""
	 */
	public static String escapeTrim(String str) {
		return escapeTrim(str, "");
	}
	
	/**
	 * 若输入为字符为null,则输出forNull
	 */
	public static String sqlTrim(String str, String forNull) {
		if(str==null) {
			return forNull;
		} else {
			return str.trim().replace("\'", "\'\'");
		}
	}
	
	/**
	 * 若输入为字符为null,则输出""
	 */
	public static String sqlTrim(String str) {
		return sqlTrim(str, "");
	}
	
	/**
	 * 将一个字符转化为可以在代码里书写的字面量,主要对下面几个特殊字符进行转换
	 * \, /, \r, \n, \b, \f, \t, \", \'
	 */
	public static String escape(String s){
		if(s==null){
			return null;
		}
        StringBuffer sb = new StringBuffer();
        escape(s, sb);
        return sb.toString();
    }
	
	/**
	 * 将一个字符转化为可以在代码里书写的字面量,主要对下面几个特殊字符进行转换
	 * \, /, \r, \n, \b, \f, \t, \", \'
	 */
	public static void escape(String s, StringBuffer sb) {
		for(int i=0;i<s.length();i++){
			char ch=s.charAt(i);
			switch(ch){
			case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '/':
				sb.append("\\/");
				break;
			default:
				if((ch>='\u0000' && ch<='\u001F') || (ch>='\u007F' && ch<='\u009F') || (ch>='\u2000' && ch<='\u20FF')){
					String ss=Integer.toHexString(ch);
					sb.append("\\u");
					for(int k=0;k<4-ss.length();k++){
						sb.append('0');
					}
					sb.append(ss.toUpperCase());
				}
				else{
					sb.append(ch);
				}
			}
		}
	}
	
	/**
	 * 如果preString为ifStr,则返回toString,否则返回原来的preString
	 */
	public static String instead(String preString, String ifStr, String toString){
		if(preString==null){
			if(ifStr==null){
				return toString;
			}else{
				return "null";
			}
		}else{
			if(preString.equals(ifStr)){
				return toString;
			}else{
				return preString;
			}
		}
	}
	
	/**
	 * 处理中文字符
	 * @param str
	 * @return
	 * @author lwh
	 */
	public static String codeToString( String str ) 
	{//处理中文字符
		String s = str;
		try {
			byte tempB[] = s.getBytes( "8859_1" );
			s =  new String( tempB );
			return s;
		} catch ( Exception ex ) {
			return s;
		}
	}
	
	/**
	 * 
	 * @param str	文件全名，如 "/template/hello.exe" 或者 "c:/template.hello.exe"
	 * @return		去掉路径的文件名，如："hello.exe"
	 */
	public static String getFileName(String str){
		str = str.replace("\\", "/");
		String[] arr = str.split("/");
		return arr[arr.length-1];
		
	}
	
	/**
	 *  
	 * @param firstStr
	 * @param replaceStr
	 * @param targetStr
	 * @return
	 */
	public static String replaceFirstChar( String firstStr, String replaceStr, String targetStr ) {
		if ( targetStr.startsWith( firstStr )) {
			targetStr = targetStr.replaceFirst( firstStr, replaceStr);
		}
		return targetStr;
	}
	
	/**
	 * 将字节流转换为base64的编码
	 */
	public static String bytesToBase64( byte[] b ){
		return ByteUtil.bytesToBase64(b);
	}
	
	/**
	 * 将base64的编码转换为字节流
	 */
	public static byte[] base64ToBytes( String s ) throws IOException {
		return ByteUtil.base64ToBytes(s);
	}
	
	/**
	 * 解析url?后的参数,如 key1=value1&key2=value2 这样的字符串
	 */
	public static Map<String, String> getUrlParameters( String param ) throws UnsupportedEncodingException {
		Map<String, String> paramMap = new TreeMap<String,String>();
		if( param==null ){
			return paramMap;
		}
		String[] arr = param.split("&");
		for( int i=0; i<arr.length; i++ ){
			String s = arr[i];
			if( !s.equals("") ){
				int pos = s.indexOf("=");
				if( pos==-1 ){	//找不到
					String key = s;
					String value = null;
					paramMap.put(key,value);
				} else {		//找到
					String key = substring(s,0,pos);
					String value = substring(s,pos+1,s.length());
					paramMap.put(key,URLDecoder.decode(value,"utf-8"));
				}
			}
		}
		return paramMap;
	}
	
	/**
	 * 
	 */
	public static String generateSessionId(){
		byte[] chars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		Random ran = new Random();
		byte[] bytes = new byte[32];
		for( int i=0; i<32; i++ ){
			bytes[i] = chars[ Math.abs(ran.nextInt())%chars.length ];
		}
		return new String(bytes);
	}
	
	/**
	 * 
	 */
	public static String generateRandom(int len){
		byte[] chars = { 
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
				'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
				'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 
				'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
				'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
		};
		Random ran = new Random();
		byte[] bytes = new byte[len];
		for( int i=0; i<len; i++ ){
			bytes[i] = chars[ Math.abs(ran.nextInt())%chars.length ];
		}
		return new String(bytes);
	}
	
	/**
	 * 
	 */
	public static String generateUUID( int len ){
		return UUID.randomUUID()+"-"+generateRandom(len);
	}
	
	/**
	 * 
	 */
	public static String generateUUID2(){
		return generateUUID2(0);
	}
	
	/**
	 * 
	 */
	public static String generateUUID2( int len ){
		return UUID.randomUUID().toString().replace("-", "")+generateRandom(len);
	}
	
	/**
	 * 
	 */
	public static String generateUUID3(){
		return generateUUID3(11);
	}
	
	/**
	 * len可以使用11,这样能生成一个长度为32的字符串
	 */
	public static String generateUUID3(int len){
		return new SimpleDateFormat("yyMMddHHmmss").format(new Date())+
				numToString( Long.toHexString(System.nanoTime()), 9)+
				generateRandom(len);
	}
	
	/**
	 * 作用类似于数据库的like条件
	 * 如: S.like("1111abc222", "%abc%") -> true 
	 */
	public static boolean like( String input, String sPattern ){
		Pattern pattern = Pattern.compile(sPattern.replace("%", "(.*)").replace("_", "(.)"));
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}
	
	/**
	 * 默认是将所有的字符处理成unicode编码
	 */
	public static String stringToUnicode(String str){
		return stringToUnicode(str, true);
	}
	
	/**
	 * @param handleAscii: true->将ascii字符处理为unicode编码，false->不处理
	 */
	public static String stringToUnicode(String str, boolean handleAscii){
		StringBuilder sb = new StringBuilder();
		char[] chars = str.toCharArray();
		for( int i=0; i<chars.length; i++ ){
			int c = chars[i] & 0x0000ffff;
			if( c>0x00ff ){	// 不是acsii字符
				String s = Integer.toHexString(c).toUpperCase();
				sb.append("\\u").append(String.format("%4s", s).replace(" ", "0"));
			} else {
				if(handleAscii){
					String s = Integer.toHexString(c).toUpperCase();
					sb.append("\\u").append(String.format("%4s", s).replace(" ", "0"));
				} else {
					sb.append((char)c);
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * 
	 */
	public static String unicodeToString(String str){
		StringBuilder sb = new StringBuilder();
		char[] chars = str.toCharArray();
		for( int i=0; i<chars.length;  ){
			char c1 = chars[i];
			if( c1=='\\' && chars.length-i>=6 && chars[i+1]=='u' ){
				try {
					sb.append( (char)Integer.parseInt(str.substring(i+2, i+6), 16) );
					i += 6;
				} catch (NumberFormatException e) {
					sb.append(c1);
					i++;
				}
			} else {
				sb.append(c1);
				i++;
			}
		}
		return sb.toString();
	}
	
//	/**
//	 * 
//	 */
//	public static String stringToUnicode(String preStr){
//		StringBuffer result = new StringBuffer();
//		for (int i = 0; i < preStr.length(); i++) {
//			String s = Integer.toHexString(preStr.charAt(i)).toUpperCase();
//			result.append("\\u").append(String.format("%4s", s).replace(" ", "0"));
//		}
//		return result.toString();
//	}
//	
//	/**
//	 * 
//	 */
//	public static String unicodeToString(String s){
//		String[] arr = s.split("\\\\u");
//		StringBuffer result = new StringBuffer();
//		for( int i=0; i<arr.length; i++ ){
//			if( !"".equals(arr[i]) ){
//				result.append( (char)Integer.parseInt(arr[i], 16) );
//			}
//		}
//		return result.toString();
//	}
	
	/**
	 * 
	 * @param ip		要检测的IP
	 * @param format	要检测的格式,如: 10.*;172.*
	 * @return
	 */
	public static boolean isIpMatchFormat(String ip, String format){
		if( ip==null || format==null ){
			return false;
		}
		String[] arr = format.split(";");
		for( String s : arr ){
			s = s.trim().replace("*", "");
			if( ip.startsWith(s) ){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取带省略号的字符串。
	 * 		比如：S.getSkipSubString("abcdefghijk", 5) -> 返回字符串：abcde...
	 * 			 S.getSkipSubString("abcde", 5)       -> 返回字符串：abcde
	 * @param str
	 * @param len
	 * @return
	 */
	public static String getSkipSubString(String str, int len) {
		String subStr;
		
		if (str == null) {
			subStr = "";
		} else if (str.length() <= len) {
			subStr = str;
		} else {
			subStr = str.substring(0, len) + "..";
		}
		
		return subStr;
	}
	
	/**
	 * 将第一个字母替换成大写
	 * @param str
	 * @return
	 */
	public static String upperFirst(String str) {
	    return str.substring(0, 1).toUpperCase() +str.substring(1);
	}
	
	/**
	 * 将第一个字母替换成小写
	 * @param str
	 * @return
	 */
	public static String lowerFirst(String str) {
	    return str.substring(0, 1).toLowerCase() +str.substring(1);
	}
	
	//*************
	
	/**
	 * 
	 */
	public static String byteToAscii(byte[] bytes){
		char[] chars = new char[]{'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		StringBuilder sb = new StringBuilder(bytes.length*2);
		for( int i=0; i<bytes.length; i++ ){
			byte b = bytes[i];
			sb.append( chars[b>>4 & 0xf] );
			sb.append( chars[b & 0xf] );
		}
		return sb.toString();
	}
	
	/**
	 * 
	 */
	public static String byteToPrintString(byte[] bytes){
		char[] chars = new char[]{'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		StringBuilder sb = new StringBuilder(bytes.length*2);
		for( int i=0; i<bytes.length; i++ ){
			byte b = bytes[i];
			sb.append( chars[b>>4 & 0xf] );
			sb.append( chars[b & 0xf] );
			sb.append( " " );
		}
		return sb.toString();
	}
	
	/**
	 * 
	 */
	public static byte[] asciiToBytes(String ascii){
		char[] chars = ascii.toUpperCase().toCharArray();
		if( chars.length%2!=0 ){
			throw new RuntimeException("字符的长度必须可以被2整除, length:["+chars.length+"]");
		}
		byte[] result = new byte[chars.length/2];
		for( int i=0; i<chars.length; i+=2  ){
			byte b = 0;
			char c1 = chars[i];
			char c2 = chars[i+1];
			if( c1<'0' || c1 >'F' ){
				throw new RuntimeException("字符必须为'0-F'");
			}
			if( c2<'0' || c2 >'F' ){
				throw new RuntimeException("字符必须为'0-F'");
			}
			b = (byte)(b | ( Integer.parseInt(""+c1, 16) << 4 ));
			b = (byte)(b | ( Integer.parseInt(""+c2, 16) & 0xf ));
			result[i/2] = b;
		}
		return result;
	}
	
	//***************
	
	public static String urlEncode(String s){
		try {
			return URLEncoder.encode(s, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String urlDecode(String s){
		try {
			return URLDecoder.decode(s, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	//-----------
	
	public static String myUrlEncode(String s){
		try {
			return URLEncoder.encode(s, "utf-8").replace("-", "%2D").replace("%", "-");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String myUrlDecode(String s){
		try {
			return URLDecoder.decode(s.replace("-", "%"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	/* **********************************************************
	 * 
	 * 这里是公共函数，写完后记得加函数说明，这样其它人也可以使用了，谢谢
	 * 
	 * **********************************************************
	 */
	
	/**
	 * 
	 */
	public static void main(String[] args) throws ParseException, UnsupportedEncodingException {
		System.out.println((int)'\t');
	}
	
}
