package pogvue.io;

import pogvue.datamodel.*;
import pogvue.gui.AlignmentPanel;
import pogvue.util.Format;
import pogvue.analysis.*;

import java.awt.*;
import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

public class MafFile extends AlignFile {

    public static String[] mamm_arr = new String[]{"Human","Chimp","Macaque","Tarsier","MouseLemur","Bushbaby","TreeShrew","Mouse","Rat","KangRat","Guineapig","Squirrel","Rabbit","Pika","Alpaca","Dolphin","Cow","Horse","Cat","Dog","Microbat","FruitBat","Hedgehog","Shrew","Elephant","Hyrax","Tenrec","Armadillo","Sloth"};


    private String       reforg = null;
    private Vector       orgs;
    private Hashtable    orgstr;

    private long         chunkpos = 0;

    public MafFile(Sequence[] s) {
	super(s);
    }
    
    public MafFile(String inStr) {
	super(inStr);
    }
    
    public void initData() {
	super.initData();
    }
    
    private MafFile(String inFile, String type, boolean parse) throws IOException {
	super(inFile,type,parse);
    }

    private MafFile(String inFile, String type) throws IOException {
	this(inFile,type,false);
    }
    
    public void parse()  {
	int prev = 0;
	
	Hashtable    sfhash;
	StringBuffer refstr = new StringBuffer();
	
	orgstr = new Hashtable();
	orgs = new Vector();
	
	try {
	    while ((sfhash = get_maf_feat(bufReader,"hg18",0,null)) != null) {
		
		SequenceFeature sf = (SequenceFeature)sfhash.get(reforg);
		
		if (sf.getStart() <= prev) {
		    System.err.println("Discarding due to overlap " + sf.getStart() + " " + prev);
		} else {
		    
		    int newstart = sf.getStart();
		    
		    Enumeration en = sfhash.keys();
		    
		    while (en.hasMoreElements()) {
			String tmporg = (String)en.nextElement();
			
			if (!orgs.contains(tmporg) && !tmporg.equals(reforg)) {
			    orgs.addElement(tmporg);
			    
			    String tmpstr = pad_string('-',refstr.length());
			    
			    orgstr.put(tmporg,new StringBuffer(tmpstr));
			}
		    }
		    
		    // This is the padding in between pieces.  Should come from a reference sequence
		    
		    refstr.append(pad_string('-',newstart-prev-1));
		    
		    // Add in organism pads

		    for (int i = 0; i < orgs.size(); i++) {
			String org = (String)orgs.elementAt(i);
			StringBuffer tmpstr = (StringBuffer)orgstr.get(org);
			
			tmpstr.append(pad_string('-',(newstart-prev-1)));
			
		    }
		    
		    String refaln = sf.getAlignString();
		    
		    char[] refaln_arr = refaln.toCharArray();
		    
		    char[][] aln_arr = new char[orgs.size()][];
		    
		    for (int i = 0; i < orgs.size(); i++) {
			String org = (String)orgs.elementAt(i);
			
			if (sfhash.containsKey(org)) {
			    SequenceFeature tmpsf = (SequenceFeature)sfhash.get(org);
			    
			    String tmpaln = tmpsf.getAlignString();
			    
			    char[] tmparr = tmpaln.toCharArray();
			    
			    aln_arr[i] = tmparr;
			}
		    }
		    
		    int len;
		    int i = 0;
		    StringBuffer refaln_new = new StringBuffer();
		    
		    // Now only add in non-gap reforg columns
		    
		    while (i < refaln.length()) {
			
			for (int ii = 0; ii < orgs.size(); ii++) {
			    String org = (String)orgs.elementAt(ii);
			    
			    if (sfhash.containsKey(org)) {
				if (refaln_arr[i] != '-') {
				    StringBuffer tmp = (StringBuffer)orgstr.get(org);
				    tmp.append(aln_arr[ii][i]);
				}
			    }
			}
			i++;
		    }
		    
		    i = 0;
		    
		    while (i < refaln.length()) {
			if (refaln_arr[i] != '-') {
			    refaln_new.append(refaln_arr[i]);
			}
			i++;
		    }
		    
		    // Add in pads for orgs not in the alignment
		    for (int ii = 0; ii < orgs.size(); ii++) {
			String org = (String)orgs.elementAt(ii);
			
			if (!sfhash.containsKey(org)) {
			    StringBuffer tmpstr = (StringBuffer)orgstr.get(org);
			    
		      tmpstr.append(pad_string('-',refaln_new.length()));
		  }
	      } 

	      refstr.append(refaln_new);

	      //	      System.err.println(refstr);

	      for (int ii = 0; ii < orgs.size(); ii++) {

		  String org = (String)orgs.elementAt(ii);
		  
		  //		  System.err.println((StringBuffer)orgstr.get(org));
	      }
	      
	      prev = sf.getEnd();
	  }
      }

      seqs = new Vector();

      seqs.addElement(new Sequence(reforg,refstr.toString().toUpperCase(),1,refstr.length()));

      for (int i = 0; i < orgs.size(); i++) {
	  String org = (String)orgs.elementAt(i);
	  String tmpstr = orgstr.get(org).toString();

	  seqs.addElement(new Sequence(org,tmpstr.toUpperCase(),1,refstr.length()));
      }

      } catch  (IOException e) {
	  System.out.println("Exception " + e);
      }
  }

    public static void print_piece(Hashtable piece) {
	
	Enumeration en = piece.keys();
	
	while  (en.hasMoreElements()) {
	    String tmporg = (String)en.nextElement();     
	    
	    if (tmporg.indexOf("pos") < 0) {
		SequenceFeature s = (SequenceFeature)piece.get(tmporg);
		
		int len = s.getAlignString().length();
		int chunk = 50;
		if (len < 50) {
		    chunk = len;
		}
		System.out.println("Org\t" + tmporg + "\t" + s.getStart() + "\t" + s.getEnd() + "\t" + s.getStrand() + "\t" + len + " " + s.getAlignString().substring(0,chunk) + "\t" + s.getAlignString().substring(len-chunk,len-1));
	    }
	}
    }

  public static String build_str(String s, int len) {
    int i = 0;

    StringBuffer out = new StringBuffer();

    while (i < len) {
      out.append(s);
      i++;
    }
    return out.toString();
  }

  private static Hashtable get_maf_feat(BufferedReader buf, String reforg,long pos,RandomAccessFile raf) throws IOException {
	String line;

	double score;
	Vector orgs = new Vector();
	Hashtable out = new Hashtable();
		
	while ((line = buf.readLine()) != null) {
	    //	    System.out.println("Line " + line);
	  pos += line.length() + 1;

	  out.put("pos",new Long(pos));

	  if (line.indexOf("a score=") == 0) {
	      long chunkpos = pos - line.length() - 1;
	    out.put("chunkpos",new Long(chunkpos));

	    while ((line = buf.readLine()) != null && !line.equals("")) {
	      pos += line.length() +1;

	      out.put("pos",new Long(pos));

		if (line.indexOf("#") != 0 && line.indexOf("s") == 0) {
		    //System.out.println(":ine" + line);
		  StringTokenizer str = new StringTokenizer(line);
		  
		  SequenceFeature sf = new SequenceFeature();
		  
		  String horg = "";
		
		  str.nextToken();
		  
		  String name    = str.nextToken();
		  int    start   = Integer.parseInt(str.nextToken());
		  int    len     = Integer.parseInt(str.nextToken());
		  String strand  = str.nextToken();
		  int    srcsize = Integer.parseInt(str.nextToken());
		  String aln     = str.nextToken();
		  
		  if (name.indexOf(".") > 0) {
		    horg = name.substring(0,name.indexOf("."));
		    name = name.substring(name.indexOf(".") + 1);
		  }
		  
		  if (reforg == null) {
		    reforg = horg;
		  }
		  
		  orgs.addElement(horg);
		  
		  sf.setId(name);
		  sf.setStart(start+1);
		  sf.setEnd(start+len);
		  
		  
		  if (strand.equals("+")) {
		    sf.setStrand(1);
		  } else if (strand.equals("-")) {
		    sf.setStrand(-1);
		  } else {
		    System.out.println("Wrong strand");
		  }
		  
		  if (strand.equals("-")) {
		    sf.setEnd(srcsize - start);
		    sf.setStart(sf.getEnd() - len);
		  }
		  
		  sf.setAlignString(aln);
		  
		  out.put(horg,sf);
		}
	    }
	    pos++;

	    out.put("pos",new Long(pos));
	    return out;
	  }
	}
	return null;
    }

    public static Hashtable trim_piece(Hashtable piece,int start, int end, String org, int piece_coord) {

	// start and end are in piece coords i.e. 0 to len-1
	// piece coord is the offset

	SequenceFeature refseq = (SequenceFeature)piece.get(org);

	if (refseq.getStrand() == -1 ) {
	    System.out.println("Reverse piece");
	}

	char[]   c      = refseq.getAlignString().toCharArray();
	
	Vector seqs = new Vector();
	Vector feat = new Vector();

	Enumeration en = piece.keys();
	
	while (en.hasMoreElements()) {
	    String tmporg = (String)en.nextElement();
	    
	    if (!tmporg.equals(org) &&
		tmporg.indexOf("pos") < 0) {

		SequenceFeature tmpseq   = (SequenceFeature)piece.get(tmporg);
		char[]          tmpchars = tmpseq.getAlignString().toCharArray();

		seqs.addElement(tmpchars);
		feat.addElement(tmpseq);
	    }
	}
	
	int i      = 0;
	int coord  = 0;
	
	int piece_start = piece_coord;
	int piece_end   = -1;
	int strend      = -1;
	int strstart    = -1;
	
	
	//  start and end are coords in the piece frame (i.e. from 0 to len-1)
	//  As we loop over all the bases in the piece we need to :
	//      Increment/decrement all the sequence coords (depending on strand)
	//      Keep a note of all coords when we hit start and end
	//
    
	// CCCAAAAAAAAAAAAAAAAAAAAAAAaa
	// CCC-----AAAAAAAAAAAAAAAAAAAA
	//    ^

	// AAAAAAAAAAAAAAAAAAAAAAAaa
	// -----AAAAAAAAAAAAAAAAAAAA
	//    
	
	int[] currcoord = new int[seqs.size()];
	int[] starts    = new int[seqs.size()];
	int[] ends      = new int[seqs.size()];

	while (i < c.length && coord <= end) {

	    // Initialize the coords

	    if (i == 0) {
		int j = 0;

		while (j < seqs.size()) {
		    char[] tmpc = (char[])seqs.elementAt(j);
		    SequenceFeature sf = (SequenceFeature)feat.elementAt(j);

		    if (tmpc[j] != '-') {
			if (sf.getStrand() == 1) {
			    currcoord[j] = sf.getStart();
			} else {
			    currcoord[j] = sf.getEnd();
			}
		    } else {
			if (sf.getStrand() == 1) {
			    currcoord[j] = sf.getStart()-1;
			} else {
			    currcoord[j] = sf.getEnd()+1;
			}
		    }
		    j++;
		}
	    } else {

		// Increment the coords where needed
		if (c[i] != '-') {
		    coord++;
		    piece_coord++;
		}

		int j = 0;

		while (j < seqs.size()) {
		    char[] tmpc = (char[])seqs.elementAt(j);
		    SequenceFeature sf = (SequenceFeature)feat.elementAt(j);
		    if (tmpc[i] != '-') {
			if (sf.getStrand() == 1) {
			    currcoord[j]++;
			} else {
			    currcoord[j]--;
			}
		    }
		    j++;
		}
	    }

	    if (coord == start) {
		strstart    = i;
		piece_start = piece_coord;

		int j = 0;

		while (j < seqs.size()) {
		    starts[j] = currcoord[j];
		    j++;
		}

	    }

	    if (coord == end) {
		strend    = i;
		piece_end = piece_coord;

		int j = 0;
		while (j < seqs.size()) {
		    ends[j] = currcoord[j];
		    j++;
		}

		break;
	    }
      

	    i++;
	}

    if (piece_end == -1) {
      System.out.println("Setting piece_end");
      piece_end = piece_coord;
      
      int j = 0;
      while (j < seqs.size()) {
	  ends[j] = currcoord[j];
	  j++;
      }
    }

    Hashtable newstr = new Hashtable();

    System.out.println("Trimming a string "  + refseq.getAlignString().length() + 
		       " long to " + strstart + " " + strend + "\n");
    System.out.println("Trimming a string "  + refseq.getAlignString().length() + 
		       " long to " + piece_start + " " + piece_end + "\n");

    int len;

    en = piece.keys();
    int j = 0;

    while (en.hasMoreElements()) {
	String tmporg = (String)en.nextElement();
	if (!tmporg.equals(org) &&
	    tmporg.indexOf("pos") < 0) {
	    SequenceFeature s = (SequenceFeature)piece.get(tmporg);

	    String seq = s.getAlignString();

	    seq = seq.substring(strstart,strend+1);
	    
	    s.setAlignString(seq);

	    if (s.getStrand() == 1) {
		s.setStart(starts[j]);
		s.setEnd(ends[j]);
	    } else {
		s.setStart(ends[j]);
		s.setEnd(starts[j]);
	    } 
	    j++;
	}
    }
    SequenceFeature s = (SequenceFeature)piece.get(org);
    
    String seq = s.getAlignString();
    
    seq = seq.substring(strstart,strend+1);
    
    s.setAlignString(seq);
    
    
    s.setStart(piece_start);
    s.setEnd(piece_end);

    return piece;
}


    public String print() {
	return "";
    }
    public static String print(Sequence[] s) {
    StringBuffer out = new StringBuffer("");

    int max = 0;
    int maxid = 0;

    int i = 0;

    while (i < s.length && s[i] != null) {
      String tmp = s[i].getName() + "/" + s[i].getStart()+ "-" + s[i].getEnd();

      if (s[i].getSequence().length() > max) {
        max = s[i].getSequence().length();
      }
      if (tmp.length() > maxid) {
        maxid = tmp.length();
      }
      i++;
    }

    if (maxid < 15) {
      maxid = 15;
    }

    int j = 0;
    while ( j < s.length && s[j] != null) {
        out.append(new Format("%-" + maxid + "s").form(s[j].getName() + "/" + s[j].getStart() + "-" + s[j].getEnd())).append(" ");

        out.append(s[j].getSequence()).append("\n");
      j++;
    }
    out.append("\n");

    return out.toString();
  }


    private String pad_string(char c, int len) {

	StringBuffer out = new StringBuffer();

	int i = 0;

	while (i < len) {
	    out.append(c);
	    i++;
	}

	return out.toString();
    }

  public static void index(String filestr,String reforg,String filechr) throws IOException {
    
    // Change this to use readers
    //RandomAccessFile file = new RandomAccessFile(filestr,"r");
    //FileReader       fReader = new FileReader(file.getFD());
    //LineNumberReader bReader = new LineNumberReader(fReader, 65536);  // default buffersize 8192

    MafFile maf = new MafFile(filestr,"File",false);

    // This should be hash of SequenceFeatures
    Hashtable maffeat;

    long pos = 0;
    while ((maffeat = MafFile.get_maf_feat(maf.bufReader,reforg,pos,maf.raf)) != null) {
      SequenceFeature sf = (SequenceFeature)maffeat.get(reforg);

      long chunkpos = Long.valueOf((Long)maffeat.get("chunkpos"));
      pos = Long.valueOf((Long)maffeat.get("pos"));
      //System.out.println("Pos " + pos + " " + chunkpos);
      if (maffeat.get(reforg) != null) {
	System.out.println(sf.getId() + " " + sf.getStart() + " " + sf.getEnd() + " " + filechr + " " + chunkpos);
      }

    }
  }

  public static Hashtable get_align(String chr, int start, int end,String reforg) throws IOException  {

    String indexfile = "./perl/chr1.maf.index";
    String maffile   = "./perl/chr1.maf";

    BSearch bs = new BSearch(indexfile,1," ");
    
    RandomAccessFile raf = bs.search_file(start);
    FileReader       fReader = new FileReader(raf.getFD());
    LineNumberReader bReader = new LineNumberReader(fReader, 65536);  // default buffersize 8192
    
    
    String line  = bReader.readLine();

    System.out.println("Line " + line);

    StringTokenizer str = new StringTokenizer(line," ");

    String orgchr   = str.nextToken();
    long   orgstart = Long.parseLong(str.nextToken());
    long   orgend   = Long.parseLong(str.nextToken());
    String hchr     = str.nextToken();
    long   pos      = Long.parseLong(str.nextToken());
      
    RandomAccessFile raf2 = new RandomAccessFile(maffile,"r");
    raf2.seek(pos);
      
    FileReader       fr = new FileReader(raf2.getFD());
    LineNumberReader br = new LineNumberReader(fr,65536);
      

    Hashtable fullstr = new Hashtable();
    
    int prev = start-1;
    
    Hashtable piece;
    
    while ((piece = get_maf_feat(br,reforg,0,null)) != null) {
      
      System.out.println( "\nOriginal piece (cf " + start + " " + end + "\n");
      
      print_piece(piece);
      
      //String ref = "Homo_sapiens";
      String ref = reforg;
      SequenceFeature  reff = (SequenceFeature)piece.get(ref);
      
      int piece_end   = reff.getEnd();
      int piece_start = reff.getStart();
      int piece_len   = piece_end-piece_start + 1;
      
      System.out.println("Piece " + start + " " + end + " " + piece_start + " " + piece_end);
      
      if (piece_end < start) {
	  System.out.println("Breaking start");
	break;
      }
      if (end < piece_start) {	
	  System.out.println("Breaking end " + (end-piece_start));
	break;
      }
	
      //Different cases
	
      // --------------------------
      //           ^start
      // piece overlaps start
	
      // TRIM
      System.out.println("Trimming");
	
      if (piece_start < (prev +1) || piece_end > end) {
	int trimstart = 0;
	int trimend   = piece_len - 1;
	
	if (piece_start < (prev+1)) {
	  trimstart = (prev + 1) - piece_start;
	  System.out.println("Trimming start by " + trimstart + "\n");
	}
	if (piece_end > end) {
	  System.out.println("Piece length " + piece_len+ "\n");
	  System.out.println("End diff " + piece_end + " " + end + "\n");

	  trimend = end - piece_start;
	  System.out.println("Trimming end to " + trimend + "\n");
	}
	    
	piece = trim_piece(piece,trimstart,trimend,ref,piece_start);
	    
	print_piece(piece);
	
	piece_start = reff.getStart();
	piece_end   = reff.getEnd();
      }
	
      // ...........--------------
      //     ^ start
      //
      //There's a gap before the piece - 
	
      if (piece_start > (prev+1)) {
	    
	int padlen     = piece_start - prev - 1;
	String padstr  = build_str("-",padlen);
	String hpadstr = build_str("N",padlen);

	System.out.println("PADDING\t" + padlen + " " + piece_start + "\n");

	for (int i = 0; i < mamm_arr.length; i++) {
	  String org = mamm_arr[i];
	  StringBuffer tmpstr = null;

	  if (fullstr.containsKey(org)) {
	    tmpstr = (StringBuffer)fullstr.get(org);
	  } else {
	    tmpstr = new StringBuffer();
	    fullstr.put(org,tmpstr);
	   }

	  if (org.equals(ref)) {
	    tmpstr.append(hpadstr);
	  } else {
	    tmpstr.append(padstr);
	  }
	} 
      }
      // Add in the section
            
      int padlen    = reff.getAlignString().length();
      String padstr = build_str("-",padlen);
      
	for (int i = 0; i < mamm_arr.length; i++) {
	  String org = mamm_arr[i];
	  StringBuffer tmpstr;

	  if (fullstr.containsKey(org)) {
	    tmpstr = (StringBuffer)fullstr.get(org);
	  } else {
	    tmpstr = new StringBuffer();
	    fullstr.put(org,tmpstr);
	  }



	  if (piece.containsKey(org)) {
	    Sequence s = (Sequence)piece.get(org);
	    tmpstr.append(s.getSequence());
		
	    if (padstr.length() != s.getSequence().length()) {
	      System.out.println("EEEEK! lengths not the same\n");
	    }
	  } else {
	    tmpstr.append(padstr);
	  }
	}
      
	
      //Set prev
      
      prev = piece_end;
      System.out.println("Ends " + piece_end + " " + end);
      if (piece_end >= end) {
	break;
      }

    }
      
    // Trim or pad end.
    
    if ((end - prev) > 0) {
      int padlen = end-prev;
      String padstr = build_str("-",padlen);
      
      for (int i = 0; i < mamm_arr.length; i++) {
	String org = mamm_arr[i];
	StringBuffer tmpstr;

	if (fullstr.containsKey(org)) {
	  tmpstr = (StringBuffer)fullstr.get(org);
	} else {
	  tmpstr = new StringBuffer();
	  fullstr.put(org,tmpstr);
	}
	tmpstr.append(padstr);
      }
    }
    
    return fullstr;
    
  }


  public static void main(String[] args) {
    try {
      if (args[0].equals("-index")) {
	MafFile.index(args[1],args[2],args[3]);
      } else {
	String indexfile = "./perl/chr1.maf.index";
	String maffile   = "./perl/chr1.maf";

	String chr = args[1];
	int start  = Integer.parseInt(args[2]);
	int end    = Integer.parseInt(args[3]);
	
	Hashtable aln = MafFile.get_align(chr,start,end,"hg18");


	Enumeration en = aln.keys();

	while (en.hasMoreElements()) {
	    String org = (String)en.nextElement();

	    StringBuffer buf = (StringBuffer)aln.get(org);
	    String seq = buf.toString();

	    System.out.println(">" + org);

	    int pos = 0;
	    while (pos < seq.length()) {
		System.out.println(seq.substring(0,50));
		pos += 50;
	    }
	}
      }
    } catch (IOException e) {
	e.printStackTrace();
    }
  }
}
