package pogvue.gui.renderer;

import pogvue.datamodel.Sequence;
import pogvue.gui.AlignViewport;
import pogvue.gui.schemes.ResidueProperties;

import java.awt.*;
import java.util.Hashtable;
import java.util.Vector;

public final class ExonScoreRenderer implements RendererI {
    protected final Color   color = Color.black;
        
    public void drawSequence(Graphics2D g, Sequence seq,int start, int end, int x1, int y1, double width, int height,boolean showScores, boolean displayBoxes, boolean displayText,Vector pid, int seqnum,AlignViewport av, Hashtable props, int intpid[][]) {
	
	int i      = start;
	int length = seq.getLength();

	Color currentColor = Color.white;

	g.setColor(Color.black);

        Sequence seq0     = av.getAlignment().getSequenceAt(0);
	
	g.setColor(Color.black);
	
	int max  = 20;
	int base = -9;
	while (i < end) {

	    int score;

	    String seq1 = seq0.getSequence().substring(i,i+3);
	    String seq2 =  seq.getSequence().substring(i,i+3);

	    String res1 = "-";
	    String res2 = "-";

	    if (ResidueProperties.codonHash2.containsKey(seq1)) {
		res1 = (String)ResidueProperties.codonHash2.get(seq1);
	    }
	    if (ResidueProperties.codonHash2.containsKey(seq2)) {
		res2 = (String)ResidueProperties.codonHash2.get(seq2);
	    }

	    
	    int num1 = (Integer) ResidueProperties.aaHash.get(res1);
	    int num2 = (Integer) ResidueProperties.aaHash.get(res2);

	    if (res2.equals("-")) {
		score = 0;
	    } else {
		score = ResidueProperties.BLOSUM62[num1][num2];
	    }
	    score -= base;

	    g.fillRect(x1 + (int)(width*(i-start-width/2)),
		       y1+height - score*height/max,
		       (int)(width+1),
                score*height/max);
	    
	    i++;
	}
    }
}
