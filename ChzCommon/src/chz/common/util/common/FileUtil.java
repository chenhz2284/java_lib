package chz.common.util.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * 跟文件操作有关
 */
public class FileUtil
{
	
	public static String getExtensionFromFileName(String fileName)
	{
		int index = fileName.lastIndexOf(".");
		if( index==-1 )
		{
			return "";
		}
		else
		{
			return fileName.substring(index+1);
		}
	}

	/*
	 * 实现文件的复制
	 */
	public static void copyFile(File prefile, File copyfile) throws IOException{
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(prefile);
			fos = new FileOutputStream(copyfile);
			byte[] b = new byte[1024];
			int len = 0;
			while(true){
				len = fis.read(b);
				if(len==-1){
					break;
				}
				fos.write(b, 0, len);
			}
		} finally {
			if( fis!=null ){
				fis.close();
			}
			if( fos!=null ){
				fos.close();
			}
		}
	}
	
	/**
	 * 
	 */
	public static byte[] getBytesFromFile(File f) throws IOException{
		InputStream in = null;
		byte[] bytes = null;
		try {
			in = StreamUtil.getInputStreamFromFile(f);
			bytes = StreamUtil.inputStreamToByteArray(in);
		} finally {
			if( in!=null ){
				in.close();
			}
		}
		return bytes;
	}
	
	/**
	 * 删除文件或者目录
	 * @param file：要删除的文件或者目录
	 * @param delete：如果file是目录，删除目录的内容之后是否删除本目录
	 */
	public static void deleteDirectory(File file, boolean delete){
		if( file.isFile() ){
			file.delete();
		} else if( file.isDirectory() ){
			File[] files = file.listFiles();
			for( File afile : files ){
				if( afile.isFile() ){
					afile.delete();
				} else {
					deleteDirectory(afile, false);
					afile.delete();
				}
			}
			if( delete ){
				file.delete();
			}
		}
	}
	
	/**
	 * 
	 */
	public static void writeBytesToFile(File file, byte[] bytes) throws IOException{
		file.getParentFile().mkdirs();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(bytes);
		} finally {
			if( fos!=null ){
				fos.close();
			}
		}
	}
	
	/**
	 * 
	 */
	public static File toAbsoluteFile(File file){
		try {
			return file.getCanonicalFile();
		} catch (IOException e) {
			return file.getAbsoluteFile();
		}
	}
	
	/**
	 * 
	 */
	public static String getCanonicalPath(File file)
	{
		try {
			return file.getCanonicalPath();
		} catch (IOException e) {
			return file.getAbsolutePath();
		}
	}
	
	/**
	 * 从一个文件中读取xml
	 * @param f
	 * @return
	 */
	public static Document readXmlFromFile(File f) throws DocumentException, IOException{
		FileInputStream fis = null;
		Document doc = null; 
		try {
			fis = new FileInputStream(f);
			// 解释xml
			SAXReader reader = new SAXReader();
			doc = reader.read(fis);
		} finally {
			if( fis!=null ){
				fis.close();
			}
		}
		return doc;
	}
	
	/**
	 * 将一个xml写入一个文件里面
	 * @param f:		要写入的文件
	 * @param doc:		要写入的xml
	 * @param encoding:	字符编码
	 */
	public static void writeXmlToFile( File f, Document doc, String encoding, String indent, boolean newLines ) throws IOException{
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(f);
			XMLWriter xmlWriter = new XMLWriter(fos, new OutputFormat(indent, newLines, encoding));
			xmlWriter.write(doc);
		} finally {
			if( fos!=null ){
				fos.close();
			}
		}
	}
	
	/**
	 * 
	 */
	public static void writeXmlToFile( File f, Document doc, String encoding ) throws IOException{
		writeXmlToFile(f, doc, encoding, "", false);
	}
	
	/**
	 * 
	 */
	public static void writeXmlToFile( File f, Document doc ) throws IOException{
		writeXmlToFile(f, doc, "utf-8", "", false);
	}
	
	/**
	 * @throws IOException 
	 * 
	 */
	public static void renameFile(File fromFile, File toFile) throws IOException{
		if( fromFile.exists()==false ){
			throw new IOException("源文件不存在: "+fromFile.getAbsolutePath());
		}
		if( toFile.exists() ){
			throw new IOException("目标文件已经存在: "+toFile.getAbsolutePath());
		}
		boolean b = fromFile.renameTo(toFile);	// renameTo方法不可靠, 有时会改名失败
		if( b==false ){		// 如果失败,则通过文件copy的方式改名
			copyFile( fromFile, toFile );
			fromFile.delete();
		}
	}
	
	/**
	 * 
	 */
	public static void loopSaveToFile(final byte[] bytes, final File saveFile, final int backupCount){
		new Runnable() {
			public void run() {
				try {
					saveFile.getParentFile().mkdirs();
					if( backupCount>0 ) {
						// 日志文件的备份数有限最大为loopCount
						if( saveFile.exists() ){
							loopRename();
						}
						writeBytesToFile(saveFile, bytes);
					} else {
						throw new RuntimeException("backupCount=["+backupCount+"]");
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			// 
			private void loopRename() throws IOException{
				File bakLoggerFile = new File( saveFile.getAbsolutePath()+"."+backupCount );
				if( !bakLoggerFile.exists() ){
					renameFile(saveFile, bakLoggerFile);
				} else {
					new File( saveFile.getAbsolutePath()+".1" ).delete();
					for( int i=2; i<=backupCount; i++ ){
						File tempFile = new File(saveFile.getAbsolutePath()+"."+i);
						File toNameFile = new File(saveFile.getAbsolutePath()+"."+(i-1));
						if( tempFile.exists() ){
							renameFile(tempFile, toNameFile);
						}
					}
					renameFile(saveFile, bakLoggerFile);
				}
			}
		}.run();
	}
	
	/**
	 * 
	 */
	public static String[] splitFileName(String fileName){
		String[] arr = new String[2];
		int index = fileName.lastIndexOf(".");
		if( index==-1 ){
			arr[0] = fileName;
			arr[1] = null;
		} else {
			arr[0] = fileName.substring(0, index);
			arr[1] = fileName.substring(index+1);
		}
		return arr;
	}
	
	/**
	 * 
	 */
	public static File getNotExistsFile(File file){
		if( file.exists()==false ){
			return file;
		}
		String[] fileNameParts = splitFileName(file.getName());
		for( int i=2; ;i++ ){
			String suffix =  (fileNameParts[1]==null) ? "" : ("."+fileNameParts[1]);
			File tFile = new File( file.getParentFile(), fileNameParts[0]+"("+i+")"+suffix );
			if( tFile.exists()==false ){
				return tFile;
			}
		}
	}
	
	public static File newFile(String... paths)
	{
		if( paths.length==0 )
		{
			return new File("").getAbsoluteFile();
		}
		File file = new File(paths[0]);
		for( int i=1; i<paths.length; i++ )
		{
			file = new File(file, paths[i]);
		}
		return file;
	}
	
	public static File newFile(File file, String... paths)
	{
		for( int i=0; i<paths.length; i++ )
		{
			file = new File(file, paths[i]);
		}
		return file;
	}
	
	public static void main(String[] args) {
		System.out.println(getExtensionFromFileName("dddd.properties"));
	}
	
}
