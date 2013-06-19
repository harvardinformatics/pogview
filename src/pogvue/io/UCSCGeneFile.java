package pogvue.io;

import pogvue.analysis.AlignSeq;
import pogvue.datamodel.*;
import pogvue.datamodel.comparer.*;
import pogvue.util.QuickSort;

import java.io.IOException;
import java.util.*;
import java.awt.event.*;

public class UCSCGeneFile extends AlignFile {
  private Vector<SequenceFeature> feat;
  private ActionListener l;
  
  public UCSCGeneFile(String inStr) {
    super(inStr);
  }
  
  public UCSCGeneFile(String inFile, String type) throws IOException {
    super(inFile,type,true);
  }

  public UCSCGeneFile(String inFile, String type,boolean parse) throws IOException {
    super(inFile,type,parse);
  }
  
  public void setActionListener(ActionListener l) {
    this.l = l;
  }
  public Vector<SequenceFeature> getFeatures() {
    return feat;
  }
  
  public Hashtable<String, GFF> getFeatureHash() {
    Hashtable<String, GFF> fhash = new Hashtable<String, GFF>();
    
    for (int i = 0; i < feat.size(); i++) {
      SequenceFeature sf = feat.elementAt(i);
      String type = sf.getType();
      
      if (fhash.get(type) == null) {
	GFF g = new GFF(type,"",1,2);
	fhash.put(type,g);
      }
      
      GFF g = fhash.get(type);
      
      g.addFeature(sf);
    }
    
    // Group the features if they have a hit id
    
    Enumeration en = fhash.keys();
    
    // Loop over feature types
    while (en.hasMoreElements()) {
      String type = (String)en.nextElement();
      
      GFF gfffeat = (GFF)fhash.get(type);
      
      Vector feat = (Vector)gfffeat.getFeatures();
      
      SequenceFeature sf = (SequenceFeature) feat.elementAt(0);
      
      
      if (sf.getHitFeature() != null) {
	
	Vector newfeat = new Vector();
	
	Hashtable hashset = hash_set(feat);
	
	Enumeration en2 = hashset.keys();

		while (en2.hasMoreElements()) {
		    String hitid = (String)en2.nextElement();

		    Vector f = (Vector)hashset.get(hitid);

		    SequenceFeature sf2 = new SequenceFeature();

		    for (int i = 0; i < f.size(); i++) {
			sf2.addFeature((SequenceFeature)f.elementAt(i));
		    }
		    newfeat.addElement(sf2);
		}
		GFF g = new GFF(type,"",1,2);

		for (int i = 0; i < newfeat.size(); i++) {
		    g.addFeature((SequenceFeature)newfeat.elementAt(i));
		}
		fhash.put(type,g);
	    }
	}
    	return fhash;
    }
    public void parse() {
	feat = new Vector();

	String line;

	try {
	  while ((line = nextLine()) != null) {
	    
	    if (line.length() > 0 && line.indexOf("#") != 0) {
	      
	      StringTokenizer str  = new StringTokenizer(line,"\t");

	      if (str.countTokens() >= 8) {

		// First parse the line into variables
		String name      = str.nextToken();
		String refid        = str.nextToken();
		String chr       = str.nextToken();
		String strand    = str.nextToken();
		int    txstart   = Integer.parseInt(str.nextToken());
		int    txend     = Integer.parseInt(str.nextToken());
		int    cdsstart  = Integer.parseInt(str.nextToken());
		int    cdsend    = Integer.parseInt(str.nextToken());
		int    exons     = Integer.parseInt(str.nextToken());
		String startstr  = str.nextToken();
		String endstr    = str.nextToken();


		String dbid = "";
		String id   = "";

		if (str.hasMoreElements()) {
		  dbid      = str.nextToken();
		}
		if (str.hasMoreElements()) {
		  id      = str.nextToken();
		}

		// Parse the start and end coords
		StringTokenizer s1 = new StringTokenizer(startstr,",");
		StringTokenizer s2 = new StringTokenizer(endstr  ,",");

		int[] starts = new int[exons];
		int[] ends   = new int[exons];

		int i = 0;
		while (s1.hasMoreElements()) {
		  starts[i] = Integer.parseInt((String)s1.nextElement());
		  i++;
		}
		i = 0;
		while (s2.hasMoreElements()) {
		  ends[i] = Integer.parseInt((String)s2.nextElement());
		  i++;
		}


		// Create the top feature
		SequenceFeature sf = new SequenceFeature();

		sf.setId(chr);

		// Add the hit feature
		SequenceFeature hf = new SequenceFeature(null,"exon",1,2,"exon");
			  
		hf.setId(name);
		sf.setHitFeature(hf);

		i = 0;

		while (i < exons) {
		  int tmpstart = starts[i];
		  int tmpend   = ends[i];

		  // Create an exon

		  Exon e = new Exon(null,"exon",tmpstart,tmpend,"exon","");

		  sf.addFeature(e);

		  int strand_int;
			
		  if (strand.equals("+") || 
		      strand.equals("1")) {
		    strand_int = 1;
		  } else if (strand.equals("-") ||
			     strand.equals("-1")) {
		    strand_int = -1;
		  } else {
		    strand_int = 0;
		  }

		  e.setStrand(strand_int);
		  e.setType("exon");
		  e.setHitFeature(hf);
		  e.setId(sf.getId());
		  e.setPhase(".");

		  // Check for coding start and end
		  if (cdsstart >= tmpstart &&
		      cdsstart <= tmpend) {

		    e.setCodingStart(cdsstart);

		  }

		  if (cdsend >= tmpstart &&
		      cdsend <= tmpend) {
		    e.setCodingEnd(cdsend);
		  }
		  i++;
		}
		feat.addElement(sf);
	      }
	    }
	  }  
	} catch (IOException e) {
	  System.out.println("Exception parsing GFFFile");
	}
    }

  private static String print(Sequence[] s) {
    return print(s,72);
  }
  private static String print(Sequence[] s, int len) {
    return print(s,len,true);
  }
  private static String print(Sequence[] s, int len,boolean gaps) {
    return "";
  }
  
  public String print() {
    return print(getSeqsAsArray());
  } 
  
  public static void main(String args[]) {
	try {
	  UCSCGeneFile gff = new UCSCGeneFile(args[0],"File");

	  Vector feat     = gff.getFeatures();

	  for (int i = 0; i < feat.size(); i++) {
	    SequenceFeature sf = (SequenceFeature)feat.elementAt(i);

	    System.out.println("Top feat " + sf.toGFFString());

	    if (sf.getFeatures() != null) {
	      Vector subf = sf.getFeatures();

	      for (int j = 0; j < subf.size(); j++) {
		SequenceFeature tmpf = (SequenceFeature)subf.elementAt(j);
		
		System.out.println("   Sub feat  " + tmpf.toGFFString());

	      }
	    }
	  }
	} catch (IOException e) {
	  System.out.println("Exception " + e);
	}
  }


    private Hashtable hash_set(Vector feat) {
	Hashtable fhash = new Hashtable();

	for (int i = 0;i < feat.size(); i++) {

	    SequenceFeature sf = (SequenceFeature)feat.elementAt(i);

	    if (fhash.get(sf.getHitFeature().getId()) == null) {
		Vector v = new Vector();
		fhash.put(sf.getHitFeature().getId(),v);
	    }

	    Vector v = (Vector)fhash.get(sf.getHitFeature().getId());

	    v.addElement(sf);
	}
	
	return fhash;
    }


}





