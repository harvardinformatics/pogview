package pogvue.gui.renderer;

import pogvue.datamodel.*;
import pogvue.gui.AlignViewport;
import pogvue.analysis.*;

import java.awt.*;
import java.util.*;

public class GraphBaseRenderer implements RendererI {
  public static Color sigColor = Color.red.darker();

  public void drawSequence(Graphics2D g, Sequence seq, int start, int end,
      int x1, 
      int y1, 
      double width, 
      int height, 
      boolean showScores,
      boolean displayBoxes, 
      boolean displayText, 
      Vector pid, 
      int seqnum,
      AlignViewport av, 
      Hashtable props, 
      int intpid[][]) {
    
    
    Sequence topseq = av.getAlignment().getSequenceAt(0);
    
    Color currentColor = Color.white;
    
    LinkedHashMap conf = av.getGFFConfig();
    
    if (seq instanceof GFF) {
      GFF gff = (GFF) seq;
      
      Vector feat = gff.overlaps(start, end);
      
      Vector peaks = new Vector();

      //System.out.println("PPPPPPPPPPPPPPPPPPPPPEAKS!!!!");
      if (width > 1 && feat.size() > 0) {
	  //peaks = find_peaks(feat,start,end);
      }
      
      //System.out.println("Peaks are " + peaks.size() + " " + width);

      System.out.println("Feat " + feat.size());
      for (int i = 0; i < feat.size(); i++) {

        SequenceFeature sf = (SequenceFeature) feat.elementAt(i);
	
        Color c = Color.black;

        if (conf != null && conf.containsKey(sf.getType())) {
          c = (Color) (conf.get(sf.getType()));
          g.setColor(c);
        } else {
	  
          c = new Color((int) (Math.random() * 255),
			(int) (Math.random() * 255), (int) (Math.random() * 255));
          conf.put(sf.getType(), c);
          g.setColor(c);
        }
	
        int fstart = sf.getStart();
        int fend = sf.getEnd();

        if (fstart < start) {
          fstart = start;
        }

        if (fend > end) {
          fend = end + 1;
        }

        int baseline = 0;

        int graphWidth = 1;

        if (width > 1) {
          graphWidth = (int) width;
        }

        double mult = 0.5;

        if (sf.getScoreVector() != null) {
          for (int j = start; j <= end; j++) {
            Vector score = sf.getScoreVectorAt(j);
            double dscore = sf.getScoreAt(j);

            int tmpy = y1;

            int tmpheight = (int) (dscore * height / 10);

            if (score.size() == 4) {
              double abase = ((Double) (score.elementAt(0))).doubleValue();
              double cbase = ((Double) (score.elementAt(1))).doubleValue();
              double gbase = ((Double) (score.elementAt(2))).doubleValue();
              double tbase = ((Double) (score.elementAt(3))).doubleValue();

              g.setColor(Color.green);

              g.fillRect(x1 + (int) ((j - start - 1) * width),
                  (int) (tmpy - mult * abase * tmpheight), graphWidth,
                  (int) (mult * abase * tmpheight));

              tmpy = tmpy - (int) (mult * abase * tmpheight);

              g.setColor(Color.blue);

              g.fillRect(x1 + (int) ((j - start - 1) * width),
                  (int) (tmpy - mult * cbase * tmpheight), graphWidth,
                  (int) (mult * cbase * tmpheight));

              tmpy = tmpy - (int) (mult * cbase * tmpheight);

              g.setColor(Color.yellow);

              g.fillRect(x1 + (int) ((j - start - 1) * width),
                  (int) (tmpy - mult * gbase * tmpheight), graphWidth,
                  (int) (mult * gbase * tmpheight));

              tmpy = tmpy - (int) (mult * gbase * tmpheight);

              g.setColor(Color.red);

              g.fillRect(x1 + (int) ((j - start - 1) * width),
                  (int) (tmpy - mult * tbase * tmpheight), graphWidth,
                  (int) (mult * tbase * tmpheight));

              tmpy = tmpy - (int) (mult * tbase * tmpheight);
            }

          }
        } else {

          g.setColor(c);

          int    pix   = x1;
	  double count = 0;
	  double tot   = 0;

          for (int j = start; j <= end+10; j ++) {

	    double score = sf.getScoreAt(j);

	    int newpix = (int)(x1 + (j - start - 1) * width);
	    
	    if (newpix > pix+1) {
              
	      double prevscore = tot/count;

	      //System.out.println("Prevscore " + prevscore);
              //if (prevscore >= 0) {
		
                double tmp = width;
                int    tmpj = j;
		
                if (tmp < 1) {
                  tmp = 1;
                }
                
                int itmp = (int) tmp;
                
                if (sf.getType().equals("4mammals")) {
		    //  itmp = (int)width*50;
		    //tmpj = tmpj - 25;
                } else if (sf.getType().equals("30mamm.euth.12mer")) {
		    //itmp = (int)width*12;
		    //tmpj = tmpj - 6;
                } else if (sf.getType().equals("30mamm.euth.6mer")) {
		    //itmp = (int)width*6;
		    //tmpj = tmpj-3;
                }
		//tmp = 1;
		//itmp = 1;
		//System.out.println("Filling for " + tmpj);
                g.fillRect(x1 + (int) ((tmpj - start - 1) * width), (int) (y1
		+ height - score), 2, (int)(score));
		//}
	      count = 0;
	      tot   = 0;
	      pix = newpix;
            }
	  }
	
          //g.setColor(Color.white);
          //g.fillRect(x1 + (int) (width), (int) (y1 + height + 1), (int) ((end- start + 1) * width), height + 2);
          g.setColor(Color.black);
          
          for (int j = 0; j < peaks.size(); j++) {
            int coord = ((Integer) peaks.elementAt(j)).intValue();

            g.fillRect(x1 + (int) ((coord - start - 1) * width + width / 2),
               (int) (y1 - height + 1), (int) (1), height + 2);

	    //g.setColor(Color.black);
            // We're fixed to a 12mer currently

//            Vector seqhashvec = AAFrequency.calculate(av.getAlignment()
 //               .getSequences(), coord - 6, coord + 6);

  //          for (int k = 0; k < 12; k++) {
   //           Hashtable seqhash = (Hashtable) seqhashvec.elementAt(k);
  //            String topchar = (String) seqhash.get("maxResidue");

//              String ch = topseq.getSubstring(coord - 6 + k, coord - 6 + k + 1);

 //             int topcount = ((Integer) seqhash.get("maxCount")).intValue();

  //            if (topchar.equals(ch)) {
 //               if (topcount > 10) {
 //                 g.setColor(Color.black);
 //               } else {
 //                 g.setColor(Color.blue);
 //               }
 //             } else {
 //               g.setColor(Color.black);
//              }
//              g.drawString(ch, x1 + (int) ((coord - 6 + k - start) * width),
  //                (int) (y1 + height - 1));
   //         }
	  }
	}
      }
    }
  }
  
  public Vector find_peaks(Vector feat, int start, int end) {
    Vector peaks = new Vector();

    int drop = 3;

    System.out.println("Finding peaks");

    for (int ii = 0; ii < feat.size(); ii++) {

      SequenceFeature f = (SequenceFeature) feat.elementAt(ii);

      int i = start+drop;
      
      while (i < end-drop) {

	double score = f.getScoreAt(i);
	double prescore = f.getScoreAt(i-drop);
	double posscore = f.getScoreAt(i+drop);

	if (score-prescore > 5 &&
	    score-posscore > 5) {
	  peaks.addElement(new Integer(i));
	}
      }
    }
    return peaks;
  }
}
