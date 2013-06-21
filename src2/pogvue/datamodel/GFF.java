package pogvue.datamodel;

import java.util.*;
import pogvue.io.*;
import pogvue.gui.AlignViewport;

public class GFF extends Sequence {
  double maxscore;
  double minscore;
  boolean found_scores = false;
  Vector<SequenceFeature> feat;
  
  public GFF(String name, String sequence, int start, int end) {
    super(name, sequence, start, end);
    feat = new Vector<SequenceFeature>();
  }
  
  public void addFeature(SequenceFeature sf) {
    feat.addElement(sf);
  }
  
  public void addFeatures(Vector feat) {
    for (int i = 0; i < feat.size(); i++) {
      addFeature((SequenceFeature) feat.elementAt(i));
    }
  }
  
  public SequenceFeature getFeatureAt(int i) {
    return feat.elementAt(i);
  }
  
  public Vector getFeatures() {
    return feat;
  }
  
  public double getMaxScore() {
    if (found_scores == false) {
      minscore = 10000000000.0;
      for (int i = 0; i < feat.size(); i++) {
	SequenceFeature sf = (SequenceFeature) feat.elementAt(i);
	
	if (sf.getScore() > maxscore) {
	  maxscore = sf.getScore();
	}
	if (sf.getScore() < minscore) {
	  minscore = sf.getScore();
	}
      }
      found_scores = true;
      
    }
    return maxscore;
  }
  
  public double getMinScore() {
    if (found_scores == false) {
      minscore = 10000000000.0;
      for (int i = 0; i < feat.size(); i++) {
	SequenceFeature sf = (SequenceFeature) feat.elementAt(i);
	
	if (sf.getScore() > maxscore) {
	  maxscore = sf.getScore();
	}
	if (sf.getScore() < minscore) {
	  minscore = sf.getScore();
	}
	
      }
      found_scores = true;
      
    }
    return minscore;
  }
  
  public Hashtable getScores() {
    if (feat.size() > 0) {
      return ((SequenceFeature) feat.elementAt(0)).getScores();
    } else {
      return null;
    }
  }
  
  public String getType() {
    if (feat.size() > 0) {
      return ((SequenceFeature) feat.elementAt(0)).getType();
    } else {
      return null;
    }
  }
  
  public Vector<SequenceFeature> overlaps(int start, int end) {
    Vector<SequenceFeature> out = new Vector<SequenceFeature>();
    
    for (int i = 0; i < feat.size(); i++) {
      SequenceFeature sf = feat.elementAt(i);
      
      if (!(sf.getStart() > end || sf.getEnd() < start)) {
	out.addElement(sf);
      }
    }
    return out;
  }
  
  
  public static Hashtable hashHitFeatures(Vector feat) {
    
    Hashtable out = new Hashtable();
    
    for (int i = 0; i < feat.size(); i++) {
      
      SequenceFeature sf = (SequenceFeature) feat.elementAt(i);
      
      if (sf.getHitFeature() != null) {
	
	String name = sf.getHitFeature().getId();
	
	if (!out.containsKey(name)) {
	  Vector tmp = new Vector();
	  tmp.addElement(sf);
	  out.put(name, tmp);
	} else {
	  Vector tmp = (Vector) out.get(name);
	  tmp.addElement(sf);
	}
      } else {
	if (!out.containsKey("Single")) {
	  Vector tmp = new Vector();
	  tmp.addElement(sf);
	  out.put("Single", tmp);
	} else {
	  Vector tmp = (Vector) out.get("Single");
	  tmp.addElement(sf);
	}
	
      }
      
    }
    
    Hashtable out2 = new Hashtable();
    Enumeration en = out.keys();
    
    while (en.hasMoreElements()) {
      
      String name = (String) en.nextElement();
      
      SequenceFeature sf = new SequenceFeature();
      sf.addFeatures((Vector) out.get(name));
      
      out2.put(name, sf);
    }
    
    return out2;
  }
  
  // These are methods transferred from the renderer to where the data is.
  
  public boolean isVisible(AlignViewport av) {   
    // What about av.getCharWidth() - change to av.overWidthThreshold()
    if (getType() == null ||  av.getCharWidth() < 0.01 && getType().equals("repeat")) {
      return false;
    } else {
      return true;
    }
  }
  
  // This isn't ideal as it is passing data back but it's a start
  public Vector overlappingFeatures(int start, int end) {
    Vector feat = overlaps(start,end);
    
    for (int i = 0; i < feat.size(); i++) {
      SequenceFeature sftmp = (SequenceFeature)feat.elementAt(i);
      
      Vector tmpf = new Vector();
      
      if (sftmp.getFeatures() != null) {
	tmpf = sftmp.getFeatures();
      } else {
	tmpf.addElement(sftmp);
      }
    }
    return feat;
  }
}
