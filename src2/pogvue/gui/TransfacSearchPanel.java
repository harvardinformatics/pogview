package pogvue.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

import pogvue.datamodel.*;
import pogvue.gui.*;
import pogvue.gui.hub.*;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class TransfacSearchPanel extends JPanel implements ItemListener,
                                                           ActionListener {
  TransfacTableModel model;

  JLabel      l1;
  JCheckBox   b1;
  
  JLabel      l2;
  JTextField  t2;

  public TransfacSearchPanel(TransfacTableModel model) {
    this.model  = model;

    init();
  }

  public void init() {

    // label and radiobutton for info/constant height

    l1 = new JLabel("Size by info. content");
    b1 = new JCheckBox();

    // label and textfield for search

    l2 = new JLabel("Search:");
    t2 = new JTextField();

    
    FormLayout layout = new FormLayout("pref, pref:grow",   //cols
				       "pref, pref");       //rows
    
    setLayout(layout);
    
    CellConstraints cc = new CellConstraints();
    
    add(l1,          cc.xy(1,1, "fill, fill"));    // col, row
    add(b1,          cc.xy(2,1, "fill, fill"));    // col, row
    add(l2,          cc.xy(1,2, "fill, fill"));    // col, row
    add(t2,          cc.xy(2,2, "fill, fill"));    // col, row


    b1.addItemListener(this);
    b1.addItemListener(model);
    
    t2.addActionListener(this);
    t2.addActionListener(model);
    
  }

  public void itemStateChanged(ItemEvent e) {
    if (e.getSource() == b1) {

      if (e.getStateChange() == ItemEvent.DESELECTED) {
	model.setSizeByInfo(false);
      } else {
	model.setSizeByInfo(true);
      }
    }
  }

  public void actionPerformed(ActionEvent evt) {
    String text = t2.getText();

    System.out.println("Text " + text);

    model.searchText(text);
  }
  public static void main(String[] args) {

    JFrame jf = new JFrame();

    TransfacSearchPanel tsp = new TransfacSearchPanel(null);

    jf.getContentPane().setLayout(new BorderLayout());
    jf.getContentPane().add("Center",tsp);

    jf.setSize(300,100);
    jf.setVisible(true);
  }
}    

    