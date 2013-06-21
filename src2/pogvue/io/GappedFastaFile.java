package pogvue.io;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import pogvue.analysis.AlignSeq;
import pogvue.datamodel.GappedSequence;
import pogvue.datamodel.Sequence;
import pogvue.datamodel.Sequence;

import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

public class GappedFastaFile extends AlignFile {

    public GappedFastaFile(Sequence[] s) {
	super(s);
    }
    public GappedFastaFile(String inStr) {
       super(inStr);
    }
    
    public GappedFastaFile(String inFile, String type) throws IOException {
	super(inFile,type);
    }
    public GappedFastaFile(String inFile, String type, boolean parse) throws IOException {
	super(inFile,type,parse);
    }
    public void parse() {

      // Gapped fasta file contains pairwise alignments for x species.  For each species there are two fasta entries; 
      // one for the human sequence and one for the species sequence

      // >human_chimp
      // AAGATCGACTGATCG------ACGTACGATCAGC-GACTACGAGCTAC
      // >org_chimp
      // AAGATCG---GATCGATGCACACGTACG--CAGCTGACTACGAGCTAC

      // All inserts (in both species) are stored in the human coordinate frame

      // >human
      // AAGATCGACTGATCG------ACGTACGATCAGC-GACTACGAGCTAC
      // >org_chimp
      // AAGATCG---GATCGATGCACACGTACG--CAGCTGACTACGAGCTAC
      // 012345678901234------5678901234567-8901234567890
      //
      //       ^       ^            ^     ^

      // So in this case there are 4 inserts after human bases 6, 14, 21 and 27

      // These are stored as strings in a Hashtable keyed by human coord
      // Inserts in human (i.e. gaps in chimp) have -ve human coords
      // Inserts in chimp (i.e. gaps in human) have +ve human coords


	String       id    = "";
	StringBuffer seq   = new StringBuffer();
	int          count = 0;
	boolean      flag  = false;
	
	int          sstart = 0;
	int          send   = 0;
	
	String line;
	
	try {
	while ((line = nextLine()) != null) {
	    if (line.length() > 0) {

		if (line.substring(0,1).equals(">") && line.indexOf("ucsc") < 0) {

		    if (count != 0) {

			// If this isn't the first sequence then create the insertion vector

			Sequence tmpseq;

			if (seqs.size() > 0 && seqs.size()%2 == 1) {
			    Sequence seq0    = (Sequence)seqs.elementAt(seqs.size()-1);			    
			    tmpseq = GappedFastaFile.get_inserts(seq0.getSequence(),seq.toString());

			    //System.out.println("Sequence " + tmpseq.getSequence().length());
			    //System.out.println("Done");
			} else {
			    
			    tmpseq = new Sequence(id,seq.toString().toUpperCase(),1,seq.toString().length());
			}

			if (sstart != 0) {
			    tmpseq.setStart(sstart);
			    tmpseq.setEnd(send);
			}

			tmpseq.setName(id);

			seqs.addElement(tmpseq);
		    }
		    
		    count++;

		    StringTokenizer str = new StringTokenizer(line," ");
		    
		    id = str.nextToken();
		    id = id.substring(1);
		    
		    if (id.indexOf("/") > 0 ) {
			
			StringTokenizer st = new StringTokenizer(id,"/");
			if (st.countTokens() == 2) {
			    id = st.nextToken();
			    String tmp = st.nextToken();
			    
			    st = new StringTokenizer(tmp,"-");
			    
			    if (st.countTokens() == 2) {
				sstart = Integer.valueOf(st.nextToken());
				send   = Integer.valueOf(st.nextToken());
			    }
			}
		    }
		    
		    seq = new StringBuffer();
		    
		} else {
		    seq = seq.append(line);
		}
	    }
	}

	if (count > 0) {

	    Sequence tmpseq;

	    if (seqs.size() > 0 && seqs.size()%2 == 1) {
		Sequence seq0    = (Sequence)seqs.elementAt(seqs.size()-1);			    
		
		tmpseq = GappedFastaFile.get_inserts(seq0.getSequence(),seq.toString());
	    } else {
		
		tmpseq = new Sequence(id,seq.toString().toUpperCase(),1,seq.toString().length());
	    }
	    
	    if (sstart != 0) {
		tmpseq.setStart(sstart);
		tmpseq.setEnd(send);
	    }

	    tmpseq.setName(id);
	    seqs.addElement(tmpseq);
	}


	// Now compress alternate sequences

	int i = 1;

	Vector newseqs = new Vector();

	newseqs.addElement(seqs.elementAt(0));

	String compstr = compress_sequence(seqs.elementAt(0).getSequence());
 	seqs.elementAt(0).setSequence(compstr);

	while (i <= seqs.size()) {
             //System.out.println("Compressing this fella" + seqs.size() );
	    if (i%2 == 1) {
		//Sequence tmpseq = (Sequence)seqs.elementAt(i-1);

		//String compstr = compress_sequence(tmpseq.getSequence());
	
		//tmpseq.setSequence(compstr);
	    } else {
		newseqs.addElement(seqs.elementAt(i-1));
	    }
	    i++;
	}
	
	seqs = newseqs;
    } catch (IOException e) {
	System.out.println("Exception parsing fastafile");
    }
    
    
  }
    
    private String compress_sequence(String str) {

	char[] c = str.toCharArray();

	int i = 0;

	StringBuffer sb = new StringBuffer();

	while (i < c.length) {
	    if (c[i] != '-') {
		sb.append(c[i]);
	    }
	    i++;
	}

	return sb.toString();
    }

  public static void write(OutputStream os,Sequence topseq, GappedSequence seq) throws IOException {
      int i = 0;
      
      Writer writer = new OutputStreamWriter(os);
    
      writer.write(">human_" + seq.getName() + "\n");
          
      String str1 = seq.getExpandedQueryString(topseq.getSequence());
      String str2 = seq.getExpandedHitString(topseq.getSequence());
      
      int j = 0;
      
      while (j < str1.length()) {
	int end = j+72;
	if (end > str1.length()) {
	  end = str1.length();
	}
	writer.write(str1.substring(j,end) + "\n");
	j+= 72;
      }
      
      writer.write(">org_" + seq.getName() + "\n");
      
      j = 0;
      
      while (j < str2.length()) {
	int end = j+72;
	if (end > str2.length()) {
	  end = str2.length();
	}
	writer.write(str2.substring(j,end) + "\n");
	j+= 72;
      }
  }
  
  private static String print(Sequence[] s) {
    return print(s,72);
  }
  private static String print(Sequence[] s, int len) {
    return print(s,len,true);
  }
  private static String print(Sequence[] s, int len,boolean gaps) {
    StringBuffer out = new StringBuffer();
    int i = 0;
    while (i < s.length && s[i] != null) {
      String seq;
      if (gaps) {
        seq = s[i].getSequence();
      } else {
        seq = AlignSeq.extractGaps(s[i].getSequence(),"-");
        seq = AlignSeq.extractGaps(seq,".");
        seq = AlignSeq.extractGaps(seq," ");
      }

      out.append(">").append(s[i].getName()).append("/").append(s[i].getStart()).append("-").append(s[i].getEnd()).append("\n");
      
      int nochunks = seq.length() / len + 1;
      
      for (int j = 0; j < nochunks; j++) {
        int start = j*len;
        int end = start + len;

        if (end < seq.length()) {
            out.append(seq.substring(start, end)).append("\n");
        } else if (start < seq.length()) {
            out.append(seq.substring(start)).append("\n");
        }
      }
      i++;
    }
    return out.toString();
  }

  public String print() {
    return print(getSeqsAsArray());
  } 

    public static GappedSequence get_inserts(String str1, String str2) {
	str1 = str1.toUpperCase();
	str2 = str2.toUpperCase();

	char c1[] = str1.toCharArray();
	char c2[] = str2.toCharArray();

	Vector inserts = new Vector();

	int i = 0;

	char prev  = 'N';
	char hprev = 'N';

	int  coord = 0;

	// This will find the positions, sizes, and sequences of inserts in the second string in the coord system of the first string
	int startqcoord  = -1;
	int startcoord   = -1;
	int starthcoord  = -1;

	int hstartqcoord = -1;
	int hstartcoord  = -1;
	int hstarthcoord = -1;

	StringBuffer newstr = new StringBuffer();
	Hashtable inserts_by_coord = new Hashtable();
	Hashtable inserts_by_hcoord = new Hashtable();

	int hcoord = -1;

	while (i < c1.length) {
	    
	  if (c1[i] != '-') {
	    coord++;

	    if (c2.length > i) {
	      newstr.append(c2[i]);
	    } else {
	      newstr.append('-');
	    }

	  }

	  if (c2[i] != '-') {
	    hcoord++;
	  }

	  if (c1[i] == '-' &&
	      prev != '-') {
	    
	    // Found start of insert in human sequence
	    
	    // Should be some catch here for gaps at the start of first sequece
	    startqcoord = coord;
	    startcoord  = i;
	    starthcoord = hcoord;
	  }
	  
	  if (c1[i] != '-' &&
	      prev == '-') {

	    // Found end of insert in human sequence

	    String    instr  = str2.substring(startcoord,i);
	    Hashtable insert = new Hashtable();

	    insert.put("String",instr);
	    insert.put("Coord", startqcoord);
	    
	    inserts_by_coord.put(startqcoord,instr);
	    inserts_by_hcoord.put(startqcoord, starthcoord);

	    inserts.addElement(insert);

		//System.out.println("Insert " + startqcoord + " " + instr);
	    }

	    if (c2[i] == '-' &&
		hprev != '-') {
		// Start of insert in hit sequence
		hstartqcoord = -coord;
		hstartcoord = i;
		hstarthcoord = hcoord;

	    }

	    if (c2[i] != '-' &&
		hprev == '-') {
		// End of hit indel
		Hashtable insert = new Hashtable();
		String    instr  = str1.substring(hstartcoord,i);

		insert.put("String",instr);
		insert.put("Hcoord", hstartqcoord);
		inserts_by_coord.put(hstartqcoord,instr);
		inserts_by_hcoord.put(hstartqcoord, hcoord);

		inserts.addElement(insert);
		//System.out.println("HInsert " + hstartqcoord + " " + instr);
	    }

	    prev  = c1[i];
	    hprev = c2[i];

	    i++;
	}
	
	if (prev == '-') {

	    // Found end of insert

	    String instr = str2.substring(startcoord,i);

	    Hashtable insert = new Hashtable();
	    insert.put("String",instr);
	    insert.put("Coord", startqcoord);
	    inserts_by_coord.put(startqcoord,instr);
	    inserts_by_hcoord.put(startqcoord, hcoord);
	    
	    inserts.addElement(insert);
	}

	if (hprev == '-') {
	    // End of hit indel
	    Hashtable insert = new Hashtable();
	    String    instr  = str1.substring(hstartcoord,i);
	    
	    insert.put("String",instr);
	    insert.put("Hcoord", hstartqcoord);
	    inserts_by_coord.put(hstartqcoord,instr);
 	    inserts_by_hcoord.put(startqcoord, hcoord);

	    inserts.addElement(insert);
	}

	String ns = newstr.toString();

	GappedSequence gs = new GappedSequence("tmp",ns,1,ns.length(),inserts,inserts_by_coord,inserts_by_hcoord);

	return gs;
    }



}



