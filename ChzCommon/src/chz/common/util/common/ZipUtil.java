package chz.common.util.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

public class ZipUtil {

	//************************
	
	/**
	 * @param sInFile: 要压缩的文件或者目录
	 * @param sOutFile: 压缩后形成的文件
	 */
	public static void compressZip(String sInFile, String sOutFile) throws IOException{
		if( sInFile==null || sInFile.trim().equals("") ){
			throw new IllegalArgumentException("sInFile is ["+sInFile+"]");
		}
		if( sOutFile==null || sOutFile.trim().equals("") ){
			throw new IllegalArgumentException("sInFile is ["+sOutFile+"]");
		}
		compressZip( new File(sInFile), new File(sOutFile) );
	}
	
	/**
	 * @param inFile: 要压缩的文件或者目录
	 * @param outFile: 压缩后形成的文件
	 */
	public static void compressZip(File inFile, File outFile) throws IOException 
	{
		FileOutputStream fos = null;
		CheckedOutputStream cos = null;
		ZipOutputStream zos = null;
		try {
			fos = new FileOutputStream(outFile);
			cos = new CheckedOutputStream(fos, new CRC32());
			zos = new ZipOutputStream(cos);
			compressZipFile(inFile, zos, "");
		} finally {
			if( zos!=null ){
				zos.close();
			}
			if( cos!=null ){
				cos.close();
			}
			if( fos!=null ){
				fos.close();
			}
		}
	}
	
	/**
	 * 
	 */
	/**
	 * @param inFile: 要压缩的文件或者目录
	 * @param outFile: 压缩后形成的文件
	 */
	public static void compressDirZip(File inFile, File outFile) throws IOException 
	{
		FileOutputStream fos = null;
		CheckedOutputStream cos = null;
		ZipOutputStream zos = null;
		try {
			File[] files = inFile.listFiles();	// 这一句必须在下面三句的前面
			fos = new FileOutputStream(outFile);
			cos = new CheckedOutputStream(fos, new CRC32());
			zos = new ZipOutputStream(cos);
			for (File afile : files) {
				compressZipFile(afile, zos, "");
			}
		} finally {
			if( zos!=null ){
				zos.close();
			}
			if( cos!=null ){
				cos.close();
			}
			if( fos!=null ){
				fos.close();
			}
		}
	}

	/*
	 * 压缩文件或者目录
	 */
	private static void compressZipFile(File inFile, ZipOutputStream zos, String basePath) throws IOException {
		if (inFile.isDirectory()) {
			// 创建文件夹
			ZipEntry entry = new ZipEntry(basePath + inFile.getName() + "/");
			zos.putNextEntry(entry);
			// 创建文件夹下的文件
			File[] files = inFile.listFiles();
			for (File afile : files) {
				compressZipFile(afile, zos, basePath + inFile.getName() + inFile.separator);
			}
		} else if (inFile.isFile()) {
			// 创建文件
			BufferedInputStream bis = null;
			try {
				bis = new BufferedInputStream(new FileInputStream(inFile));
				ZipEntry entry = new ZipEntry(basePath + inFile.getName());
				zos.putNextEntry(entry);
				int count;
				byte[] data = new byte[102400];
				while ((count = bis.read(data)) != -1) {
					zos.write(data, 0, count);
				}
			} finally {
				bis.close();
			}
		} else {
			throw new IOException("[" + inFile.getPath() + "]不是目录,也不是文件");
		}
	}
	
	//***********************
	
	/**
	 * @param sInFile: 要解压缩的文件
	 * @param sOutDir: 解压后存放的目录
	 */
	public static void decompressZip(String sInFile, String sOutDir) throws IOException {
		if( sInFile==null || sInFile.trim().equals("") ){
			throw new IllegalArgumentException("sInFile is ["+sInFile+"]");
		}
		if( sOutDir==null || sOutDir.trim().equals("") ){
			throw new IllegalArgumentException("sOutDir is ["+sOutDir+"]");
		}
		decompressZip( new File(sInFile), new File(sOutDir) );
	}
	
	/**
	 * @param sInFile: 要解压缩的文件
	 * @param sOutDir: 解压后存放的目录
	 */
	public static void decompressZip(File inFile, File outDir) throws IOException {
		// 创建输出目录
        if( outDir.isDirectory()==false ){
        	if( outDir.mkdirs()==false ){
        		throw new IOException("无法创建目录: "+outDir.getAbsolutePath());
        	}
        }
        // 解压
        ZipFile inZipFile = new ZipFile(inFile);
        Enumeration<ZipEntry> enumeration = inZipFile.getEntries();
		while (enumeration.hasMoreElements()) {
			ZipEntry zipEntry = enumeration.nextElement();
			String zipEntryNameStr = zipEntry.getName();
			File entryFile = new File(outDir, zipEntryNameStr);
			if( zipEntryNameStr.endsWith("/") ){
				// 创建文件夹
				entryFile.mkdirs();
			} else {
				// 创建所在的目录
				entryFile.getParentFile().mkdirs();
				// 写文件
				InputStream inputStream = null;
				OutputStream outputStream = null;
				try {
					inputStream = inZipFile.getInputStream(zipEntry);   
					outputStream = new FileOutputStream( entryFile );      
					byte[] buf = new byte[102400];   
					int len;   
					while ( (len=inputStream.read(buf))>=0) {   
						outputStream.write(buf, 0, len);   
					}
				} finally {
					if(inputStream!=null){
						inputStream.close();
					}
					if(outputStream!=null){
						outputStream.close();
					}
				}  
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		compressDirZip(new File("test"), new File("test/test.zip"));
	}
	
}
