package chz.common.util.runtime;

import java.io.File;

public class ClasspathBuilder {

	private StringBuilder sb = new StringBuilder();
	
	public ClasspathBuilder(){
	}
	
	public void add(String path){
		path = path.replaceAll("(^;*)|(;*$)", "");
		if( sb.length() > 0 ){
			sb.append(";");
		}
		sb.append(path);
	}
	
	public void add(File file){
		sb.append(";").append(file.getAbsolutePath());
	}
	
	public void addDirectory(File dir){
		File[] files = dir.listFiles();
		for( File file : files ){
			if( file.getName().toLowerCase().endsWith(".jar") ){
				add(file);
			}
		}
	}
	
	public String toString(){
		return sb.toString();
	}
	
	public static void main(String[] args) {
		ClasspathBuilder cpbuilder = new ClasspathBuilder();
		cpbuilder.add(";;;;;;;;;;;;;ddd;ddddd;;;;;");
		cpbuilder.add("ddddfff");
		System.out.println(cpbuilder.toString());
	}
}
