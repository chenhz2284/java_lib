package chz.logger;

public class ConsoleLogger extends AbstractLogger {

	public ConsoleLogger(int level) {
		this.setLevel(level);
	}

	@Override
	protected void print(String message) {
		System.out.println(message);
	}

	public void dispose() {
	}

}
