package pogvue.datamodel;

import java.util.Hashtable;
import java.util.Vector;

public final class Chromosome {

  private Vector    bands;
  private Vector    features;
  private Hashtable feature_hash;
  private Hashtable featureDensity;
  private Vector    types;
  private int       binsize;
  private int       length;
  private final String    name;
  private boolean density = false;

  public Chromosome(int length,String name) {
    this(length,new Vector(),name);
  }
  
  public Chromosome(int length, Vector bands,String name) {
    this.length = length;
    this.bands  = bands;
    this.name   = name;

    features     = new Vector();
    feature_hash = new Hashtable();
    types        = new Vector();

  }
  

  private void _addFeature(SequenceFeature sf ) {
    if (features == null) {
      features     = new Vector();
      feature_hash = new Hashtable();
      types        = new Vector();
    }
    
    features.addElement(sf);
    
    if (!types.contains(sf.getType())) {
      types.addElement(sf.getType());
    }
    
    Vector tmp;
    
    if (!feature_hash.containsKey(sf.getType())) {
      tmp = new Vector();
    } else {
      tmp = (Vector)feature_hash.get(sf.getType());
    }
    
    tmp.addElement(sf);
    feature_hash.put(sf.getType(),tmp);
  }
  // Adding data to the chromosome
  public void addFeatures(Vector feat, boolean parse) {

    for (int i = 0; i < feat.size(); i++) {
      SequenceFeature sf = (SequenceFeature)feat.elementAt(i);
      
      if (parse) {
	  if (sf.getId().equals(name)) {
	  _addFeature(sf);
	}
      } else {
	_addFeature(sf);
      }
    }
  }
  public Hashtable featureDensity(int binsize) {

    if (featureDensity == null ||
	binsize != this.binsize) {

	featureDensity = new Hashtable();

	this.binsize = binsize;

	// Initialize the density vector

	int maxbin = length/binsize + 1;

	// Loop over all feature types

	for (int i = 0; i < types.size(); i++) {
	    String type = (String)types.elementAt(i);

	    Vector feat = (Vector)feature_hash.get(type);
	    Vector dens = get_density(feat,maxbin,binsize);
	
	    featureDensity.put(type,dens);
	}
    }
    return featureDensity;
  }
  
  private Vector get_density(Vector feat, int maxbin, int binsize) {

    Vector fdens = new Vector();

    for (int i = 0; i <= maxbin; i++) {
	fdens.addElement(new FeatureBin (name,i*binsize,(i+1)*binsize));
    }

    if (getDensity() == false) {
      for (int i = 0; i < feat.size(); i++){
	  SequenceFeature sf = (SequenceFeature)feat.elementAt(i);
	  int count = (int)sf.getScore();
	  int bin   = sf.getStart()/binsize;

	  if (bin >= 0 && bin < fdens.size()) {
	    //  System.out.println("Bin is " + bin);
	    FeatureBin fbin = (FeatureBin)fdens.elementAt(bin);
	      
	    if (fdens.elementAt(bin) != null) {
	      fbin.addFeature(sf);
	    }
	  } else {
	      System.out.println("ERROR: Feature off end of chromosome");
	  }

      }
    } else {
	for (int i = 0; i < feat.size(); i++){
	    SequenceFeature sf = (SequenceFeature)feat.elementAt(i);
      
	    int num = (int) sf.getScore();
	    int bin = (int) sf.getStart()/binsize;
	
	    if (fdens.elementAt(bin) != null) {
		FeatureBin fbin = (FeatureBin)fdens.elementAt(bin);
		fbin.setSize(num);
	    }
	}
    }
    return fdens;
  }
  public Vector getBands() {
    return bands;
  }
  public int getBinSize() {
    return binsize;
  }  
  private boolean getDensity() {
    return this.density;
  }
  public Vector getFeatures() {
    if (features == null) {
      features = new Vector();
    }
    return features;
  }
  public int getLength() {
    return length;
  }

  public String getName() {
    return name;
  }

  
  // Get/sets
  private void setBands(Vector bands) {
    this.bands = bands;
  }
  

  // Generates the density by bin

  public void setDensity(boolean dens) {
    this.density = dens;
  }


  private void setLength(int length) {
    this.length = length;
  }

}
       
