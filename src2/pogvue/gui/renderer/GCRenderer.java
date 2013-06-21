package pogvue.gui.renderer;

import pogvue.datamodel.Sequence;
import pogvue.gui.AlignViewport;

import java.awt.*;
import java.util.Hashtable;
import java.util.Vector;

public final class GCRenderer implements RendererI {
    protected final Color   color = Color.black;
        
    public void drawSequence(Graphics2D g, Sequence seq,int start, int end, int x1, int y1, double width, int height,boolean showScores, boolean displayBoxes, boolean displayText,Vector pid, int seqnum,AlignViewport av, Hashtable props, int intpid[][]) {
	
	int i;
	int length = seq.getLength();

	Color currentColor = Color.white;

	g.setColor(Color.black);

        int window = 500;
        int max    = window; 

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
	
	String str = seq.getSequence();
	
	i = cpgstart;

	while (i < cpgend) { 
	    
	    int num = 0;

	    int j = i-window/2;

	    g.setColor(Color.gray);

	    while (j < i + window/2) {
		if (str.substring(j,j+1).equals("G") ||
		    str.substring(j,j+1).equals("C")) {
		    num++;
		}
		j++;
	    }
	    
	    g.fillRect(x1 + (int)(width*(i-start)),
		       y1+height - num*height/max,
		       (int)(step*width+1),
                num*height/max);
				  
	    i += step;
	}
    }
}
