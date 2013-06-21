package pogvue.gui.renderer;

import pogvue.datamodel.Alignment;
import pogvue.datamodel.Alignment;
import pogvue.datamodel.GappedSequence;
import pogvue.datamodel.Sequence;
import pogvue.gui.AlignViewport;
import pogvue.io.GappedFastaFile;

import java.awt.*;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

public class InsertDensityRenderer implements RendererI {
    
	public void drawSequence(Graphics2D g,Sequence seq,int start, int end, int x1, int y1, double width, int height,boolean showScores, boolean displayBoxes, boolean displayText,Vector pid, int seqnum,AlignViewport av, Hashtable props, int intpid[][]) {
        Graphics2D g2 = (Graphics2D)g;
	
	int i      = start;
	int length = seq.getLength();

	Color currentColor = Color.white;

	g.setColor(Color.black);


	Alignment al = av.getAlignment();

	int k;

	int window         = 20;

	int currstart = start - window/2;
	int currend;

	if (currstart < 0) {
	    currstart = 0;
	    currend   = window-1;
	}

	while (currstart < end) {

	    currend = currstart + window - 1;

	    k = 1;

	    int totbases       = 0;
	    int totinsertbases = 0;
	    int totinserts     = 0;
	    
	    while (k < al.getHeight()) {

		Sequence s = al.getSequenceAt(k);
		    
		    if (s instanceof GappedSequence &&
			s.getLength() > currend) {

			String  str = s.getSequence().substring(currstart,currend);

			char c[] = str.toCharArray();
			
			int l = 0;
			
			while (l < c.length) {
			    
			    if (c[l] != '-') {
				totbases++;
			    }
			    l++;
			}

			Hashtable ins = ((GappedSequence)s).inserts_by_coord;
			
			int j = currstart;

			while (j <= currend && j < s.getLength()) {

			    if (ins.containsKey(new Integer(j))) {
				String insert = (String)ins.get(new Integer(j));
				
				totinserts++;
				totinsertbases += insert.length();
				
			    }
			
			    j++;
			}
		    }
	   
		    k++;
	    }

	    // Calculate density of inserts per 1000 bases

	    //	    System.out.println("Density " + (currstart + window/2) + " " + totinserts + " " + totbases);		
	    if (totbases > 0) {

		int dens = totinserts * 1000/totbases;

		g.setColor(Color.red);
		
		g.fillRect(x1 + (int)(width*(currstart+window/2-start)),y1,1, dens*height/50);
	    }
	    currstart++;
	}
    }
    public static void main(String[] args) {

	GappedFastaFile g = null;

	try {
	    
	    g = new GappedFastaFile(args[0],"File");

	} catch (IOException e) {
	    e.printStackTrace();
	}

	if (g != null) {
	    
	    Alignment al = new Alignment(g.getSeqsAsArray());
	    
	    
	int window = 20;
	


	int i = window/2;

	while (i < al.getWidth()-window/2) {

	    int start = i - window/2;
	    int end   = i + window/2;

	    int totbases       = 0;
	    int totinsertbases = 0;
	    int totinserts     = 0;

	    int k = 1;
	    
	    while (k < al.getHeight()) {
		
		Sequence s = al.getSequenceAt(k);
		    
		if (s instanceof GappedSequence) {

		    String    str = s.getSequence().substring(start,end);
		    
		    char c[] = str.toCharArray();
		    
		    int l = 0;
		    
		    while (l < c.length) {
			
			if (c[l] != '-') {
			    totbases++;
			}
			l++;
		    }
		    
		    Hashtable ins = ((GappedSequence)s).inserts_by_coord;
		    
		    int j = start;
		    
		    while (j <= end && j < s.getLength()) {
			
			if (ins.containsKey(new Integer(j))) {
			    String insert = (String)ins.get(new Integer(j));
			    
			    totinserts++;
			    totinsertbases += insert.length();
			    
			}
			
			j++;
		    }
		}
		
		k++;
	    }
	    
	    // Calculate density of inserts per 1000 bases

	    int dens = 0;

	    if (totbases > 0) {
		dens = totinserts * 1000/totbases;
	    }

	    System.out.println("Density\t" + i + "\t" + totbases + "\t" + totinserts + "\t" + totinsertbases + "\t" + dens);

	    i++;
	}
    }
    }
}
