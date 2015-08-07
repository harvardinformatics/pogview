package pogvue.gui.renderer;

import pogvue.datamodel.*;
import pogvue.gui.AlignViewport;

import java.awt.*;
import java.util.*;

public class GFFBaseRenderer implements RendererI {

  Font f = new Font("Helvetica",Font.PLAIN,10);

  public void drawSequence(Graphics2D g,Sequence seq,int start, int end, int x1, int y1, double width, int height,boolean showScores, boolean displayBoxes, boolean displayText,Vector pid, int seqnum,AlignViewport av, Hashtable props, int intpid[][]) {
    Sequence      topseq       = av.getAlignment().getSequenceAt(0);
    Color         currentColor = Color.white;
    LinkedHashMap conf         = av.getGFFConfig();
    
    boolean confchanged = false;
    
    if (seq instanceof GFF) {

      GFF gff = (GFF)seq;
      
      // This should be factored inside GFF
      if (gff == null || gff.getType() == null ||  width < 0.01 && gff.getType().equals("repeat")) {
	return;
      }
      
      // This is probably ok
      Vector feat = gff.overlaps(start,end);
      
      
      double minscore = gff.getMinScore();
      double maxscore = gff.getMaxScore();
      
      if (av.getGFFHeightByScore()) {
	minscore = 1;
	maxscore = 1;
      }
      
      // Should this be in GFF and has gff.getFeatures(start,end)?
      // Actually - I think the renderers should be passed to GFF as that's where the data is
      
      // If we put all the gubbins in GFF then we can use the remote fetch as we need it.  GFF can be an automatic cache.
      
      for (int i = 0; i < feat.size(); i++) {

	SequenceFeature sftmp = (SequenceFeature)feat.elementAt(i);
	
	Vector tmpf = new Vector();
	
	if (sftmp.getFeatures() != null) {
	  tmpf = sftmp.getFeatures();
	} else {
	  tmpf.addElement(sftmp);
	}
	
	for (int j = 0; j < tmpf.size(); j++) {

	  SequenceFeature sf = (SequenceFeature)tmpf.elementAt(j);

	  if (sf.getHitFeature() != null  && sf.getHitFeature().getId().indexOf("GPR177") == 0) {
	    //System.out.println("Got feature coord " + av.getOffset() + " " + sf.getStart() + " " + (sf.getStart()+av.getOffset()));
	  }
	  Color c;
	  
	  // Similarly the conf should be generating the colors not here
	  if (conf != null && conf.containsKey(sf.getType())) {
	    c = (Color)(conf.get(sf.getType()));
	    g.setColor(c);
	  } else {
	    
	    //c = new Color((int)(Math.random()*200+50),(int)(Math.random()*200+50),(int)(Math.random()*200+50));
	    c = Color.black;
	    conf.put(sf.getType(),c);
	    g.setColor(c);
	    confchanged = true;
	  }
	  
	  // Similarly for the strand?
	  
	  if (sf.getStrand() == -1) {
	    g.setColor(c.darker());
	  }
	  
	  int fstart = sf.getStart();
	  int fend   = sf.getEnd();
	  
	  // Again should be in GFF
	  if (fstart < start) {
	    fstart = start;
	  }
	  
	  if (fend > end) {
	    fend = end+1;
	  }
	  
	  // Again in GFF
	  int fheight = (int)( (sf.getScore() - minscore)*height/(maxscore-minscore+1));
	  
	  if (maxscore == minscore) {
	    fheight = 3*height/4;
	  }
	  //System.out.println("Height " + fheight);
	  //g.fillRect(x1+(int)((fstart-start-1)*width),y1+1,(int)((fend-fstart+1)*width+1),3*height/4);
	  g.fillRect(x1+(int)((fstart-start-1)*width),y1+height-fheight,(int)((fend-fstart+1)*width+1),fheight);
	  
	  String name = sf.getId();
	  
	  if (sf.getHitFeature() != null) {
	    name = sf.getHitFeature().getId();
	  }
	  if (name == null) {
	    name  = sf.getType();
	  }
	  
	  
	  if ((width > 2 && (sf.getStart() >= start && sf.getStart() <= end) && !sf.getType().equals("repeat"))) {
	    
	      	g.setFont(f);
	      	g.setColor(Color.black);
	      	g.drawString(name,
	      		     (int)(x1+(int)((fstart-start-1.0*name.length())*width)), 
	      		     (int)(y1+3.5*height/4));
	      }
	      
	      g.setColor(c);
	      
	      // Draw the connecting lines and strand info if this is a subfeature
	      if (sftmp.getFeatures() != null) {       // Has sub features
	      	
	      	if (j > 0 && (sftmp.getType().equals("gene") || sftmp.getType().equals("non_coding") || sftmp.getType().equals("blat") || sftmp.getType().equals("est") || sftmp.getType().indexOf("BAM")>=0)) {
		  SequenceFeature prev = (SequenceFeature)tmpf.elementAt(j-1);
		  
		  int tmpstart = prev.getEnd();
		  int tmpend   = sf.getStart();
		  
		  if (!(tmpstart > end || tmpend < start)) {
		    if (tmpstart < start) {
		      tmpstart = start;
		    }
		    
		    if (tmpend > end) {
		      tmpend = end+2;
		    }
		    g.drawLine(x1 + (int)((tmpstart-start)*width + 1),y1+height/2,x1+(int)((tmpend-start-1)*width),y1+height/2);
		  }
	      	}
	      }
	    }
	    // This is for gene names only
	    
	    
	    if (sftmp.getType().equals("gene")) {
	    	
	    	SequenceFeature sf = (SequenceFeature)tmpf.elementAt(0);
	    	
	    	String name = sf.getId();
	    	
	    	if (sf.getHitFeature() != null) {
	    		name = sf.getHitFeature().getId();
	    	}
	    	
	    	int sfstart = sftmp.getStart();
	    	int sfend   = sftmp.getEnd();
	    	
	    	
	    	int mid = (sfstart+sfend)/2;
	    	
	    	if (mid > end) {
	    		//mid = end - 100;
	    	}
	    	if (mid < start) {
	    		//mid = start + 100;
	    	}
	    	
	    	if (start == 0 || (mid > (start-150/width) && mid < (end+150/width))) {
	    		g.setFont(f);
	    		g.setColor(Color.black);
	    		g.drawString(name,
	    				(int)((x1 + ((mid-start)*width))),
	    				(int)(y1-height/4));
	    	}
	    }
	  }
	}
	

	if (av.getMinipog() != null) {
	  av.getMinipog().getAlignViewport().setGFFConfig(conf);
	}

	av.setGFFConfig(conf);

	if (confchanged) {
	  av.writeGFFConfig();
	}
  }
}
