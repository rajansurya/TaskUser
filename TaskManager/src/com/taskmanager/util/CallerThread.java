/**
 * 
 */
package com.taskmanager.util;

/**
 * @author IBM_ADMIN
 *
 */
public class CallerThread implements Runnable{
	Object target;
	private String methodName;
	public CallerThread(Object target) {
		this.target=target;
	}
	
	@Override
	public void run() {
		
		
	}
	public void execute(String methodName){
		try {
			this.methodName=methodName;
			Thread thread=new Thread(this);
			thread.start();
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
