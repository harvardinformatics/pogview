package pogvue.gui.hub;

import java.io.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

import pogvue.datamodel.*;
import pogvue.io.*;
import pogvue.datamodel.GFF;

public  class GetFeatureThread extends Thread {
	private String         idstr;
	private String         chrstr;
	private String         startstr;
	private String         endstr;
    
	private GFFFile        gff;
	private GraphFile      grf;
	private FastaFile      ff;

	private ActionListener l;
	private Vector         feat;

	private int done = 0;
   
	private boolean genes = true;
	private boolean repeats = true;
	private boolean omegas  = true;

    public GetFeatureThread(String chrstr,String startstr, String endstr, ActionListener l, boolean genes, boolean repeats,boolean omegas) {
	this.chrstr   = chrstr;
        this.startstr = startstr;
        this.endstr   = endstr;
    
        this.genes = genes;
        this.repeats = repeats;
        this.omegas  = omegas;

        this.l     = l;
    }
   
  
	
    public String getRegionString() {
      return  "query=" + chrstr + "&start=" + startstr + "&end=" + endstr + "&z=2";
    }
    
    public long getSize() {
	//return gff.getEstimatedSize();
	return 100;
    }
    public void run() {
	
  	try {
            if (genes && !repeats){
	      gff = GenomeInfoFactory.getRegionGenes(getRegionString(),null);
            } 
            if (repeats && !genes) {
	      gff = GenomeInfoFactory.getRegionRepeats(getRegionString());
            }
            if (repeats && genes) {
	      gff = GenomeInfoFactory.getRegionFeatures(getRegionString(),null);
            }

	    if (omegas) {
	      grf = GenomeInfoFactory.getRegionGraph(getRegionString(),null);
	    }
            //System.out.println("Got Features");
            gff.setActionListener(l);
	    gff.parse();
	    
	    //System.out.println("Got graph features");
	    if (omegas) {
	      grf.setActionListener(l);
	      grf.parse();
	    }
	    feat = new Vector();

	    // First a fake top string
	    StringBuffer tmp = new StringBuffer();

            for (int i = 0; i < (Integer.parseInt(endstr)-Integer.parseInt(startstr)+1);i++) {
		tmp.append("N");
	    }
	    feat.addElement(new Sequence("tmp",tmp.toString()));

	    // Now the genes and repeats
	    Vector tmpfeat = gff.getGFFFeatures();

	    for (int i = 0; i < tmpfeat.size(); i++){
	      //		System.out.println("Feat " + tmpfeat.elementAt(i));
		GFF tmpgff = (GFF)tmpfeat.elementAt(i);
		SequenceFeature sf = tmpgff.getFeatureAt(0);
		feat.addElement(tmpfeat.elementAt(i));
	    }

	    // And now the graph features.
	    if (omegas) {
	      Hashtable   fhash  = grf.getFeatureHash();
	      Enumeration en     = fhash.keys();
	    
	      while (en.hasMoreElements()) {
		String type = (String)en.nextElement();
		GFF g = (GFF)fhash.get(type);
                
		if (type.equals("12merpvalue") || type.equals("omega") || type.equals("pi")) {
		  GFF f = new GFF("","",1,2);
		  feat.addElement(f);
		  feat.addElement(f);
		  feat.addElement(f);
		  feat.addElement(f);
		}
		feat.addElement(g);
		
		//System.out.println("features " + g);
	      }
	    }

	    
            //System.out.println("Features are " + feat);

            done = 1;
     
	    //getFragment();
            ActionEvent e = new ActionEvent(this,0,"Done");

	    if (l != null) {
	      
		l.actionPerformed(e);
	    }

	} catch (IOException e1) {
	    e1.printStackTrace();	
	}
    }
    public Vector getOutput() {
	return feat;
    }
    public int isDone() {
	return done;
    }
}
