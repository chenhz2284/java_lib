package chz.logger;

import java.io.File;
import java.io.FileNotFoundException;

public class LoggerFactory {

	public static Logger createConsoleLogger(int level){
		return new ConsoleLogger(level);
	}
	
	public static Logger createFileLogger(File loggerFile, int level) throws FileNotFoundException{
		return new FileLogger(loggerFile, level);
	}
	
	public static Logger createFileLoopLogger(File loggerFile, int level) throws FileNotFoundException{
		return new FileLoopLogger(loggerFile, level, 3, 1024*1024*10);	// 3个日志文件备份，一个文件10M
	}
	
	public static Logger createFileLoopLogger(File loggerFile, int level, int loopCount, long loopSize) throws FileNotFoundException{
		return new FileLoopLogger(loggerFile, level, loopCount, loopSize);
	}
}
