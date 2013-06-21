package pogvue.gui.renderer;

import pogvue.datamodel.Sequence;
import pogvue.gui.AlignViewport;
import pogvue.gui.schemes.ResidueProperties;

import java.awt.*;
import java.util.Hashtable;
import java.util.Vector;

public class FeatureRenderer implements RendererI {

        
	public void drawSequence(Graphics2D g, Sequence seq,int start, int end, int x1, int y1, double width, int height,boolean showScores, boolean displayBoxes, boolean displayText,Vector pid, int seqnum,AlignViewport av, Hashtable props, int intpid[][]) {
	
	int i      = start;
	int length = seq.getLength();

	Color currentColor = Color.white;

	int    prev   = -1;
        int    fstart = 0;
        char   prevc   = '-';

	g.setColor(Color.magenta);

	while (i <= end && i < length) {
            char c = seq.getCharAt(i);

	    if (c != '-') {

                if (c == 'F') {
		    g.setColor(Color.green);                  // trf or forward exon
                } else if (c == 'R') {                        // rev exon or LTR
                  g.setColor(Color.cyan);
                } else if (c == 'M') {
		    g.setColor(Color.magenta);                // ? exon or MER
                } else if (c == 'P') {           
		    g.setColor(Color.yellow);                 //  (*)n
                } else if (c == 'V') {
                  g.setColor(Color.pink);
                } else if (c == 'E') {
                  g.setColor(Color.orange);
                } else if (c == 'W') {
                  g.setColor(Color.red);
                } else if (c == 'S') {
		    g.setColor(Color.blue);                    // SVA
                } else if (c == 'A') {                         // Alu
                  g.setColor(Color.gray);
                } else if (c == 'L') {                         // L repeats
		    g.setColor(ResidueProperties.taylor[13]);  //bgg 
                } else if (c == 'I') {                         // MIR
		    g.setColor(ResidueProperties.taylor[11]);  //rbb   (purple)
                } else if (c == 'C') {                         // _rich
		    g.setColor(ResidueProperties.taylor[14]);  // ryy
                } else if (c == 'D') {                         // dust  (orange)
		    g.setColor(ResidueProperties.taylor[16]);  // ryr
                } else if (c == 'B') {                         // start gene
		    g.setColor(Color.black);                   // 
                } else if (c == 'Y') {                         // end gene
		    g.setColor(Color.black);                   // 
                } else if (c == 'X') {                         // exon
		    g.setColor(Color.magenta);                   // 
                } else  {                         
		    g.setColor(Color.lightGray);

                }

		g.fillRect(x1+(int)((i-start)*width),y1+height/4,(int)(width+1),height/2);

	    }
	    i++;
	}

    }
}
