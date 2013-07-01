package com.chomko.android.dronePhone;

import java.util.LinkedList;

class RunningAverage {
	 
	 private int size;
	 private LinkedList<Float> values;

	 /**
	  * Running Average.
	  * @param size - number of values to average over.
	  */
	 public RunningAverage(int size){
	  this.size = size;
	  values = new LinkedList<Float>();
	 }
	 
	 /**
	  * Adds a value to the queue, and polls off the oldest value if queue has reached it's max.
	  * @param value
	  */
	 public void add(float value){
	  values.add(value);
	  if(values.size() > size){
	   values.poll();
	  }
	 }
	 
	 /**
	  * Returns an average value derived from all values in the queue.
	  * @return float - average value.
	  */
	 public float getAverage(){
	  float total = 0;
	  for(float num : values){
	   total += num;
	  }
	  return total / (float)size;
	 }
} 