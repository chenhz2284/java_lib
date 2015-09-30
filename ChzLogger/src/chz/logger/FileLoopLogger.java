package chz.logger;

import java.io.File;
import java.io.FileNotFoundException;

public class FileLoopLogger extends AbstractLogger {

	private File loggerFile;
	private FileLogger logger;
	
	private int backupCount = -1;		// ����ѭ����������־�ļ�����-1��ʾ���޸���־�ļ�ѭ��
	private long backupSize = 0;		// 0��ʾ��־�ļ��������޴�
	
	private int loggerSuffix = 1;	// ��Ҫ���ݵ���־�ļ����ĺ�׺
	
	//----------------
	
	public FileLoopLogger(File file, boolean append, int level) throws FileNotFoundException{
		this(file, append, level, 3, 1024*1024*10);		// 3����־�ļ���ÿ��10M
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
		// ��־�ļ��������޴�
		if( backupSize<=0 ){
			return this.logger;
		}
		// ��־�ļ���û�е��ô�С
		if( this.logger.getLength()<this.backupSize ){
			return this.logger;
		}
		// ��־�ļ��Ѿ�����ת���Ĵ�С
		changeLogger();
		return this.logger;
	}
	
	private void changeLogger() throws FileNotFoundException{
		System.out.println("changeLogger");
		this.logger.dispose();
		bakLoggerFile();	// ������־�ļ�
		createNewLogger();	// ��������־����
	}
	
	/*
	 * ������־�ļ�
	 */
	private void bakLoggerFile() throws FileNotFoundException{
		System.out.println("bakLoggerFile");
		if( this.backupCount==-1 ){			
			// ���޸���־�ļ�
			directlyRename();
		} else if( this.backupCount==0 ) {	
			// ����Ҫ�ļ����ݣ�ֱ�ӽ���־ɾ��
			loggerFile.delete();
		} else if( this.backupCount>0 ) {
			// ��־�ļ��ı������������ΪloopCount
			loopRename();
		} else {
			throw new RuntimeException("loopCount=["+backupCount+"]");
		}
	}
	
	/*
	 * ����־�ļ�����ΪbakLoggerFile���ļ���
	 */
	private void directlyRename(){
		System.out.println("directlyRename");
		while( true ){
			File bakLoggerFile = new File( loggerFile.getParentFile(), loggerFile.getName()+"."+loggerSuffix );
			if( bakLoggerFile.exists() ){	// �����ļ����Ѿ����ڣ���������
				loggerSuffix++;
				continue;
			} else {	// �����ļ��������ڣ�����
				this.loggerFile.renameTo(bakLoggerFile);
				loggerSuffix++;
				break;
			}
		}
	}
	
	/*
	 * ����־�ļ�����ΪbakLoggerFile���ļ���
	 */
	private void loopRename(){
		System.out.println("loopRename");
		File bakLoggerFile = new File( loggerFile.getParentFile(), loggerFile.getName()+"."+loggerSuffix );
		if( !bakLoggerFile.exists() ){	// �����ڣ�ֱ�Ӹ���
			System.out.println("loopRename -> �����ڣ�ֱ�Ӹ�����bakLoggerFile["+bakLoggerFile.getAbsolutePath()+"]");
			this.loggerFile.renameTo(bakLoggerFile);
			loggerSuffix = (loggerSuffix>=backupCount) ? backupCount : loggerSuffix+1;
			System.out.println("loopRename -> loggerSuffix["+loggerSuffix+"]");
		} else {	// ����
			System.out.println("loopRename -> ���ڣ�bakLoggerFile["+bakLoggerFile.getAbsolutePath()+"]");
			File deleteFile = new File( loggerFile.getParentFile(), loggerFile.getName()+".1" );
			System.out.println("loopRename -> ɾ��["+deleteFile.getAbsolutePath()+"]");
			deleteFile.delete();	// �����Ϊ1����־�ļ�ɾ��
			for( int i=2; i<=loggerSuffix; i++ ){
				File tempFile = new File(loggerFile.getParentFile(), loggerFile.getName()+"."+i);
				File toNameFile = new File(loggerFile.getParentFile(), loggerFile.getName()+"."+(i-1));
				boolean b = tempFile.renameTo(toNameFile);
				System.out.println("loopRename -> ������tempFile["+tempFile.getAbsolutePath()+"]��toNameFile["+toNameFile.getAbsolutePath()+"] "+b);
			}
			System.out.println("loopRename -> ������loggerFile["+loggerFile.getAbsolutePath()+"]��bakLoggerFile["+bakLoggerFile.getAbsolutePath()+"]");
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
