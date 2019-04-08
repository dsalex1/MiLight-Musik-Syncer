package werwer;

import java.io.ByteArrayOutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class AudioHandler {
	
	boolean stopCapture = false;
	ByteArrayOutputStream byteArrayOutputStream;
	AudioFormat audioFormat;
	TargetDataLine targetDataLine;
	AudioInputStream audioInputStream;
	SourceDataLine sourceDataLine;

	
	
	public AudioHandler(int line){
		Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
		System.out.println("Available mixers:");
		for(int cnt = 0; cnt < mixerInfo.length;
		                                    cnt++){
			System.out.println("("+cnt+")  "+mixerInfo[cnt].
			                              getName());
			if (line==-1 && mixerInfo[cnt].getName().startsWith("Mikrofon")) line=cnt;
		}//end for loop
		
		//Get everything set up for capture
		audioFormat = getAudioFormat();
		
		DataLine.Info dataLineInfo =
		                      new DataLine.Info(
		                      TargetDataLine.class,
		                      audioFormat);
		
		//Select one of the available
		// mixers.
		Mixer mixer = AudioSystem.
		                    getMixer(mixerInfo[line]);
		System.out.println("");
		System.out.println("Mixer selected:"+mixer.getMixerInfo());
		System.out.println("");
		//Get a TargetDataLine on the selected
		// mixer.
		try {
			targetDataLine = (TargetDataLine)
			               mixer.getLine(dataLineInfo);
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Prepare the line for use.
		try {
			targetDataLine.open(audioFormat);
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		targetDataLine.start();
	}
	 public TargetDataLine getTargetDataLine(){
		 return targetDataLine;
	 }
	 private AudioFormat getAudioFormat(){
		    float sampleRate = 8000.0F;
		    //8000,11025,16000,22050,44100
		    int sampleSizeInBits = 8;
		    //8,16
		    int channels = 1;
		    //1,2
		    boolean signed = false;
		    //true,false
		    boolean bigEndian = false;
		    //true,false
		    return new AudioFormat(
		                      sampleRate,
		                      sampleSizeInBits,
		                      channels,
		                      signed,
		                      bigEndian);
		  }//end getAudioFormat
}
