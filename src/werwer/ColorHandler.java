package werwer;

import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ColorHandler extends JFrame {

	private DatagramSocket socket;
	private InetAddress address;

	private Color currentColor = Color.white;
	private JPanel canvas;

	private byte sID1=0;
	private byte sID2=0;
	private byte SB=0;
	

	
	

	ColorHandler(String ip) {
		super();
		this.pack();
		this.setVisible(true);
		canvas = new JPanel() {
			public void paintComponent(Graphics g) {
				g.setColor(currentColor);
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
				g.dispose();
			}
		};
		this.add(canvas);

		
		
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			address = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 byte[] SessionRes=send( hexToBytes("20 00 00 00 16 02 62 3A D5 ED A3 01 AE 08 2D 46 61 41 A7 F6 DC AF D3 E6 00 00 1E"));

	     sID1 = SessionRes[19];
	     sID2 = SessionRes[20];

	}

	

	
	
	
	
	
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte b) {
		return bytesToHex(new byte[] { b });
	}

	private static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 3];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 3] = hexArray[v >>> 4];
			hexChars[j * 3 + 1] = hexArray[v & 0x0F];
			hexChars[j * 3 + 2] = ' ';

		}
		return new String(hexChars);
	}

	private static byte[] hexToBytes(String s) {
		s = s.replace(" ", "");
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}
	
	
	
	
	
	

	private static byte checksum(byte[] arr) {
		byte sum = 0;
		for (int i = 0; i < arr.length; i++)
			sum += arr[i];
		return sum;
	}
	
	private byte[] send(byte[] buf) {
		DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 5987);
		try {
			socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] recieve = new byte[1024];
		packet = new DatagramPacket(recieve, recieve.length);
		try {
			socket.receive(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
	}

	private byte[] sendCommand(String command,int zone){
		if (zone<0 || zone >4) throw new IllegalArgumentException("Zone must be 0 for all, or between 1 and 4");
		
        String zoneStr="0"+zone+" ";
        String fullCommand = "80 00 00 00 11 "+ bytesToHex(sID1)+ bytesToHex(sID2)+"00 "+bytesToHex(SB++)+" 00 "+command+zoneStr+"00 "+bytesToHex(checksum(hexToBytes(command+zoneStr)));
        return send(hexToBytes(fullCommand));
	}


	
	
	

	public void setOn() {
		setOn(0);
	}
	public void setOff() {
		setOff(0);
	}
	public void setWhite(){
		setWhite(0);
	}
	public void setHue(int hue) {
		setHue(hue,0);
	}
	public void setSaturation(int sat) {
		setSaturation(sat,0);
	}
	public void setBrightness(int brg) {
		setBrightness(brg,0);
	}
	public void setKelvin(int K) {
		setKelvin(K,0);
	}

	public void setOn(int zone) {
		sendCommand("31 00 00 08 04 01 00 00 00 ",zone);
	}
	public void setOff(int zone) {
		sendCommand("31 00 00 08 04 02 00 00 00 ",zone);
	}
	public void setWhite(int zone){
		sendCommand("31 00 00 08 05 64 00 00 00",zone);

	}
	public void setHue(int hue,int zone) {
		if (hue<0 || hue>0xFF) throw new IllegalArgumentException("Hue must be between 0 and 0xFF");
		String hueStr=bytesToHex((byte)hue);
		sendCommand("31 00 00 08 01 "+hueStr+" "+hueStr+" "+hueStr+" "+hueStr+" ",zone);
	}
	public void setSaturation(int sat,int zone) {
		if (sat<0 || sat>0x64) throw new IllegalArgumentException("Saturation must be between 0 and 0x64");
		String satStr=bytesToHex((byte)sat);
		sendCommand("31 00 00 08 02 "+satStr+" 00 00 00 ",zone);
	}
	public void setBrightness(int brg,int zone) {
		if (brg<0 || brg>0x64) throw new IllegalArgumentException("Brightness must be between 0 and 0x64");
		String brgStr=bytesToHex((byte)brg);
		sendCommand("31 00 00 08 03 "+brgStr+" 00 00 00 ",zone); 
	}
	public void setKelvin(int K,int zone) {
		if (K<0 || K>0x64) throw new IllegalArgumentException("Kelvin must be between 0 and 0x64");
		String KStr=bytesToHex((byte)K);
		sendCommand("31 00 00 08 05 "+KStr+" 00 00 00 ",zone); 
	}

	void setColor(Color col,int zone) {
		currentColor = col;
		canvas.repaint();
		float[] tmp = Color.RGBtoHSB(col.getRed(), col.getGreen(), col.getBlue(), null);
		System.out.println(Arrays.toString(tmp));
		if (tmp[0]==0 && tmp[1]==0 && tmp[2]==1) {
			setOn(zone);
			setWhite(zone);
			setBrightness(0x64,zone);
			setKelvin(0x32,zone);
			return;
		}
		if (tmp[2]==0) {
			setOff(zone);
			return;
		}
		setOn(zone);
		setHue((int)((tmp[0]*0xFF+0x8)%0xFF),zone);
		setSaturation((int)(0x64-tmp[1]*0x64),zone);
		setBrightness((int)(tmp[2]*0x64),zone);
	}
	

}
