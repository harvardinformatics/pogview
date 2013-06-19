package pogvue.gui;

// test comment

import java.util.*;
import java.io.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;

import javax.swing.*;

import com.jgoodies.forms.layout.*;
import com.jgoodies.forms.builder.*;

import pogvue.datamodel.*;
import pogvue.datamodel.expression.*;
import pogvue.gui.event.*;
import pogvue.gui.hub.*;
import pogvue.analysis.*;
import pogvue.io.*;
import pogvue.util.*;


public class ExpressionPanel extends JPanel implements ActionListener, MouseListener {
  
  public Vector  exp_levels;
  public Vector  grids = null;
  public Font    font = new Font("Helvetica",Font.PLAIN,10);

  public boolean   absolute = false;
  public int       minval = (int)1e10;
  public int       maxval;
  public JCheckBox b;
  public JTextField textfield;
  public Vector    labels;

  public ExpressionPanel(Vector exp_levels) {
    this(exp_levels,false);
  }

  public ExpressionPanel(Vector exp_levels, boolean absolute) {
    this.exp_levels = exp_levels;

    // First the layout

    setLayout(new BorderLayout());

    // Add the tissue row
    Vector level0 = (Vector)(exp_levels.elementAt(0));

    TissueGrid tg = new TissueGrid(level0);

    FormLayout layout1 = new FormLayout("right:165dlu, 4dlu, pref:grow 7dlu","");     // 2 columns    

    DefaultFormBuilder builder1 = new DefaultFormBuilder(layout1); 

    builder1.setDefaultDialogBorder();
    builder1.setLineGapSize(Sizes.pixel(1));
    builder1.append("tissue",tg);
    
    FormLayout layout2 = new FormLayout("right:110dlu,right:40dlu,right:25dlu, 4dlu, pref:grow 7dlu","");     // 6 columns
    


    // Add rows dynamically
    
    DefaultFormBuilder builder = new DefaultFormBuilder(layout2);
    //PanelBuilder builder = new PanelBuilder(layout2); 
    //builder.setDefaultDialogBorder();
    builder.setLineGapSize(Sizes.pixel(0));

    if (absolute) {
      setMinMax();
      
      System.out.println("Min/Max " + minval + " " + maxval);

    }

    grids  = new Vector();
    labels = new Vector();

    setFont(font);

    for (int i = 0; i < exp_levels.size(); i++) {
      
      ExpressionGrid grid = new ExpressionGrid((Vector)(exp_levels.elementAt(i)));

      grids.addElement(grid);
      grid.setAbsoluteValues(absolute);

      if (absolute == true) {
	grid.setMinValue(minval);
	grid.setMaxValue(maxval);
      }

      Vector exp  = (Vector)exp_levels.elementAt(i);
      ExpressionLevel e = (ExpressionLevel)exp.elementAt(0);
      CellConstraints cc = new CellConstraints(); 

      builder.appendRow("10dlu");
      JLabel l1 = new JLabel(e.getEnsg());
      JLabel l2 = new JLabel(e.getName());
      JLabel l3 = new JLabel(e.getCluster());
      JLabel l4 = new JLabel(e.getPfam());

      l1.setFont(font);
      l2.setFont(font);
      l3.setFont(font);
      l4.setFont(font);

      //      builder.add(l1, cc.xy(1,  1*i+1)); 
      builder.add(l4, cc.xy(1,  1*i+1)); 
      builder.add(l2, cc.xy(2,  1*i+1)); 
      builder.add(l3, cc.xy(3,  1*i+1));
      builder.add(grid,        cc.xy(5,  1*i+1));

      //      builder.nextLine();
    }

    // Now add the checkbox

    JPanel p3 = new JPanel();
    p3.setLayout(new BorderLayout());

    b = new JCheckBox();
    b.setSelected(absolute);
    b.addActionListener(this);

    textfield = new JTextField();
    textfield.addActionListener(this);
    
    p3.add(b,BorderLayout.WEST);
    p3.add(textfield,BorderLayout.CENTER);


    JPanel p = builder.getPanel();

    p.addMouseListener(this);

    JScrollPane jsp  = new JScrollPane(p);

    setLayout(new BorderLayout());
    add(builder1.getPanel(),BorderLayout.NORTH);
    add(jsp,BorderLayout.CENTER);
    add(p3,BorderLayout.SOUTH);
    
  }

  public void mouseEntered(MouseEvent evt) { }
  public void mouseExited (MouseEvent evt) { }
  public void mouseClicked(MouseEvent evt) { 
    int x = evt.getX();
    int y = evt.getY();
      
    System.out.println("XY " + x + " " + y);
      
    int r = (int)((y - 0)/ 13);

    Vector v = (Vector)exp_levels.elementAt(r);

    ExpressionLevel e = (ExpressionLevel)v.elementAt(0);
    
    System.out.println("E " + e.getName() + " " + e.getPfam());

  }
  public void mouseMoved  (MouseEvent evt)  { }
  public void mousePressed(MouseEvent evt)  { }
  public void mouseDragged(MouseEvent evt)  { }
  public void mouseReleased(MouseEvent evt) { }

  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == b) {
      setMinMax();
      if (b.isSelected() != absolute) {
	absolute = b.isSelected();
	System.out.println("Setting selected");

	for (int i = 0; i < grids.size(); i++) {
	  ExpressionGrid g = (ExpressionGrid)grids.elementAt(i);

	  g.setAbsoluteValues(absolute);
	}
	repaint();
      }
    } else if (e.getSource() == textfield) {
      String text = textfield.getText();
      System.out.println("Text " + text);

      Vector level0 = (Vector)(exp_levels.elementAt(0));

      int sortcol = -1;

      for (int i = 0; i < level0.size(); i++) {
	ExpressionLevel l = (ExpressionLevel)level0.elementAt(i);

	if (l.getTissue().equals(text)) {
	  sortcol = i;
	  i = level0.size();
	}
      }

      if (sortcol > -1) {

	for (int i = 0; i < exp_levels.size(); i++) {
	  Vector v = (Vector)exp_levels.elementAt(i);

	  for (int j = 0; j < v.size(); j++) {
	    ExpressionLevel level = (ExpressionLevel)v.elementAt(j);
	    level.setSortCol(sortcol);
	  }
	}
	
	Collections.sort(exp_levels,new ExpressionVectorComparer());
	Collections.reverse(exp_levels);

	for (int i = 0; i < grids.size(); i++) {
	  ExpressionGrid g = (ExpressionGrid)grids.elementAt(i);
	  Vector         v = (Vector)exp_levels.elementAt(i);

	  g.setLevels(v);


	  JLabel label = (JLabel)labels.elementAt(i);
	  label.setText(((ExpressionLevel)v.elementAt(0)).getGene());
	}
	repaint();
      }
    }
  }
  public void setMinMax() {
    if (exp_levels != null) {
      Vector tmp = new Vector();

      minval = (int)1e10;
      maxval = 0;

      for (int i = 0; i < exp_levels.size(); i++) {
	Vector v = (Vector)exp_levels.elementAt(i);

	for (int j = 0; j < v.size(); j++) {
	  ExpressionLevel l = (ExpressionLevel)v.elementAt(j);

	  tmp.addElement(new Integer(l.getLevel()));

	  if (l.getLevel() > maxval) {
	    maxval = l.getLevel();
	  }
	  
	  if (l.getLevel() < minval) {
	    minval = l.getLevel();
	  }
	}
	//Collections.sort(v,new ExpressionLevelComparer());
	//ExpressionLevel l = (ExpressionLevel)v.lastElement();

	//System.out.println("Max " + l.getGene() + " " + l.getLevel());


      }

      minval = 10;
      maxval = 20;

      for (int i = 0; i < grids.size(); i++) {
	ExpressionGrid grid = (ExpressionGrid)grids.elementAt(i);
	
	grid.setMinValue(minval);
	grid.setMaxValue(maxval);
      }

      Collections.sort(tmp);

    }
  }
  public void setAbsoluteValues(boolean val) {
    absolute = val;
  }
    
  public Dimension preferredSize() {
    if (exp_levels != null) {
      return new Dimension(79*10 + 100,10*exp_levels.size());
    } else {
      return new Dimension(400,300);
    }
  }

  public Dimension minimumSize() {
    return preferredSize();
  }

  public static void main(String[] args) {

    try {
      Hashtable opts = GetOptions.get(args);
      String    expfile = null;
      int       sortcol = -1;
      boolean   absolute = false;

      if (opts.containsKey("-absolute")) {
	absolute = Boolean.parseBoolean((String)opts.get("-absolute"));
      }
      if (opts.containsKey("-expfile")) {
	expfile = (String)opts.get("-expfile");
      }
      if (opts.containsKey("-sort")) {
	sortcol = Integer.parseInt((String)opts.get("-sort"));
      }
      
      ExpFile ef = new ExpFile(expfile,"File");
      
      Vector exp_levels = ef.getExpLevels();

      if (sortcol > 0) {
	for (int i = 0; i < exp_levels.size(); i++) {
	  Vector v = (Vector)exp_levels.elementAt(i);

	  for (int j = 0; j < v.size(); j++) {
	    ExpressionLevel level = (ExpressionLevel)v.elementAt(j);
	    level.setSortCol(sortcol);
	  }
	}

	Collections.sort(exp_levels,new ExpressionVectorComparer());
      }

      JFrame jf = new JFrame();

      ExpressionPanel ep = new ExpressionPanel(exp_levels,absolute);
      jf.add(ep);
      jf.setSize(400,300);
      jf.setVisible(true);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

      
  



	
    
