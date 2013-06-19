package pogvue.io;

import pogvue.datamodel.*;
import pogvue.analysis.BSearch;
import java.util.*;
import java.io.*;

public class EMFFile {
  
  public static String[] mammarr = new String[]{"Human","Chimp","Macaque","Tarsier","MouseLemur","Bushbaby","TreeShrew","Mouse","Rat","KangRat","Guineapig","Squirrel","Rabbit","Pika","Alpaca","Dolphin","Cow","Horse","Cat","Dog","Microbat","FruitBat","Hedgehog","Shrew","Elephant","Hyrax","Tenrec","Armadillo","Sloth"};


  public static String[] latin_orgs = new String[]{"Homo_sapiens"           ,
						   "Pan_troglodytes"        ,
						   "Pongo_pygmaeus"         ,
						   "Macaca_mulatta"         ,
						   "Tarsius_syrichta"       ,
						   "Microcebus_murinus"     ,
						   "Otolemur_garnettii"     ,
						   "Tupaia_belangeri"       ,
						   "Mus_musculus"           ,
						   "Rattus_norvegicus"      ,
						   "Dipodomys_ordii"        ,
						   "Cavia_porcellus"        ,
						   "Spermophilus_tridecemlineatus"  ,
						   "Oryctolagus_cuniculus"  ,
						   "Ochotona_princeps"      ,
						   "Vicugna_pacos"          ,
						   "Tursiops_truncatus"     ,
						   "Bos_taurus"             ,
						   "Equus_caballus"         ,
						   "Felis_catus"            ,
						   "Canis_familiaris"       ,
						   "Myotis_lucifugus"       ,
						   "Pteropus_vampyrus"      ,
						   "Erinaceus_europaeus"    ,
						   "Sorex_araneus"          ,
						   "Loxodonta_africana"     ,
						   "Procavia_capensis"      ,
						   "Echinops_telfairi"      ,
						   "Dasypus_novemcinctus"   ,
  };

  public static final Hashtable latin2readable() {
    Hashtable lat2real = new Hashtable();

    lat2real.put("Homo_sapiens",          "Human");
    lat2real.put("Pan_troglodytes",       "Chimp");
    lat2real.put("Pongo_pygmaeus","Orang");
    lat2real.put("Macaca_mulatta","Macaque");
    lat2real.put("Tarsius_syrichta","Tarsier");
    lat2real.put("Microcebus_murinus","MouseLemur");
    lat2real.put("Otolemur_garnettii","Bushbaby");
    lat2real.put("Tupaia_belangeri","TreeShrew");
    lat2real.put("Mus_musculus","Mouse");
    lat2real.put("Rattus_norvegicus","Rat");
    lat2real.put("Dipodomys_ordii","KangRat");
    lat2real.put("Cavia_porcellus","Guineapig");
    lat2real.put("Spermophilus_tridecemlineatus","Squirrel");
    lat2real.put("Oryctolagus_cuniculus","Rabbit");
    lat2real.put("Ochotona_princeps","Pika");
    lat2real.put("Vicugna_pacos","Alpaca");
    lat2real.put("Tursiops_truncatus","Dolphin");
    lat2real.put("Bos_taurus","Cow");
    lat2real.put("Equus_caballus","Horse");
    lat2real.put("Felis_catus","Cat");
    lat2real.put("Canis_familiaris","Dog");
    lat2real.put("Myotis_lucifugus","BrownBat");
    lat2real.put("Pteropus_vampyrus","FruitBat");
    lat2real.put("Erinaceus_europaeus","Hedgehog");
    lat2real.put("Sorex_araneus","Shrew");
    lat2real.put("Loxodonta_africana","Elephant");
    lat2real.put("Procavia_capensis","Hyrax");
    lat2real.put("Echinops_telfairi","Tenrec");
    lat2real.put("Dasypus_novemcinctus","Armadillo");
    
    return lat2real;
  }

  public String[] getOrgs() {
    return mammarr;
  }
  public static Hashtable read_piece(LineNumberReader bReader, int qstart, int qend) throws IOException {
    Hashtable piece = new Hashtable();

    String line;

    Vector orgs = new Vector();
    Vector seqs = new Vector();

    String hchr = "N";
    int    hstart = 0;
    int    hend  = 0;
    int    hstrand = 0;

    int fulllen = 0;

    int refnum = -1;
    String reforg = "Homo_sapiens";
    
    Hashtable regions = new Hashtable();

    while ((line = bReader.readLine()) instanceof String) {

	if (line.indexOf("SEQ") == 0) {
	System.out.println("Seq " + line);
	StringTokenizer str  = new StringTokenizer(line," ");

	str.nextToken();

	String org    = str.nextToken();
	String chr    = str.nextToken();
	int    start  = Integer.parseInt(str.nextToken());
	int    end    = Integer.parseInt(str.nextToken());
	int    strand = Integer.parseInt(str.nextToken());


	ChrRegion tmpreg = new ChrRegion(chr,start,end,strand);

	regions.put(org,tmpreg);

	if (end-start+1 > fulllen) {
	  fulllen = end-start+1;
	}
	// Get the real org here
	orgs.addElement(org);

	seqs.addElement(new StringBuffer());

	if (org.equals(reforg)) {
	  refnum = orgs.size() - 1;
	  hchr = chr;
	  hstart = start;
	  hend   = end;
	  hstrand = strand;
	} 
      }
      
      boolean foundregion = false;

      if (line.indexOf("DATA") == 0) {
	System.out.println("Got data");
	int count = 0;
	int len   = 0;

	int tmplen = (hend-hstart+1)*2;

	System.out.println("Len " + fulllen + " " + hstart + " " + hend);

	char[][] tmpseqs = new char[orgs.size()][fulllen*20];
	
	boolean found_region = false;

	while ((line = bReader.readLine())!= null && line.indexOf("//") <  0) {

	  len += line.length();
	  
	  char[] c = line.toCharArray();
	  
	  if (count >= tmplen) {
	    System.out.println("EEEK " + count + " " + tmplen);
	  }
	  

	  int i = 0;
	  
	  if (c[refnum] != '-' &&
	      c[refnum] != '.') {

	    while (i < orgs.size()) {
	      tmpseqs[i][count] = c[i];
	      i++;
	    }
	  
	    count++;

	  }
	}
	
	int i = 0;
	while (i < orgs.size()) {
	  char[] tmparr = new char[count];
	  System.arraycopy(tmpseqs[i],0,tmparr,0,count-1);
	  ((StringBuffer)seqs.elementAt(i)).append(new String(tmparr));
	  i++;
	}

	System.out.println("Read data " + count + " lines " + len + " bytes");

	//System.out.println("line is " + line + " " + orgs.size());
	
	for (i = 0; i < orgs.size(); i++) {
	  
	  String org = (String)orgs.elementAt(i);
	  String seq = ((StringBuffer)seqs.elementAt(i)).toString();
	  ChrRegion reg = (ChrRegion)regions.get(org);

	  if (org.equals("Homo_sapiens")) {
	    Sequence s = new Sequence(org,seq,hstart, hend);
	    s.setChrRegion(hchr,hstart,hend,hstrand);
	    piece.put(org,s);
	  } else {
	    Sequence s = new Sequence(org,seq,1,2);
	    s.setChrRegion(reg);
	    piece.put(org,s);
	  }
	  //System.out.println("Org " + org + " " + seq.substring(0,50));
	}
	
	return piece;
      }
    }
    return null;
  }
  public static void index(String filestr) throws IOException {
    
    // Change this to use readers
    RandomAccessFile file = new RandomAccessFile(filestr,"r");
    
    long endpos  = file.length();
		
    Vector out;
    String line;
    boolean atstart = true;
    long    pos = 0;
    long    chunkpos = pos;

    
    FileReader       fReader = new FileReader(file.getFD());
    LineNumberReader bReader = new LineNumberReader(fReader, 65536);  // default buffersize 8192
 
    int linenum = 0;

    while ((line = bReader.readLine()) != null) {
      System.out.println("Topline " + line);
      linenum++;
      pos += line.length()+1;

      if (line.indexOf("#") != 0) {
	System.out.println("Pos " + linenum + " " + pos + " " + " " + file.getFilePointer());	  
	if (line.indexOf("//") != 0 || atstart) {
	  chunkpos = pos - line.length();
	  boolean found = false;
	    
	  boolean gotstart = false;
	  while ((found == false) && (line = bReader.readLine()) != null && line.indexOf("//") != 0) {
	    String tmpline = line.toUpperCase();
	    if (tmpline.indexOf("A") != 0 && tmpline.indexOf("C") != 0 && tmpline.indexOf("T") != 0 && tmpline.indexOf("G") != 0 && tmpline.indexOf("-") != 0 && tmpline.indexOf(".") != 0 && tmpline.indexOf("N") != 0) {
	    }
	    linenum++;
	    pos += line.length()+1;

	    if (line.indexOf("SEQ") == 0 && !gotstart) {
	      System.out.println("Got start " + linenum + " " + chunkpos + " " + line);
	      gotstart = true;
	    }
	    //System.out.println("Pos " + pos + " " + line.length() + " " + file.getFilePointer());
	    if (line.indexOf("SEQ") == 0 && 
		line.indexOf("Homo_sapiens") > 0) {

	      found = true;

	      StringTokenizer str = new StringTokenizer(line," ");

	      String tok0 = str.nextToken();
	      String tok1 = str.nextToken();
	      
	      String chr    = str.nextToken();
	      int    start  = Integer.parseInt(str.nextToken());
	      int    end    = Integer.parseInt(str.nextToken());
	      int    strand = Integer.parseInt(str.nextToken());
		
	      System.out.println(start + "\t" + end + "\t" + chunkpos);

	      while (line.indexOf("//") != 0 && (line = bReader.readLine()) != null) {
		linenum++;
		pos += line.length()+1;
	      }
	    }
	  }
	  atstart = false;
	}
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

  public static Hashtable get_align(String chr, int start, int end) throws IOException  {

    String indexfile = "./data/" + chr + ".emf.index";
    //String emffile = "/ahg/scr3/epo_29_eutherian/" + chr + ".emf";
    //String indexfile = "./" + chr + ".emf.index";
    String emffile = "./data/"+ chr + ".emf";

      BSearch bs = new BSearch(indexfile,0);

      RandomAccessFile raf = bs.search_file(start);

      FileReader       fReader = new FileReader(raf.getFD());
      LineNumberReader bReader = new LineNumberReader(fReader, 65536);  // default buffersize 8192
      

      String line  = bReader.readLine();
      StringTokenizer str = new StringTokenizer(line,"\t");
      System.out.println("Line " + line);
      str.nextToken();
      str.nextToken();
      
      long pos = Long.parseLong(str.nextToken());
      
      RandomAccessFile raf2 = new RandomAccessFile(emffile,"r");

      raf2.seek(pos);
      
      FileReader       fr = new FileReader(raf2.getFD());
      LineNumberReader br = new LineNumberReader(fr,65536);
      
      Hashtable fullstr = new Hashtable();
      
      int prev = start-1;
      
      Hashtable piece;
      
      while ((piece = read_piece(br,start,end)) != null) {
	
	System.out.println( "\nOriginal piece (cf " + start + " " + end + "\n");
	
	print_refpiece(piece);

      String ref = "Homo_sapiens";
	
      Sequence  refseq = (Sequence)piece.get(ref);
      ChrRegion reg    = refseq.getChrRegion();

      int piece_end   = reg.getEnd();
      int piece_start = reg.getStart();
      int piece_len   = piece_end-piece_start + 1;
      
      System.out.println("Piece " + start + " " + end + " " + piece_start + " " + piece_end);

      if (piece_end < start) {
	break;
      }
      if (end < piece_start) {
	break;
      }
	
      //Different cases
	
      // --------------------------
      //           ^start
      // piece overlaps start
	
      // TRIM
	
	
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
	
	piece_start = reg.getStart();
	piece_end   = reg.getEnd();
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

	for (int i = 0; i < latin_orgs.length; i++) {
	  String org = latin_orgs[i];
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
            
      int padlen    = refseq.getSequence().length();
      String padstr = build_str("-",padlen);
      
	for (int i = 0; i < latin_orgs.length; i++) {
	  String org = latin_orgs[i];
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
      
      for (int i = 0; i < latin_orgs.length; i++) {
	String org = latin_orgs[i];
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

  public static Hashtable trim_piece(Hashtable piece,int start, int end, String org, int piece_coord) {
    //First find the string offsets in the human sequence (which will be gapped)

    Sequence refseq = (Sequence)piece.get(org);
    char[]   c      = refseq.getSequence().toCharArray();

    System.out.println("Trim start end " + start + " " + end + " for seqlen " + refseq.getSequence().length() + "\n");

    int i      = 0;
    int coord  = 0;

    int piece_start = piece_coord;
    int piece_end = -1;
    int strend = -1;
    int strstart = -1;

    System.out.println("Length is " + (end-start+1));
    while (i < c.length && coord <= end) {
      //System.out.println("Corrd " + coord  + " " + c.length + " " + i);

      if (coord == start) {
	strstart    = i;
	piece_start = piece_coord;
      }
      if (coord == end) {
	strend    = i;
	piece_end = piece_coord;

	break;
      }
      
      if (c[i] != '-') {
	coord++;
	piece_coord++;
      }

      //if (strstart != -1) {
      //	System.out.println("Values are " + (piece_coord-piece_start+1) + " " + coord + " " + end);
      //}
      i++;
    }

    if (piece_end == -1) {
      System.out.println("Setting piece_end");
      piece_end = piece_coord;
    }

    System.out.println("Coords are " + piece_coord + " " + (strend-strstart+1));
    if (strend == -1) {
      strend    = i;
      piece_end = piece_coord;
    }

    Hashtable newstr = new Hashtable();

    System.out.println("Trimming a string "  + refseq.getSequence().length() + " long to " + strstart + " " + strend + "\n");
    System.out.println("Trimming a string "  + refseq.getSequence().length() + " long to " + piece_start + " " + piece_end + "\n");

    int len;

    Enumeration en = piece.keys();

    while (en.hasMoreElements()) {
      org = (String)en.nextElement();
      Sequence s = (Sequence)piece.get(org);

      String seq = s.getSequence();
      seq = seq.substring(strstart,strend+1);

      s.setSequence(seq);

      if (org.equals("Homo_sapiens")) {
	//System.out.println("Seq is " + seq + " " + seq.length());
	s.getChrRegion().setStart(piece_start);
	s.getChrRegion().setEnd(piece_end);
      }
    }
    return piece;
}

  public static Hashtable trim_piece2(Hashtable piece,int start, int end, String org, int piece_coord) {
    //First find the string offsets in the reference sequence (which will be gapped)

      Sequence refseq = (Sequence)piece.get(org);
      
      Vector seqs = new Vector();
      Enumeration en2 = piece.keys();

      while (en2.hasMoreElements()) {
	  String   tmporg  = (String)en2.nextElement();

	  if (!tmporg.equals(org)) {
	      Sequence tmps = (Sequence)piece.get(org);
	      seqs.addElement(tmps);
	  }
      }
	  
      char[]   c      = refseq.getSequence().toCharArray();

      System.out.println("Trim start end " + start + " " + end + " for seqlen " + refseq.getSequence().length() + "\n");

      int i      = 0;
      int coord  = 0;

      int piece_start = piece_coord;
      int piece_end   = -1;
      int strend      = -1;
      int strstart    = -1;

      System.out.println("Length is " + (end-start+1));

      while (i < c.length && coord <= end) {

	  if (coord == start) {
	      strstart    = i;
	      piece_start = piece_coord;
	  }
	  if (coord == end) {
	      strend    = i;
	      piece_end = piece_coord;
	      
	      break;
	  }
	  
	  if (c[i] != '-') {
	      coord++;
	      piece_coord++;
	  }

	  i++;
      }

      if (piece_end == -1) {
	  System.out.println("Setting piece_end");
	  piece_end = piece_coord;
      }
      
    System.out.println("Coords are " + piece_coord + " " + (strend-strstart+1));
    if (strend == -1) {
      strend    = i;
      piece_end = piece_coord;
    }

    Hashtable newstr = new Hashtable();

    System.out.println("Trimming a string "  + refseq.getSequence().length() + " long to " + strstart + " " + strend + "\n");
    System.out.println("Trimming a string "  + refseq.getSequence().length() + " long to " + piece_start + " " + piece_end + "\n");

    int len;

    Enumeration en = piece.keys();

    while (en.hasMoreElements()) {
      org = (String)en.nextElement();
      Sequence s = (Sequence)piece.get(org);

      String seq = s.getSequence();
      seq = seq.substring(strstart,strend+1);

      s.setSequence(seq);

      if (org.equals("Homo_sapiens")) {
	//System.out.println("Seq is " + seq + " " + seq.length());
	s.getChrRegion().setStart(piece_start);
	s.getChrRegion().setEnd(piece_end);
      }
    }
    return piece;
}


  public static void print_piece(Hashtable piece) {

    Enumeration en = piece.keys();

    while (en.hasMoreElements()) {
      String org = (String)en.nextElement();
      Sequence s = (Sequence)piece.get(org);
      ChrRegion reg = s.getChrRegion();

      int len = s.getSequence().length();
      int chunk = 50;

      if (len < 50) {
	chunk = len;
      }

      System.out.println("Org\t" + org + "\t" + len + "\t"  + reg + "\t" + s.getSequence().substring(0,chunk) + "\t" + s.getSequence().substring(len-chunk,len-1));
    }
  }
  public static void print_refpiece(Hashtable piece) {

    Enumeration en = piece.keys();
    String reforg = "";

    while (en.hasMoreElements()) {
      String org = (String)en.nextElement();
      Sequence s = (Sequence)piece.get(org);

      if (s.getChrRegion() != null) {
	reforg = org;
	System.out.println("\nPrinting ref piece " + org + " " + s.getChrRegion());
      }
    }

    en = piece.keys();


    while (en.hasMoreElements()) {
      String org = (String)en.nextElement();
      Sequence s = (Sequence)piece.get(org);

      if (org.equals(reforg)) {
	int len = s.getSequence().length();
	int chunk = 50;
	if (len < 50) {
	  chunk = len;
	}
	System.out.println("Org\t" + org + "\t" + len + " " + s.getSequence().substring(0,chunk) + "\t" + s.getSequence().substring(len-chunk,len-1));
      }
    }
  }
    
  public static void main(String[] args) {
    try {

      if (args[0].equals("1")) {
	EMFFile.index(args[1]);
      } else {
      String indexfile = "./data/chr1.index";
      //String emffile   = "/ahg/scr3/epo_29_eutherian/chr1.emf";
      String emffile   = "./data/chr1.emf";

      String chr    = args[1];
      int start     = Integer.parseInt(args[2]);
      int end       = Integer.parseInt(args[3]);

      Hashtable aln = EMFFile.get_align(chr,start,end);

      Hashtable realorgs = latin2readable();
      String[]  latorgs = EMFFile.latin_orgs;

      //Enumeration en = aln.keys();
      //while (en.hasMoreElements()) {

      int i = 0;
      while (i < latorgs.length) {
	//String key = (String)en.nextElement();
	String key = latorgs[i];
	if (aln.containsKey(key)) {
	  String seq = ((StringBuffer)aln.get(key)).toString();

	String realorg = (String)realorgs.get(key);
	System.out.println(">" + realorg + " " + key);
	int len = 0;
	int seqlen = seq.length();
	while (len < seqlen - 80) {
	  System.out.println(seq.substring(len,len+79));
	  len += 79;
	}
	System.out.println(seq.substring(len));
	}
	i++;
      }
      }
      } catch (IOException e) {
	e.printStackTrace();
      }
  }
}

