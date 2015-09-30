package chz.common.util.runtime;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;

import chz.common.util.logger.LogFactory;

public class ErrorStreamHandler extends Thread {

	private InputStream in;
	
	public ErrorStreamHandler(InputStream in){
		this.in = in;
	}
	
	@Override
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			while( (line=br.readLine())!=null ){
				log.error(line);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static Log log = LogFactory.getLog(ErrorStreamHandler.class);
}
