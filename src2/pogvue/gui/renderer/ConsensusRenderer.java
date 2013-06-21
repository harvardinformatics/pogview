package pogvue.gui.renderer;

import pogvue.datamodel.*;
import pogvue.gui.AlignViewport;
import pogvue.gui.schemes.ResidueProperties;

import java.awt.*;
import java.util.Hashtable;
import java.util.Vector;

public final class ConsensusRenderer implements RendererI {

    private final Color grayRed    = new Color(237,171,173);
    private final Color grayBlue   = new Color(123,145,166);
    private final Color grayYellow = new Color(255,250,165);
    private final Color grayGreen  = new Color(115,177,138);
            
    private final Color duskyRed   = new Color(255,84,90);
    final Color duskyGreen = new Color(93,252,128);
    private final Color duskyBlue  = new Color(93,128,255);
  
    public void drawSequence(Graphics2D g, Sequence seq,int start, int end, int x1, int y1, double width, int height,boolean showScores, boolean displayBoxes, boolean displayText,Vector pid, int seqnum,AlignViewport av, Hashtable props, int intpid[][]) {
	
        if (width < .01) {
            return;
        }

	int i      = start;
	int length = seq.getLength();

	Color currentColor = Color.white;

	g.setColor(Color.black);

        int prev = -1;


	GFF gff;


	// Find the features

	Vector feat = new Vector();

	for (int k = 0; k < av.getAlignment().getSequences().size(); k++) {
	  if (av.getAlignment().getSequences().elementAt(k) instanceof GFF) {
	    gff = (GFF)av.getAlignment().getSequences().elementAt(k);
	    feat = gff.overlaps(start,end);
	  }
	}

	int pixel = -1;

	Vector     inserts          = null;
	Hashtable  inserts_by_coord = null;
	Sequence   topseq           = (Sequence)av.getAlignment().getSequences().elementAt(0);

	if (seq instanceof GappedSequence) {
	  inserts = ((GappedSequence)seq).inserts;
	  inserts_by_coord = ((GappedSequence)seq).inserts_by_coord;
	}


	char prevtr = '-';
	char prevc = '-';

	int pixchunk = (int)(1.0/av.getCharWidth());

	if (pixchunk < 1) {
	  pixchunk = 1;
	}
	if (pid == null) {
	  //return;
	}
	// Loop over bases
	while (i <= end && i < length) {

	  char tr  = topseq.getCharAt(i);
	  char c   = seq.getCharAt(i);

	  if (pid != null) {
	    Hashtable h = (Hashtable)pid.elementAt(i-start);
	    String topresstr = (String)(h.get("maxResidue"));	    

	    //System.out.println("Topstr " + topresstr);
	    tr = topresstr.charAt(0);
	    //char tr = topseq.getCharAt(i);
	  }
	  int newpixel = x1 + (int)((i-start)*width);
	    
	  // Draw the insert lollipops if present
	  if (av.showGaps() == true && seq instanceof GappedSequence) {
	    //System.out.println("Showing gaps");
	    String insert = "";
	    int    mod = 0;
	    
	    if (inserts_by_coord.containsKey(new Integer(i))) {
	      insert = (String)inserts_by_coord.get(new Integer(i));
	      mod    = insert.length()%3;
	    } else if (inserts_by_coord.containsKey(new Integer(-i))) {	       
	      insert = (String)inserts_by_coord.get(new Integer(-i));
	      mod    = insert.length()%3;
	    }
	    if (insert.length() > 0) {
	      if (mod != 0) {
		g.setColor(duskyRed);
	      } else {
		g.setColor(duskyBlue);
	      }
	      g.fillRect((int)(newpixel-width),y1,2,height-1);
	      
	      int tmpsize = insert.length();
	      
	      if (tmpsize > 10) {
		tmpsize = 10;
	      }
	      
	      g.fillRect((int)(newpixel-width)-tmpsize/2,y1-tmpsize,tmpsize,tmpsize);
	    }
	  }

	    // Draw match/mismatch to consensus char
            if ( c != '-') {

		if (c == tr) {
		  g.setColor(Color.lightGray);
		} else {
		  g.setColor(Color.gray);
		}

		if (pixel == -1 || newpixel > pixel || i == end) {

		    g.fillRect(newpixel,y1,(int)(width+1),height-2);

		    // Draw base letter if enough room

		    if (width >= 5) {
		      String tmp = seq.getSubstring(i,i+1);

		      g.setColor(Color.black);
		      g.drawString(tmp,x1+(int)(width*(i-start)),y1+3*height/4);
		    }
		}
		
		
		// Now conserved splices

	//	if (prevtr == 'G' && tr == 'T') {
	//	  if (prevc == 'G' && c == 'T') {
	//	    g.setColor(Color.orange);
	//	    g.fillRect((int)(newpixel-width),y1,(int)(2*width+1),height-2);

	//	    if (width > 5) {
	//	      String tmp1 = seq.getSubstring(i-1,i);
	//	      String tmp2 = seq.getSubstring(i,i+1);

	//	      g.setColor(Color.black);
	//	      g.drawString(tmp1,x1+(int)(width*(i-start-1)),y1+3*height/4);
	//	      g.drawString(tmp2,x1+(int)(width*(i-start)),y1+3*height/4);

	//	    }
	//	  }

	//	} else if (prevtr == 'A' && tr == 'G') {
	//	  if (prevc == 'A' && c == 'G') {

	//	    g.setColor(Color.yellow);
	//	    g.fillRect((int)(newpixel-width),y1,(int)(2*width+1),height-2);

//		    if (width > 5) {
//		      String tmp1 = seq.getSubstring(i-1,i);
//		      String tmp2 = seq.getSubstring(i,i+1);

//		      g.setColor(Color.black);
//		      g.drawString(tmp1,x1+(int)(width*(i-start-1)),y1+3*height/4);
//		      g.drawString(tmp2,x1+(int)(width*(i-start)),y1+3*height/4);
//
//		    }
//		  }
//		}
	    }
	    pixel = newpixel;
		
	    prevtr = tr;
	    prevc  = c;
	    i += pixchunk;
	}
	
	int j = 0;

			     
    }
}
