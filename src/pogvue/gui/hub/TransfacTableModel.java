package pogvue.gui.hub;

import java.io.*;
import java.util.*;

import pogvue.io.*;
import pogvue.datamodel.*;
import pogvue.gui.*;
import pogvue.datamodel.motif.*;


import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.*;

import java.awt.event.*;
import java.awt.*;
import java.awt.print.*;

/**
 *
 * @author Michele
 */

public class TransfacTableModel extends AbstractTableModel implements KeyListener,
							   ItemListener,
							   ActionListener {
  Vector matrices;
  Vector selected;

    String[] colnames = {"Name","Logo","Description"};
  
  JTable table;
  LogoPanel logo;

  JScrollPane jsp;

  boolean sizeByInfo = false;

  public TransfacTableModel(Vector matrices) {
    this.matrices = matrices;
    
    selected = new Vector();
  }
  
  public void addSelected(TFMatrix mat) {
    if (!selected.contains(mat)) {
      selected.addElement(mat);
    }
  }

  public void clearSelected() {
    selected.removeAllElements();
  }

  public int getRowCount() {
    if (selected.size() > 0) {
      return selected.size();
    } else {
      return matrices.size();
    }
  }
  
  public int getColumnCount() {
    return colnames.length;
  }
  public String getColumnName(int col) {
    return colnames[col];
  }
  
  public Class getColumnClass(int col) {
    return getValueAt(0,col).getClass();
  }
  
  public Object getValueAt(int row, int col) {
    TFMatrix mat = (TFMatrix)matrices.elementAt(row);
    
    if (selected.size() > 0) {
      mat = (TFMatrix)selected.elementAt(row);
    }

    if (col == 0) {
      return mat.getName();
    } else if (col == 2) {
      return mat.getDesc();
    } else if (col == 1) {
      return new LogoPanel(mat);
    }
    return "";
  }
  
  public void setTable(JTable t) {
    this.table = t;
  }
  
  public void itemStateChanged(ItemEvent e) {
    if (e.getSource() instanceof JCheckBox) {
      table.repaint();
    }
  }

  public void actionPerformed(ActionEvent evt) {
    table.repaint();    
  
    table.revalidate();

  }
  public void setScrollPane(JScrollPane jsp) {
    this.jsp = jsp;
  }
  public void searchText(String str) {
    clearSelected();

    str = str.toUpperCase();

    for (int i = 0; i < matrices.size(); i++) {

      TFMatrix tfm = (TFMatrix)matrices.elementAt(i);

      if (tfm.getName().toUpperCase().indexOf(str) >= 0 ||
	  tfm.getDesc().toUpperCase().indexOf(str) >= 0) {
	addSelected(tfm);
      }
    }
  }
  public void keyTyped(KeyEvent e) {
    displayInfo(e, "KEY TYPED: ");
    
    if (e.getKeyChar() == 'p') {
      PrinterJob job = PrinterJob.getPrinterJob();
      job.setPrintable(new TransfacPrinter(matrices));
      boolean doPrint = job.printDialog();
      
      if (doPrint) {
	try {
	  job.print();
	} catch (PrinterException pe) {
	  System.out.println("The job didn't complete");
	}
      }
    }
  }
  public void keyPressed(KeyEvent e) {
    displayInfo(e, "KEY PRESSED: ");
  }
  public void keyReleased(KeyEvent e) {
    displayInfo(e, "KEY RELEASED: ");
  }
  private void displayInfo(KeyEvent e, String keyStatus){
    // Should only rely on the key char if the event is a key typed event
    
    int id = e.getID();
    
    String keyString;
    
    if (id == KeyEvent.KEY_TYPED) {
      char c = e.getKeyChar();
      keyString = "key character = '" + c + "'";
    } else {
      int keyCode = e.getKeyCode();
      keyString = "key code = " + keyCode + " (" + KeyEvent.getKeyText(keyCode) + ")";
    }
    
    int modifiersEx = e.getModifiersEx();
    
    String modString = "extended modifier = " + modifiersEx;
    String tmpString = KeyEvent.getModifiersExText(modifiersEx);
    
    if (tmpString.length() > 0) {
      modString += " (" + tmpString + ")";
    } else {
      modString += " (no extended modifiers)";
    } 
    
    String actionString = "action key? ";
    
    if (e.isActionKey()) {
      actionString += "YES";
    } else {
      actionString += "NO";
    }
    
    String locationString = "key location: ";
    int location =  e.getKeyLocation();
    
    if (location == KeyEvent.KEY_LOCATION_STANDARD) {
      locationString += "standard";
    } else	if (location == KeyEvent.KEY_LOCATION_LEFT) {
      locationString += "left";
    } else	if (location == KeyEvent.KEY_LOCATION_RIGHT) {
      locationString += "right";
    } else	if (location == KeyEvent.KEY_LOCATION_NUMPAD) {
      locationString += "numpad";
    } else	if (location == KeyEvent.KEY_LOCATION_UNKNOWN) {
      locationString += "unknown";
    } 
    
    System.out.println(keyStatus + " " + keyString + " " + modString +  " " + actionString + " " + locationString);
  }
  public void setSizeByInfo(boolean sizeByInfo) {
    this.sizeByInfo = sizeByInfo;

    TableCellRenderer r  = table.getDefaultRenderer(LogoPanel.class);

    if (r instanceof TransfacTableLogoRenderer) {
      ((TransfacTableLogoRenderer)r).setSizeByInfo(sizeByInfo);
    }
  }



  public static void main(String[] args) {
    Hashtable opts = pogvue.util.GetOptions.get(args);
    
    String tffile = (String)opts.get("-matrixfile");
    String format = (String)opts.get("-format");

    boolean sort = false;

    if (opts.containsKey("-sort")) {
	sort = true;
    }

    try {
      Vector matrices = null;
      
      if (format.equals("transfac")) {
	TFMatrixFile tfmf  = new TFMatrixFile(tffile,"File");
	matrices = tfmf.getMatrices();


      } else if (format.equals("pwmline")) {
	PwmLineFile pwm = new PwmLineFile(tffile,"File");
	pwm.parse();
	matrices = pwm.getTFMatrices();
      } else if (format.equals("ormat")) {
	OrmatFile omf = new OrmatFile(tffile,"File");
	matrices = omf.getTFMatrices();
      }

      if (sort) {
	//Collections.sort(matrices,new TransfacNameComparer());
	//  Collections.sort(matrices,new TransfacInfComparer());
      }

      TransfacTableModel    tm    = new TransfacTableModel(matrices);
      JTable                table = new JTable(tm);
      TransfacSearchPanel   tsp   = new TransfacSearchPanel(tm);
      
      table.setRowHeight(40);

      //Now the renderer
      
      TableCellRenderer renderer = table.getDefaultRenderer(LogoPanel.class);
      
      table.setDefaultRenderer(LogoPanel.class, new TransfacTableLogoRenderer(renderer));
      
      //table.setFillsViewportHeight(true);
      //table.setAutoCreateRowSorter(true);

      JFrame      jf  = new JFrame(tffile);
      JScrollPane sp  = new JScrollPane(table);

      tm.setTable(table);
      tm.setScrollPane(sp);
      
      table.addKeyListener(tm);
      table.setFocusable(true);
      
      jf.getContentPane().setLayout(new BorderLayout());
      jf.getContentPane().add("North",tsp);
      jf.getContentPane().add("Center",sp);
      
      Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
      
      jf.setLocation(sd.width  / 2 - 900 / 2,
		     sd.height / 2 - 900 / 2);
      
      jf.setSize(900,900);
      
      jf.setVisible(true);
      
    } catch (IOException e) {
      System.out.println("ERROR: " + e);
    }
  }
}

class TransfacTableLogoRenderer implements TableCellRenderer {
  private TableCellRenderer renderer;
  boolean sizeByInfo = false;
  
  public TransfacTableLogoRenderer(TableCellRenderer renderer) {
    this.renderer = renderer;
  }
  public void setSizeByInfo(boolean sizeByInfo) {
    this.sizeByInfo = sizeByInfo;
  }
  public Component getTableCellRendererComponent(JTable table,
						 Object value,
						 boolean isSelected,	
						 boolean hasFocus,
						 int row, 
						 int column) {
    if (value instanceof LogoPanel){
      LogoPanel lp = (LogoPanel)value;
      lp.setSizeByInfo(sizeByInfo);
      return (Component)lp;
    }else { 
      return renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
  }
}



class TransfacPrinter implements Printable {
    private Vector matrices;

    public TransfacPrinter(Vector matrices) {
	this.matrices = matrices;
    }


    //BufferedImage[] images = new BufferedImage[10];

    // Then use the print() method as shown in the following code fragment:

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)  throws PrinterException {
	Graphics2D g2d = (Graphics2D)graphics;

	// 0,0 is usually outside printable area so we have to translate
	g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

	//if (pageIndex < images.length) {
	    // What are arguments to this?
	//    graphics.drawImage(images[pageIndex], 100, 100, null);

	    // Work out which matrix to start and end at based on the size of the logo

	    // Size of logo = 70 pixels high

	    int logoHeight = 70;

	    double pageHeight = pageFormat.getImageableHeight();

	    int logosPerPage = (int)(pageHeight/logoHeight);
	    int numBreaks    = (matrices.size()-1)/logosPerPage;
	    
	    int[] pageBreaks  = new int[numBreaks];
	    
	    for (int b = 0; b < numBreaks; b++) {
		pageBreaks[b] = (b+1)*logosPerPage;
	    }
	
	    int y = 0;
	    int start = (pageIndex == 0) ? 0 : pageBreaks[pageIndex-1];
	    int end   = (pageIndex == pageBreaks.length) 
		? matrices.size() : pageBreaks[pageIndex];

	    for (int logo = start; logo < end; logo++) {
		y+= logoHeight;
		
		// Draw logo number logo at 0,y
	    }
	    return PAGE_EXISTS;   
	    //} else {
	    //return NO_SUCH_PAGE;
	    //}
    }
}
