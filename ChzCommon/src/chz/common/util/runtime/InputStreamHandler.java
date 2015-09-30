package chz.common.util.runtime;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;

import chz.common.util.logger.LogFactory;

public class InputStreamHandler extends Thread {

	private InputStream in;
	
	public InputStreamHandler(InputStream in){
		this.in = in;
	}
	
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			while( (line=br.readLine())!=null ){
				log.info(line);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static Log log = LogFactory.getLog(InputStreamHandler.class);
}
