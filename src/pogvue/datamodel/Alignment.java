package pogvue.datamodel;

import pogvue.analysis.*;

import pogvue.io.*;
import pogvue.gui.hub.*;
import java.io.*;

import pogvue.gui.SearchPanel;
import pogvue.util.Comparison;
import pogvue.util.QuickSort;
import pogvue.gui.AlignViewport;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.*;

import javax.swing.JFrame;
import pogvue.datamodel.motif.*;
public class Alignment  {

  private ChrRegion chrRegion;
  private Vector         sequences;

  private int maxLength = -1;

  public Alignment() {
  	sequences = new Vector();
  }
  public Alignment(Sequence[] seqs) {
  	sequences = new Vector();
    
  	for (Sequence seq : seqs) {
  		sequences.addElement(seq);
  	}
    maxLength = -1;
    getWidth();
  }
  public void addSequence(Sequence snew) {
  	sequences.addElement(snew);
  	maxLength = -1;
  	getWidth();
  }

  public void addSequence(Sequence[] seq) {
    for (Sequence aSeq : seq) {
      addSequence(aSeq);
    }
    maxLength = -1;
    getWidth();
  }

  public void addSequences(Vector feat) {
  	
    for (int i = 0; i < feat.size(); i++) {
    	Sequence seq = (Sequence)feat.elementAt(i);
    	addSequence(seq);
    }
  }

  public void addSequences(Vector feat, int space) {

  	for (int i = 0; i < feat.size(); i++) {
  		Sequence seq = (Sequence)feat.elementAt(i);
  
  		for (int j = 0; j < space; j++) {
  			GFF gff = new GFF(seq.getName(),"",1,2);
  			addSequence(gff);
  			j++;
  		}
      addSequence(seq);
    }
  }

  public void  deleteSequence(int i) {
  	sequences.removeElementAt(i);
  	maxLength = -1;
  	getWidth();
  }

  public void deleteSequence(Sequence s) {
  	for (int i=0; i < getHeight(); i++) {
  		if (getSequenceAt(i) == s) {
  			deleteSequence(i);
  			maxLength = -1;
  			getWidth();
      }
    }
  }
  
  public int findIndex(Sequence s) {
  	int i = 0;
  	
  	while (i < sequences.size()) {
  		if (s == getSequenceAt(i)) {
      	return i;
  		}
      i++;
  	}
    return -1;
  }

  public Sequence findName(String name) {
  	int i = 0;
  	
  	while (i < sequences.size()) {
  		
  		Sequence s = getSequenceAt(i);
  		
  		if (s.getName().equals(name)) {
  			return s;
  		}
  		i++;
  	}
    return null;
  }

  public int getHeight() {
      return sequences.size();
  }
  public int getMaxIdLength() {
    int max = 0;
    int i   = 0;

    while (i < sequences.size()) {
      Sequence seq = getSequenceAt(i);
      if (seq.getIdLength() > max) {
        max = seq.getIdLength();
      }

      i++;
    }
    return max;
  }

  public Hashtable getNameHash() {
    Hashtable out = new Hashtable();

    for (int i = 0; i < sequences.size(); i++) {
      Sequence seq = getSequenceAt(i);
      out.put(seq.getName(),seq);
    }
    return out;
  }

  public Sequence[] getSequenceArray() {
  	return (Sequence[])(sequences.toArray(new Sequence[sequences.size()]));
  }

  public Sequence getSequenceAt(int i) {
  	if (i >= 0 && i < sequences.size()) {
  		return (Sequence)sequences.elementAt(i);
  	}
  	return null;
  }

  public ChrRegion getChrRegion() {
    return chrRegion;
  }

  public Vector      getSequences() {
  	return sequences;
  }

  public int getWidth() {

  	if (maxLength == -1) {
  		for (int i = 0; i < sequences.size(); i++) {

  			if (getSequenceAt(i).getLength() > maxLength) {

  				maxLength = getSequenceAt(i).getLength();
  			}
  		}
  	}
  	return maxLength;
  }

  public void insertSequenceAt(Sequence seq,int pos) {
    if (pos >=0 && pos < getHeight()) {
      sequences.insertElementAt(seq,pos);
    }
    maxLength = -1;
    getWidth();
  }

  public void setSequenceAt(int i,Sequence snew) {
  	Sequence oldseq = getSequenceAt(i);
    deleteSequence(oldseq);

    sequences.setElementAt(snew,i);
  }
  public void setChrRegion(ChrRegion r) {
    this.chrRegion = r;
    maxLength = -1;
    getWidth();
  }
  public void setSequences(Vector seq) {
    this.sequences = seq;
    maxLength = -1;
    getWidth();
  }
  public Sequence getSequenceByName(String name) {
  	
  	int i = 0;
  	Sequence gff = null;
  	boolean newSequence = true;
  	
  	while (i < getHeight() && gff == null) {
  		if (getSequenceAt(i) instanceof Sequence &&
  				getSequenceAt(i).getName().equals(name)) {
  			gff = (Sequence)getSequenceAt(i);
  			newSequence = false;
  		}
  		i++;
  	}
    return gff;

  }
  public Vector getGFFByName(String name) {

    int i = 0;
    Vector out = new Vector();
    
    while (i < getHeight()) {
    	if (getSequenceAt(i) instanceof GFF &&
    			getSequenceAt(i).getName().equals(name)) {
    		GFF gff = (GFF)getSequenceAt(i);
    		out.addElement(gff);
    	}
    	i++;
    }
    return out;

  }

  
	public static Alignment getDummyAlignment(String name,String chr,int start, int end) {
    Sequence[] s = new Sequence[1];
    
    StringBuffer tmpseq = new StringBuffer();
	
    int i = 0;
    
    while (i < end-start+1) {
      tmpseq.append('X');
      i++;
    }
    
    s[0] = new Sequence(name,tmpseq.toString(),1,end-start+1);
    
    Alignment     al = new Alignment(s);
    ChrRegion r = new ChrRegion(chr,start,end);
    
    al.setChrRegion(r);
    
    return al;
  }
	
	public void search(AlignViewport av) {
	  JFrame      jf = new JFrame("Find...");
	  SearchPanel sp = new SearchPanel(this,av,jf);
	  
	  // Need to listen to when it finishes
	  int width =  450;
	  int height = 250;
	  
	  jf.getContentPane().add(sp);
	  jf.setSize(width,height);
	  
	  Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
	  
	  jf.setLocation(sd.width / 2 - width / 2,
	      sd.height / 2 - height / 2);
	  
	  jf.setVisible(true);
	} 
	public int countSequenceEntries() {
	  int count = 0;
	  
	  for (int i = 0; i < getHeight(); i++) {
	    Sequence seq = getSequenceAt(i);
	    
	    if (seq.getSequence().length() > 1) {
	      count++;
	    }
	  }
	  return count;
	}
  
  public AlignStats getStats(int start, int end) {
    int num = sequences.size();


    double[] cov = new double[num];
    double[] tot = new double[num];
    double[] pid = new double[num];

    Sequence topseq = (Sequence)sequences.elementAt(0);

      for (int i = 0; i < num; i++) {
	tot[i] = 0;
	pid[i] = 0;
	cov[i] = 0;
	i++;
      }


      for (int j = start; j <= end; j++) {
      

      char refc = topseq.getCharAt(j);

      if (refc != '-' && refc != 'N') {
	for (int i = 0; i < num; i++) {

	  Sequence s = (Sequence)sequences.elementAt(i);
	  char c = s.getCharAt(j);

	  if (c != '-') {
	    tot[i]++;	  
	  }

	  if (refc == c) {
	    pid[i]++;
	  }
	}
      }
    }


    for (int i = 0;i < num; i++) {
      if (tot[i] > 0) {
	double tmpcov = tot[i]/(end-start+1);
	double tmppid = pid[i]/tot[i];
	
	cov[i] = tmpcov;
	pid[i] = tmppid;
      } else {
	cov[i] = 0;
	pid[i] = 0;
      }
      
    }

    AlignStats as = new AlignStats(cov,pid,tot);

    return as;
  }

  public Pwm getPwm(int start, int end) {
    double[] seqvec = pogvue.analysis.Correlation4.seqvec(this, start, end);
    
    return new Pwm(seqvec,"Pwm");
  }
  public void printStats(String chr,int start, int end, int offset,String label) {

    AlignStats as = getStats(start,end);

    double[] cov = as.getCoverage();
    double[] pid = as.getPID();
    double[] bases = as.getBases();
    
    int j = 0;
    
    while (j < cov.length) {
      System.out.printf("%10s\t%10s\t%10s\t%8d\t%8d\t%15s\t%7.2f\t%7.2f\t%7.2f",chr ,label,label,start+offset,end+offset,getSequenceAt(j).getName(), cov[j] ,pid[j],bases[j]);
      if (getSequenceAt(j).getName().equals("Dog") || j == 0) {
	System.out.println("\t" + getSequenceAt(j).getSequence());
      } else {
	System.out.println();
      }
      j++;

      
    }
    System.out.println();
  }
  public static void main(String[] args) {
    
    try {


      if (args[0].equals("-gfffile")) {
	String gfffile = args[1];

	GFFFile gff = new GFFFile(gfffile,"File");

	Vector feat = gff.getFeatures();

	for (int i = 0; i < feat.size(); i++) {

	  SequenceFeature sf = (SequenceFeature)feat.elementAt(i);
	  String chr = sf.getId();
	  int start = sf.getStart();
	  int end   = sf.getEnd();

	  FastaFile ff = GenomeInfoFactory.getUngappedRegion(chr,start,end);
	  ff.parse();

	  Sequence[] seqs = ff.getSeqsAsArray();
	
	  Alignment al = new Alignment(seqs);
	
	  al.printStats(chr,0,end-start+1,start,sf.getType() + ":" + sf.getHitFeature().getId());
	}
      } else {
	
	String chr    = args[0];
	int    start  = Integer.parseInt(args[1]);
	int    end    = Integer.parseInt(args[2]);
	
	
	int chunk = 100000;
	
	while (start+chunk < end) {
	  
	  FastaFile ff = GenomeInfoFactory.getUngappedRegion(chr,start,start+chunk-1);
	  ff.parse();
	  
	  Sequence[] seqs = ff.getSeqsAsArray();
	  
	  Alignment al = new Alignment(seqs);
	  
	  int i      = start;
	  int window = 100;
	  
	  while (i < start+chunk-window) {
	    al.printStats(chr,i-start,i+window-1-start,start,chr + "." + start + "-" + end);
	    i+= 1000;
	  }
	  
	  start+= chunk;
	}
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void scanLogos(Vector mats) {
    Vector newseqs = new Vector();
    int size = sequences.size();
    for (int i = 0; i < size; i++) {
      Sequence s = getSequenceAt(i);

      if (s instanceof GFF) {
	GFF g = (GFF)s;

	Vector feat = g.getFeatures();

	for (int j = 0;j < feat.size(); j++) {
	  SequenceFeature sf = (SequenceFeature)feat.elementAt(j);

	  if (sf.getTFMatrix() != null) {
	    TFMatrix tfm = sf.getTFMatrix();

	    SearchWorker sw = new SearchWorker(tfm.getPwm(),mats);
	    String name = "SP1 CAAT TATA OCT E2F";
	    sw.setThreshold(0.2);
	    //sw.setNames(name);
	    System.out.println("Running");
	    sw.run();

	    System.out.println("Run " + i);

	    Vector out = sw.getOutput();		
	    GFF tmpg = new GFF(g.getType(), "", 1, 2);
	    tmpg.addFeatures(out);	    
	    Vector newvec = GFFFile.bumpGFF(tmpg);

	    newseqs.addAll(newvec);

	  }
	}
      }
    }
    addSequences(newseqs);
  }

}








