package pogvue.gui.menus;

import org.jibble.epsgraphics.EpsGraphics2D;
import pogvue.datamodel.*;
import pogvue.io.*;
import pogvue.gui.*;
import pogvue.gui.event.*;
import pogvue.gui.hub.*;


import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.io.*;


public class FileMenu extends PanelMenu implements ActionListener {

  private JMenuItem            make_img;
  private JMenuItem            postscript;
  private JMenuItem            find;

  public JFrame                parent;

  public AlignSplitPanel       asp;

  public FileMenu(JFrame parent,AlignSplitPanel asp,AlignViewport av,Controller c) {
    super("File",asp,av,c);
    this.parent = parent;
    this.asp    = asp;

    init();
  }

  protected void init() {

    make_img   = new JMenuItem("Make image...");
    postscript = new JMenuItem("Postscript...");
    find       = new JMenuItem("Find...");
    
    postscript.setMnemonic(KeyEvent.VK_P);
    find.      setMnemonic(KeyEvent.VK_F);

    postscript.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,ActionEvent.CTRL_MASK));
    find.      setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,ActionEvent.CTRL_MASK));

    make_img  .addActionListener(this);
    postscript.addActionListener(this);
    find.      addActionListener(this);

    add(make_img);
    add(postscript);
    add(find);
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == postscript) {

      JFileChooser fc = new JFileChooser();
      
      int returnVal = fc.showSaveDialog(parent);
      
      File file = fc.getSelectedFile();
      
      if (file != null) {
      	jibbleprint(file,false);
      }
    } else if (e.getSource() == make_img) {
      JFileChooser fc = new JFileChooser();
      
      int returnVal = fc.showSaveDialog(parent);
      
      File file = fc.getSelectedFile();
      
      if (file != null) {
      	jibbleprint(file,true);
      }
    }
  }
  
  private void jibbleprint(File file,boolean useImage) {
    try {

      AlignmentPanel ap1 = asp.getAlignmentPanel1();
      AlignmentPanel ap2 = asp.getAlignmentPanel2();

      FileOutputStream outputStream = new FileOutputStream(file);
      
      ap1.getAlignViewport().useImage(useImage);
      
      EpsGraphics2D g = new EpsGraphics2D("demo",outputStream,0,0,ap1.size().width,asp.size().height);

      System.out.println("Painting postscript");

      asp.getAlignmentPanel1().getAlignViewport().useImage(useImage);
      asp.getAlignmentPanel2().getAlignViewport().useImage(useImage);
      asp.paint(g);
      System.out.println("Done");
	
      g.flush();
      g.close();

      asp.getAlignmentPanel1().getAlignViewport().useImage(true);
      asp.getAlignmentPanel2().getAlignViewport().useImage(true);
	
    } catch (Exception e2) {
      e2.printStackTrace();
    }
    
  }
}
