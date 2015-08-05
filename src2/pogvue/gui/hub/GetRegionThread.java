package pogvue.gui.hub;

import java.io.*;
import javax.swing.*;
import java.awt.event.*;
import pogvue.datamodel.Sequence;
import pogvue.io.FastaFile;
import pogvue.io.GappedFastaFile;

public  class GetRegionThread extends Thread {
    private String         idstr;
    private String         chrstr;
    private String         startstr;
    private String         endstr;
    
    private GappedFastaFile ff;
    
    private ActionListener l;
    private Sequence[]     s;
    private int done = 0;
    
    private String org;

  public GetRegionThread(String chrstr,String startstr, String endstr, ActionListener l,String org) {
    this(chrstr,startstr,endstr,l);
    this.org = org;
  }
    public GetRegionThread(String chrstr,String startstr, String endstr, ActionListener l) {
	this.chrstr   = chrstr;
        this.startstr = startstr;
        this.endstr   = endstr;
    
        this.l     = l;
    }
 
    public String getRegionString() {
      if (org == null) {
	return  "query=" + chrstr + "&start=" + startstr + "&end=" + endstr + "&z=2";
      } else {
	return  "query=" + chrstr + "&start=" + startstr + "&end=" + endstr + "&z=2" + "&org=" + org;
      }
    }
    
    public long getSize() {
      return ff.getEstimatedSize();
    }
    public void run() {
	
  	try {

	    String line;
	    ff = GenomeInfoFactory.getRegion(getRegionString());
          
            ff.setEstimatedSize(40*(Integer.parseInt(endstr) - Integer.parseInt(startstr)));
            ff.setActionListener(l);
	    ff.parse();
            
            s = ff.getSeqsAsArray();
            
            done = 1;
            
            ActionEvent e = new ActionEvent(this,0,"Done");
	    if (l != null) {
		l.actionPerformed(e);
	    }
	} catch (IOException e1) {
	    e1.printStackTrace();	
	}
    }
    public Sequence[] getOutput() {
	return s;
    }
    public int isDone() {
	return done;
    }
}
