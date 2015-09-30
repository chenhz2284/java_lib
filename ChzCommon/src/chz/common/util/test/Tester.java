package chz.common.util.test;

public class Tester {

	public static void testRuntime(Runnable runnable){
		testRuntime(runnable, 1000000);
	}
	
	public static void testRuntime(Runnable runnable, double d){
		long d1 = System.nanoTime();
		runnable.run();
		long d2 = System.nanoTime();
		System.out.println("Ö´ÐÐÊ±¼ä["+(d2-d1)/d+"]");
	}
}
