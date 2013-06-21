package pogvue.datamodel;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public final class GFFGroup extends GFF {
    private final Hashtable feat;

    private GFFGroup(String name, String sequence, int start, int end) {
	super(name,sequence,start,end);
	feat = new Hashtable();
    }

    public GFFGroup(Vector features) {


	this("group","",1,2);
      System.out.println("******************* NEW GROUP **************************");
	addFeatures(features);
    }

    public void addFeature(SequenceFeature sf) {
	if (sf.getHitFeature() == null) {
	    System.out.println("ERROR: No hit feature so can't group by hitname in GFFHash");
	} else {
	    if (feat.get(sf.getHitFeature().getId()) == null)  {
		System.out.println("Adding vector for " + sf.getHitFeature().getId());
		Vector f = new Vector();
		feat.put(sf.getHitFeature().getId(),f);
	    }

	    Vector f = (Vector)feat.get(sf.getHitFeature().getId());

	    f.addElement(sf);
	}

    }
    public SequenceFeature getFeatureAt(int i) {
	Vector v = getFeatures();

	if (v.size() > i) {
	    return (SequenceFeature)v.elementAt(i);
	} else {
	    return null;
	}
    }
  
    public Hashtable getFeatureHash() {
	return feat;
    }

    public Vector<SequenceFeature> getFeatures() {
	Vector v = new Vector();

	Enumeration en = feat.keys();

	while (en.hasMoreElements()) {
	    String id = (String)en.nextElement();

	    Vector tmp = (Vector)feat.get(id);

	    for (int i = 0; i < tmp.size(); i++) {
		v.addElement(tmp.elementAt(i));
	    }
	}
	return v;
    }

    public Vector getFeatureSet(String id) {
	if (feat.get(id) != null) {
	    return (Vector)feat.get(id);
	} else {
	    return null;
	}
    }

    public Vector<SequenceFeature> overlaps(int start, int end) {
	Vector<SequenceFeature> out = new Vector<SequenceFeature>();
	
	Vector v = getFeatures();

	for (int i = 0; i < v.size(); i++) {
	    SequenceFeature sf = (SequenceFeature)v.elementAt(i);
	    
	    if (!(sf.getStart() > end || sf.getEnd() < start)) {
		out.addElement(sf);
	    }
	}
	return out;
    }

    public Hashtable overlapsHash(int start, int end) {
      Hashtable out = new Hashtable();
	
      Hashtable h = getFeatureHash();

      Enumeration en = h.keys();

      while (en.hasMoreElements()) {
	String name = (String)en.nextElement();
	Vector v    = (Vector)h.get(name);

	int overlap = 0;
	for (int i = 0; i < v.size(); i++) {
	  SequenceFeature sf = (SequenceFeature)v.elementAt(i);
	    
	  if (!(sf.getStart() > end || sf.getEnd() < start)) {
	    overlap = 1;
	    i = v.size();
	  }
	}
	if (overlap == 1) {
	  out.put(name,v);
	}
      }
      return out;
    }

}











