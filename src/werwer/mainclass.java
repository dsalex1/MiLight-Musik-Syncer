package werwer;

/*File AudioCapture02.java
This program demonstrates the capture and 
subsequent playback of audio data.

A GUI appears on the screen containing the 
following buttons:
Capture
Stop
Playback

Input data from a microphone is captured and 
saved in a ByteArrayOutputStream object when the
user clicks the Capture button.

Data capture stops when the user clicks the Stop 
button.

Playback begins when the user clicks the Playback
button.

This version of the program gets and  displays a
list of available mixers, producing the following
output:

Available mixers:
Java Sound Audio Engine
Microsoft Sound Mapper
Modem #0 Line Record
ESS Maestro

Thus, this machine had the four mixers listed 
above available at the time the program was run.

Then the program gets and uses one of the 
available mixers instead of simply asking for a 
compatible mixer as was the case in a previous 
version of the program.

Either of the following two mixers can be used in
this program:

Microsoft Sound Mapper
ESS Maestro

Neither of the following two mixers will work in
this program.  The mixers fail at runtime for 
different reasons:

Java Sound Audio Engine
Modem #0 Line Record

The Java Sound Audio Engine mixer fails due to a 
data format compatibility problem.

The Modem #0 Line Record mixer fails due to an 
"Unexpected Error"

Tested using SDK 1.4.0 under Win2000
************************************************/

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.sound.sampled.*;

public class mainclass extends JFrame {
	
	private static final int SELECTED_AUDIO_DEVICE = 6;
	public static final int TARGET_FPS=20;
	
	public static boolean INTERPOLATE=true;
	private static double min, max;
	private static double i;
	private static float colLastMeanVal,brgLastMeanVal,colorShift;
	private static AudioHandler aud;
	private static ColorHandler col;
	private static JButton saveAsButton,saveButton,renameButton,deleteButton;
	private static ArrayList<Preset> presets;
	private static JLabel infoLabel;
	JComboBox<Preset> presetBox;
	
	JCheckBox useColor;
	JCheckBox isActive;
	sliderEx colpostoff; 
	sliderEx colscale ;
	sliderEx colderivscale;
	sliderEx colshift ;
	sliderEx brgpostoff;
	sliderEx brgscale; 
	sliderEx brgderivscale;
	sliderEx preScale;
	
	public static byte[] getDataBuffer() {
		return dataBuffer;
	}

	public static void setDataBuffer(byte[] buffer) {
		dataBuffer = buffer;
	}


	Canvas canvas;
	
	static byte[] dataBuffer;
	static int framesPastUpdate=0;
	
	public static void main(String args[]) throws LineUnavailableException {
		new mainclass();
	} 
	
	public mainclass(){
		super();
		this.setLayout(new BorderLayout());
		
		JPanel topPanel=new JPanel();
		JPanel rightPanel=new JPanel();
		
		topPanel.setLayout(new GridLayout(1,8));
		
		aud = new AudioHandler(SELECTED_AUDIO_DEVICE);
		col = new ColorHandler("192.168.2.101");
		presets=new ArrayList<Preset>();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		canvas=new Canvas();
		canvas.setMinimumSize(new Dimension(300,300));
		canvas.setPreferredSize(new Dimension(300,300));
		this.add(topPanel,BorderLayout.CENTER);
		this.add(canvas,BorderLayout.PAGE_END);
		
		this.pack();
        this.setVisible(true);
        
        
        useColor = new JCheckBox("Use Color");
        isActive = new JCheckBox("Active");
        useColor.setSelected(true);
        isActive.setSelected(true);
        
		JPanel sidePanel=new JPanel();
		
		//sidePanel.setLayout(new GridLayout(2,1));
		sidePanel.add(useColor);
		sidePanel.add(isActive);
        topPanel.add(sidePanel);
        
		colpostoff = new sliderEx("COLOR OFFSET",-200,200,100);
		topPanel.add(colpostoff);
		colscale = new sliderEx("COLOR SCALAR",0,1000,100);
		topPanel.add(colscale);
		colderivscale = new sliderEx("COLOR DERIVATE SCALAR",0,1000,100);
		topPanel.add(colderivscale);
		colshift = new sliderEx("COLOR SHIFT",0,240,1000);
		topPanel.add(colshift);
		
		brgpostoff = new sliderEx("BRIGHT OFFSET",-600,600,100);
		topPanel.add(brgpostoff);
		brgscale = new sliderEx("BRIGHT SCALAR",0,1000,100);
		topPanel.add(brgscale);
		brgderivscale = new sliderEx("BRIGHT DERIVATE SCALAR",0,1000,100);
		topPanel.add(brgderivscale);
        
		rightPanel.setLayout(new FlowLayout());
		rightPanel.add(new JLabel("<Html>Preset:<br/><html/>"));
		topPanel.add(rightPanel);
        
		presetBox = new JComboBox<Preset>();
		rightPanel.add(presetBox);
		
		
		readPresetsFile();
		presetBox.addActionListener(new ActionListener(){
		    public void actionPerformed(ActionEvent e) {
		        JComboBox cb = (JComboBox)e.getSource();
		        Preset p = (Preset)cb.getSelectedItem();
		        applyPreset();

		    }
		});
		
		saveButton=new JButton("Save Preset");
		rightPanel.add(saveButton);
		saveAsButton=new JButton("Save as...");
		rightPanel.add(saveAsButton);
		renameButton=new JButton("Rename Preset");
		rightPanel.add(renameButton);
		deleteButton=new JButton("Delete Preset");
		rightPanel.add(deleteButton);
		
		infoLabel=new JLabel();
		rightPanel.add(infoLabel);
		
		preScale = new sliderEx("PRE SCALAR",0,100,1000);
		preScale.setValue(0.05f);
		rightPanel.add(preScale);
		
		JLabel crLabel=new JLabel("© 2016 by Alexander Seidler");
		rightPanel.add(crLabel);
		
		saveButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				((Preset) presetBox.getSelectedItem()).update( colpostoff.getValue(),
													colscale.getValue(), 
													colderivscale.getValue(), 
													colshift.getValue(), 
													brgpostoff.getValue(), 
													brgscale.getValue(),
													brgderivscale.getValue()); 
				savePresetsFile();
				applyPreset();
			}
			
		});
		
		saveAsButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String tmpName= JOptionPane.showInputDialog(rightPanel,
                        "Enter name for new preset");
				presetBox.addItem(new Preset(tmpName,
											 colpostoff.getValue(),
											 colscale.getValue(), 
											 colderivscale.getValue(), 
											 colshift.getValue(), 
											 brgpostoff.getValue(), 
											 brgscale.getValue(),
											 brgderivscale.getValue())); 
				savePresetsFile();	
				applyPreset();
			}
			
		});
		
		renameButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String tmpName= JOptionPane.showInputDialog(rightPanel,
                        "Enter new name");
				((Preset) presetBox.getSelectedItem()).name=tmpName;
				savePresetsFile();
				applyPreset();
			}
			
		});
		
		deleteButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				presetBox.removeItem(presetBox.getSelectedItem());
				savePresetsFile();	
				applyPreset();
			}
			
		});
		
		
		byte tempBuffer[] = new byte[512];
		
		applyPreset();
		
		while (true) {
			(new Thread(new Runnable(){

				@Override
				public void run() {
					while(true){
						dataTime=System.nanoTime();
						int cnt = aud.getTargetDataLine().read(tempBuffer, 0, tempBuffer.length);
						dataTime=System.nanoTime()-dataTime;
						if (dataTime>100000){
							double[] tmp = new double[tempBuffer.length];
							setDataBuffer(tempBuffer);
							framesPastUpdate=0;
					}
				}
				}})).start();
			
			long timeTaken=0;
			double sum=0;
			//double k=0,l=0;
			while(true){
				try {Thread.sleep(1000/TARGET_FPS-timeTaken);} catch (Exception e) {};
				timeTaken=System.currentTimeMillis();
				
				int lBound=0;
				int rBound=0;
				
				if (INTERPOLATE){
					lBound=framesPastUpdate    *getDataBuffer().length/(TARGET_FPS/8);
					rBound=(framesPastUpdate+1)*getDataBuffer().length/(TARGET_FPS/8);
				}else{
					lBound=0;
					rBound=getDataBuffer().length;
				}
				
				if (rBound<=getDataBuffer().length){
					sum = 0;
					for (int j = lBound; j <rBound; j++){
						double curval=Byte.toUnsignedInt(getDataBuffer()[j])-128;
						//sum += getDataBuffer()[j] * getDataBuffer()[j];
						sum += Math.pow(Math.abs(curval),1);
						//System.out.println(curval+" "+l);
						//canvas.addPoint(new ColoredPoint(k++/100,curval/1.5+100,Color.GREEN));
						//canvas.addPoint(new ColoredPoint(k++/100,l/1.5+100,Color.BLUE));
						//if (k/100>canvas.getWidth()) {
						//	k=0;
						//	canvas.clear();
						//}
					}
					//l=sum*127/26000;
				}
				
				float mean=0;
				
				if (INTERPOLATE){
					mean=(float) (sum / (getDataBuffer().length/(TARGET_FPS/8)));
				}else{
					mean=(float) (sum / getDataBuffer().length);
				}
				//System.out.println("frame:"+framesPastUpdate+" lBound:"+lBound+" rBound:"+rBound+" size:"+getDataBuffer().length+" sum:"+sum+" mean:"+mean);
				if (isActive.isSelected()){
					processLevel(mean);
					framesPastUpdate++;
					i++;
				}
				timeTaken=System.currentTimeMillis()-timeTaken;

			}
		
		}
	}
	
	
	protected void applyPreset() {
		Preset p=(Preset) presetBox.getSelectedItem();
		colpostoff.setValue(p.getValues()[0]);
		colscale.setValue(p.getValues()[1]);
		colderivscale.setValue(p.getValues()[2]);
		colshift.setValue(p.getValues()[3]);
		brgpostoff.setValue(p.getValues()[4]);
		brgscale.setValue(p.getValues()[5]);
		brgderivscale.setValue(p.getValues()[6]);
	}


	double lastTimeStamp = 0,sendTime = 0,dataTime = 0,calcTime = 0;
	void processLevel(float meanLevel){
		meanLevel=meanLevel*preScale.getValue();
		calcTime=System.nanoTime();
		if ((System.nanoTime()-lastTimeStamp)/1000000>10)
			infoLabel.setText("<html>"+"FPS: "+((int)(1/(System.nanoTime()-lastTimeStamp)*1000000000*100))/100f+"<br/>"
									  +"Data time: "+((int)(dataTime/1000000*100))/100f+"<br/>"
									  +"Send time: "+((int)(sendTime/1000000*100))/100f+"<br/>"
								      +"Calc time: "+((int)(calcTime/1000000*100))/100f+"<br/>"+"<html/>");
		
		
		lastTimeStamp=System.nanoTime();
		float colMeanVal=meanLevel;
		
		float colCurVal = colMeanVal*colscale.getValue()+Math.abs(colLastMeanVal-colMeanVal)*colderivscale.getValue()+colpostoff.getValue();
		
		colLastMeanVal = colMeanVal;
		
		float brgMeanVal=meanLevel;
		
		float brgCurVal = brgMeanVal*brgscale.getValue()+Math.abs(brgLastMeanVal-brgMeanVal)*brgderivscale.getValue()+brgpostoff.getValue();
		
		brgLastMeanVal = brgMeanVal;
		
		
		colorShift+=colshift.getValue()*2;
		calcTime=System.nanoTime()-calcTime;
		
		
		sendTime=System.nanoTime();
		//col.setColor(Color.getHSBColor(modex(colCurVal+colorShift), useColor.isSelected() ? 1 : 0, ((brgCurVal<0.008?0.008f:brgCurVal) > 1 ? 1 : (brgCurVal<0.008?0.008f:brgCurVal))));
		col.setColor(Color.getHSBColor(modex(colCurVal+colorShift), useColor.isSelected() ? 1 : 0, ((brgCurVal<0?0:brgCurVal) > 1 ? 1 : (brgCurVal<0?0:brgCurVal))),0);
		sendTime=System.nanoTime()-sendTime;
		
		if (useColor.isSelected())
			canvas.addPoint(new ColoredPoint(2*i%this.getWidth(),colCurVal*150+20,Color.BLUE));
		canvas.addPoint(new ColoredPoint(2*i%this.getWidth(),brgCurVal*150+20,Color.GREEN));
		if (2*i>this.getWidth()) {
			i=0;
			canvas.clear();
		}
		
	}
	
	private void readPresetsFile() {
		try {
			ObjectInputStream ois;
			FileInputStream fin = new FileInputStream("presets.dat");
			ois = new ObjectInputStream(fin);
			ArrayList<Preset> presets= (ArrayList<Preset>) ois.readObject();
			ois.close();
			
			for(Preset p:presets)
				presetBox.addItem(p);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	protected void savePresetsFile() {
		ArrayList<Preset> presets=new ArrayList<Preset>();
		
		for (int i=0;i<presetBox.getItemCount();i++){
			presets.add(presetBox.getItemAt(i));
		}
		try {
			FileOutputStream fout = new FileOutputStream("presets.dat");
			ObjectOutputStream oos = new ObjectOutputStream(fout);   
			oos.writeObject(presets);
			oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		
	}

	 public float modex(float f){
		 f=f-(int)(f);
		while (!(f>=0 && f<=1)){
			if (f>1) f--;
			if (f<0) f++;
		}
		colorShift=colorShift-(int)(colorShift);
		return f;
	}
}