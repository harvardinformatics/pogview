package pogvue.gui.renderer;

import pogvue.datamodel.Sequence;
import pogvue.gui.AlignViewport;

import java.awt.*;
import java.util.Hashtable;
import java.util.Vector;

public class UnconservedBaseRenderer implements RendererI {

        
	public void drawSequence(Graphics2D g, Sequence seq,int start, int end, int x1, int y1, double width, int height,boolean showScores, boolean displayBoxes, boolean displayText,Vector pid, int seqnum,AlignViewport av, Hashtable props,int intpid[][]) {

        Graphics2D g2 = (Graphics2D)g;
        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        //g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,RenderingHints.VALUE_COLOR_RENDER_SPEED);

	int i      = start;
	int length = seq.getLength();

	Color currentColor = Color.white;

	int    prev   = -1;
        int    fstart = 0;
        char   prevc   = '-';

	g.setColor(Color.magenta);

        Sequence topseq = av.getAlignment().getSequenceAt(0);
	int pixel = -1;

	while (i <= (end+1) && i < length) {
            char c = seq.getCharAt(i);
	    char tc = topseq.getCharAt(i);

	    if (c != '-') {
		if (tc != c) {

		    if (c == 'A') {
			g.setColor(Color.green);                  // trf or forward exon
		    } else if (c == 'C') {                        // rev exon or LTR
			g.setColor(Color.red);
		    } else if (c == 'G') {
			g.setColor(Color.blue);                // ? exon or MER
		    } else if (c == 'T') {           
			g.setColor(Color.yellow);                 //  (*)n
		    }
		} else {
		    g.setColor(Color.lightGray);
		}
	    
		//g.fillRect(x1+(int)((i-start)*width),y1+height/4,(int)(width+1),height/2);
		//g.fillRect(x1+(int)((i-start)*width),y1-1,(int)(width+1),height-2);

		int newpixel = x1 + (int)((i-start)*width);

		if (pixel == -1 || newpixel > pixel || i == end) {
		  g.fillRect(newpixel,y1,(int)(width+1),height-1);
		  
		  if (width > 5) {
		    g.setColor(Color.black);
		    g.drawString(seq.getSequence().substring(i,i+1),x1+(int)(width*(i-start)),y1+3*height/4);
		  }
		}
		pixel = newpixel;

	    }
	    i++;
	}

    }
}
