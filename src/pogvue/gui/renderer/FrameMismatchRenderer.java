package pogvue.gui.renderer;

import pogvue.datamodel.Sequence;
import pogvue.gui.AlignViewport;

import java.awt.*;
import java.util.Hashtable;
import java.util.Vector;

public class FrameMismatchRenderer implements RendererI {

	public void drawSequence(Graphics2D g, Sequence seq,int start, int end, int x1, int y1, double width, int height,boolean showScores, boolean displayBoxes, boolean displayText,Vector pid, int seqnum,AlignViewport av, Hashtable props, int intpid[][]) {
	
	int i      = start;
	int length = seq.getLength();

	Color currentColor = Color.white;

	int    prev   = -1;
        int    fstart = 0;
        char   prevc   = '-';

	g.setColor(Color.magenta);

        Sequence topseq = av.getAlignment().getSequenceAt(0);

	while (i <= end && i < length) {
            char c = seq.getCharAt(i);
	    char tc = topseq.getCharAt(i);

	    if (c != '-') {
		if (tc != c) {

                    int frame =  i %3;

                    if (frame == 0) {
                        g.setColor(Color.red);
                    } else if (frame == 1) {
                        g.setColor(Color.orange);
                    } else if (frame == 2) {
                        g.setColor(Color.cyan);
                    }
		} else {
		    g.setColor(Color.lightGray);
		}
	    
		g.fillRect(x1+(int)((i-start)*width),y1,(int)(width+1),height);
		
		if (width > 5) {
		    g.setColor(Color.black);
		    g.drawString(seq.getSequence().substring(i,i+1),x1+(int)(width*(i-start)),y1+3*height/4);
		}

	    }
	    i++;
	}

    }
}
