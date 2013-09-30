/*
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Advanced Android Development_
    http://commonsware.com/AdvAndroid
 */

package com.chomko.android.dronePhone;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.chomko.android.phoneDrone.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class phoneDroneActivity extends Activity {
  
	
  private SurfaceView preview=null;
  private SurfaceHolder previewHolder=null;
  
 
  private static Handler messageHandler;
  private static Handler timerHandler;
 
  
  private ThreadedImageTask threadedImageTask;
  private ThreadedCompressionTask threadedCompressionTask;
  private ThreadedCommandTask threadedCommandTask;
  private ThreadedDataTask threadedDataTask;

 
  private SensorClass sensors;
  private PrefsClass preferences;
  private CameraClass cam;
  private AudioClass audio;
  
  
  private int photoInterval;
 

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    setContentView(R.layout.main);
    preview=(SurfaceView)findViewById(R.id.preview);
    preview.setKeepScreenOn(true);
    previewHolder=preview.getHolder();
    previewHolder.addCallback(surfaceCallback);
    previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    
    messageHandler = new Handler(Looper.getMainLooper()) {
    	
        /*
         * handleMessage() defines the operations to perform when the
         * Handler receives a new Message to process.
         */
    	
        @Override
        public void handleMessage(Message inputMessage) {

            //Receive data from command thread
        	Bundle bundle = inputMessage.getData();
        	String input = bundle.getString("INPUT_KEY");
        	
        	//If the input equals something
        	if(input.equals("p")){
        		
        		//Do something
        		cam.togglePreview();
        		Log.w("message_handler", "play/pause image thread");
        	
        	}
        	
        	//For example, if we send an f
        	if(input.equals("f")){
        		
        		//Toggle the camera flash on/off
        		cam.toggleFlash();
        		Log.w("message_handler", "toggle flash");
        	
        	}
        	
        	//Or play a sound
        	if(input.equals("s")){
        		audio.playClip();
        		Log.w("message_handler", "play clip");
        	  }
         }
    };
    
    audio = new AudioClass((AudioManager)getSystemService(AUDIO_SERVICE), this);
    
    sensors = new SensorClass((SensorManager) this.getSystemService(SENSOR_SERVICE), 
			  (LocationManager) this.getSystemService(Context.LOCATION_SERVICE));
    preferences = new PrefsClass(PreferenceManager.getDefaultSharedPreferences(getBaseContext()));
    
    threadedImageTask = new ThreadedImageTask(preferences.getServerName(), preferences.getImagePort());
    threadedImageTask.start();
   	
    threadedCompressionTask = new ThreadedCompressionTask(threadedImageTask);
    threadedCompressionTask.start();
    
	threadedDataTask = new ThreadedDataTask(preferences.getServerName(), preferences.getDataPort());
    if(preferences.getSendDataToggle()){
    
    	threadedDataTask.start();
        threadedDataTask.open();
    }

    cam = new CameraClass(previewHolder, threadedImageTask);
    
    timerHandler = new Handler();
    
    photoInterval = preferences.getPhotoInterval();
    timerHandler.postDelayed(runnableTakePicture, photoInterval);
    
    threadedCommandTask = new ThreadedCommandTask(preferences.getServerName(), preferences.getCommandPort(), messageHandler); //commandPort
    if(preferences.getCommandListen()){
    	
    	threadedCommandTask.start();
    	threadedCommandTask.open();
    
    }
    
    
   
    

   
  }

  @Override
  public void onResume() {
    super.onResume();
    
   
    
    //Create new instances of the threads
    threadedImageTask = new ThreadedImageTask(preferences.getServerName(), preferences.getImagePort());
   	threadedImageTask.start();
    
    threadedCompressionTask = new ThreadedCompressionTask(threadedImageTask);
    threadedCompressionTask.start();
   	
   
    if(preferences.getCommandListen() == true){
    	threadedCommandTask = new ThreadedCommandTask(preferences.getServerName(), preferences.getCommandPort(), messageHandler); //commandPort
    	threadedCommandTask.start();
    	threadedCommandTask.open();
    }else if(preferences.getCommandListen() == false && threadedCommandTask.isAlive()){
    	threadedCommandTask.close();
    }
    
    photoInterval = preferences.getPhotoInterval();
    timerHandler.postDelayed(runnableTakePicture, photoInterval);
    
   
    if(preferences.getSendDataToggle() == true){
    	threadedDataTask = new ThreadedDataTask(preferences.getServerName(), preferences.getDataPort());
    	threadedDataTask.start();
        threadedDataTask.open();
    }else if(preferences.getSendDataToggle() == false && threadedDataTask.isAlive()){
    		threadedDataTask.close();
    	
    	
    }

   
    //Resume Sensors
    sensors.resume();
    
    //Resume Camera
    cam.resume();
    
  }

  @Override
  public void onPause() {
	
	//Pause Camera
	cam.pause();
	
	//Stop timer
	timerHandler.removeCallbacks(runnableTakePicture);
	
	
	//Close threads
	if(preferences.getSendDataToggle() == true){
		threadedDataTask.close();
	}
	
	threadedCompressionTask.close();
    threadedImageTask.close();
    
    if(preferences.getCommandListen()  == true){
    	threadedCommandTask.close();
    }
  
    
    
    //Pause Sensors
    sensors.pause();
    
   
    
    super.onPause();
  }

  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    new MenuInflater(this).inflate(R.menu.options, menu);

    return(super.onCreateOptionsMenu(menu));
    
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
	  
    if (item.getItemId() == R.id.camera) {
    	cam.takePicture();
    }
    
    if(item.getItemId() == R.id.settings) {
         //Open Settings Activity
		 Intent settingsActivity = new Intent(getBaseContext(), SettingsActivity.class);
		 startActivity(settingsActivity);
	 }
    
	return(super.onOptionsItemSelected(item));
	
  }
  
  
  SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback() {
    public void surfaceCreated(SurfaceHolder holder) {
      // no-op -- wait until surfaceChanged()
    }
    

    public void surfaceChanged(SurfaceHolder holder, int format,
                               int width, int height) {
      cam.initPreview(width, height);
      cam.startPreview();
      
     
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
      // no-op
    
    }
  };

  
  
  
  public String getLocalIpAddress() {
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress()) {
	                    return inetAddress.getHostAddress().toString();
	                }
	            }
	        }
	    } catch (SocketException ex) {
	        Log.e("server_message", ex.toString());
	    }
	    return null;
	}
  
  private Runnable runnableTakePicture = new Runnable(){
	
	@Override
	public void run() {
		
			cam.takePicture();
			
			if(threadedDataTask.isAlive()){
				threadedDataTask.addData(sensors.getSensorData());
			}
			
			timerHandler.postDelayed(this, photoInterval);
			Log.w("location",sensors.getSensorData());
		}	
	};

}







