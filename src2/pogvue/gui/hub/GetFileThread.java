package pogvue.gui.hub;

import java.io.*;
import javax.swing.*;
import java.awt.event.*;
import pogvue.datamodel.Sequence;
import pogvue.datamodel.Sequence;
import pogvue.io.FastaFile;
import pogvue.io.GappedFastaFile;

public  class GetFileThread extends Thread {
    private String         fileStr;
    private ActionListener l;
    private Sequence[]     s;
    private int done = 0;

    public GetFileThread(String fileStr, ActionListener l) {
	this.fileStr = fileStr;
        this.l     = l;
    }
   
    public void run() {
	
  	try {

	    String line;
	    FastaFile ff = GenomeInfoFactory.getFastaFile(fileStr);
          
            ff.setActionListener(l);
	    ff.parse();
            
            s = ff.getSeqsAsArray();
            
            System.out.println("Sequences are " + s);
            done = 1;
            
            ActionEvent e = new ActionEvent(this,0,"Done");
            l.actionPerformed(e);
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
