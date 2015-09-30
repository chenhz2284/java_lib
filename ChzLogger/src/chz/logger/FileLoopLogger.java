package chz.logger;

import java.io.File;
import java.io.FileNotFoundException;

public class FileLoopLogger extends AbstractLogger {

	private File loggerFile;
	private FileLogger logger;
	
	private int backupCount = -1;		// 备份循环命名的日志文件数，-1表示无限个日志文件循环
	private long backupSize = 0;		// 0表示日志文件可以无限大
	
	private int loggerSuffix = 1;	// 将要备份的日志文件名的后缀
	
	//----------------
	
	public FileLoopLogger(File file, boolean append, int level) throws FileNotFoundException{
		this(file, append, level, 3, 1024*1024*10);		// 3个日志文件，每个10M
	}
	
	public FileLoopLogger(File file, int level) throws FileNotFoundException{
		this(file, true, level);
	}
	
	public FileLoopLogger(File file, int level, int loopCount, long loopSize) throws FileNotFoundException{
		this(file, true, level, loopCount, loopSize);
	}
	
	public FileLoopLogger(File file, boolean append, int level, int backupCount, long backupSize) throws FileNotFoundException{
		this.loggerFile = file;
		this.backupCount = backupCount;
		this.backupSize = backupSize;
		initLoggerSuffix();
		this.loggerFile.getAbsoluteFile().getParentFile().mkdirs();
		this.logger = new FileLogger(this.loggerFile, append, level);
		System.out.println("initLoggerSuffix -> backupCount:["+this.backupCount+"], backupSize:["+this.backupSize+"], loggerSuffix:["+this.loggerSuffix+"], ");
	}
	
	private void initLoggerSuffix(){
		for( loggerSuffix=backupCount; loggerSuffix>0; loggerSuffix-- ){
			File tempFile = new File( loggerFile.getParentFile(), loggerFile.getName()+"."+loggerSuffix );
			if( tempFile.exists() ){
				loggerSuffix = (loggerSuffix>=backupCount) ? backupCount : loggerSuffix+1;
				break;
			}
		}
		if( loggerSuffix<1 ){
			loggerSuffix = 1;
		}
	}
	
	//------------------
	
	private FileLogger getLogger() throws FileNotFoundException{
		// 日志文件可以无限大
		if( backupSize<=0 ){
			return this.logger;
		}
		// 日志文件还没有到该大小
		if( this.logger.getLength()<this.backupSize ){
			return this.logger;
		}
		// 日志文件已经到了转换的大小
		changeLogger();
		return this.logger;
	}
	
	private void changeLogger() throws FileNotFoundException{
		System.out.println("changeLogger");
		this.logger.dispose();
		bakLoggerFile();	// 备份日志文件
		createNewLogger();	// 创建新日志对象
	}
	
	/*
	 * 备份日志文件
	 */
	private void bakLoggerFile() throws FileNotFoundException{
		System.out.println("bakLoggerFile");
		if( this.backupCount==-1 ){			
			// 无限个日志文件
			directlyRename();
		} else if( this.backupCount==0 ) {	
			// 不需要文件备份，直接将日志删除
			loggerFile.delete();
		} else if( this.backupCount>0 ) {
			// 日志文件的备份数有限最大为loopCount
			loopRename();
		} else {
			throw new RuntimeException("loopCount=["+backupCount+"]");
		}
	}
	
	/*
	 * 将日志文件改名为bakLoggerFile的文件名
	 */
	private void directlyRename(){
		System.out.println("directlyRename");
		while( true ){
			File bakLoggerFile = new File( loggerFile.getParentFile(), loggerFile.getName()+"."+loggerSuffix );
			if( bakLoggerFile.exists() ){	// 备份文件名已经存在，继续尝试
				loggerSuffix++;
				continue;
			} else {	// 备份文件名不存在，改名
				this.loggerFile.renameTo(bakLoggerFile);
				loggerSuffix++;
				break;
			}
		}
	}
	
	/*
	 * 将日志文件改名为bakLoggerFile的文件名
	 */
	private void loopRename(){
		System.out.println("loopRename");
		File bakLoggerFile = new File( loggerFile.getParentFile(), loggerFile.getName()+"."+loggerSuffix );
		if( !bakLoggerFile.exists() ){	// 不存在，直接改名
			System.out.println("loopRename -> 不存在，直接改名，bakLoggerFile["+bakLoggerFile.getAbsolutePath()+"]");
			this.loggerFile.renameTo(bakLoggerFile);
			loggerSuffix = (loggerSuffix>=backupCount) ? backupCount : loggerSuffix+1;
			System.out.println("loopRename -> loggerSuffix["+loggerSuffix+"]");
		} else {	// 存在
			System.out.println("loopRename -> 存在，bakLoggerFile["+bakLoggerFile.getAbsolutePath()+"]");
			File deleteFile = new File( loggerFile.getParentFile(), loggerFile.getName()+".1" );
			System.out.println("loopRename -> 删除["+deleteFile.getAbsolutePath()+"]");
			deleteFile.delete();	// 将标号为1的日志文件删除
			for( int i=2; i<=loggerSuffix; i++ ){
				File tempFile = new File(loggerFile.getParentFile(), loggerFile.getName()+"."+i);
				File toNameFile = new File(loggerFile.getParentFile(), loggerFile.getName()+"."+(i-1));
				boolean b = tempFile.renameTo(toNameFile);
				System.out.println("loopRename -> 改名，tempFile["+tempFile.getAbsolutePath()+"]，toNameFile["+toNameFile.getAbsolutePath()+"] "+b);
			}
			System.out.println("loopRename -> 改名，loggerFile["+loggerFile.getAbsolutePath()+"]，bakLoggerFile["+bakLoggerFile.getAbsolutePath()+"]");
			this.loggerFile.renameTo(bakLoggerFile);
		}
	}
	
	private void createNewLogger() throws FileNotFoundException{
		this.logger = new FileLogger(this.loggerFile, false, level);
	}
	
	@Override
	protected void print(String message) {
		try {
			getLogger().print(message);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public void dispose() {
		this.logger.dispose();
	}

}
