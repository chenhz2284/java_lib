package chz.logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class FileLogger extends AbstractLogger {

	private File loggerFile;
	private PrintWriter writer;
	private String lineSeparator = System.getProperty("line.separator");
	
	//----------------
	
	public FileLogger(File file, boolean append, int level) throws FileNotFoundException{
		this.setLevel(level);
		this.loggerFile = file;
		this.writer = new PrintWriter(new FileOutputStream(this.loggerFile, append));
	}
	
	public FileLogger(File file, int level) throws FileNotFoundException{
		this(file, true, level);
	}
	
	//------------------
	
	@Override
	protected void print(String message) {
		this.writer.print(message);
		this.writer.print(lineSeparator);
		this.writer.flush();
	}

	public void dispose() {
		writer.close();
	}

	public long getLength(){
		return this.loggerFile.length();
	}
	
	public File getLoggerFile(){
		return this.loggerFile;
	}

}
