package pogvue.io;

import pogvue.analysis.*;
import pogvue.datamodel.*;
import pogvue.util.QuickSort;
import pogvue.datamodel.motif.*;

import java.io.IOException;
import java.util.*;
import java.awt.event.*;
import pogvue.datamodel.comparer.*;

public class PiFile {
  private Vector<SequenceFeature> feat;
  
  private ActionListener l;
  
  private boolean donePhase = false;
  
  private int col;
  private BSearch bs;
  
  private ChrRegion reg;

  private Vector files;
  private int    filecount = 0;

  public PiFile(ChrRegion reg) throws IOException {
    this.reg = reg;
    parse();
  }
  
  private void init() {

    if (files == null) {
      files     = new Vector();
      filecount = 0;

      String chr   = reg.getChr();
      int    start = reg.getStart();
      int    end   = reg.getEnd();
      int    strand = reg.getStrand();
      
      String dir = "/ahg/scr4/30mammals/eutherian/pi/";
      
      int filestart = (int)(start/1000000) * 1000000;
      int fileend   = filestart + 999999;
      
      while (filestart < end) {
	String tmpchr = chr;
	
	if (chr.indexOf("chr") == 0) {
	  tmpchr = chr.substring(3);
	}
	
	String pifile = dir + tmpchr + "/" + chr + "_" + filestart + "-" + fileend + ".pi";
	
	files.addElement(pifile);
	
	// This doesn't account for chr lengths
	
	filestart += 1000000;
	fileend   += 1000000;
      }
    }
  }

  public void parse() throws IOException {
    feat = new Vector();

    init();
    
    SequenceFeature sf = new SequenceFeature();

    sf.setId   (reg.getChr());
    sf.setStart(reg.getStart());
    sf.setEnd  (reg.getEnd());
    sf.setStrand(reg.getStrand());
    sf.setType("pi");

    feat.add(sf);

    Hashtable pis    = new Hashtable();
    
    for (int i = 0; i < files.size(); i++) {
      String file = (String)files.elementAt(i);
      BSearch bs  = new BSearch(file,0,"\t");
      bs.search_file(reg.getStart()-10);

      String line;

      while (bs != null &&(line = bs.readLine()) != null) {
	//System.out.println("Line " + line);
	StringTokenizer str = new StringTokenizer(line,"\t");

	
	if (str.countTokens() >= 7) {
	  int    coord = Integer.parseInt(str.nextToken());
	  coord++;
	  double a     = Double.parseDouble(str.nextToken());
	  double c     = Double.parseDouble(str.nextToken());
	  double g     = Double.parseDouble(str.nextToken());
	  double t     = Double.parseDouble(str.nextToken());
	  double pi    = Double.parseDouble(str.nextToken());
	  double tree  = Double.parseDouble(str.nextToken());
	
	  if (coord > reg.getEnd()){
	    i = files.size();
	    bs = null;
	  } else  if (coord >= reg.getStart()) {
	    
	    Vector pivec = new Vector();
	    
	    pivec.addElement(a);
	    pivec.addElement(t);
	    pivec.addElement(c);
	    pivec.addElement(g);
	    pivec.addElement(pi);
	    
	    pis.put(coord,pivec);
	  }
	}
      }
    }
    sf.setScores(pis);
  }
  
  public Vector<SequenceFeature> getFeatures() {
    return feat;
  }

  public static Vector searchPwm(Pwm pwm1obj,  Pwm pwm2obj, double thresh) {
    Vector out = new Vector();

    int win = pwm2obj.getPwm().length/4;
    
    int pos1 = 0;
    
    double[] pwm1 = pwm1obj.getPwm();
    double[] pwm2 = pwm2obj.getPwm();
    double[] revpwm2 = pwm2obj.getRevPwm();
    
    int len = pwm1.length/4;

    while (pos1 < len - win) {

      double[] tmppwm1 = ClusterSeqs.slice(pwm1,pos1,pos1+win-1);
	  
	double corr = Correlation4.get(tmppwm1,pwm2);
	//System.out.println("Corr " + corr);
	if (corr > thresh) {
	  System.out.println("Corr\t" + pwm1obj.getName() + "\t" + pos1 + "\t" + Pwm.getConsensus(tmppwm1) + "\t" + Pwm.getConsensus(pwm2) + "\t" + corr);

	  SequenceFeature sf = new SequenceFeature();
	  sf.setId(pwm2obj.getName());
	  sf.setStart(pos1);
	  sf.setEnd(pos1+win-1);
	  sf.setStrand(1);
	  sf.setScore(corr);
	  out.addElement(sf);

	  Pwm.printLogo(tmppwm1);
	  //System.out.println("Maxcorr\t " + corr + "\t" + pos1);
	  //System.out.println("Cons1\t" + pwm1obj.getName() + "\t" + Pwm.getConsensus(tmppwm1) + "\t" + Pwm.getConsensus(pwm2));
	      
	}
	corr = Correlation4.get(tmppwm1,revpwm2);
	    
	if (corr > thresh) {
	  SequenceFeature sf = new SequenceFeature();
	  sf.setId(pwm2obj.getName());
	  //sf.setStart(len - pos1);
	  //sf.setEnd(len - (pos1+win-1));
	  sf.setStart(pos1+1);
	  sf.setEnd(pos1+win);
	  sf.setStrand(-1);
	  sf.setScore(corr);
	  out.addElement(sf);
	  Pwm.printLogo(tmppwm1);
	  System.out.println("Corr\t" + pwm1obj.getName() + "\t" + pos1 + "\t" + Pwm.getConsensus(tmppwm1) + "\t" + Pwm.getConsensus(revpwm2) + "\t" + corr);

	  //System.out.println("Maxcorr\t " + corr + "\t" + pos1);
	  //System.out.println("Cons1\t" + pwm1obj.getName() + "\t" + Pwm.getConsensus(tmppwm1) + "\t" + Pwm.getConsensus(revpwm2));
	      
	}
	pos1++;
      }
    return out;
  }

  public static PwmCluster collapsePwm(Vector pwms) {
    PwmCluster clus = null;

    for (int i = 0;i < pwms.size(); i++) {
      Pwm pwm = (Pwm)pwms.elementAt(i);

      if (clus == null) {
	clus = new PwmCluster(pwm);
      } else {
	try {
	  clus.addPwm(pwm);
	} catch (Exception e) {
	  e.printStackTrace();
	}
      }
    }

    return clus;
  }

  public static void gff2pwm(String gfffile) {

    try {
      GFFFile gff = new GFFFile(gfffile,"File");

      Vector  feat = gff.getFeatures();

      PwmCluster clus = null;

      for (int i = 0;i < feat.size(); i++) {

	SequenceFeature sf = (SequenceFeature)feat.elementAt(i);

	Pwm pwm = feat2pwm(sf);
	pwm.printPwmLine2(pwm.getPwm(),pwm.getName(),10,10);
	if (clus == null) {
	  clus = new PwmCluster(pwm);
	} else {
	  clus.addPwm(pwm);
	  //clus.print();
	}
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Pwm feat2pwm(SequenceFeature sf) throws IOException {
    PiFile pi = new PiFile(sf.getRegion());

	Vector f = pi.getFeatures();

	for (int ii = 0;ii < f.size(); ii++) {
	  SequenceFeature sf2 = (SequenceFeature)f.elementAt(ii);
	  sf2.setScore(sf.getScore());

	  int len = sf2.getLength();

	  double[] pwm = new double[len*4];

	  Hashtable pis = sf2.getScores();

	  System.out.println("\nFeature " + sf2 + " " + sf2.getStrand() + " " + sf.getHitFeature().getId());

	  TreeMap  treeMap = new TreeMap(pis);
	  
	  int j = 0;
	  
	  while (j < len) {
	    if (treeMap.containsKey(j+sf2.getStart())) {
	      Vector v = (Vector)treeMap.get(j+sf2.getStart());
	      
	      if (sf2.getStrand() == 1) {
		pwm[j*4+0] = ((Double)v.elementAt(0)).doubleValue(); // a
		pwm[j*4+1] = ((Double)v.elementAt(1)).doubleValue(); // t
		pwm[j*4+2] = ((Double)v.elementAt(2)).doubleValue(); // c
		pwm[j*4+3] = ((Double)v.elementAt(3)).doubleValue(); // g
	      } else {

		double tmpa = ((Double)v.elementAt(0)).doubleValue();
		double tmpc = ((Double)v.elementAt(2)).doubleValue();
		pwm[(len-j-1)*4+0] = ((Double)v.elementAt(1)).doubleValue(); // a -> t
		pwm[(len-j-1)*4+1] = tmpa;                                   // t -> a
		pwm[(len-j-1)*4+2] = ((Double)v.elementAt(3)).doubleValue(); // c -> g
		pwm[(len-j-1)*4+3] = tmpc;                                   // g -> c
	      }
	    } else {
	      if (sf2.getStrand() == 1) {
		
		pwm[j*4+0] = 0.25;
		pwm[j*4+1] = 0.25;
		pwm[j*4+2] = 0.25;
		pwm[j*4+3] = 0.25;
	      } else {
		pwm[(len - j - 1)*4+0] = 0.25;
		pwm[(len - j - 1)*4+1] = 0.25;
		pwm[(len - j - 1)*4+2] = 0.25;
		pwm[(len - j - 1)*4+3] = 0.25;
	      }
	    }
	    
	    j++;
	    
	  }
	  Pwm pwmobj = new Pwm(pwm,sf2.getRegion().toString());
	  
	  return pwmobj;
	  
	}
	return null;
  }

	
  public static void main(String[] args) {
    try {



      if (args[0].equals("-collapse")) {
	PwmLineFile mf = new PwmLineFile(args[1],"File");
	mf.parse();
	
	PwmCluster clus = collapsePwm(mf.getPwmMatrices());

	System.out.println("Cluster " + clus.getConsensus(clus.getPwm()));

	Pwm.printPwmLine2(clus.getPwm(),args[1],1,clus.size());
      } else  if (args[0].equals("-pool")) {
	gff2pwm(args[1]);
      } else {
	
	PwmLineFile mf = new PwmLineFile(args[1],"File");
	mf.parse();
	Vector mats = mf.getTFMatrices();
	System.out.println("Matrices " + mats);
	GFFFile gff = new GFFFile(args[0],"File");
	
	double thresh = Double.parseDouble(args[2]);
	Vector feat = gff.getFeatures();
	
	for (int i = 0;i < feat.size(); i++) {

	  try {

	  SequenceFeature gfffeat = (SequenceFeature)feat.elementAt(i);
	  
	  PiFile pi = new PiFile(gfffeat.getRegion());
	  
	  Vector f = pi.getFeatures();

	  for (int ii = 0;ii < f.size(); ii++) {

	    SequenceFeature sf2 = (SequenceFeature)f.elementAt(ii);

	    sf2.setScore(gfffeat.getScore());

	    int len = sf2.getLength();

	    System.out.println("Length " + len + " " + (sf2.getEnd()-sf2.getStart()+1) + " " + sf2.getStart() + " " + sf2.getEnd());

	    double[] pwm = new double[len*4];

	    Hashtable pis = sf2.getScores();

	    System.out.println("\nFeature " + sf2 + " " + sf2.getStrand() + " " + gfffeat.getHitFeature().getId());

	    TreeMap  treeMap = new TreeMap(pis);

	    int j = 0;
	    
	    while (j < len) {
	      if (treeMap.containsKey(j+sf2.getStart())) {
		Vector v = (Vector)treeMap.get(j+sf2.getStart());
		
		if (sf2.getStrand() == 1) {
		  pwm[j*4+0] = ((Double)v.elementAt(0)).doubleValue(); // a
		  pwm[j*4+1] = ((Double)v.elementAt(1)).doubleValue(); // t
		  pwm[j*4+2] = ((Double)v.elementAt(2)).doubleValue(); // c
		  pwm[j*4+3] = ((Double)v.elementAt(3)).doubleValue(); // g
		} else {
		  
		  double tmpa = ((Double)v.elementAt(0)).doubleValue();
		  double tmpc = ((Double)v.elementAt(2)).doubleValue();
		  pwm[(len-j-1)*4+0] = ((Double)v.elementAt(1)).doubleValue(); // a -> t
		  pwm[(len-j-1)*4+1] = tmpa;                                   // t -> a
		  pwm[(len-j-1)*4+2] = ((Double)v.elementAt(3)).doubleValue(); // c -> g
		  pwm[(len-j-1)*4+3] = tmpc;                                   // g -> c
		}
	      } else {
		pwm[j*4+0] = 0.25;
		pwm[j*4+1] = 0.25;
		pwm[j*4+2] = 0.25;
		pwm[j*4+3] = 0.25;
	      }

	      j++;

	    }

	    Pwm pwmobj = new Pwm(pwm,sf2.getRegion().toString());
	    //	    pwmobj.printPwmLine2(pwm,sf2.getId(),sf2.getStart(),sf2.getEnd());
	     System.out.println(pwmobj.getConsensus(pwm));
	    //Pwm.printLogo(pwm);

	    for (int k = 0;k < mats.size(); k++) {
	      TFMatrix tfm = (TFMatrix)mats.elementAt(k);
	      Pwm      pwmk = tfm.getPwm();
	      Vector out = PiFile.searchPwm(pwmobj,pwmk,thresh);

	      for (int l = 0; l < out.size(); l++) {
		SequenceFeature sf = (SequenceFeature)out.elementAt(l);
		String id = sf.getId();

		if (gfffeat.getStrand() == -1) {
		  int start = gfffeat.getEnd() - sf.getEnd();
		  int end   = gfffeat.getEnd() - sf.getStart();

		System.out.println(gfffeat.getId() + "\tmotif\tmotif\t" + (start) + "\t" + (end) + "\t" + sf.getScore() + "\t" + (gfffeat.getStrand()*sf.getStrand()) + "\t.\t" + id);;
		} else {
		  System.out.println(gfffeat.getId() + "\tmotif\tmotif\t" + (gfffeat.getStart()+sf.getStart()) + "\t" + (gfffeat.getStart() + sf.getEnd()) + "\t" + sf.getScore() + "\t" + (gfffeat.getStrand()*sf.getStrand()) + "\t.\t" + id);;
		//System.out.println("SF\t" + sf + "\t" + gfffeat.getHitFeature().getId());
		}
	      }
	    }

	  }
	} catch (Exception e2) {
	  e2.printStackTrace();
	}
      }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  

}
