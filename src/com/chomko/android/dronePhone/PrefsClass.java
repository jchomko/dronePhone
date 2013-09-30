package com.chomko.android.dronePhone;

import java.util.Set;

import android.content.SharedPreferences;
import android.util.Log;

public class PrefsClass {
	SharedPreferences prefs;
	
	PrefsClass(SharedPreferences _prefs){
		prefs = _prefs;
	
		
	}
	
public void editAvailableCamSizes(Set<String> strings){
//SharedPreferences settings = getSharedPreferences("pref_data_camera", 0);
//		
//		Set<String> test = new TreeSet<String>();
//		
//		test.add("hi");
//		test.add("hello");
//		test.add("goodbye");
//		
//		SharedPreferences.Editor prefsEditor = prefs.edit();
//		prefsEditor.putString("target_ip_address", "I CHANGED THE VALUE");
//		prefsEditor.putStringSet("imgsize", test);
//		prefsEditor.apply();
//		

    	
  }

public String getServerName(){
	
	String serverName = null;
	serverName = prefs.getString("target_ip_address", "ip address empty");
	Log.w("server_message", serverName);
	return(serverName);
	
	}

public Integer getImagePort(){
	String imagePortString = "";
	int imagePort;
	imagePortString = prefs.getString("target_port_image", "3300");
	Log.w("server_message", imagePortString);
	imagePort = Integer.parseInt(imagePortString);
	return(imagePort);
	}

public Integer getCommandPort(){
	int commandPort;
	String commandPortString = "";
	commandPortString = prefs.getString("target_port_command", "3302");
	commandPort = Integer.parseInt(commandPortString); 
	Log.w("server_message", commandPortString);
	return(commandPort);
}

public Integer getDataPort(){
	int dataPort;
	String dataPortString = "";
	dataPortString = prefs.getString("target_port_data", "3301");
	dataPort = Integer.parseInt(dataPortString); 
	Log.w("server_message", dataPortString);
	return(dataPort);
}

public Integer getPhotoInterval(){
	int photoInterval;
	String photoIntervalString = "";
	photoIntervalString = prefs.getString("photo_interval", "1000");
	photoInterval = Integer.parseInt(photoIntervalString);
	return(photoInterval);
	
}

public Boolean getCommandListen(){
	
	boolean commandListen = prefs.getBoolean("toggle_command_listen", false);
	return(commandListen);
	
}

public Boolean getSendDataToggle(){
	
	boolean dataToggle = prefs.getBoolean("toggle_data_send", false);
	return(dataToggle);
	
}
	
}
