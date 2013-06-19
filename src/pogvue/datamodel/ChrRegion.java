package pogvue.datamodel;

import java.text.NumberFormat;
import java.util.*;
public class ChrRegion {

  private String chr;
  private int    start;
  private int    end;
  private int    strand;
  private boolean hasFeatures;
  private ArrayList feat;
  
  public ChrRegion(String chr,int start,int end) {
  	this(chr,start,end,1);
  }
  public ChrRegion(String chr,int start,int end,int strand) {
  	this.chr = chr;
  	this.start = start;
  	this.end   = end;
  	this.strand = strand;
    }
    public String getChr() {
	return chr;
    }
    public int getEnd() {
	return end;
    }
    public int getStart() {
	return start;
    }
    public void setEnd(int end) {
	this.end = end;
    }
    public void setStart(int start) {
	this.start = start;
    }
  public int getStrand() {
    return strand;
  }
  public void setStrand(int strand) {
    this.strand = strand;
  }
    public boolean overlaps(ChrRegion reg) {
    	
    	if (!(reg.getStart() > getEnd() ||
    			reg.getEnd() < getStart())) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public void expand(int size) {
      int tmpwidth = end-start+1;
      start = start - size;
      if (start < 1) {
        start = 1;
      }
      end = start + tmpwidth-1;
    }
    public void expandRegions(ArrayList regions) {

    	if (regions.size() == 0) {
    		regions.add(this);
  		}
    	
  		ArrayList ovs = new ArrayList();
  		
  		for (int i = 0; i < regions.size(); i++) {
  			ChrRegion r1 = (ChrRegion)regions.get(i);
  		
  			
  			// This could be done with and intersect in ChrRegion
  			
  			if (overlaps(r1)) {
  				
  				//        ###############     reg
  				//   @@@@@@@@@                r1

  				
  				if (end     > r1.getEnd() &&
  						start   <= r1.getEnd() &&
  						start     >= r1.getStart()) {
  					ovs.add(new ChrRegion(chr,start,r1.getEnd()));
  					
  				}
  				
  				//        ###############     reg				
  				//   @@@@@@@@@@@@@@@@@@@@@@@  r1
  				if (start >= r1.getStart() &&
  						end   <= r1.getEnd()) {
  					ovs.add(new ChrRegion(chr,start,end));
  				}
  				
  				//        ###############     reg				
  				//          @@@@@@@@@         r1
  				if (start < r1.getStart() &&
  						end   > r1.getEnd()) {
  					ovs.add(new ChrRegion(chr,r1.getStart(),r1.getEnd()));
  				}
  				
  				//        ###############     reg
  				//                 @@@@@@@@@  r1
  				if (end <=  r1.getEnd() &&
  						end >=  r1.getStart() &&
  						start < r1.getStart()) {
  					ovs.add(new ChrRegion(chr,r1.getStart(),end));
  				}

  			}
  		}
  		int coord = start;
  		
  		if (ovs.size() == 0) {
  			regions.add(new ChrRegion(chr,start,end));
  			return;
  		}
  		
  		for (int i = 0; i < ovs.size(); i++) {
  			ChrRegion tmp = (ChrRegion)ovs.get(i);
  			
  			if (tmp.getStart() - 1 > coord) {
  				regions.add(new ChrRegion(chr,coord,tmp.getStart()-1));
  			}
  			coord = tmp.getEnd()+1;
  		}
  		
  		if (end> coord) {
  			regions.add(new ChrRegion(chr,coord,end));
  		}
  	}
    public void hasFeatures(boolean b) {
    	feat = new ArrayList();
    }
    public boolean hasFeatures() {
    	if (feat == null) {
    		return false;
    	} else {
    		return true;
    	}
    }
    
    public Vector overlaps(Vector feat) {
      Vector out = new Vector();
      
      for (int i = 0; i < feat.size(); i++) {
        SequenceFeature sf = (SequenceFeature)feat.elementAt(i);
        
        if (sf.overlaps(this)) {
          out.addElement(sf);
        }
      }
      return out;
    }
    public String toString() {
      
      NumberFormat nf = NumberFormat.getInstance();
      
      return chr + ":" +  nf.format(start) + "-" +  nf.format(end) + " ( " + nf.format(end-start+1) + " bases)";
    }
}

