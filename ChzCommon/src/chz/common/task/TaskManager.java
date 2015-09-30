package chz.common.task;

import java.util.ArrayList;
import java.util.List;

import chz.common.interfaces.Closeable;
import chz.common.interfaces.Startable;
import chz.common.interfaces.Task;

public class TaskManager {

	private TaskManager(){
	}
	
	//*****************
	
	private static TaskManager instance;
	
	public synchronized static TaskManager getInstance(){
		if( instance==null ){
			instance = new TaskManager();
		}
		return instance;
	}
	
	//*****************
	
	private List<Task> taskList = new ArrayList<Task>();
	
	public void addTask(Task task){
		taskList.add(task);
	}
	
	public void startTasks(){
		int startNum = 0;
		for( int i=0; i<taskList.size(); i++ ){
			Task task = taskList.get(i);
			if( task instanceof Startable ){
				((Startable)task).start();
				startNum++;
			}
		}
	}
	
	public void closeTasks(){
		int closeNum = 0;
		for( int i=0; i<taskList.size(); i++ ){
			Task task = taskList.get(i);
			if( task instanceof Closeable ){
				((Closeable)task).close();
				closeNum++;
			}
		}
	}
	
	//******************
	
}
