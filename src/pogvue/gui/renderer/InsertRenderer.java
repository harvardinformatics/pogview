package pogvue.gui.renderer;

import pogvue.datamodel.Alignment;
import pogvue.datamodel.GappedSequence;
import pogvue.datamodel.Sequence;
import pogvue.gui.AlignViewport;

import java.awt.*;
import java.util.Hashtable;
import java.util.Vector;

public class InsertRenderer implements RendererI {
    
	public void drawSequence(Graphics2D g,Sequence seq,int start, int end, int x1, int y1, double width, int height,boolean showScores, boolean displayBoxes, boolean displayText,Vector pid, int seqnum,AlignViewport av, Hashtable props, int intpid[][]) {
        Graphics2D g2 = (Graphics2D)g;
	
	int i      = start;
	int length = seq.getLength();

	Color currentColor = Color.white;

	g.setColor(Color.black);


	Alignment al = av.getAlignment();

	int k = 0;

	while (k < al.getHeight()) {

	    Sequence s = al.getSequenceAt(k);

	    if (s instanceof GappedSequence) {
		Vector inserts = ((GappedSequence)s).inserts;

		int j = 0;

		while (j < inserts.size()) {
		    Hashtable ins  = (Hashtable)inserts.elementAt(j);
		    
		    if (ins.containsKey("Coord")) {
			int coord = (Integer) (ins.get("Coord"));
			
			String insert = (String)ins.get("String");
			
			if (coord >= start && coord <= end) {
			    
			    if (insert.length() % 3 == 0) {
				g.setColor(Color.gray);
			    } else {
				g.setColor(Color.red);
			    }
			
			    g.fillRect(x1 + (int)(width*(coord-start)),y1,1,3+insert.length());
			}
		    } else {
			int coord = (Integer) (ins.get("Hcoord"));
			
			String insert = (String)ins.get("String");
			
			if (coord >= start && coord <= end) {
			    
			    if (insert.length() % 3 == 0) {
				g.setColor(Color.cyan);
			    } else {
				g.setColor(Color.blue);
			    }
			    
			    g.fillRect(x1 + (int)(width*(coord-start)),y1,1,3+insert.length());
			}
		    }
		    j++;
		}
	    }
	    k++;
	}
    }

}
