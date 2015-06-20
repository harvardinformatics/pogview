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
import javax.swing.event.*;
import javax.swing.BorderFactory; 
import javax.swing.border.*;
import javax.swing.colorchooser.*;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


public class TrackSelectionPanel extends JPanel implements ItemListener, ChangeListener {
  public JLabel      track_label;
  public ColourPanel colour_panel;
  public JCheckBox   display_box;
  public JCheckBox   collapse_box;

  public Color       color;
  public String      name;
  public boolean     isDisplayed;
  public boolean     isCollapsed = false;
  public JColorChooser jcc;
  public JFrame       jf2;

  public TrackSelectionPanel(String name, Color color,boolean isDisplayed, boolean isCollapsed) {
    this.name        = name;
    this.color       = color;
    this.isDisplayed = isDisplayed;
    this.isCollapsed = isCollapsed;

    componentInit();
  }

  public void componentInit() {
    track_label  = new JLabel(name);
    colour_panel = new ColourPanel(color);
    
    display_box  = new JCheckBox("Visible");
    collapse_box = new JCheckBox("Collapsed");

    display_box.setSelected(isDisplayed);
    collapse_box.setSelected(isCollapsed);

    display_box.addItemListener(this);
    collapse_box.addItemListener(this);

    Border border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

    setBorder(border);

    FormLayout layout = new FormLayout("pref:grow,50px,pref,pref",
				       "pref");
    
    setLayout(layout);
    
    CellConstraints cc = new CellConstraints();
    
    add(track_label,                cc.xy(1,1, "fill, fill"));
    add(colour_panel,               cc.xy(2,1, "fill, fill"));
    add(display_box,                cc.xy(3,1, "fill, fill"));
    add(collapse_box,               cc.xy(4,1, "fill, fill"));

    //colour_panel.addMouseListener(this);

  }
   
  public void itemStateChanged(ItemEvent e) {

    Object source = e.getItemSelectable();
    
    if (source == display_box) {
      System.out.println("Display changed");
    } else if (source == collapse_box) {
      System.out.println("Collapse changed");
    }
    
    if (e.getStateChange() == ItemEvent.DESELECTED) {
      System.out.println("Deselected " + source);
    } else if (e.getStateChange() == ItemEvent.SELECTED) {
      System.out.println("Selected " + source);
    }
  }

  public void setColorChooser(JColorChooser jcc) {
    this.jcc = jcc;
  }

  public void stateChanged(ChangeEvent e) {
    Color newColor = jcc.getColor();
    colour_panel.color = newColor;
    repaint();
  }

  public void mouseClicked(MouseEvent evt) { }
  public void mouseEntered(MouseEvent evt) { }
  public void mouseExited (MouseEvent evt) { }
  public void mouseMoved  (MouseEvent evt) { }
  public void mousePressed(MouseEvent evt) { }
  public void mouseDragged(MouseEvent evt) { }
  public void mouseReleased(MouseEvent evt) { }

  public static void main(String[] args) {
    String chr   = args[0];
    String start = args[1];
    String end   = args[2];

    LinkedHashMap typeorder = AlignViewport.readGFFConfig("data/gff.conf","URL");

    String regstr = "query=" + chr + "&start=" + start + "&end=" + end + "&z=2";
    try {
      GFFFile   gff = GenomeInfoFactory.getRegionFeatures(regstr,null);
      
      gff.parse();
      
      Vector  tmpfeat = gff.getFeatures();
      Vector  gfffeat =  SequenceFeature.hashFeatures(tmpfeat,0,typeorder,false);
	
      JFrame jf = new JFrame();
      
      jf.getContentPane().setLayout(new GridLayout(gfffeat.size(),1));
      
      for (int i = 0; i < gfffeat.size(); i++) {

	if (gfffeat.elementAt(i) instanceof GFF) {
	  System.out.println("GFF " + gfffeat.elementAt(i));
	  GFF tmp = (GFF)gfffeat.elementAt(i);
	  String type = tmp.getType();
	  Color c;
	  
	  if (typeorder.keySet().contains(type) == false) {
	    c = new Color((int)(Math.random()*200+50),(int)(Math.random()*200+50),(int)(Math.random()*200+50));
	  } else {
	    c = (Color)(typeorder.get(type));
	  }
	  TrackSelectionPanel tsp = new TrackSelectionPanel(type,c,true,true);

	  jf.getContentPane().add(tsp);
	}
      }
      
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      
      jf.setLocation(dim.width/5,
		     dim.height/10);
      
      
      
      jf.pack();
      jf.setVisible(true);
    } catch (IOException e) {
      System.out.println("Exception " + e);
    }
  }
}

class ColourPanel extends JPanel {
  
  public Color color;
  public int   size = 30;
  
  public ColourPanel(Color color) {
    this.color = color;
  }
  
  public void paintComponent(Graphics g) {
    g.setColor(color);
    g.fillRect(1,1,size-2,size-2);
  }
  
  public Dimension getPreferredSize() {
    return new Dimension(size,size);
  }
}
