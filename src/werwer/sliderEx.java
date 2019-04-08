package werwer;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class sliderEx extends JPanel{
	JSlider mainslider ;
	float scalar;
	public sliderEx(String name, int min, int max, float d) throws IllegalArgumentException{
		super();
		this.scalar=d;
		this.setLayout(new BorderLayout());
		JLabel title = new JLabel(name);
		title.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel mainsliderValue = new JLabel("0");
        mainsliderValue.setHorizontalAlignment(SwingConstants.CENTER);
        
        
        mainslider = new JSlider(JSlider.VERTICAL,
                min, max, 0);
        mainslider.setMajorTickSpacing((int) (5*Math.pow(10, (int) (Math.log10(max-min)-1))));
        mainslider.setMinorTickSpacing(1);
        Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
        for (int i=0;i<=mainslider.getMaximum()/mainslider.getMajorTickSpacing();i++)
        	labelTable.put( new Integer( i*mainslider.getMajorTickSpacing() ), new JLabel(Float.toString(i*mainslider.getMajorTickSpacing()/(float)d)) );
        mainslider.setLabelTable( labelTable );
        mainslider.setPaintTicks(true);
        mainslider.setPaintLabels(true);
        mainslider.setBorder(
                BorderFactory.createEmptyBorder(0,0,10,0));
        mainslider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				mainsliderValue.setText(Float.toString(((JSlider)e.getSource()).getValue()/d));
				
			}
        });
        this.add(title,BorderLayout.PAGE_START);
        this.add(mainslider,BorderLayout.CENTER);

        this.add(mainsliderValue,BorderLayout.PAGE_END);
	}
	public void setValue(float i){
		mainslider.setValue((int) (i*scalar));
		
	}
	float getValue(){
		return mainslider.getValue()/(float)scalar;
	}
}
