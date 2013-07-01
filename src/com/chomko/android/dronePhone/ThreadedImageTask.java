package com.chomko.android.dronePhone;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.util.Log;

/**
 * ThreadedImageTask
 * @author Aaron Siegel
 */
class ThreadedImageTask extends Thread{
	
	private String serverName;
	private int port;
	private boolean running;
	private ConcurrentLinkedQueue<byte[]> queue;
	private Object mPauseLock;
	private boolean mPaused;
	private boolean mFinished;
	
	
	/**
	 * ThreadedImageTask creates a queue of images and sends them over a socket when available.
	 * @param serverName - String of remote computer receiving images.
	 * @param port - integer port number of remote computer service.
	 */
	public ThreadedImageTask(String serverName, int port){
		this.serverName = serverName;
		this.port = port;
		queue = new ConcurrentLinkedQueue<byte[]>();
	}
	
	public void run(){
		running = true;
		while(running){
			// do everything in here
			try{
				if(queue.size() > 0){
					Log.w("queue_size", Integer.toString(queue.size()));
					
					// poll data
					byte[] data = new byte[0];
					
					while(queue.size() > 0){
						data = queue.poll();
					}
					
					// connect to socket
					Socket client = new Socket(serverName, port);
					client.setSoTimeout(10000);
					OutputStream outToServer = client.getOutputStream();
					DataOutputStream out = new DataOutputStream(outToServer);
					
					out.write(data);
			        out.close();
			        
			       
				}
			}catch(Exception e){
	        	e.printStackTrace();
	        }
		}
		
		
		
	}
	
	/**
	 * Receives byte data from the camera preview callback and appends to queue.
	 * @param data
	 */
	public void addImage(byte[] data){
		if(data!= null){
		queue.add(data);
		}
	 }
	
	
	
	/**
	 * Sets the thread to end.
	 */
	public void close(){
		running = false;
	}
	
	public void open(){
		running = true;
	}
	

	
}
