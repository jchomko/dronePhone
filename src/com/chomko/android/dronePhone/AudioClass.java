package com.chomko.android.dronePhone;

import com.chomko.android.phoneDrone.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.Log;

public class AudioClass {
	
	  private SoundPool soundPool;
	  private AudioManager audioManager;
	  private boolean soundLoaded = false;
	  private int soundID;
	  private Context context;
	  
	 AudioClass(AudioManager _audioManager, Context _context){
		    
		 	context = _context;
		    audioManager = _audioManager;
		    soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		    soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener(){
		    
		    
				@Override
				public void onLoadComplete(SoundPool arg0, int arg1, int arg2) {
					// TODO Auto-generated method stub
					soundLoaded = true;
				}
		    });
		    
		    soundID = soundPool.load(context,R.raw.sht,1);
		 
	 }
	 
	
	 
	 void playClip(){
			
			//AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		    
		      float maxVolume = (float) audioManager
		          .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		    
		      // Is the sound loaded already?
		      if (soundLoaded) {
		    	soundPool.play(soundID, maxVolume, maxVolume, 1, 0, 1f);
		        Log.e("Test", "Played sound");
		      }
			
		}
//		
		
	
}
