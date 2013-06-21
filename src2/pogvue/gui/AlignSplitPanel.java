package pogvue.gui;

import java.util.*;
import java.io.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import javax.swing.*;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import pogvue.datamodel.*;
import pogvue.gui.event.*;
import pogvue.gui.hub.*;
import pogvue.gui.menus.*;
import pogvue.io.*;
import pogvue.util.*;

public class AlignSplitPanel extends JPanel implements StatusListener {

  private AlignmentPanel ap1;
  private AlignmentPanel ap2;

  private JSplitPane     jsp;

  private JPanel sp;
  private JLabel position;
  private JLabel fname;
  private JLabel fscore;

  public AlignSplitPanel(Alignment al,String title, double width1, double width2) {

    setLayout(new BorderLayout());

    AlignViewport av1 = new AlignViewport(al);
    AlignViewport av2 = new AlignViewport(al);

    LinkedHashMap conf = av1.getGFFConfig();

    for (int i = 0; i < al.getHeight();i++) {
      Sequence seq = al.getSequenceAt(i);
      
      if (seq instanceof GFF) {
	GFF g = (GFF)seq;

	if (conf != null && !conf.containsKey(g.getType())) {
	  Color c = new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
	  conf.put(g.getType(),c);
	}
      }
    }
    av2.setGFFConfig(conf);
    Controller    c  = new Controller();

    ap1 = new AlignmentPanel(av1,c);
    ap2 = new AlignmentPanel(av2,c);

    av1.setController(c);
    av2.setController(c);

    MenuManager m = new MenuManager(this, av1, c);

    
    av1.setMinipog(ap2);
    av1.setCharWidth(width1,"pog");
    av1.setCharHeight(10);
    av1.setStartRes(0);

    ap2.getAlignViewport().setCharWidth(width2,"pog");
    ap2.getAlignViewport().setCharHeight(10);
    ap2.getAlignViewport().setStartRes(al.getWidth()/2);
    jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT,ap1,ap2);
        
    jsp.setOneTouchExpandable(true);
    jsp.setDividerLocation(1.0D);

    // These make sure the JSPlitPane can resize the panels.
    ap1.setMinimumSize(new Dimension(0,0));
    ap2.setMinimumSize(new Dimension(0,0));
	
    jsp.setSize(1500,1000);

    // This is needed when the panel is brought up from a Choosepanel button.
    ap1.repaint();
    ap2.repaint();
	
    add("Center",jsp);

    sp = new JPanel();

    position  = new JLabel("Position : ");
    fname     = new JLabel("Feature name : ");
    fscore    = new JLabel("Score : ");

    sp.setLayout(new FlowLayout());

    sp.add(position);
    sp.add(fname);
    sp.add(fscore);
    add("South",sp);
    c.addListener(this);
  }

  public void setDividerLocation(int pos) {
    jsp.setDividerLocation(pos);
  }

  public void setDividerLocation(double pos) {
    jsp.setDividerLocation(pos);
  }

  public void setAlignment(Alignment al) {
    ap1.getAlignViewport().setAlignment(al);
    ap2.getAlignViewport().setAlignment(al);

    ap1.repaint();
    ap2.repaint();

  }
  public boolean handleStatusEvent(StatusEvent evt) {
    //System.out.println("Status Event " + evt);

    if (evt.getType() == StatusEvent.POSITION) {
      position.setText("Position : " + evt.getText());
    } else  if (evt.getType() == StatusEvent.FNAME) {
      fname.setText("Feature name : " + evt.getText());
    } else  if (evt.getType() == StatusEvent.FSCORE) {
      fscore.setText("Feature score : " + evt.getText());
    }
    return true;
  }
  public AlignmentPanel getAlignmentPanel1() {
    return ap1;
  }
  public AlignmentPanel getAlignmentPanel2() {
    return ap2;
  }
}



