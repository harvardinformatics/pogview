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


public class TrackSelectionFrame extends JFrame implements MouseListener, ChangeListener, ItemListener {
  Vector        gfffeat;
  LinkedHashMap typeorder;
  JColorChooser jcc;
  Vector        panels;
  JFrame        jf;
  TrackSelectionPanel selected_tsp;
  Controller    c;
  AlignViewport av;

  public TrackSelectionFrame(Vector gfffeat, LinkedHashMap typeorder, AlignViewport av) {
    this.gfffeat   = gfffeat;
    this.typeorder = typeorder;
    this.av        = av;
    this.c         = av.getController();

    componentInit();
  }
  public void componentInit() {

    panels = new Vector();

    Hashtable done = new Hashtable();

    for (int i = 0; i < gfffeat.size(); i++) {

      if (gfffeat.elementAt(i) instanceof GFF) {
	GFF tmp = (GFF)gfffeat.elementAt(i);
	String type = tmp.getType();
	Color c;
	
	if (typeorder.keySet().contains(type) == false) {
	  c = new Color((int)(Math.random()*200+50),(int)(Math.random()*200+50),(int)(Math.random()*200+50));
	  typeorder.put(type,c);
	} else {
	  c = (Color)(typeorder.get(type));
	}
	if (type != null && !done.containsKey(type)) {
	  TrackSelectionPanel tsp = new TrackSelectionPanel(type,c,!av.isHiddenType(type),true);
	  panels.addElement(tsp);
	  tsp.addMouseListener(this);
	  tsp.display_box.addItemListener(this);
	  tsp.collapse_box.addItemListener(this);
	  done.put(type,type);

	}
      }
    }

    getContentPane().setLayout(new GridLayout(panels.size(),1));

    for (int i = 0; i < panels.size(); i++) {
      TrackSelectionPanel tsp = (TrackSelectionPanel)panels.elementAt(i);
      getContentPane().add(tsp);
    }

  }
  public void updateGFF() {

    for (int i = 0; i < panels.size(); i++) {
      TrackSelectionPanel tsp = (TrackSelectionPanel)panels.elementAt(i);

      if (tsp.display_box.isSelected()) {
	av.showType(tsp.name);
      } else {
	av.hideType(tsp.name);
      }
    }
    c.handleAlignViewportEvent(new AlignViewportEvent(this,null,AlignViewportEvent.COLOURING));
  }
  public void stateChanged(ChangeEvent e) {
    System.out.println("State changed " + e);
    Color newColor = jcc.getColor();
    
    if (selected_tsp != null) {
      selected_tsp.colour_panel.color = newColor;
      selected_tsp.color = newColor;
      typeorder.put(selected_tsp.name,newColor);
      repaint();

      if (c != null) {
	System.out.println("New event");
	c.handleAlignViewportEvent(new AlignViewportEvent(this,null,AlignViewportEvent.COLOURING));
      }
    }
    
  }

  public void mouseClicked(MouseEvent evt) {
    System.out.println("Clicked " + evt);

    if (evt.getSource() instanceof TrackSelectionPanel) {
      selected_tsp = (TrackSelectionPanel)evt.getSource();
      
      if (jcc == null) {
	jcc = new JColorChooser(selected_tsp.color);
	jcc.getSelectionModel().addChangeListener(this);
	jcc.setBorder(BorderFactory.createTitledBorder("Choose Track Color"));
      
	jf = new JFrame();
	jf.setLayout(new BorderLayout());
	jf.getContentPane().add("Center",jcc);
	jf.setSize(500,300);

	Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	
	jf.setLocation(dim.width/2 - 250,
		       dim.height/2 - 150);
	


	jf.setVisible(true);
      } else {
	jcc.setColor(selected_tsp.color);
      }
      jf.setVisible(true);
    }      
  }

  public void itemStateChanged(ItemEvent e) {

    Object source = e.getItemSelectable();

    System.out.println("Source " + source);
    
    if (e.getStateChange() == ItemEvent.DESELECTED) {
      System.out.println("Deselected " + source);
    } else if (e.getStateChange() == ItemEvent.SELECTED) {
      System.out.println("Selected " + source);
    }

    updateGFF();


  }

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
	
      TrackSelectionFrame tsf = new TrackSelectionFrame(gfffeat,typeorder,null);

      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      
      tsf.setLocation(dim.width/5,
		      dim.height/10);
      
      
      
      tsf.pack();
      tsf.setVisible(true);
    } catch (IOException e) {
      System.out.println("Exception " + e);
    }
  }
}
