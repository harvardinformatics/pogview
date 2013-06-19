package pogvue.gui.renderer;

import pogvue.analysis.AAFrequency;
import pogvue.datamodel.*;
import pogvue.gui.AlignViewport;

import java.awt.*;
import java.util.Hashtable;
import java.util.Vector;
import java.util.*;
public class ScoreRenderer implements RendererI {
            
    public void drawSequence(Graphics2D g, Sequence seq,int start, int end, int x1, int y1, double width, int height,boolean showScores, boolean displayBoxes, boolean displayText,Vector pid, int seqnum,AlignViewport av, Hashtable props,int intpid[][]) {
      LinkedHashMap conf         = av.getGFFConfig();
      boolean confchanged = false;
      int length = seq.getLength();

      Color currentColor = Color.white;

      g.setColor(Color.black);

      int prevx = -1;
      int prevy = -1;
      
      int prevpixel = 0;
      
      if (!(seq instanceof GFF)) {
	return;
      }
      height -= 2;

      GFF gff = (GFF)seq;
      
      double minscore = gff.getMinScore();
      double maxscore = gff.getMaxScore();
      
      minscore = (int)(minscore - (maxscore-minscore+1)*0.1);
      maxscore = (int)(maxscore + (maxscore-minscore+1)*0.1);

      if (gff.getType().equals("Patient_Flow")) {
	minscore = 1600;
      }
      //System.out.println("Min/Max " + minscore + " " + maxscore);
      Vector feat = gff.overlaps(start,end);
      
      //System.out.println("Got features " + feat.size());
      
      int prev = -1;
      for (int i = 0; i < feat.size(); i++) {
	
	SequenceFeature sftmp = (SequenceFeature)feat.elementAt(i);

	int coord = sftmp.getStart();

	if (coord >= minscore) {
	  Color c = Color.black;

	  String key = sftmp.getType();
	  if (key.indexOf("::") > 0) {
	    key = key.substring(0,key.indexOf("::"));
	    sftmp.setType(key);
	  }
	  if (conf != null && conf.containsKey(sftmp.getType())) {
	    c = (Color)(conf.get(sftmp.getType()));
	    g.setColor(c);
	  } else {
	    
	    //c = new Color((int)(Math.random()*200+50),(int)(Math.random()*200+50),(int)(Math.random()*200+50));
	    c = Color.black;
	    conf.put(sftmp.getType(),c);
	    g.setColor(c);
	    confchanged = true;
	  }
	  
	  
	  Vector tmpf = new Vector();
	  
	  if (sftmp.getFeatures() != null) {
	    tmpf = sftmp.getFeatures();
	  } else {
	    tmpf.addElement(sftmp);
	  }

	  int    tmpheight = height;
	  double score     = sftmp.getScore();

	  tmpheight = (int)((score-minscore+1)*(height)/(maxscore-minscore+1));
	  
	  int tmpx = x1+(int)((coord-start)*width)+1;
	  int tmpy = y1+height-tmpheight+1;
	  
	  if (prevx == -1) {
	    prevx = tmpx;
	    prevy = tmpy;
	  }

	  if (tmpx-prevx <= 2) {
	    g.drawLine(prevx,prevy,tmpx,tmpy);
	  }

	  prevx = tmpx;
	  prevy = tmpy;
	}
	i++;
      }
    }
}
