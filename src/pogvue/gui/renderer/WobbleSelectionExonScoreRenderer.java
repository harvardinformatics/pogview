package pogvue.gui.renderer;

import pogvue.datamodel.Sequence;
import pogvue.gui.AlignViewport;

import java.awt.*;
import java.util.Hashtable;
import java.util.Vector;

public final class WobbleSelectionExonScoreRenderer implements RendererI {
    protected final Color   color = Color.black;
        
    public void drawSequence(Graphics2D g, Sequence seq,int start, int end, int x1, int y1, double width, int height,boolean showScores, boolean displayBoxes, boolean displayText,Vector pid, int seqnum,AlignViewport av, Hashtable props,int intpid[][]) {
	
      int window = 50;//av.getPIDWindow();

	window = 2* (window/3);

	if (window == 0) {
	    window = 1;
	}
	
	if (av.getSelection().asVector().size() == 0) {
	    return;
	}
	seqnum = 0;

	int i      = start;
	int length = seq.getLength();

	Color currentColor = Color.white;

	g.setColor(Color.black);

        int prev = -1;

	while (i <= end && i < length) {
            char c = seq.getCharAt(i);

            if (c != '-') {
		Vector values = (Vector)pid.elementAt(i-start);
		//int val = ((Integer)values.elementAt(seqnum)).intValue();
		
		int val = 0;

		for (int ii = 0; ii < av.getSelection().asVector().size(); ii++) {
		    val += (Integer) values.elementAt(ii);
		}

		val = val/av.getSelection().asVector().size();

		int frac = 100*val/(window+1);
		
		//System.out.println("Val " + i + " " + val + " " + frac + " " + window);		

		int baseline = 50;//av.getPIDBaseline();

		frac -= baseline;

		//System.out.println("Val " + i + " " + val + " " + frac);		

		if (frac < 0) {
		    frac = 0;
		}
		if (baseline == 100) {
		    baseline = 99;
		}



             Graphics2D g2d = (Graphics2D)g;

	     //	     g2d.setColor(new Color(255*frac/(100-baseline),50*frac/(100-baseline),50*frac/(100-baseline)));
	     g2d.setColor(Color.pink);
	     if (i%3 == 0) {
		 g.setColor(Color.red);
	     } else if (i%3 == 1) {
		 g.setColor(Color.green);
	     } else {
		 g.setColor(Color.blue);
	     }

             //g.setColor(new Color(255*frac/(100-baseline),50*frac/(100-baseline),255-50*frac/(100-baseline)));
	     g.fillRect(x1+(int)(width*(i-start)),
			y1+height - frac*height/(100-baseline),
			(int)width+1,
                 frac*height/(100-baseline));

             //int[] xpoints = new int[4];
             //int[] ypoints = new int[4];

             //if (prev == -1) {
             //  prev = y1 + height - (int)(frac*height/(100-baseline));
             //}

	     //   1----2
	     //   |    |
	     //   4----3

	     //xpoints[0] = (x1+ (int)(width*(i-start)));
             //ypoints[0] = prev;

             //xpoints[1] = (x1 + (int)(width*(i-start+1)));
             //ypoints[1] = y1 + height - (int)(frac*height/(100-baseline));

             //xpoints[3] = (x1 + (int)(width*(i-start)));;
             //ypoints[3] = y1 + height;

             //xpoints[2] = (x1  + (int)(width*(i-start+1)));
             //ypoints[2] = y1 + height; 

	     //Polygon poly = new Polygon();

	     //poly.xpoints = xpoints;
	     //poly.ypoints = ypoints;
	     //poly.npoints = 4;

             //g2d.fill(poly);
	     
             //if (width > 5) {
	     //		 g.setColor(Color.black);
	     //	 g.drawString(seq.getSequence().substring(i,i+1),x1+(int)(width*(i-start)),y1+height);
             //} else {
	     //	 g.setColor(Color.black);
		 
	     //	 if (prev != -1) {

	     //g.drawLine(x1+(int)(width*(i-start)-width/2),  prev,
	     //           x1+(int)(width*(i-start+1)-width/2),y1+height - (int)(frac*height/(100-baseline)));
             // } else {
             // g.drawLine(x1+(int)(width*(i-start)),  y1+height - (int)(frac*height/(100-baseline)),
             //            x1+(int)(width*(i-start+1)),y1+height - (int)(frac*height/(100-baseline)));
             // }

	     //}
	     //prev = ypoints[1];
	     //}  else {
             // prev = -1;
	     //}
	
	    }
	    i++;
	}
    }
}
