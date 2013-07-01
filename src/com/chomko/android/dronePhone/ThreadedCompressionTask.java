package com.chomko.android.dronePhone;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;

class ThreadedCompressionTask extends Thread{
	
	private ThreadedImageTask threadedImageTask;
	private boolean running;
	private ConcurrentLinkedQueue<byte[][]> queue;
	private int width, height;
	private byte[][] data = new byte[0][0];
	private Object mPauseLock;
	private boolean mPaused;
	private boolean mFinished;
	
	
	
	public ThreadedCompressionTask(ThreadedImageTask threadedImageTask){
		this.threadedImageTask = threadedImageTask;
		queue = new ConcurrentLinkedQueue<byte[][]>();
	}
	
	public ThreadedCompressionTask(ThreadedImageTask threadedImageTask, int width, int height){
		this.threadedImageTask = threadedImageTask;
		this.width = width;
		this.height = height;
		queue = new ConcurrentLinkedQueue<byte[][]>();
	}
	
	public void run(){
		
		running = true;
		
		while(running){
	        if(queue.size() > 0){
	        	// empty queue to prevent it from overflowing
	        	while(queue.size() > 0){
	        		data = queue.poll();
	        	}
	        	
	        	// compress image data to jpeg
	        	try{
		        
	        	ByteArrayOutputStream outstr = new ByteArrayOutputStream();
		        Rect rect = new Rect(0, 0, width, height); 
		        YuvImage yuvimage = new YuvImage(data[0], ImageFormat.NV21, width, height, null);
		        yuvimage.compressToJpeg(rect, 100, outstr);
		        // send byte data to ThreadedImageTask for network stuff.
		        threadedImageTask.addImage(outstr.toByteArray());
		        
	        	}catch(IllegalArgumentException iae){
	        		iae.printStackTrace();
	        	}
		       
			} 
		}
		
		
		
		queue.clear();
	}
	
	public void addImage(byte[]... data){
		queue.add(data);
	}
	
	public void addImage(int width, int height, byte[]... data){
		this.width = width;
		this.height = height;
		queue.add(data);
	}
	
	public void close(){
		running = false;
	}
	
	public void open(){
		running = true;
	}
	
	
}
