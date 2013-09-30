package com.chomko.android.dronePhone;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * ThreadedCommandTask
 * @author Aaron Siegel
 */
class ThreadedCommandTask extends Thread{
	
	private Handler handler;
	private String serverName;
	private int port;
	private boolean running;
	private String outputMessage;
	
	
	
	/**
	 * ThreadedCommandTask creates a queue of images and sends them over a socket when available.
	 * @param serverName - String of remote computer receiving images.
	 * @param port - integer port number of remote computer service.
	 * @param handler - Handler to communicate with UI thread
	 */
	public ThreadedCommandTask(String serverName, int port, Handler handler){
		this.serverName = serverName;
		this.port = port;
		this.handler = handler;
		outputMessage = "ready";
		
	}
	
	public void run(){
		
		//running = true;
		while(running){
			// do everything in here
			
			try{
					// connect to socket
				    
					//Log.w("server_message", "ready to connect to server");
					Socket client = new Socket(serverName, port);
					client.setSoTimeout(10000);
					
					//Log.w("server_message", "connected to server");
					
					//tell the server that the client is ready
					OutputStream outToServer = client.getOutputStream();
				
					DataOutputStream out  = new DataOutputStream(outToServer);
					out.writeUTF("ready");
				
					//Read the message from the server 
					InputStream inFromServer = client.getInputStream();
				
					DataInputStream in = new DataInputStream(inFromServer);
			        String input = in.readUTF();
					Log.w("server_message", input);
					
					//Make a bundle and put the input in it
					Bundle bundle = new Bundle();
					Message msg = new Message();
					bundle.putString("INPUT_KEY", input);
			        msg.setData(bundle);
			        
			        //Send the input message to the UI thread using the passed in handler
					handler.sendMessage(msg);
					
		        
				
			}catch(Exception e){
	        	e.printStackTrace();
	        }
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
