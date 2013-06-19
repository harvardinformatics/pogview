package pogvue.gui.renderer;

import pogvue.analysis.AAFrequency;
import pogvue.datamodel.Sequence;
import pogvue.datamodel.Sequence;
import pogvue.gui.AlignViewport;

import java.awt.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public final class CpGRenderer implements RendererI {
    protected final Color   color = Color.black;
        
    public void drawSequence(Graphics2D g, Sequence seq,int start, int end, int x1, int y1, double width, int height,boolean showScores, boolean displayBoxes, boolean displayText,Vector pid, int seqnum,AlignViewport av, Hashtable props,int intpid[][]) {
	
	int i      = start;
	int length = seq.getLength();

	Color currentColor = Color.white;

	g.setColor(Color.black);

        int window = 500;
        int max    = 60; 

        int step = window/10;

        if (step > window) {
          step = window;
        }

        if (window < 2) {
           window = 2;
           step = 1;
        }

        int cpgstart = start;
        int cpgend   = end;

	if (cpgstart <= window/2 ) {
	    cpgstart = window/2;
	}
	if (cpgend+window/2 >= length) {
	    cpgend = length-window/2;
	}

        Vector v = new Vector();
	v.addElement(new Sequence("CpG","CG"));
        Sequence seq0     = av.getAlignment().getSequenceAt(0);
	Hashtable vals     = AAFrequency.findKmerCount(seq0,seq,cpgstart,cpgend,window,step,v);
	Vector    peaks    = AAFrequency.findKmerPeaks(seq0,seq,cpgstart,cpgend,window,step,v);
	Hashtable consvals = AAFrequency.findConservedKmerCount(seq0,seq,cpgstart,cpgend,window,step,v);
	
	Enumeration en = vals.keys();
	Enumeration en2 = consvals.keys();

	Vector valset = new Vector();
	Vector consvalset = new Vector();
	
	while (en.hasMoreElements()) {
	    valset.add(en.nextElement());
	    consvalset.add(en2.nextElement());
	}

	Collections.sort(valset);
	Collections.sort(consvalset);
	
	g.setColor(Color.black);
	
	int j = 0;

	while (j < valset.size()) { 
	    
	    Integer posInt = (Integer)valset.elementAt(j);
	    int     count  = (Integer) vals.get(posInt);
	    int     conscount  = (Integer) consvals.get(posInt);
	    int     pos    = posInt;

	    if (count > 30) {
		g.setColor(Color.orange);
            } else { 
               g.setColor(Color.lightGray);
            }

	    
	    g.fillRect(x1 + (int)(width*(pos-start-width/2)),
		       y1+height - count*height/max,
		       (int)(step*width+1),
                count*height/max);
	    
	    j++;
	}
    }
}
