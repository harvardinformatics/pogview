package pogvue.gui.renderer;

import pogvue.datamodel.GFF;
import pogvue.datamodel.Sequence;
import pogvue.gui.AlignViewport;
import pogvue.gui.schemes.ResidueProperties;

import java.awt.*;
import java.util.Hashtable;
import java.util.Vector;

public final class TotalExonScoreRenderer implements RendererI {
    protected final Color   color = Color.black;
        
    public void drawSequence(Graphics2D g,Sequence seq,int start, int end, int x1, int y1, double width, int height,boolean showScores, boolean displayBoxes, boolean displayText,Vector pid, int seqnum,AlignViewport av, Hashtable props,int intpid[][]) {
	
	Vector colors = new Vector();

	colors.addElement(Color.red);
	colors.addElement(Color.blue);
	colors.addElement(Color.green);

	int i;
	int length = seq.getLength();

	Color currentColor = Color.white;

	g.setColor(Color.black);

        Sequence seq0     = av.getAlignment().getSequenceAt(0);
	
	g.setColor(Color.black);
	
	int max  = 20;
	int base = 0;
	
	int num;
	int score = 0;
	int window = 90;

	if (start < window/2) {
	    start = window/2;
	}
	if (end > seq0.getLength() - window/2) {
	    end = seq0.getLength() - window/2;
	}

	i = start;
	num = 0;

	int strand = 1;

	if (seq.getName().indexOf("Rev") >= 0) {
	    strand = -1;
	}

	while (i < end) {
	    num = 0;
	    int ii = i-window/2;
	    
	    while (ii < i+window/2) {
		for (int j = 1; j < av.getAlignment().getHeight(); j++) {

		    Sequence jseq = av.getAlignment().getSequenceAt(j);

		    if (!av.getSelection().contains(jseq) && !(jseq instanceof GFF) && jseq.getName().toUpperCase().indexOf("GFF") != 0 && jseq.getName().indexOf("Exon") != 0 && (ii+3) < jseq.getLength()) {

			
			String seq1 = seq0.getSequence().substring(ii,ii+3);
			String seq2 = jseq.getSequence().substring(ii,ii+3);
			

			if (strand == -1) {
			    seq1 = ResidueProperties.reverseComplement(seq1);
			    seq2 = ResidueProperties.reverseComplement(seq2);
			}

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

			if (res1.equals("*") || res2.equals("*")) {
			    num++;			
			    score += -1000;
			    
			} else if (!res2.equals("-")) {
			    num++;			
			    score += ResidueProperties.BLOSUM62[num1][num2];
			}
			
		    }
		
		}
		ii += 3;
	    }

	    //score /= num;
	    score /= window;

	    g.setColor((Color)colors.elementAt(i%3));

	    g.fillRect(x1 + (int)(width*(i-start-width/2)),
		       y1+height - (score-base)*height/(max-base),
		       (int)(width+1),
                (score-base)*height/(max-base));
	    
	    i++;
	}
    }
}
