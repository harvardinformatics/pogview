package pogvue.gui;

import pogvue.gui.schemes.*;

import pogvue.io.*;
import pogvue.gui.hub.*;
import pogvue.analysis.*;
import pogvue.datamodel.*;
import pogvue.gui.event.*;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

public class LayeredPanel extends JLayeredPane {
  private AlignViewport av;
  private Controller    c;

  private GlassCanvas gc;
  private SeqCanvas   sc;

  private boolean donebounds = false;

  public LayeredPanel(AlignViewport av, Controller c) {
    this.av = av;
    this.c  = c;

    gc = new GlassCanvas(av,c);
    sc = new SeqCanvas(av,c);

    add(sc, new Integer(0));
    //    add(gc, new Integer(1));

    //    gc.setVisible(true);


    sc.setBounds(0,0,1500,1000);
    gc.setBounds(0,0,1500,1000);
    
 
 }
  
  public Dimension getPreferredSize() {

    if (getSize().width > 0) {
      if (!donebounds) {
	  System.out.println("Redoing bounds " + getParent());

	Container parent = getParent();
	if (parent != null && parent instanceof JPanel) {
	    JPanel p = (JPanel)parent;
	    System.out.println("Parent width height = " + p.size().width + " " + p.size().height);

	    sc.setBounds(0,0,parent.getWidth(),parent.getHeight());
	    gc.setBounds(0,0,parent.getWidth(),parent.getHeight());

	    //donebounds = true;
	    // p.setVisible(false);
	    //p.invalidate();
	    //p.validate();
	    //p.setVisible(true);

	}

      }
	
      return getSize();
    } else {
      return new Dimension(1000,700);
    }
  }

  private SeqCanvas getSeqCanvas() {
    return sc;
  }
  private GlassCanvas getGlassCanvas() {
    return gc;
  }
}