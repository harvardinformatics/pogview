package pogvue.gui.renderer;

import pogvue.datamodel.Sequence;
import pogvue.gui.AlignViewport;
import pogvue.gui.schemes.ResidueProperties;

import java.awt.*;
import java.util.Hashtable;
import java.util.Vector;

public class TranslationRenderer implements RendererI {

    public void drawSequence(Graphics2D g, Sequence seq,int start, int end, int x1, int y1, double width, int height,boolean showScores, boolean displayBoxes, boolean displayText,Vector kmers, int seqnum,AlignViewport av, Hashtable props,int intpid[][]) {
	

	int length = seq.getLength();

	Color currentColor = Color.white;

	g.setColor(Color.black);

        int prev  = -1;
	int seqno = -1;

	int i = start-3;

	if (i < 0) { 
	    i = 0;
	}

	end = end + 2;

	if (end > seq.getLength()) {
	    end = seq.getLength();
	}



	    g.setColor(Color.black);

	    while (i <= end-3) {
		
		String str = seq.getSequence().substring(i,i+3);
		
		String res = (String)ResidueProperties.codonHash2.get(str);

		if (width > 5) {		
		    if (res != null) {
			int ypos = y1+height - (i%3)*height/3;
			
			if (res.equals("*")) {
			    g.setColor(Color.red);
			    g.drawString(res,x1+(int)(width*(i-start)),ypos);
			    g.setColor(Color.black);
			} else {
			    g.drawString(res,x1+(int)(width*(i-start)),ypos);	       
			}
		    }
		} else if (width < 5 && width >= 0.1) {
		    int ypos = y1+height - height/3 - (i%3)*height/3;
			
		    if (res != null && res.equals("*")) {
			g.setColor(Color.red);
			g.fillRect(x1+(int)(width*(i-start)),ypos,(int)(width+1),height/3);
			g.setColor(Color.black);

		    }
		}
		i++;
	    }
    }
}

