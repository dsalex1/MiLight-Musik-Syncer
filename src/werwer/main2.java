package werwer;
  import java.awt.BorderLayout;
  import java.awt.Color;
  import java.awt.Font;

  import javax.swing.BorderFactory;
  import javax.swing.JColorChooser;
  import javax.swing.JFrame;
  import javax.swing.JLabel;
  import javax.swing.colorchooser.ColorSelectionModel;
  import javax.swing.event.ChangeEvent;
  import javax.swing.event.ChangeListener;

  public class main2 {

    public static void main(String[] a) {
        ColorHandler col = new ColorHandler("192.168.2.101");
    	col.setOn(1);
    	
      JFrame frame = new JFrame("JColorChooser Popup");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      final JLabel label = new JLabel("www.java2s.com", JLabel.CENTER);
      label.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 48));
      frame.add(label, BorderLayout.SOUTH);

      final JColorChooser colorChooser = new JColorChooser(label.getBackground());
      colorChooser.setBorder(BorderFactory.createTitledBorder("Pick Color for java2s.com"));

      ColorSelectionModel model = colorChooser.getSelectionModel();
      ChangeListener changeListener = new ChangeListener() {
        public void stateChanged(ChangeEvent changeEvent) {
          Color newForegroundColor = colorChooser.getColor();
      	  col.setColor(colorChooser.getColor(), 1);
          label.setForeground(newForegroundColor);
        }
      };
      model.addChangeListener(changeListener);
      frame.add(colorChooser, BorderLayout.CENTER);

      frame.pack();
      frame.setVisible(true);
    }

  }
     
