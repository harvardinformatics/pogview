package pogvue.io;

import pogvue.analysis.AlignSeq;
import pogvue.datamodel.*;
import pogvue.util.QuickSort;

import java.io.IOException;
import java.util.*;
import java.awt.event.*;

public class GeneInfoFile extends AlignFile {
    private Hashtable genes;

    private ActionListener l;

  
    public GeneInfoFile(String inStr) {
	super(inStr);
    }

    public GeneInfoFile(String inFile, String type) throws IOException {
	super(inFile,type,true);
    }

    public GeneInfoFile(String inFile, String type,boolean parse) throws IOException {
	super(inFile,type,parse);
    }

    public void setActionListener(ActionListener l) {
	this.l = l;
    }

    public Hashtable getGeneHash() {
    	return genes;
    }

    public void parse() {
	genes = new Hashtable();

	String line;

	try {
	    while ((line = nextLine()) != null) {
	      //System.out.println("line " + line);
	      
		if (line.length() > 0) {

		    StringTokenizer str  = new StringTokenizer(line,"\t");
		    String gene = str.nextToken();
		    String type = str.nextToken();
		    String name = str.nextToken();
		    String value = str.nextToken();

		    //System.out.println("Gene " + type);
		    Hashtable hash;

		    if (genes.containsKey(gene)) {
			hash = (Hashtable)genes.get(gene);
		    } else {
			hash = new Hashtable();
		    }

		    if (hash.containsKey(type)) {
			Vector vals = (Vector)hash.get(type);
			vals.addElement(value);
		    } else {
			Vector vals = new Vector();
			vals.addElement(value);
			hash.put(type,vals);
		    }
		    if (hash.containsKey("name")) {
			Vector vals  = (Vector)hash.get("name");
			if (! vals.contains(name)) {
			    vals.addElement(name);
			    hash.put("name",vals);
			}
		    } else {
			Vector vals = new Vector();
			vals.addElement(name);
			hash.put("name",vals);
		    }
		    genes.put(gene,hash);

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

    public static void main(String args[]) {
    }
}
    




