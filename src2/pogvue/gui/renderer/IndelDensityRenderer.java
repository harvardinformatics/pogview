package pogvue.gui.renderer;

import pogvue.datamodel.Alignment;
import pogvue.datamodel.GappedSequence;
import pogvue.datamodel.Sequence;
import pogvue.gui.AlignViewport;

import java.awt.*;
import java.util.Hashtable;
import java.util.Vector;

public class IndelDensityRenderer implements RendererI {
    
	public void drawSequence(Graphics2D g, Sequence seq,int start, int end, int x1, int y1, double width, int height,boolean showScores, boolean displayBoxes, boolean displayText,Vector pid, int seqnum,AlignViewport av, Hashtable props, int intpid[][]) {
        Graphics2D g2 = (Graphics2D)g;
	
	int i      = start;
	int length = seq.getLength();

	Color currentColor = Color.white;

	g.setColor(Color.black);


	Alignment al = av.getAlignment();

	int k;

	int window         = 40;

	int currstart = start - window/2;
	int currend   = start + window/2;

	if (currstart < 0) {
	    currstart = 0;
	    currend   = window-1;
	}


	while (currstart < currend) {
	    currend = currstart + window - 1;

	    k = 0;

	    int totbases       = 0;
	    int totinsertbases = 0;
	    int totinserts     = 0;
	    
	    while (k < al.getHeight()) {

		Sequence s = al.getSequenceAt(k);
		String    str = s.getSequence().substring(start + currstart,start + currend);

		char c[] = str.toCharArray();

		int l = 0;

		while (l < c.length) {
		    if (c[k] != '-') {
			totbases++;
		    }
		    l++;
		}
		
		if (s instanceof GappedSequence) {
		    Vector inserts = ((GappedSequence)s).inserts;
		    
		    int j = 0;

		    while (j < inserts.size()) {
			Hashtable ins  = (Hashtable)inserts.elementAt(j);
			
			if (ins.containsKey("Coord")) {
			    int coord = (Integer) (ins.get("Coord"));
			    
			    String insert = (String)ins.get("String");
			    
			    if (coord >= start && coord <= end) {
				
				totinserts++;
				totinsertbases += insert.length();
				
			    }
			} else {
			    int coord = (Integer) (ins.get("Hcoord"));
			    
			    String insert = (String)ins.get("String");
			    
			    if (coord >= start && coord <= end) {
				
				totinserts++;
				totinsertbases += insert.length();
				
			    }
			}
			j++;
		    }
		}
		k++;
	    }

	    // Calculate density of inserts per 1000 bases

	    int dens = totinserts * 1000/totbases;

	    g.setColor(Color.red);

	    g.fillRect(x1 + (int)(width*(currstart+window/2-start)),y1,1, dens*height/100);

	    currstart++;
	}
    }

}
