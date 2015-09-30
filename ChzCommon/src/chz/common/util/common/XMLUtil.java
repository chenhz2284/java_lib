package chz.common.util.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class XMLUtil {

	public static byte[] xmlToByteArray(Document doc) throws IOException{
		return xmlToByteArray(doc, new OutputFormat("", false, "utf-8"));
	}
	
	public static byte[] xmlToByteArray(Document doc, OutputFormat format) throws IOException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		new XMLWriter(bos, format).write(doc);
		return bos.toByteArray();
	}
	
	public static InputStream xmlToInputStream(Document doc, OutputFormat format) throws IOException{
		return new ByteArrayInputStream(xmlToByteArray(doc, format));
	}
	
	public static void writeXMLToFile(Document doc, OutputFormat format, File file) throws IOException{
		FileOutputStream fos = new FileOutputStream(file);
		new XMLWriter(fos, format).write(doc);
		fos.close();
	}
	
	public static Document readXMLFromFile(File file) throws DocumentException{
		return new SAXReader().read(file);
	}
	
}
