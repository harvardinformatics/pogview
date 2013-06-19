package pogvue.io;

import pogvue.analysis.AlignSeq;
import pogvue.datamodel.*;
import pogvue.datamodel.comparer.*;
import pogvue.util.QuickSort;

import java.io.IOException;
import java.util.*;
import java.awt.event.*;

public class BlatFile extends AlignFile {
  private Vector<SequenceFeature> feat;
  private ActionListener l;
  
  public BlatFile(String inStr) {
    super(inStr);
  }
  
  public BlatFile(String inFile, String type) throws IOException {
    super(inFile,type,false);
  }

  public BlatFile(String inFile, String type,boolean parse) throws IOException {
    super(inFile,type,parse);
  }
  
    public Vector getFeatures() {
	
	return feat;
    }

    public void parse() {
	feat = new Vector();

	String type = "blat";
	String line;

	try {
	  while ((line = nextLine()) != null) {
	      
	    if (line.length() > 0 && line.indexOf("#") != 0) {
		//System.out.println("Line " + line);
	      //585     1286    3       40      0       0       0       3       847     -       
	      //BC101271        1334    0       1329    chr10   135413628       82995   85171   4       
	      //1059,111,109,50,   5,1064,1175,1284,       82995,84554,84743,85121,


	      // Columns are :
	      // bin no_matches no_mismatches no_repmatches nCount? qNumInsert qBaseInsert tNumInsert tBaseInsert strand
	      // qName qsize qstart qend tname tsize tstart tend blockCount
	      // blockSizes qstarts,tstarts

	      // So we'll end up with a top feature containing subfeatures

	      StringTokenizer str  = new StringTokenizer(line,"\t");

	      // First parse the line into variables
	      int      bin        = Integer.parseInt(str.nextToken());
	      int      matches    = Integer.parseInt(str.nextToken());
	      int      mismatches = Integer.parseInt(str.nextToken());
	      int      repmatches = Integer.parseInt(str.nextToken());
	      int      ncount     = Integer.parseInt(str.nextToken());
	      int      q_n_insert = Integer.parseInt(str.nextToken());
	      int      q_b_insert = Integer.parseInt(str.nextToken());
	      int      t_n_insert = Integer.parseInt(str.nextToken());
	      int      t_b_insetr = Integer.parseInt(str.nextToken());
	      String   strand     = str.nextToken();
	      String   qname      = str.nextToken();
	      int      qsize      = Integer.parseInt(str.nextToken());
	      int      qsstart    = Integer.parseInt(str.nextToken())+1;
	      int      qend       = Integer.parseInt(str.nextToken());
	      String   tname      = str.nextToken();
	      int      tsize      = Integer.parseInt(str.nextToken());
	      int      tstart     = Integer.parseInt(str.nextToken())+1;
	      int      tend       = Integer.parseInt(str.nextToken());

	      int      blocks     = Integer.parseInt(str.nextToken());

	      String   bsizestr   = str.nextToken();
	      String   qstartstr  = str.nextToken();
	      String   tstartstr  = str.nextToken();

	      //System.out.println("Chr start " + tstart + " " + tname);
	      // Parse the start and end coords

	      StringTokenizer s1 = new StringTokenizer(qstartstr,",");
	      StringTokenizer s2 = new StringTokenizer(tstartstr  ,",");
	      StringTokenizer s3 = new StringTokenizer(bsizestr,",");

	      int[] bsizes    = new int[blocks];
	      int[] qstarts   = new int[blocks];
	      int[] tstarts   = new int[blocks];

		int i = 0;
		while (s1.hasMoreElements()) {
		  qstarts[i] = Integer.parseInt((String)s1.nextElement())+1;
		  i++;
		}
		i = 0;
		while (s2.hasMoreElements()) {
		  tstarts[i] = Integer.parseInt((String)s2.nextElement())+1;
		  i++;
		}
		i = 0;
		while (s3.hasMoreElements()) {
		  bsizes[i] = Integer.parseInt((String)s3.nextElement());
		  i++;
		}


		// Create the top feature
		SequenceFeature sf = new SequenceFeature();

		sf.setId(tname);

		// Add the hit feature - this is a dummy feature
		SequenceFeature hf = new SequenceFeature(null,"exon",1,2,"exon");
			  
		hf.setId(qname);
		sf.setHitFeature(hf);

		i = 0;

		int offset = 0;

		while (i < blocks) {
		  int tmpqstart   = qstarts[i];
		  int tmptstart   = tstarts[i];
		  int bsize       = bsizes[i];

		  // Create a query exon and a target exon (the target is actually the genome - what we usually call the query)

		  if (i == 0) {
		    offset = tmptstart - tstart;
		    if (type.equals("est")) {
			offset--;
		      }
		  }
		  Exon qe = new Exon(null,type,tmpqstart,tmpqstart+bsize-1,type,"");
		  Exon te = new Exon(null,type,tmptstart-offset,tmptstart-offset+bsize-1,type,"");

		  qe.setId(qname);
		  te.setId(tname);

		  te.setHitFeature(qe);

		  sf.addFeature(te);

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

		  te.setStrand(strand_int);
		  te.setPhase(".");

		  i++;
		}

		feat.addElement(sf);
		}
		if (line.indexOf("#type=") == 0) {
		  StringTokenizer str = new StringTokenizer(line);

		  type = str.nextToken();
		  type = type.substring(6);
		}
	    }
	  
	} catch (IOException e) {
	    e.printStackTrace();
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
	  BlatFile gff = new BlatFile(args[0],"File");

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
