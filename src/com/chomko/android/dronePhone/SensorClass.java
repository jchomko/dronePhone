package com.chomko.android.dronePhone;




import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class SensorClass{
	 
	  private SensorManager sensmgr;
	  private LocationManager locmgr;
	  private static Sensor mag_field;
	  private static Sensor accel;
	 
	  private String magFieldData;
	  private String accelData;
	  private String locData;
	
	 
	public SensorClass(SensorManager _sensmgr, LocationManager _locmgr){	 
		 
		 sensmgr = _sensmgr; 
		 locmgr = _locmgr;
		 mag_field = sensmgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		 accel = sensmgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		 locmgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,onLocationChanged);
		 locmgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, onLocationChanged);
		 locData = "-360,-360,-360";
		 
	 }
	  
	 public void resume(){
		 
		 sensmgr.registerListener(onMagFieldChanged, mag_field, SensorManager.SENSOR_DELAY_NORMAL);
		 sensmgr.registerListener(onAccelChanged, accel, SensorManager.SENSOR_DELAY_NORMAL);
		 locmgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,onLocationChanged);
      	 locmgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, onLocationChanged);
		 
		 
	 }
	 
	 public void pause(){
		  sensmgr.unregisterListener(onMagFieldChanged);
		  sensmgr.unregisterListener(onAccelChanged);
		  locmgr.removeUpdates(onLocationChanged);
	 }
	 
	 
	 
	 SensorEventListener onMagFieldChanged = new SensorEventListener() {
		 	
		   @Override
			public void onSensorChanged(SensorEvent event) {
				// TODO Auto-generated method stub
				 float azimuth_angle = event.values[0];
				 float pitch_angle = event.values[1];
				 float roll_angle = event.values[2];

				 magFieldData = Float.toString(azimuth_angle) + 
				 		 "," + Float.toString(pitch_angle) +
				 		 "," + Float.toString(roll_angle);
				 
				 Log.w("sensor", magFieldData);
				
			}

			@Override
			public void onAccuracyChanged(Sensor arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
		};
		
		
	 SensorEventListener onAccelChanged = new SensorEventListener() {
		 	
		 	@Override
			public void onSensorChanged(SensorEvent event) {
				// TODO Auto-generated method stub
				 float azimuth_angle = event.values[0];
				 float pitch_angle = event.values[1];
				 float roll_angle = event.values[2];
				 accelData = Float.toString(azimuth_angle) + 
				 		 "," + Float.toString(pitch_angle) +
				 		 "," + Float.toString(roll_angle);
				 
				 Log.w("sensor", accelData);
				 
				
				
			}

			@Override
			public void onAccuracyChanged(android.hardware.Sensor sensor,
					int accuracy) {
				// TODO Auto-generated method stub
				
			}
			
			
		};
		
		LocationListener onLocationChanged = new LocationListener() {
			    public void onLocationChanged(Location loc) {
			        
			    	
			    	locData =  Double.toString(loc.getLatitude()) + "," 
			        + Double.toString(loc.getLongitude()) + ","  
			        + Double.toString(loc.getAltitude()) + ","
			        + Double.toString(loc.getBearing()) + ","
			        + Double.toString(loc.getSpeed()) + ","
			        + Double.toString(loc.getAccuracy()) + ",";  
			        
			    	Log.w("location", locData);
			    
			        
			    }
			     
			    public void onProviderDisabled(String provider) {
			    // required for interface, not used
			    }
			     
			    public void onProviderEnabled(String provider) {
			    // required for interface, not used
			    }
			     
			    public void onStatusChanged(String provider, int status,
			    Bundle extras) {
			    // required for interface, not used
			    }
		 };
		
		 /**
		  * Get latest sensor data
		  * @return String of comma separated values
		  */
		 	
		public String getSensorData(){
			String data = "";
			String currentTime = Long.toString(System.currentTimeMillis()) + ",";
			data = currentTime + locData + magFieldData;
			return(data);
		}
		/**
		 * Get latest accelerometer data
		 * @return String of three comma separated floats
		 */
		public String getAccelData(){
			String data = "accel data";
			return(data);
		}
		
		/**
		 * Get latest location data from GPS provider
		 * @return String of comma separated values
		 */
		public String getLatestLoc(){
			
			try{	
			
			  String networkProvider = LocationManager.GPS_PROVIDER; // or LocationManager.NETWORK_PROVIDER
			  Location loc = locmgr.getLastKnownLocation(networkProvider);
			  locData =  Double.toString(loc.getLatitude()) + "," 
				        + Double.toString(loc.getLongitude()) + ","  
				        + Double.toString(loc.getAltitude()) + ","
				        + Double.toString(loc.getBearing()) + ","
				        + Double.toString(loc.getSpeed()) + ","
				        + Double.toString(loc.getAccuracy()) + ",";  
			  
			
			  
			}catch(Exception e){
				Log.w("location", e.toString());
				 
			}
			   return(locData);
			
		}
		
	
	
}
