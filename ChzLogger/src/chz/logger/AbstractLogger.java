package chz.logger;

public abstract class AbstractLogger implements Logger {

	protected int level;
	
	//---------------
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	//-----------------
	
	public void debug(String message){
		if( Level.LEVEL_DEBUG>=this.level ){
			print(message);
		}
	}
	
	public void info(String message){
		if( Level.LEVEL_INFO>=this.level ){
			print(message);
		}
	}
	
	public void warn(String message){
		if( Level.LEVEL_WARN>=this.level ){
			print(message);
		}
	}
	
	public void error(String message){
		if( Level.LEVEL_ERROR>=this.level ){
			print(message);
		}
	}
	
	public void fatal(String message){
		if( Level.LEVEL_FATAL>=this.level ){
			print(message);
		}
	}
	
	//-----------------
	
	protected abstract void print(String message);
	
}
