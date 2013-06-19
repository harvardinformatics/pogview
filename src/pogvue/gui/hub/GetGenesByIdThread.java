package pogvue.gui.hub;

import java.io.*;
import javax.swing.*;
import java.awt.event.*;

public  class GetGenesByIdThread extends Thread {
	private StringBuffer  genes = new StringBuffer();
	private String        idstr;
	private ActionListener l;

    public GetGenesByIdThread(String idstr) {
	this.idstr = idstr;
    }
    public void setActionListener(ActionListener l) {
	this.l = l;
    }
    public void run() {
	
  	try {
	    System.out.println("Runnable Thread " + Thread.currentThread());

	    String line;
	    BufferedReader r = GenomeInfoFactory.getGenesById(idstr);

	    int count = 0;
	     while ((line = r.readLine()) != null) {
	    	genes.append(line + "\n");

		if (count == 0) {
		    System.out.println("line " + line);
		    ActionEvent e = new ActionEvent(this,0,line);
		    l.actionPerformed((ActionEvent)e);
		} else {
		    ActionEvent e = new ActionEvent(this,0,"Line");
		    l.actionPerformed((ActionEvent)e);
		}
		count++;
	     }

	} catch (IOException e1) {
	    e1.printStackTrace();	
	    //	} catch (InterruptedException e) {
	    //e.printStackTrace();
	}
    }
    public StringBuffer getOutput() {
	return genes;
    }
}
