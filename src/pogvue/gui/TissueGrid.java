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
import pogvue.datamodel.expression.*;
import pogvue.gui.event.*;
import pogvue.gui.hub.*;
import pogvue.analysis.*;
import pogvue.io.*;
import pogvue.util.*;


public class TissueGrid extends JPanel {
  
  public JPanel panel;
  public Vector exp_levels;
  public int    boxsize = 12;
  public Font   font;

  public TissueGrid(Vector exp_levels) {

    this.exp_levels = exp_levels;

    font = new Font("Helvetica",Font.PLAIN,12);
  }

  public void paint(Graphics g) {

    // Now draw the tissue strings

    Graphics2D g2 = (Graphics2D)g;
    g2.setFont(font);
      
    g2.setColor(Color.black);

    g2.translate(0,100);
    g2.rotate(-Math.PI/2.0);

    for (int i = 0; i < exp_levels.size(); i++) {
    
      String tissue = ((ExpressionLevel)exp_levels.elementAt(i)).getTissue();

      g2.drawString(tissue, 0,(i+1)*boxsize);


    }
  }

  public Dimension preferredSize() {
    if (exp_levels != null) {
      return new Dimension(boxsize*exp_levels.size(),100);
    } else {
      return new Dimension(boxsize*79,100);
    }
  }

  public Dimension minimumSize() {
    return preferredSize();
  }

}

      
  



	
    
