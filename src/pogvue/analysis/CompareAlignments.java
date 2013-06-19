package pogvue.analysis;

import pogvue.datamodel.Alignment;
import pogvue.datamodel.Sequence;
import pogvue.io.FastaFile;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.io.IOException;

public final class CompareAlignments {

    private final Vector align;
    private String refId;
    private final Vector ids;

    private CompareAlignments(Vector align) {

	this.align = align;

	ids = new Vector();

	Alignment al0 = (Alignment)align.elementAt(0);

	for (int i = 0; i < al0.getHeight(); i++) {
	    Sequence seq = al0.getSequenceAt(i);

	    ids.addElement(seq.getName());

	    if (i == 0) {
		setReferenceId(seq.getName());
	    }
	}

    }

    private void compare() {

	Hashtable positions = new Hashtable();
	
	for (int k = 0; k < ids.size(); k++) {

	    String id = (String)ids.elementAt(k);

	    System.out.println("Ids " + id + " " + refId);

	    if (!id.equals(refId)) {
		
		Hashtable fullhash = new Hashtable();

		for (int i = 0; i < align.size(); i++) {
		    System.out.println("Alignment " + i);
		    
		    Alignment al = (Alignment)align.elementAt(i);
		    
		    Sequence refseq = null;
		    
		    for (int j = 0; j < al.getHeight(); j++) {
			if (al.getSequenceAt(j).getName().equals(refId)) {
			    refseq = al.getSequenceAt(j);
			}
		    }
		    
		    if (refseq != null) {
			
			System.out.println("Refseq " + refseq.getName());
			
			for (int jj = 0; jj < al.getHeight(); jj++) {
			    Sequence seq = al.getSequenceAt(jj);
			    
			    if (seq.getName().equals(id)) {
				Hashtable hash = getAlignPositions(seq,refseq);
				
				Enumeration keys = hash.keys();
				
				while (keys.hasMoreElements()) {
				    Integer key = (Integer)keys.nextElement();
				    //				System.out.println(key + " " + hash.get(key));
				    if (fullhash.get(key) == null) {
					fullhash.put(key,new Vector());
				    }
				    
				    Vector tmp = (Vector)fullhash.get(key);
				    
				    tmp.addElement(hash.get(key));
				}
			    }
			    
			}
		    }
		}
		
		System.out.println ("\nId " + id);
		
		Enumeration keys = fullhash.keys();


		int totdiff = 0;
		while (keys.hasMoreElements()) {
		    Integer key = (Integer)keys.nextElement();
		    
		    Vector tmp = (Vector)fullhash.get(key);
		    

		    
		    int diff0 = (Integer) tmp.elementAt(0);
		    int diff = 0;

		    for (int l = 1; l < tmp.size(); l++) {
			diff += Math.abs(diff0 - (Integer) tmp.elementAt(l));
			
		    }
			
		    if (diff > 0) {
			totdiff++;
			System.out.print(id + " Ref pos " + key + " : " + diff0 + " " + diff + " : ");

			for (int l = 1; l < tmp.size(); l++) {
			    System.out.print(diff0 - (Integer) tmp.elementAt(l) + " ");
			}
			System.out.println();
		    }
		}

		System.out.println("Total " + id + " " + totdiff);

	    }
	}
    }
		
	    
    private void setReferenceId(String id) {
	this.refId = id;
    }

    private Hashtable getAlignPositions(Sequence seq1, Sequence seq2) {

	Hashtable hash = new Hashtable();

	int i = 0;

	int pos1 = 0;
	int pos2 = 0;

	while (i < seq1.getLength()) {
	    
	    char c1 = seq1.getCharAt(i);
	    char c2 = seq2.getCharAt(i);

	    if (c1 != '-') {
		pos1++;
	    }

	    if (c2 != '-') {
		pos2++;
	    }

	    if (c1 != '-') {
		hash.put(pos1, pos2);
	    }

	    i++;
	}
	return hash;
    }

    
	    
    public static void main(String[] args) {
	
	Vector align = new Vector();
	
	int i = 0;
	
	while (i < args.length) {
	    try {
	    	FastaFile ff = new FastaFile(args[i],"File");
	    	Sequence[] s = ff.getSeqsAsArray();
	    
	    	Alignment al = new Alignment(s);

	    	align.addElement(al);
			} catch (IOException e) {
				System.out.println("Exception in CompareAlignments  for " + args[i] + " " + e);
			}
	    i++;
	}

	CompareAlignments comp = new CompareAlignments(align);

	comp.compare();
    }
}
