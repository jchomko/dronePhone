package com.chomko.android.dronePhone;

import java.util.List;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;


public class CameraClass {
	
		
	  private SurfaceHolder previewHolder;
	  private Camera camera=null;
	  private boolean inPreview=false;
	  private boolean cameraConfigured=false;
	  private boolean flash = false;
	 
	  private ThreadedImageTask threadedImageTask;
	  
	  CameraClass(SurfaceHolder _previewHolder, ThreadedImageTask _threadedImageTask){
		  previewHolder = _previewHolder;
		  threadedImageTask = _threadedImageTask;
	  }

	 void resume(){

		    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
		      Camera.CameraInfo info=new Camera.CameraInfo();

		      for (int i=0; i < Camera.getNumberOfCameras(); i++) {
		        Camera.getCameraInfo(i, info);

		        if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
		          camera=Camera.open(i);
		        }
		      }
		    }

		    if (camera == null) {
		      camera=Camera.open();
		    }
		    
		    camera.setPreviewCallback(cb);
		    startPreview();
		    
		   
		   
		  
	  }
	  
	  
	 void pause(){
		  //Release camera and preview callback
		if (inPreview) {
	      camera.stopPreview();
	    }
	    camera.setPreviewCallback(null);
	    camera.release();
	    camera=null;
	    inPreview=false;
	    
	  
		  
	  }
	  
	  
	  public void initPreview(int width, int height) {
			
		  if (camera != null && previewHolder.getSurface() != null) {
	      try {
	        camera.setPreviewDisplay(previewHolder);
	    	camera.setPreviewCallback(cb);
	        
	       }
	      
	      catch (Throwable t) {
	        Log.e("PreviewDemo-surfaceCallback",
	              "Exception in setPreviewDisplay()", t);

	      }

	      if (!cameraConfigured) {
	       
	    	Camera.Parameters parameters=camera.getParameters();
	        Camera.Size size = getBestPreviewSize(width, height, parameters);
	        //Camera.Size pictureSize=getSmallestPictureSize(parameters);
	        Camera.Size biggestPicSize = getBiggestPictureSize(parameters);
	        
	        Log.w("pic_size", biggestPicSize.toString());
	        
	        if (size != null && biggestPicSize != null) {
	        	
	          parameters.setPreviewSize(size.width, size.height);
	          parameters.setPictureSize(biggestPicSize.width,biggestPicSize.height);
	          parameters.setPictureFormat(ImageFormat.JPEG);
	          parameters.setPreviewFormat(ImageFormat.NV21);
	         
	          
	          
	          List<Size> supportedImageFormats = parameters.getSupportedPictureSizes();
	          for(int i = 0; i < supportedImageFormats.size(); i ++){
	        	  
	        	  Log.w("picture_format", Integer.toString(supportedImageFormats.get(i).width));
	        	  
	          }
	          
	          camera.setParameters(parameters);
	          cameraConfigured=true;
	        }
	      }
	    }
	  }
	  
	  public void startPreview() {
		    if (cameraConfigured && camera != null) {
		      
		      camera.startPreview();
		      inPreview=true;
		     }
	  }
	  
	  private Camera.Size getBestPreviewSize(int width, int height,Camera.Parameters parameters) {
		    Camera.Size result=null;

		    for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
		      if (size.width <= width && size.height <= height) {
		        if (result == null) {
		          result=size;
		        }
		        else {
		          int resultArea=result.width * result.height;
		          int newArea=size.width * size.height;

		          if (newArea > resultArea) {
		            result=size;
		          }
		        }
		      }
		    }

		    return(result);
		  }

		  
		  
	  private Camera.Size getSmallestPictureSize(Camera.Parameters parameters) {
		    Camera.Size result=null;

		    for (Camera.Size size : parameters.getSupportedPictureSizes()) {
		      if (result == null) {
		        result=size;
		       
		      }
		      else {
		        int resultArea=result.width * result.height;
		        int newArea=size.width * size.height;
		        
		        if (newArea < resultArea) {
		          result=size;
		        }
		      }
		    }

		    return(result);
		  }
		  
		  
		  
		private Camera.Size getBiggestPictureSize(Camera.Parameters parameters){
			  Camera.Size result = null;
			  for(Camera.Size size : parameters.getSupportedPictureSizes()){
				
				 if(result == null){
					 result = size;
				 } else{
					 int resultArea = result.width * result.height;
					 int newArea = size.width * size.height;
					 
					 if( newArea > resultArea){
						 result = size;
						 
					 }
				}
				  Log.w("picsizes", Integer.toString(result.width));
			  }
			 
			  return(result);
		  }
		  

		public void toggleFlash(){
			  flash = !flash;
			  
			  if(flash){
			  Camera.Parameters p = camera.getParameters();
			  
			  p.setFlashMode(Parameters.FLASH_MODE_TORCH);
			  camera.setParameters(p);
			  }else{
				  Camera.Parameters p = camera.getParameters();
				  p.setFlashMode(Parameters.FLASH_MODE_OFF);
				  camera.setParameters(p);
				  
			  }
		  }
		  

		public void togglePreview(){
			 // showPreview = !showPreview;
		  }
		  
		  
		public void takePicture(){
			  if(inPreview){
			  camera.takePicture(null, null, photoCallback);
	 		  inPreview=false;
			  }
		  }
		  
		  
		  
		  Camera.PictureCallback photoCallback=new Camera.PictureCallback() {
		    public void onPictureTaken(byte[] data, Camera camera) {
		      //new SavePhotoTask().execute(data);
		      
		      //Photo requires no conversion, add directly to image queue
		      Log.w("picsizes", Integer.toString(data.length));
		      threadedImageTask.addImage(data);    
		      
		      camera.startPreview();
		      inPreview=true;
		   
		      
		    }
		  };
		  
		  
		  
	Camera.PreviewCallback cb = new Camera.PreviewCallback() {
			
			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {
				
				
//				Camera.Parameters parameters = camera.getParameters();
//				int width = parameters.getPreviewSize().width;
//			    int height = parameters.getPreviewSize().height;
			   
			    
			   
//			    if(inPreview && showPreview){
//			    	
//					//Send image data to compression/conversion task 
//			    	//If preview is in JPEG form it can can be sent directly to threadedImageTask
//			    	//Log.w("preview_callback", "sending image");
//			    	//threadedCompressionTask.addImage(width, height, data);
//			    }
			    
//			    frameCount ++;
//				 
//				 if(System.currentTimeMillis()-frameTimestamp > 1000){
//					 
//					 runningAvg.add(frameCount);
//					 
//					 frameCount = 0;
//					
//					 //Log.w("framerate", Float.toString(runningAvg.getAverage()));
//					 frameTimestamp = System.currentTimeMillis();
				 }
			 
		};



	  
}
