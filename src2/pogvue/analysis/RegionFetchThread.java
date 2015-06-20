package pogvue.analysis;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import pogvue.datamodel.*;
import pogvue.gui.*;
import pogvue.gui.hub.*;

public class RegionFetchThread extends Thread implements ActionListener {

  private String     chr;
  private int        start;
  private int        end;

  private ActionListener l;
  private Alignment al = null;

  private JFrame    jf;
  private Vector    feat;
  public  boolean human = true;

  public RegionFetchThread(String chr,int start, int end,Vector feat) {
	this.chr        = chr;
	this.start      = start;
	this.end        = end;
	this.feat       = feat;

    }

  public RegionFetchThread(ChrRegion reg) {
    this.chr = reg.getChr();
    this.start = reg.getStart();
    this.end   = reg.getEnd();
    this.feat  = new Vector();
  }
  public void setActionListener(ActionListener l) {
    this.l = l;
  }
  public Alignment getOutput() {
    return al;
  }

  @Override
	public void run() {

    ProgressPanel progressPanel = new ProgressPanel(0,100,this);

    jf = new JFrame();

    int width = 200;
    int height = 50;

    jf.getContentPane().setLayout(new BorderLayout());
    jf.getContentPane().add(progressPanel, BorderLayout.PAGE_START);
    jf.setSize(width,height);
    jf.pack();

    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	  
    jf.setLocation(dim.width  / 2 - width / 2,
		   dim.height / 2 - height / 2);


    
    jf.setVisible(true);

    al  = GenomeInfoFactory.requestRegion(chr,start,end,feat, progressPanel);    

    if (l != null) {
      ActionEvent e = new ActionEvent(this,0,"Done");
      
      l.actionPerformed(e);
    }
    jf.setVisible(false);
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("cancel")) {
      jf.setVisible(false);
      stop();
    }
  }
}