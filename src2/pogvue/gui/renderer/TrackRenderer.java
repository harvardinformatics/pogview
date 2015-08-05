package pogvue.gui.renderer;

import pogvue.gui.*;
import pogvue.gui.schemes.*;

import pogvue.io.*;
import pogvue.gui.hub.*;
import pogvue.analysis.*;
import pogvue.datamodel.*;
import pogvue.datamodel.motif.*;
import pogvue.gui.event.*;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

public class TrackRenderer {

  private static final LogoRenderer      lr  = new LogoRenderer();
  private static final GFFBaseRenderer   gf  = new GFFBaseRenderer();
  private static final GraphBaseRenderer gbr = new GraphBaseRenderer();
  private static final GraphRenderer     gr  = new GraphRenderer();
  private static final CpGRenderer       cp  = new CpGRenderer();
  private static final ConsensusRenderer cr  = new ConsensusRenderer();
  private static final BaseRenderer      br  = new BaseRenderer();
  private static final PiRenderer        pr  = new PiRenderer();
  private static final ScoreRenderer     scr = new ScoreRenderer();
  private static final ConservedBaseRenderer cbr = new ConservedBaseRenderer();
  private static final FrameMismatchRenderer fmr = new FrameMismatchRenderer();
  private static final GCRenderer gcr = new GCRenderer();
  private static final PercentIdentityRenderer pir = new PercentIdentityRenderer();
  private static final ConflateAlignRenderer car = new ConflateAlignRenderer();
  private static final UnconservedBaseRenderer ubr = new UnconservedBaseRenderer();

  public static void drawTracks(Graphics2D g, int x1, int x2, int y1, int y2,
      int startx, int starty, int offset, int offsety, AlignViewport av) {

    //System.out.println(" DrawPanel x1 x2 " + x1 + " " + x2);
    //System.out.println(" DrawPanel y1 y2 " + y1 + " " + y2);
    //System.out.println("Startx/y         " + startx + " " + starty);
    //System.out.println("Starting drawpanel");

    if (x1 > x2) {
      System.out.println("X coords inverted " + x1 + " " + x2);
      return;
    }

    // g.drawLine(startx,starty,startx+100,starty+100);
    // g.setFont(av.getFont());

    Vector pid = null;
    int[][] intpid = null;

    Alignment da         = av.getAlignment();
    double    charWidth  = av.getCharWidth();
    int       charHeight = av.getCharHeight();
    
    RendererI sr = av.getRenderer();

    //sr = scr;
    sr = gr;
    //System.out.println("Renderer ******* " + sr);
    Vector tmpseq = new Vector();
    Vector hide = av.hiddenSequences();

    for (int i = 0; i < av.getAlignment().getHeight(); i++) {
      
      Sequence s = av.getAlignment().getSequenceAt(i);
      //System.out.println("Seq " + s.getName() + " " + s.getSequence().length() + " " + charWidth);
      //if (!hide.contains(s) && !(s.getSequence().length() > 1 && charWidth < .1)) {
	//System.out.println("Adding sequence " + s.getSequence().length());
        tmpseq.addElement(s);
	//}
    }

    if (x1 >= 0 && pid == null && charWidth > .1
        && (sr instanceof ConsensusRenderer || av.getVisibleSequence() == true)) {

      pid = AAFrequency.calculate(av.getAlignment().getSequences(), x1, x2);
    }
    
    if (x1 >= 0 && intpid == null
        && (sr instanceof GraphRenderer && av.getVisibleSequence() == true)) {

      intpid =
	AAFrequency.calculatePID_test(av.getAlignment().getSequenceAt(0),
				      av.getAlignment().getSequences(),
				      50,x1,x2);
    }


    // av.getEndSeq());

    if (y2 > starty && y1 < av.getEndSeq()) {

      for (int i = 0; i < tmpseq.size(); i++) {
	      
        if (i >= y1 && i <= y2) {
	      
          RendererI r = av.getRenderer();
	  r = scr;
          Sequence seq = (Sequence) tmpseq.elementAt(i);

          if (seq instanceof GFF) {
            GFF gff = (GFF) seq;

            if (gff.getScores() == null) {
              r = gf;
            } else {
              r = gr;
            }
          } else if (i == 0) {
            r = av.getRenderer();
          }

          if (seq.getName().indexOf("Logo") == 0
              || seq.getName().equals("Transfac")
              || seq.getName().equals("motif")) {
            r = lr;
          }

	  if (seq instanceof GFF && seq.getName().equals("PWM")) {
	      r = lr;
	  }
          if (seq.getName().indexOf("30mamm.pi") == 0) {
            r = pr;
          }

          if (i % 2 == 0) {
            g.setColor(AlignViewport.stripeColor);
            g.fillRect(offset + (int) ((x1 - startx) * charWidth), offsety
                + av.getPixelHeight(starty, i, av.getCharHeight()),
                (int) ((x2 - x1 + 1) * charWidth), av.getCharHeight());
          } else {
            g.setColor(Color.white);
            g.fillRect(offset + (int) ((x1 - startx) * charWidth), offsety
                + av.getPixelHeight(starty, i, av.getCharHeight()),
                (int) ((x2 - x1 + 1) * charWidth), av.getCharHeight());
          }

          if (i == 0) {
            // System.out.println("Drawing sequence " + offset + " " +
            // (x1-startx)*charWidth + " " + (offset +
            // (int)((x1-startx)*charWidth)));
          }

	  //System.out.println("Start pixel " +
          // (offset+(int)(x1-startx)*charWidth));
          // System.out.println("End pixel " +
          // (offset+(int)(x2-startx)*charWidth));
	  r = scr;
	  //r = cr;  // This worked grey match/mismatch
	  //r = gbr;  // Not work
	  //r = gf;  // This just shows features
	  //r = pir; // Works if not gff
	  //r = gcr; // GC density
	  //r = cbr; // Works but just colored bases
	  //r = fmr; // Works
	  //r = gr;    // Works if we don't have a gff but a sequence with features
	  //r = car;
	  if (seq.getSequence().length() > 0) {
	    r = ubr;
	  } else {
	    r = gf;
	  }

          r.drawSequence(g, seq, x1, x2, offset
              + (int) ((x1 - startx) * charWidth), offsety
              + av.getPixelHeight(starty, i, av.getCharHeight()),
              charWidth, charHeight, false, true, true, pid, i, av,
              new Hashtable(), intpid);
        }
      }
    }
  }

  public static void fillBackground(Graphics2D g, Color c, int x1, int y1,
      int width, int height) {
    g.setColor(c);
    g.fillRect(x1, y1, width, height);
  }

}
