package chz.logger;


public class FolderLogger // extends AbstractLogger
{
//	private File folder;
//	private File logFiler;
//	private FileLogger logger;
//	private long maxLogSize;
//	
//	public FolderLogger(File folder, String logFileName, boolean append, int level, long maxLogSize) throws FileNotFoundException{
//		this.folder = folder;
//		this.logFiler = new File(this.folder, new File(logFileName).getName());
//		this.logger = new FileLogger(this.logFiler, true, level);
//		this.maxLogSize = maxLogSize;
//	}
//
//	private FileLogger getLogger() throws FileNotFoundException{
//		// 日志文件可以无限大
//		if( maxLogSize<=0 ){
//			return this.logger;
//		}
//		// 日志文件还没有到该大小
//		if( this.logger.getLength()<this.maxLogSize ){
//			return this.logger;
//		}
//		// 日志文件已经到了转换的大小
//		changeLogger();
//		return this.logger;
//	}
//	
//	private void changeLogger() throws FileNotFoundException{
//		System.out.println("changeLogger");
//		this.logger.dispose();
//		bakLoggerFile();	// 备份日志文件
//		createNewLogger();	// 创建新日志对象
//	}
//	
//	@Override
//	protected void print(String message) {
//		try {
//			getLogger().print(message);
//		} catch (FileNotFoundException e) {
//			throw new RuntimeException(e);
//		}
//	}
//
//	public void dispose() {
//		this.logger.dispose();
//	}

}
