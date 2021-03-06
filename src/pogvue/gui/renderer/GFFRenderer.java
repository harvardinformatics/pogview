package pogvue.gui.renderer;

import pogvue.datamodel.Exon;
import pogvue.datamodel.GFF;
import pogvue.datamodel.SequenceFeature;
import pogvue.datamodel.Sequence;
import pogvue.gui.AlignViewport;

import java.awt.*;
import java.util.*;

public class GFFRenderer implements RendererI {

        
	public void drawSequence(Graphics2D g, Sequence seq,int start, int end, int x1, int y1, double width, int height,boolean showScores, boolean displayBoxes, boolean displayText,Vector pid, int seqnum,AlignViewport av, Hashtable props, int intpid[][]) {
	

	Color currentColor = Color.white;
	LinkedHashMap conf = av.getGFFConfig();

	System.out.println("Rendering!!!!");
	
	if (seq instanceof GFF) {
	    GFF gff = (GFF)seq;

	    SequenceFeature sf1 = gff.getFeatureAt(0);

	    System.out.println("GFF " + sf1.getType() + " " + width);
	    
	    Vector feat = gff.overlaps(start,end);
	    
	    
	    for (int i = 0; i < feat.size(); i++) {
		SequenceFeature sf = (SequenceFeature)feat.elementAt(i);

		Vector tmpf = new Vector();

		for (int j = 0; j < tmpf.size(); j++) {
		  
		  if (conf != null && conf.containsKey(sf.getType())) {
			Color c = (Color)(conf.get(sf.getType()));
			g.setColor(c);
		    } else {
			
			Color c = new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
			conf.put(sf.getType(),c);
		    }
		    
		    
		    int fstart = sf.getStart();
		    int fend   = sf.getEnd();
		    
		    if (fstart < start) {
			fstart = start;
		    }
		    
		    if (fend > end) {
			fend = end+1;
		    }
		    
		    if (sf instanceof Exon) {
			
			Exon ex = (Exon)sf;
			
			g.drawRect(x1+(int)((fstart-start)*width),y1+height/4,(int)((fend-fstart+1)*width+1),height/2);
			int prevstart = fstart;
			int prevend   = fend;
			
			if (ex.getCodingStart() != -1) {
			    if (ex.getStrand() == 1) {
				
				fstart = ex.getStart() + ex.getCodingStart() -1;
				
				if (fstart < prevstart) {
				    fstart = prevstart;
				}
				
			    } else {
				fend = ex.getEnd() - ex.getCodingStart() + 1;
				
				if (fend > prevend) {
				    fend = prevend;
				}
				
			    }
			}
			
			if (ex.getCodingEnd() != -1) {
			    if (ex.getStrand() == 1) {
				fend = ex.getStart() + ex.getCodingEnd() -1;
				
				if (fend > prevend) {
				    fend = prevend;
				}
			    } else {
				
				fstart = ex.getEnd() - ex.getCodingEnd() + 1;
				
				if (fstart < prevstart) {
				    fstart = prevstart;
				}
			    }
			    
			    g.fillRect(x1+(int)((fstart-start)*width),y1+height/4,(int)((fend-fstart+1)*width+1),height/2);
			}
			
			if (ex.getCodingEnd() == -1 &&
			    ex.getCodingStart() == -1 ) {
			    //ex.getPhase() != -1) {
			    g.fillRect(x1+(int)((fstart-start)*width),y1+height/4,(int)((fend-fstart+1)*width+1),height/2);
			} else {
			    g.fillRect(x1+(int)((fstart-start)*width),y1+height/4,(int)((fend-fstart+1)*width+1),height/2);
			}
		    }
		}
	    }	    
	}
	if (av.getMinipog() != null) {
	  System.out.println("Settting conf for minipog");
	  av.getMinipog().getAlignViewport().setGFFConfig(conf);
	}

    }
}
