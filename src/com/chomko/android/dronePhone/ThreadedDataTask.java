package com.chomko.android.dronePhone;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.util.Log;




public class ThreadedDataTask extends Thread {
	
	private String serverName;
	private int port;
	private boolean running;
	private String outputMessage;
	private boolean messageNew;
	private boolean isConnected;
	
	
	
	/**
	 *ThreadedDataTask receives a string of gps and sensor data and sends it over a socket when available.
	 * @param serverName - String of remote computer receiving images.
	 * @param port - integer port number of remote computer service.
	 */
	
	public ThreadedDataTask(String serverName, int port){
		this.serverName = serverName;
		this.port = port;
		messageNew = true;
		outputMessage = "blank data message";
		isConnected = false;
	
		
	}
	
	public void run(){
		
		running = true;
		while(running && messageNew){
			// do everything in here
		
			try{
					
				//if(messageNew){
					
					//Create new socket
					Socket client = new Socket(serverName, port);
					client.setSoTimeout(10000);
					//Create output stream
					OutputStream outToServer = client.getOutputStream();
				
					DataOutputStream out  = new DataOutputStream(outToServer);
					
					out.writeUTF(outputMessage);
					out.close();
					messageNew = false;
					isConnected = true;
				//}
					
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
	
//	public boolean checkConnection(){
//		return(isConnected);
//	}
	
	/**
	 * Add new data to be sent to the server
	 * @param data - string of data to be send
	 */
	public void addData(String data){
		outputMessage = data;
		messageNew = true;
	}
	
	
}
