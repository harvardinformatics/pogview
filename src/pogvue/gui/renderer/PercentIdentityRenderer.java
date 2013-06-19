package pogvue.gui.renderer;

import pogvue.analysis.AAFrequency;
import pogvue.datamodel.Sequence;
import pogvue.gui.AlignViewport;

import java.awt.*;
import java.util.Hashtable;
import java.util.Vector;

public class PercentIdentityRenderer implements RendererI {
            
    public void drawSequence(Graphics2D g, Sequence seq,int start, int end, int x1, int y1, double width, int height,boolean showScores, boolean displayBoxes, boolean displayText,Vector pid, int seqnum,AlignViewport av, Hashtable props,int intpid[][]) {
	
	int i      = start;
	int length = seq.getLength();

	Color currentColor = Color.white;

	g.setColor(Color.black);

        int prev = -1;

	pid = AAFrequency.calculate(av.getAlignment().getSequences(),
				    start,end);

	int prevpixel = 0;
	
	while (i <= end && i < length) {

	  int pixel = (int)(x1 + (i-start)*width);

	  //System.out.println("Pixel " + pixel);

	  if (pixel > prevpixel) {
	    Hashtable h = (Hashtable)pid.elementAt(i-start);
	    
	    String c = seq.getSequence().substring(i,i+1);
	    
	    if (! c.equals("-")) {
	      int count = (Integer) h.get("totCount");
	      int num   = (Integer) h.get(c);
	      int tmppid   = 100*num/count;
	      
	      if (tmppid == 100) {
		g.setColor(Color.red);
	      } else if (tmppid > 80) {
		g.setColor(Color.orange);
	      } else if (tmppid > 65) {
		g.setColor(Color.pink);
	      } else if (tmppid > 50) {
		g.setColor(Color.yellow);
	      } else {
		g.setColor(Color.lightGray);
	      }
	      
	      g.fillRect(x1+(int)((i-start)*width),y1+height/2,(int)(width+1),height/2);
	      
	      if (width > 5) {
		g.setColor(Color.black);
		g.drawString(seq.getSequence().substring(i,i+1),x1+(int)(width*(i-start)),y1+height);
	      }
	    }
	  } else {
	    //System.out.println("Skipping pixel " + i);
	  }

	  prevpixel = pixel;
	  i++;
	}
    }
}
