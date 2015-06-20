package pogvue.gui.renderer;

import pogvue.datamodel.GappedSequence;
import pogvue.datamodel.Sequence;
import pogvue.gui.AlignViewport;

import java.awt.*;
import java.util.Hashtable;
import java.util.Vector;

public final class GraphRenderer implements RendererI {
    protected final Color   color = Color.black;
    protected final Color   maroon = new Color(200,0,50);
    private final Hashtable colhash = new Hashtable();
	public boolean conflate = true;
  public int os = 0;
    
  public void drawSequence(Graphics2D g, Sequence seq,int start, int end, int x1, int y1, double width, int height,boolean showScores, boolean displayBoxes, boolean displayText,Vector pid, int seqnum,AlignViewport av, Hashtable props, int[][] intpid) {
    Graphics2D g2d = (Graphics2D)g;
	
	int i      = start;
	int length = seq.getLength();

	Color currentColor = Color.white;

	g.setColor(Color.black);

        int prev  = -1;
	int pixel = -1;

	Vector inserts = null;


	System.out.println("Drawing GraphRenderer ======");

	if (seq instanceof GappedSequence) {
	    inserts = ((GappedSequence)seq).inserts;
	}

	int    baseline       = 70;//av.getPIDBaseline();

	double window_mult = 1;//(av.getPIDWindow()+1);
	double height_mult = height/(100.0-baseline);
	double width_mult  = width/2.0 + os;

	// Don't draw above a certain zoom level

	if (width <= .001) {
	    return;
	}

	Color cc = Color.lightGray;

	if (seq.getName().indexOf("ucsc") == 0) {
	    cc = Color.yellow;
	}

	while (i <= end && i < length) {

	  int newpixel = x1 + (int)(width*(i-start));

	  if (pixel == -1 || newpixel > pixel ||
	      i == end) {

	    char c = seq.getCharAt(i);

	    char compchar = '-';
	    
	    if (seqnum%2 == 1) {
	      compchar = av.getAlignment().getSequenceAt(seqnum-1).getCharAt(i);
	    } else if ((1+seqnum) != av.getAlignment().getHeight() && av.getAlignment().getSequenceAt(seqnum+1).getLength() > i) {
	      compchar = av.getAlignment().getSequenceAt(seqnum+1).getCharAt(i);
	    }

	    if (c != '-') {
	      int val =0;
	      if (intpid != null) {
		val   = (int)(100*intpid[i-start][seqnum]/50.0);
	      }
		

	      int frac = (int)(val*window_mult); 
	      

	      frac -= baseline;
	      
	      if (frac < 0) {
		frac = 0;
	      }
	      if (baseline == 100) {
		baseline = 99;
	      }
	      
	      g2d.setColor(cc);
		
	      int frac_tot = (int)(frac*height_mult);
		
	      if (conflate) {
	      if ( c != '-' && c != '.') {
		if (compchar != '-' && compchar != '.') {
		  if (c == compchar) {
		    if (seqnum%2 == 0) {
		      g.setColor(Color.lightGray);
		    } else {
		      g.setColor(Color.gray);
		    }
		  } else {
		    if (seqnum%2 == 0) {
		      g.setColor(Color.gray);
		    } else {
		      g.setColor(Color.black);
		    }
		  }
		  
		} else {
		  if (seqnum % 2 == 0) {
		    g.setColor(Color.pink);
		  } else {
		    g.setColor(Color.yellow);
		  }
		}
	      }
	      }
	      //System.out.println("Drawing " + newpixel + " " + (frac_tot) + " " + frac);
	      g.fillRect(newpixel,
			 y1 + height - frac_tot,
			 (int)width+1,       
			 frac_tot);
	      
	      g.setColor(Color.black);
		
	      if (prev != -1) {
		
		g.drawLine(x1+(int)(width*(i-start)-width_mult),  prev,
			   x1+(int)(width*(i-start+1)-width_mult),y1+height - frac_tot);
	      } else {
		g.drawLine(x1+(int)(width*(i-start))+os,  y1+height - frac_tot,
			   x1+(int)(width*(i-start+1))+os,y1+height - frac_tot);
	      }
	      prev = y1 + height - frac_tot;
	    }
	  }
	  
	  pixel = newpixel;
	
	  i++;
	} 



        os = 0;

	int j = 0;


    }

    public Color getColor(int val) {
	if (!colhash.containsKey(new Integer(val))) {
	    Color tmp = new Color(val,0,0);
	    colhash.put(val,tmp);
	}

	return (Color)colhash.get(new Integer(val));
    }
}
