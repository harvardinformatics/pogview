package pogvue.gui.renderer;

import pogvue.datamodel.Sequence;
import pogvue.gui.AlignViewport;

import java.awt.*;
import java.util.Hashtable;
import java.util.Vector;

public class StartStopRenderer implements RendererI {
        
	public void drawSequence(Graphics2D g,Sequence seq,int start, int end, int x1, int y1, double width, int height,boolean showScores, boolean displayBoxes, boolean displayText,Vector pid, int seqnum,AlignViewport av, Hashtable props, int intpid[][]) {
	
	int i      = start;
	int length = seq.getLength();

	Color currentColor = Color.white;

	int    prev   = -1;
        int    fstart = 0;
        char   prevc   = '-';

	g.setColor(Color.magenta);

	while (i <= end && i < length) {
	    String str = seq.getSequence().substring(i,i+3);
	    String str2 = seq.getSequence().substring(i,i+1);

	    if (! str2.equals("-")) {
		g.setColor(Color.lightGray);
		g.fillRect(x1+(int)((i-start)*width),y1+height/4,(int)(3*width),height);
		
		if (str.equals("ATG")) {
		    
		    g.setColor(Color.green);
		    g.fillRect(x1+(int)((i-start)*width),y1+height/4,(int)(3*width),height);
		} else if (str.equals("CAT")) {
		    g.setColor(Color.cyan);
		    g.fillRect(x1+(int)((i-start)*width),y1+height/4,(int)(3*width),height);

		} else if (str.equals("TGA") ||
			   str.equals("TAA") ||
			   str.equals("TAG")) {
		    g.setColor(Color.magenta);
		    
		    g.fillRect(x1+(int)((i-start)*width),y1+height/4,(int)(3*width),height);
		}else if ( str.equals("TCA") ||
			   str.equals("TTA") ||
			   str.equals("CTA")) {
		    g.setColor(Color.red);
		    
		    g.fillRect(x1+(int)((i-start)*width),y1+height/4,(int)(3*width),height);

		}
	    }
	    if (width > 5) {
		g.setColor(Color.black);
		g.drawString(seq.getSequence().substring(i,i+1),x1+(int)(width*(i-start)),y1+3*height/4);
	    }
	    
	    i++;
	}

    }
}
