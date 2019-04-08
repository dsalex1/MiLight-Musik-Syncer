package werwer;

import java.io.Serializable;

public class Preset implements Serializable{
		/**
	 * 
	 */
	private static final long serialVersionUID = -7803877794566393817L;
		String name;
		float[] values=new float[7];
		
		public Preset(String name, float val1,float val2,float val3,float val4,float val5,float val6,float val7){
			this.name=name;
			values[0]=val1;
			values[1]=val2;
			values[2]=val3;
			values[3]=val4;
			values[4]=val5;
			values[5]=val6;
			values[6]=val7;
		}
		
		public String toString(){
			return name;
		}
		
		float[] getValues(){
			return values;
		}
		
		public void update(float val1,float val2,float val3,float val4,float val5,float val6,float val7){
			values[0]=val1;
			values[1]=val2;
			values[2]=val3;
			values[3]=val4;
			values[4]=val5;
			values[5]=val6;
			values[6]=val7;
		}
		
		
}
