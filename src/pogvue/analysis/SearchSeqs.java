package pogvue.analysis;

import pogvue.io.*;
import pogvue.util.*;
import pogvue.datamodel.*;
import pogvue.datamodel.motif.*;
import pogvue.datamodel.comparer.*;

import java.util.*;

public class SearchSeqs {
  Vector clus;
  public static double[] slice(double[] val,int start, int end) {

    double[] tmpval = new double[(end-start+1)* 4];

    int i = start;

    while (i <= end) {
      tmpval[(i-start)*4 + 0] = val[i*4 + 0];
      tmpval[(i-start)*4 + 1] = val[i*4 + 1];
      tmpval[(i-start)*4 + 2] = val[i*4 + 2];
      tmpval[(i-start)*4 + 3] = val[i*4 + 3];

      i++;
    }

    return tmpval;
  }


  public static void scanMotif(SequenceFeature sf, Vector mats, double thresh) {

    String chr    = sf.getId();
    int    start  = sf.getStart();
    int    end    = sf.getEnd();
    int    strand = sf.getStrand();

    // First  - get the pwm for the sequence - could go through the website or do it directly


    // Loop over the sequence

    // Loop over the matrices

    // For all hits over the threshold print mat name, score,  mat position, sf postion - sf pwm.

    
    
  }
    
  public static Hashtable  processHits(Vector hits,Pwm pwm1) {
    Hashtable pwmhash = new Hashtable();

    int regstart = pwm1.getChrRegion().getStart();
    int regend   = pwm1.getChrRegion().getEnd();

    int len = (regend-regstart+1);

    for (int j = 0; j < hits.size(); j++) {
      double[] tmppwm = pwm1.getPwm();

      SequenceFeature sf = (SequenceFeature)hits.elementAt(j);
      SequenceFeature hf = sf.getHitFeature();
      String hitname     = hf.getId();
      

      //                 --------------------------------------------------------------------
      //                regstart                 |                  |                      regend
      //  <                      sf.getStart()  >
      //  <                                            sf.getEnd() >
      //
      // Trim end = regend - sf.getEnd();

      
      if (sf.getStrand() == -1) {
	tmppwm  = pwm1.revComp();
      }
      System.out.println("Trim\t" + regend + "\t" + sf.getEnd() + "\t" + (regend-sf.getEnd()) + "\t" + (tmppwm.length/4) + "\t" + sf.getStrand());
      //      tmppwm = Pwm.trimPwmEnd(tmppwm,tmppwm.length/4 - sf.getEnd() + pwm1.getChrRegion().getStart()-1);

      tmppwm = Pwm.trimPwmEnd(tmppwm,regend-sf.getEnd());


      tmppwm = Pwm.trimPwmStart(tmppwm,sf.getStart() - pwm1.getChrRegion().getStart());
      
      Pwm pwmobj = new Pwm(tmppwm,pwm1.getName());
      int orient = sf.getHitFeature().getStrand()* sf.getStrand();
      System.out.println("GFF " + pwm1.getName() + "\t" + sf.toGFFString());
		
      if (sf.getHitFeature().getStrand()*sf.getStrand() == -1) {
	tmppwm = pwmobj.revComp();
	pwmobj.setPwm(tmppwm);
      }
      
      System.out.println("Name\t" + hitname + "\t" + pwmobj.getConsensus() + "\t" + pwm1.getName() +  "\t" + sf.getHitFeature().getStrand() + " " + sf.getStrand() + " " + sf.getScore());
      
      
      if (pwmhash.containsKey(hitname)) {
	
	Vector v = (Vector)pwmhash.get(hitname);
	
	v.addElement(pwmobj);
	
      } else {
	Vector v = new Vector();
	v.addElement(pwmobj);
	pwmhash.put(hitname,v);
      }
    }
    return pwmhash;
  }

  public static void main(String[] args) {
    try {
    Hashtable opts = GetOptions.get(args);

    String pwmfile = (String) opts.get("-pwmfile");
    double thresh  = Double.parseDouble((String) opts.get("-thresh"));
    String format  = (String) opts.get("-format");
    String name    = (String) opts.get("-name");
    boolean log    = false;

    if (opts.containsKey("-log")) {
      log = true;
    }

    String matfile = null;
    int    win = 10;

    if (opts.containsKey("-window")) {
      win = Integer.parseInt((String) opts.get("-window"));
    }

    if (opts.containsKey("-matfile")) {
      matfile = (String) opts.get("-matfile");
    }

    PwmLineFile  pf    = new PwmLineFile(pwmfile, "File");

    if (matfile != null) {
      PwmLineFile mf = new PwmLineFile(matfile,"File");

      mf.parse();

      Vector mats = mf.getTFMatrices();

      Pwm pwm1 = null;

      Hashtable pwmhash = new Hashtable();


      while ((pwm1 = pf.nextPwm()) != null) {

	try {
	System.out.println("Pwm " + pwm1.getName());
	for (int i = 0;i < mats.size(); i++) {
	  TFMatrix tfm = (TFMatrix)mats.elementAt(i);
	  Pwm      pwm = tfm.getPwm();
	
	  if (name == null || pwm.getName().indexOf(name) >= 0) {
	      
	    Vector hits = searchPwm(pwm1,pwm,thresh,log);
	      
	    Hashtable tmphash = SearchSeqs.processHits(hits,pwm1);
	  }
	}
	} catch (Exception e) {
	  System.out.println("ERROR " + pwm1.getName());
	  e.printStackTrace();
	}
      }
    
      Enumeration en = pwmhash.keys();
      Vector clusvec = new Vector();
      while (en.hasMoreElements()) {
	
	String tmpname = (String)en.nextElement();

	Vector v = (Vector)pwmhash.get(tmpname);

	PwmCluster clus = null;

	for (int i = 0;i < v.size();i++) {

	  Pwm p = (Pwm)v.elementAt(i);
	  p.setName(tmpname);
	  p.printPwmLine();
	  if (clus == null) {
	    clus = new PwmCluster(p);
	  } else {
	    clus.addPwm(p);
	  }

	  System.out.println("Cluster " + tmpname + " " + PwmCluster.getConsensus(clus.getPwm()));
	}
	clusvec.addElement(clus);
	//Pwm.printPwmLine2(clus.getPwm(),tmpname + "." + clus.size(),1,clus.size());
      }
      Collections.sort(clusvec, new PwmClusterComparer());
      for (int i = 0;i < clusvec.size(); i++) {
	PwmCluster pwmclus = (PwmCluster)clusvec.elementAt(i);
	
	Pwm.printPwmLine2(pwmclus.getPwm(),pwmclus.getName(),1,pwmclus.size());
      }

    } else {
      clusterPwms(pf,win,thresh);
    }
    } catch (Exception e) {
      System.out.println("\nException " + e);
      e.printStackTrace();
      
      
      System.exit(0);
    }
    

  }


  public static void clusterPwms(PwmLineFile pf, int win, double thresh) {
    
    try {

      Vector       clus  = new Vector();

      Pwm pwm1;

      while ((pwm1 = pf.nextPwm()) != null) {
	
	PwmCluster maxclus = null;
	double     maxcorr = -1;
	int        maxpos1 = 0;
	int        maxpos2 = 0;

	double[] maxpwm1 = new double[win*4];
	double[] maxpwm2 = new double[win*4];

	System.out.println("Pwm " + Pwm.getConsensus(pwm1.getPwm()));

	for (int i = 0; i < clus.size(); i++) {

	  int pos1 = 0;

	  PwmCluster clus1 = (PwmCluster)clus.elementAt(i);
	  
	  double[] pwm2 = clus1.getPwm();
	  
	  while (pos1 < pwm1.getPwm().length/4 - win) {
	    
	    double[] tmppwm1 = ClusterSeqs.slice(pwm1.getPwm(),pos1,pos1+win-1);
	    
	    double corr = Correlation4.get(tmppwm1,pwm2);
	    
	    if (corr > thresh) {
		System.out.println("Corr\t" + pos1 + " " + i + " "  + Pwm.getConsensus(tmppwm1) + "\t" + Pwm.getConsensus(pwm2) + "\t" + corr);
		maxcorr = corr;
		maxpos1 = pos1;
		maxpos2 = i;
		maxpwm1 = tmppwm1;
		maxpwm2 = pwm2;
		maxclus = clus1;
		//}
		System.out.println("Maxcorr\t " + maxcorr + "\t" + maxpos1 + "\t" + maxpos2);
		System.out.println("Cons1\t" + pwm1.getName() + "\t" + Pwm.getConsensus(maxpwm1));
		System.out.println("Cons2\t" + maxclus.getConsensus(maxclus.getPwm()) + "\t" + Pwm.getConsensus(maxpwm2));
		maxclus.addPwm(new Pwm(maxpwm1, pwm1.getName()));
	      
	    }
	    pos1++;
	  }
	}
	if (maxclus != null) {
	} else {
	  System.out.println("No cluster found");

	  int pos1 = 0;
	  while (pos1 < pwm1.getPwm().length/4 - win) {
	    double[] tmppwm1 = ClusterSeqs.slice(pwm1.getPwm(),pos1,pos1+win-1);

	    PwmCluster tmpclus = new PwmCluster(new Pwm(tmppwm1,pwm1.getName()));
	    System.out.println("Made new cluster " + Pwm.getConsensus(tmppwm1));
	    clus.addElement(tmpclus);
	    pos1++;
	  } 
	  
	}

      }
      
      Collections.sort(clus, new PwmClusterComparer());
      System.out.println("Number of clusters = " + clus.size());
      for (int i = 0; i < clus.size(); i++) {
	PwmCluster tmpclus = (PwmCluster)clus.elementAt(i);
	tmpclus.print();
      }

    } catch (Exception e) {
      System.out.println("\nException " + e);
      e.printStackTrace();


      System.exit(0);
    }

  }

  public static Vector searchPwm(Pwm pwm1,  Pwm pwm, double thresh,boolean log) {
    
    try {
      Vector hits = new Vector();
      //System.out.println("Searching " + pwm.getName() + " " + Pwm.getConsensus(pwm.getPwm()));

      int win = pwm.getPwm().length/4;
 
      double[] maxpwm1 = new double[win*4];
      double[] maxpwm2 = new double[win*4];
      
      //System.out.println("\nPwm " + Pwm.getConsensus(pwm1.getPwm()) + "\n");
      
      int pos1 = 0;
      
      double[] pwm2 = pwm.getPwm();
      double[] revpwm2 = pwm.getRevPwm();
      
      Pwm revpwm = new Pwm(revpwm2,pwm.getName());
      while (pos1 < pwm1.getPwm().length/4 - win) {
	
	double[] tmppwm1 = ClusterSeqs.slice(pwm1.getPwm(),pos1,pos1+win-1);
	Pwm      pwm1obj = new Pwm(tmppwm1,pwm1.getName());
	double corr = 0;

	if (log) {
	  corr = pwm.getLogScore(pwm1obj);
	} else {
	  corr = Correlation4.get(tmppwm1,pwm2);
	}
	
	if (corr > thresh) {
	  
	  // Make gff for the match
	  
	  SequenceFeature sf = new SequenceFeature();

	  SequenceFeature sf2 = new SequenceFeature();
	  sf2.setId(pwm.getName());
	  sf.setHitFeature(sf2);
	    

	  hits.addElement(sf);
	  ChrRegion reg = pwm1.getChrRegion();
	  

	  String gffstr  = "";
	  String gffstr2 = "";

	  gffstr2 = pwm1.getName() + "\tlocalmatch\tlocalmatch\t";

	  if (reg != null) {
	    sf.setId(reg.getChr());
	    sf.setType("match");
	    sf2.setStrand(1);
	    gffstr = reg.getChr() + "\tmatch\tmatch\t";


	    if (reg.getStrand() == 1) {
	      //   Forward strand
	      //     tss---------------------------------------150
	      //   regstart                 ^ pos1
	      
	      int tmpstart = reg.getStart() + pos1;
	      int tmpend   = reg.getStart() + pos1 + win -1;
	      
	      gffstr = gffstr + tmpstart + "\t" + tmpend;

	      sf.setStart(tmpstart);
	      sf.setEnd(tmpend);
	      sf.setStrand(1);

	    } else {
	      
	      //    150-----------------------------------------tss
	      //   regstart             <pos1------------------>
	      
	      int tmpstart = reg.getEnd() - pos1 - win +1;
	      int tmpend   = reg.getEnd() - pos1;
	      
	      gffstr = gffstr + tmpstart + "\t" + tmpend;

	      sf.setStart(tmpstart);
	      sf.setEnd(tmpend);
	      sf.setStrand(-1);

	    }

	    
	    gffstr2 = gffstr2 + pos1 + "\t" + (pos1+win-1);
	    gffstr2 = gffstr2 + "\t" + corr + "\t" + 1 + "\t.\t" + pwm.getName();

	    sf.setScore(corr);
	    gffstr = gffstr + "\t" + corr + "\t" + reg.getStrand() + "\t.\t" + pwm.getName() + "\t" + pwm1.getName();

	    //System.out.println(gffstr);
	    System.out.println(gffstr2);
	      
	  }
	}
	corr = 0;
	if (log) {
	  corr = revpwm.getLogScore(pwm1obj);
	} else {
	  corr = Correlation4.get(tmppwm1,revpwm2);
	}
	
	if (corr > thresh) {

	    SequenceFeature sf = new SequenceFeature();
	    SequenceFeature sf2 = new SequenceFeature();

	    sf2.setId(pwm.getName());
	    sf.setHitFeature(sf2);

	    hits.addElement(sf);

	    ChrRegion reg = pwm1.getChrRegion();
	    String gffstr = "";
	    String gffstr2 = "";

	    gffstr2 = pwm1.getName() + "\tlocalmatch\tlocalmatch\t";

	    if (reg != null) {

	      sf2.setStrand(-1);
	      gffstr = reg.getChr() + "\tmatch\tmatch\t";

	      sf.setId(reg.getChr());
	      sf.setType("match");

	      if (reg.getStrand() == 1) {
		
		//     tss---------------------------------------150
		//   regstart                 ^ pos1
		
		int tmpstart  = reg.getStart() + pos1;
		int tmpend    = reg.getStart()   + pos1 + win -1;
		
		gffstr = gffstr + tmpstart + "\t" + tmpend;

		sf.setStart(tmpstart);
		sf.setEnd(tmpend);
		sf.setStrand(1);
	      } else {
		
		//    150-----------------------------------------tss
		//   regstart             <pos1------------------>

		int tmpstart = reg.getEnd() - pos1 - win +1;
		int tmpend   = reg.getEnd() - pos1;

		gffstr = gffstr + tmpstart + "\t" + tmpend;
		sf.setStart(tmpstart);
		sf.setEnd(tmpend);
		sf.setStrand(-1);
	      }

	      gffstr2 = gffstr2 + pos1 + "\t" + (pos1+win-1);
	      gffstr2 = gffstr2 + "\t" + corr + "\t" + -1 + "\t.\t" + pwm.getName();

	      sf.setScore(corr);
	      gffstr = gffstr + "\t" + corr + "\t" + (-1 * reg.getStrand()) + "\t.\t" + pwm.getName() + "\t" + pwm1.getName();
	      
	      //  System.out.println(gffstr);
	      System.out.println(gffstr2);
	      
	    }
	  }
	      
	  pos1++;
	}
      return hits;
	//}
    } catch (Exception e) {
      System.out.println("\nException " + e);
      e.printStackTrace();
      
      
      System.exit(0);
    }
    return null;
  }


  public static void compareCluster(PwmCluster c, Vector mats, double thresh) {
    double pwm1[] = c.getPwm();
    int len1 = pwm1.length / 4;

    for (int i = 0; i < mats.size(); i++) {
      TFMatrix tfm = (TFMatrix) mats.elementAt(i);

      double[] pwm2 = tfm.getPwm().getPwm();
      int len2 = pwm2.length / 4;

      if (len1 < len2) {

        int j = 0;

        while (j < len2 - len1) {

          double[] winpwm2 = Correlation4.substring(pwm2, j, j + len1 - 1);
          double tmpcorr = Correlation4.get(pwm1, winpwm2);

          j++;

          if (tmpcorr > thresh) {
            System.out.println("Correlation " + PwmCluster.getConsensus(pwm1)
                + " " + tfm.getName() + " " + tfm.getConsensus() + " "
                + tmpcorr);
            c.addMatch(tfm);
          }

        }
      } else if (len1 > len2) {
        int j = 0;

        while (j < len1 - len2) {

          double[] winpwm1 = Correlation4.substring(pwm1, j, j + len2 - 1);
          double tmpcorr = Correlation4.get(winpwm1, pwm2);

          // System.out.println("Len2 " + len1 + " " + len2 + " " + j + " " +
          // winpwm1.length/4);

          j++;

          // Correlation4.printPWM(pwm1,true);
          // System.out.println();
          // Correlation4.printPWM(winpwm1,true);
          // System.out.println();
          // Correlation4.printPWM(pwm2,true);
          // System.out.println();
          if (tmpcorr > thresh) {
            System.out.println("Correlation " + PwmCluster.getConsensus(pwm1)
                + " " + tfm.getName() + " " + tfm.getConsensus() + " "
                + tmpcorr);
            c.addMatch(tfm);
          }

        }
      } else if (len1 == len2) {
        double tmpcorr = Correlation4.get(pwm1, pwm2);
        if (tmpcorr > thresh) {
          System.out.println("CorrelationA " + PwmCluster.getConsensus(pwm1)
              + " " + tfm.getName() + " " + tfm.getConsensus() + " " + tmpcorr);
          c.addMatch(tfm);
        }
      }
    }
  }

  public static double[] seqvec(Sequence seq) {
    return seqvec(seq, 0, seq.getLength() - 1);
  }

  public static double[] seqvec(Sequence seq, int start, int end) {

    double seqvec[] = new double[seq.getLength() * 4];

    for (int j = start; j <= end; j++) {
      char c = seq.getCharAt(j);

      seqvec[j * 4] = 0;
      seqvec[j * 4 + 1] = 0;
      seqvec[j * 4 + 2] = 0;
      seqvec[j * 4 + 3] = 0;
      if (c == 'A') {
        seqvec[j * 4] = 1;
      } else if (c == 'T') {
        seqvec[j * 4 + 1] = 1;
      } else if (c == 'C') {
        seqvec[j * 4 + 2] = 1;
      } else if (c == 'G') {
        seqvec[j * 4 + 3] = 1;
      } else if (c == '-') {
        seqvec[j * 4] = 0.25;
        seqvec[j * 4 + 1] = 0.25;
        seqvec[j * 4 + 2] = 0.25;
        seqvec[j * 4 + 3] = 0.25;
      }
    }
    return seqvec;
  }

  public static double[] seqvec(String seq, int start, int end) {

    seq = seq.toUpperCase();

    double seqvec[] = new double[(end - start + 1) * 4];

    int offset = start * 4;

    for (int j = start; j <= end; j++) {
      char c = seq.charAt(j);

      seqvec[j * 4 - offset] = 0;
      seqvec[j * 4 + 1 - offset] = 0;
      seqvec[j * 4 + 2 - offset] = 0;
      seqvec[j * 4 + 3 - offset] = 0;
      if (c == 'A') {
        seqvec[j * 4 - offset] = 1;
      } else if (c == 'T') {
        seqvec[j * 4 + 1 - offset] = 1;
      } else if (c == 'C') {
        seqvec[j * 4 + 2 - offset] = 1;
      } else if (c == 'G') {
        seqvec[j * 4 + 3 - offset] = 1;
      } else if (c == '-') {
        seqvec[j * 4 - offset] = 0.25;
        seqvec[j * 4 + 1 - offset] = 0.25;
        seqvec[j * 4 + 2 - offset] = 0.25;
        seqvec[j * 4 + 3 - offset] = 0.25;
      }
    }
    return seqvec;
  }

  public static double[] seqvec(Alignment al, int start, int end) {

    Vector seqs = al.getSequences();

    double seqvec[] = new double[(end - start + 1) * 4];

    int offset = start * 4;

    for (int j = start; j <= end; j++) {
      int numseqs = al.getHeight();

      seqvec[j * 4 - offset] = 0;
      seqvec[j * 4 + 1 - offset] = 0;
      seqvec[j * 4 + 2 - offset] = 0;
      seqvec[j * 4 + 3 - offset] = 0;

      int totseq = 0;

      for (int i = 0; i < numseqs; i++) {

        if (al.getSequenceAt(i).getSequence().length() > 10) {

          totseq++;
          Sequence seq = al.getSequenceAt(i);
          char c = '-';
          if (seq.getSequence().length() > j) {
            c = seq.getSequence().charAt(j);
          }
          if (c == 'A') {
            seqvec[j * 4 - offset] += 1;
          } else if (c == 'T') {
            seqvec[j * 4 + 1 - offset] += 1;
          } else if (c == 'C') {
            seqvec[j * 4 + 2 - offset] += 1;
          } else if (c == 'G') {
            seqvec[j * 4 + 3 - offset] += 1;
          } else if (c == '-') {
            // seqvec[j*4 - offset] += 0.25;
            // seqvec[j*4+1 - offset] += 0.25;
            // seqvec[j*4+2 - offset] += 0.25;
            // seqvec[j*4+3 - offset] += 0.25;
          }
        }
      }
      if (totseq > 0) {
        seqvec[j * 4 - offset] /= totseq;
        seqvec[j * 4 + 1 - offset] /= totseq;
        seqvec[j * 4 + 2 - offset] /= totseq;
        seqvec[j * 4 + 3 - offset] /= totseq;
      }
      if (j % 10 == 0) {

        // System.out.println("Vals " + seqvec[j*4-offset] + " " +
        // seqvec[j*4+1-offset] + " " + seqvec[j*4+2-offset] + " " +
        // seqvec[j*4+3-offset]);
      }
    }
    return seqvec;
  }

  public static String vec2seq(double[] vec) {

    StringBuffer str = new StringBuffer();

    int i = 0;

    while (i < vec.length / 4) {
      int max = maxOfFour(vec, i);

      if (i == 0) {
        str.append("A");
      } else if (i == 1) {
        str.append("T");
      } else if (i == 2) {
        str.append("C");
      } else if (i == 1) {
        str.append("G");
      }
      i += 4;
    }
    return str.toString();
  }

  public static int maxOfFour(double[] vec, int offset) {

    double max = 0;
    int maxindex = -1;

    int i = 0;

    while (i < 4) {
      if (vec[offset + i] >= max) {
        max = vec[offset + i];
        maxindex = i;
      }
      i++;
    }
    return maxindex;
  }

  public static void printPWM(double[] pwm, boolean oneline) {

    int i = 0;

    while (i < pwm.length / 4) {
      int j = 0;

      while (j < 4) {
        System.out.printf("%10.2f\t", pwm[i * 4 + j]);
        j++;
      }
      if (oneline == false) {
        System.out.println();
      }
      i++;
    }

  }

  public Vector getClusters() {
    return clus;
  }
}
