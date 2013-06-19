package pogvue.applet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonAlignApplet extends AlignApplet implements ActionListener {
    private Button b;

    public void init() {
	super.init();

	try {
	    UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
	    
	} catch (UnsupportedLookAndFeelException exception) {
	    exception.printStackTrace();
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	} catch (InstantiationException e2) {
	    e2.printStackTrace();
	} catch (Exception e3){ 
	    e3.printStackTrace();
    }
	
	componentInit();
	System.out.println("init");
    }

    private void componentInit() {
	b = new Button("Pogvue");
	add(b);
	b.addActionListener(this);
    }

    public void actionPerformed(ActionEvent evt) {
	if (evt.getSource() == b) {
	    b.requestFocus();
	    System.out.println("Button pressed");
	    makeFrame();
	}
	}
}
