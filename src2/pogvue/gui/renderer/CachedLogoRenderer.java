package pogvue.gui.renderer;

import pogvue.analysis.AAFrequency;
import pogvue.datamodel.*;
import pogvue.gui.AlignViewport;
import pogvue.gui.schemes.*;
import pogvue.util.QuickSort;
import pogvue.analysis.*;
import pogvue.datamodel.motif.*;

import java.awt.*;
import java.util.Hashtable;
import java.util.Vector;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import java.io.*;
import java.awt.font.*;
import java.awt.geom.*;

public class CachedLogoRenderer extends JPanel implements RendererI {

    public static boolean sizeByInfo = true;
    Color[] colors = new Color[4];
    TFMatrix tfm;
    Font charfont;
    Font f;

    
    Image[][] images = null;
    char[] base = new char[4];
    int   charwidth  = 7;
    int   charheight = 20;

    public CachedLogoRenderer() {
	super();
	colors[0] = Color.green.darker();
	colors[1] = Color.red.darker();
	colors[2] = Color.blue;
	colors[3] = Color.yellow.darker();

	base[0] = 'A';
	base[1] = 'T';
	base[2] = 'C';
	base[3] = 'G';


    }

    public Font scaleFont(Font font, float xval, float yval) {
	AffineTransform ascale = getScaleTransform(xval, yval);

	Font newfont = font.deriveFont(ascale);

	return newfont;
    }

    public AffineTransform getScaleTransform(float xscale, float yscale) {
	AffineTransform ascale = new AffineTransform();

	ascale.scale(xscale, yscale);

	return ascale;
    }

    public int drawLetter(Graphics2D g2, String s, Font font, Color c,
			  FontRenderContext frc, int x, int y) {

	TextLayout tstring = new TextLayout(s, font, frc);

	g2.setColor(c);
	tstring.draw(g2, x, y);

	float yoff = (float) tstring.getBounds().getHeight();

	// System.out.println("Drawing letter " + x + " " + y + " " +tstring);
	return (int) (yoff);

    }

    public void drawLogo(Graphics2D g, int xstart, int ystart, int charwidth,
			 int charheight, TFMatrix matrix, int strand, String topstr) {
	int w;
	int h;


	double[] pwm = matrix.getPwm().getPwm();

	if (strand == -1) {
	    pwm = matrix.getPwm().getRevPwm();
	}

	int len = pwm.length / 4;
	System.out.println("Len " + len);
	Graphics2D g2 = (Graphics2D) g;

	// Loop over the positions in the matrix
	for (int i = 0; i < len; i++) {
	    int ch = 0;

	    // Get info content for this column bases are in ATCG order
	    int ii = 0;
	    double bit = 0;

	    // find the order of the elements
	    float[] vals   = new float[4];
	    Integer[] order =  new Integer[4];

	    while (ii < 4) {
		vals[ii] = (float)pwm[i*4+ii];
		order[ii] = ii;
		ii++;
	    }

	    QuickSort.sort(vals,order);

	    ii = 0;

	    while (ii < 4) {
		double pi = vals[ii];

		if (pi > 0) {
		    bit += pi * Math.log(pi) / Math.log(2);
					
		}
		ii++;
	    }

	    bit = 2 + bit;

	    ii = 0;

	    String t = "-";

	    if (topstr != null && topstr.length() > i) {
		t = topstr.substring(i, i + 1);
	    }
	    
	    while (ii < 4) {
		int j = order[ii].intValue();

		String str = " ";

		if (j == 0) {
		    str = "A";
		}
		if (j == 1) {
		    str = "T";
		}
		if (j == 2) {
		    str = "C";
		}
		if (j == 3) {
		    str = "G";
		}

		Color c = Color.lightGray;
				
		if (topstr == null || t.equals(str)) {
		    c = colors[j];
		}

		// Scale the info scaled font by pi and info
		float val = (float) (vals[ii]);
			  
		if (sizeByInfo == true) {
		    val = (float) (val * bit / 2);
		}
		//		System.out.println("Val " + (int)(val*20));

		//System.out.println("Choosing img " + j + " " + (int)(val*20) + " " + xstart + " " + ystart);

		Image img = images[j][(int)val*20];

		g2.drawImage(img,xstart+i*charwidth,ystart,this);

		ii++;
	    }
	}
    }

    public void drawSequence(Graphics2D g, Sequence seq,
			     int start, int end, int x1, int y1, double width, int height,
			     boolean showScores, boolean displayBoxes, boolean displayText,
			     Vector pid, int seqnum, AlignViewport av, Hashtable props, int intpid[][]) {


	Alignment al = av.getAlignment();
	Sequence s = al.getSequenceAt(0);
	images = av.getImages();
	if (seq instanceof GFF) {
	    GFF gff = (GFF) seq;

	    Vector feat = gff.overlaps(start, end);

	    for (int i = 0; i < feat.size(); i++) {
		SequenceFeature sf = (SequenceFeature) feat.elementAt(i);
		String str = s.getSequence(sf.getStart(), sf.getEnd() + 1);
		TFMatrix mat = av.getMatrix(sf.getId());
				
		if (sf.getTFMatrix() != null) {
		    mat = sf.getTFMatrix();
		    str = null;
		}
		int tmpstart = x1 + (int) ((sf.getStart() - start) * width);

		if (width >= 3) {
		    drawLogo(g, tmpstart, y1, (int) width, height, mat, sf.getStrand(),
			     str);
		} else {
		    g.setColor(Color.lightGray);
		    g.fillRect(tmpstart, y1,
			       (int) ((sf.getEnd() - sf.getStart() + 1) * width), height);
		}
		g.setColor(Color.black);
		if (f == null) {
		    f = new Font("Helvetica", Font.PLAIN, 8);
		}
		g.setFont(f);
		// g.drawString(sf.getId()+ "." +
		// String.valueOf((int)sf.getScore()),tmpstart,y1+height);
		g.drawString(sf.getId(), tmpstart, y1 + height);

	    }

	} else {
	    // Calculate the matrix out of the alignment
	    // Draw logo at start to end of the region.

	    if (width >= 3) {
		double[] seqvec = Correlation4.seqvec(av.getAlignment(), start, end);

		int i = 0;

		int cols = seqvec.length / 4;

		double[][] vals = new double[cols][4];

		while (i < cols) {
		    vals[i][0] = seqvec[i * 4 + 0] * 2;
		    vals[i][1] = seqvec[i * 4 + 1] * 2;
		    vals[i][2] = seqvec[i * 4 + 2] * 2;
		    vals[i][3] = seqvec[i * 4 + 3] * 2;
		    i++;
		}
		TFMatrix mat = new TFMatrix(vals, cols, 4);
		// System.out.println("Drawing logo " + mat.getConsensus());
		drawLogo(g, x1, y1, (int) width, height, mat, 1, null);
	    }

	}
    }
}
