	package pogvue.datamodel.motif;

import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import pogvue.datamodel.*;

public class Pwm {


  public static String getConsensus(double[] pwm) {
    StringBuffer cons = new StringBuffer();

    int i = 0;
    
    while (i < pwm.length/4) {


      double maxval = pwm[i*4];
      String maxch  = "A";
	  
      if (pwm[i*4+1] > maxval) {
	maxval = pwm[i*4+1];
	maxch  = "T";
      } 
      if (pwm[i*4+2] > maxval) {
	maxval = pwm[i*4+2];
	maxch  = "C";
      } 
      if (pwm[i*4+3] > maxval) {
	maxval = pwm[i*4+3];
	maxch  = "G";
      }

      cons.append(maxch);
      i++;
    }
    return cons.toString();
    
  }
  public static void print(double pwm[]) {

    int j = 0;

    while (j < pwm.length/4) {

      int i = 0;

      System.out.print("A T C G ");
      while (i  < 4) {
	System.out.print(pwm[j*4+i] + " ");
	i++;
      }
      System.out.println();
      j++;
    }
  }
  public static void printLogo(double pwm[]) {
    
    int i = 0;
    
    String[] bases = new String[4];
    
    bases[0] = "A";
    bases[1] = "T";
    bases[2] = "C";
    bases[3] = "G";
    
    while (i < pwm.length/4) {
      
      double inf = 0;
      int j = 0;
      
      while (j < 4) {
	
	if (pwm[i*4+j] > 0) {
	  inf += pwm[i*4+j] * Math.log(pwm[i*4+j])/Math.log(2);
	}
	j++;
      }
      
      inf = 2 + inf;
      
      j = 0;
      
      System.out.print("Base " + i + "\t");

      while (j < 4) {
	
	int len = (int)(pwm[i*4+j] * 20 * inf);
	
	int k = 0;
	while (k < len) {
	  System.out.print(bases[j]);
	  k++;
	}
	if (k > 1) {
	  System.out.print(" ");
	}
	j++;
      }
      System.out.println();
      i++;
    }
  }
  public static void print100PwmLine(double pwm[]) {
    int i = 0;
    
    while (i < pwm.length/4) {
      System.out.println((int)(100*pwm[i*4])/10 + "\t" + (int)(100*pwm[i*4+1])/10 + "\t" + (int)(100*pwm[i*4+2])/10 + "\t" + (int)(100*pwm[i*4+3])/10);
      i++;
    }
  }
  public void printPwmLine() {
    int i = 0;

    System.out.print(getName() + "\t1\t1");
    
    while (i < pwm.length/4) {
      System.out.printf("\t%7.4f\t%7.4f\t%7.4f\t%7.4f",pwm[i*4 + 0], pwm[i*4 + 1], pwm[i* 4 + 2], pwm[i*4 + 3]);
      i++;
    }

    System.out.println();
    System.out.println("I " + i);
  }
  public static void printPwmLine2(double pwm[],String name,int c1, int c2) {
    int i = 0;
    
    System.out.print(name + "\t" + c1 + "\t" + c2);

    while (i < pwm.length/4) {
      System.out.printf("\t%7.4f\t%7.4f\t%7.4f\t%7.4f",pwm[i*4+0],pwm[i*4+1],pwm[i*4+2],pwm[i*4+3]);
      i++;
    }
    System.out.println();
  }

  private double[] lods = null;
  private double[] pwm;
  private double[] logpwm;
  private String   name;
  private ChrRegion region;

  public double maxscore = -1;
  public double minscore = -1;
  public double gc = 0.2;
    public double at = 0.3;

    public Pwm(double[] pwm,String name) {
    setPwm(pwm);
    this.name = name;
  }
  public ChrRegion getChrRegion() {

    if (region != null) {
      return region;
    }

    if (name.indexOf("chr") > 0 &&
	name.indexOf("-") > name.indexOf("chr")) {

      StringTokenizer str = new StringTokenizer(name,".");

      String chr  = null;
      int    start = -1;
      int    end   = -1;
      int    strand = 0;

      while (str.hasMoreTokens()) {
	String tmp = str.nextToken();

	if (chr != null && start != -1) {
	  strand = Integer.parseInt(tmp);
	  if (strand != 1 && strand != -1) {
	    System.out.println("Can't parse regions string for strand " + name);
	  }
	  region = new ChrRegion(chr,start,end,strand);

	  return region;
	}
	


	if (chr != null && tmp.indexOf("-") > 0) {
	  StringTokenizer str2 = new StringTokenizer(tmp,"-");

	  if (str2.countTokens() == 2) {

	    start = Integer.parseInt(str2.nextToken());
	    end   = Integer.parseInt(str2.nextToken());
	  } else {
	    System.out.println("Can't parse region string for chr.start-end " + name);
	  }

	}

	if (tmp.indexOf("chr") == 0) {
	  chr = tmp;
	}
      }
    }
    return region;
  }


  public String getConsensus() {
    StringBuffer cons = new StringBuffer();

    int i = 0;
    
    while (i < pwm.length/4) {
      double maxval = pwm[i*4];
      String maxch  = "A";
	  
      if (pwm[i*4+1] > maxval) {
	maxval = pwm[i*4+1];
	maxch  = "T";
      } 
      if (pwm[i*4+2] > maxval) {
	maxval = pwm[i*4+2];
	maxch  = "C";
      }
      if (pwm[i*4+3] > maxval) {
	maxval = pwm[i*4+3];
	maxch  = "G";
      }
      cons.append(maxch);
      i++;
    }
    return cons.toString();
    
  }

    public double getInfContent() {
      int i = 0;
      double totinf = 0;

      while (i < pwm.length/4) {

	double inf = 0;
	int j = 0;

	while (j < 4) {

	  if (pwm[i*4+j] > 0) {
	    inf += pwm[i*4+j] * Math.log(pwm[i*4+j])/Math.log(2);
	  }
	  j++;
	}

	totinf += 2 + inf;
	i++;
      }
      return totinf;
    }

  public double[] trimByInfContent(double thresh) {

      int i      = 0;

      int start = -1;
      int end   = -1;

      while (i < pwm.length/4) {

	double inf = 0;
	int j      = 0;

	while (j < 4) {

	  if (pwm[i*4+j] > 0) {
	    inf += pwm[i*4+j] * Math.log(pwm[i*4+j])/Math.log(2);
	  }
	  j++;
	}

	inf = 2 + inf;

	if (start == -1 && inf >= thresh) {
	  start = i;
	}
	i++;
      }
      i = pwm.length/4-1;
      while (i >= 0) {

	double inf = 0;
	int j      = 0;

	while (j < 4) {

	  if (pwm[i*4+j] > 0) {
	    inf += pwm[i*4+j] * Math.log(pwm[i*4+j])/Math.log(2);
	  }
	  j++;
	}

	inf = 2 + inf;

	if (end == -1 && inf >= thresh) {
	  end = i;
	}
	i--;
      }

      System.out.println("Trimming " + start + " " + (pwm.length/4-end) + " " + pwm.length/4 + "\t" + PwmCluster.getConsensus(pwm));

      if (end > 0) {
	pwm = trimPwmEnd(pwm,pwm.length/4-end);
      }
      System.out.println("Trimming " + start + " " + (pwm.length/4-end) + " " + pwm.length/4 + "\t" + PwmCluster.getConsensus(pwm));
      if (start > 0) {
	pwm = trimPwmStart(pwm, start-1);
      }
      System.out.println("Pwm " + pwm.length);
      return pwm;
  }

    
  public double getInformationContent() {
      int i = 0;
      double inf = 0;    
      while (i < pwm.length/4) {


	int j = 0;

	while (j < 4) {

	  if (pwm[i*4+j] > 0) {
	    inf += pwm[i*4+j] * Math.log(pwm[i*4+j])/Math.log(2);
	  }
	  j++;
	}

	inf = 2 + inf;
	i++;
      }
      return inf;
  }
  public int getLength() {
    return pwm.length/4;
  }

  public double[] getLogOdds() {

    //if (lods == null) {

      lods = new double[pwm.length];

      int i = 0;
    
      while (i < pwm.length/4) {

	double inf = 0;
	int j = 0;

	while (j < 4) {

	  if (pwm[i*4+j] > 0) {
	    inf += pwm[i*4+j] * Math.log(pwm[i*4+j])/Math.log(2);
	  }
	  j++;
	}

	inf = (2 + inf)/2;

	//	System.out.println("Inf " + inf);

	j = 0;

	while (j < 4) {
	  
	  if (pwm[i*4+j] > 0) {

	      lods[i*4+j] = (Math.log(pwm[i*4+j]/0.24)/Math.log(2))*inf;
	    //System.out.println("pwm  " + pwm[i*4+j] + " " + lods[i*4+j]);
	  } else {
	      lods[i*4+j] = (Math.log(.0001)/Math.log(2))*inf;
	  }
	  j++;
	}
	i++;
      }
      //}
    return lods;
  }

  public double getLogScore(Pwm newpwm) {
    // i = position
    // j = base

    // Logscorei =  log2 ( pij/bj )
    
    // For a complete sequence  the odds score is a product of the base scores which
    // is a sum of the log2 scores
    
    // Logscore  = SUMi log2 (pij/bj)

      double score = 0;
      int    i     = 0;

      double[] pwmarr = newpwm.getPwm();
      
      if (pwmarr.length/4 != pwm.length/4) {
        System.out.println("Input sequence for getLogScore must equals motif length [" + pwmarr.length/4 + "][" + pwm.length/4 + "]");
        return 0;
      }
      double log2 = Math.log(2);

      double gc = 0.2;
      double at = 0.3;

      int len = pwm.length/4;

      //System.out.println("Consensi are " + getName() + "\t" + getConsensus() + "\t" +  newpwm.getName() + "\t" + PwmCluster.getConsensus(pwmarr));
      //printLogo(pwmarr);
      //System.out.println();
      //printLogo(pwm);
      while (i < len) {

	double tmpscore = 0;

	//	tmpscore += pwmarr[i*4+0] * Math.log(pwm[i*4+0]/at);
	//tmpscore += pwmarr[i*4+1] * Math.log(pwm[i*4+1]/at);
	//tmpscore += pwmarr[i*4+2] * Math.log(pwm[i*4+2]/gc);
	//tmpscore += pwmarr[i*4+3] * Math.log(pwm[i*4+3]/gc);

	tmpscore += pwmarr[i*4+0] * logpwm[i*4+0];//Math.log(pwm[i*4+0]/at);
	tmpscore += pwmarr[i*4+1] * logpwm[i*4+1];//Math.log(pwm[i*4+0]/at);
	tmpscore += pwmarr[i*4+2] * logpwm[i*4+2];//Math.log(pwm[i*4+0]/at);
	tmpscore += pwmarr[i*4+3] * logpwm[i*4+3];//Math.log(pwm[i*4+0]/at);


	score += tmpscore;
	
        i++;
      }

      return score/len;
  }

  public double getLogScore(char[] seqarr) {
    // i = position
    // j = base

    // Logscorei =  log2 ( pij/bj )

    // For a complete sequence  the odds score is a product of the base scores which
    // is a sum of the log2 scores

    // Logscore  = SUMi log2 (pij/bj)


    double score = 0;

    int i = 0;



    if (seqarr.length != pwm.length/4) {
      System.out.println("Input sequence for getLogScore must equals motif length [" + seqarr.length + "][" + pwm.length/4);
      return 0;
    }
    double log2 = Math.log(2);

    double gc = 0.2;
    double at = 0.3;

    while (i < pwm.length/4) {
      
      if (seqarr[i] == 'A') {
        score += Math.log(pwm[i*4 + 0]/at)/log2;
      } else if (seqarr[i] == 'T') {
        score += Math.log(pwm[i*4 + 1]/at)/log2;
      } else if (seqarr[i] == 'C') {
        score += Math.log(pwm[i*4 + 2]/gc)/log2;
      } else if (seqarr[i] == 'G') {
        score += Math.log(pwm[i*4 + 3]/gc)/log2;
      }
      i++;
    }

    double normscore = (score - getMinLogScore())/(getMaxLogScore()- getMinLogScore());




    
    return normscore;
  }

  public double getLogScore(String seq) {
    char[] seqarr = seq.toUpperCase().toCharArray();

    return getLogScore(seqarr);
  }

    public double getMaxLogScore() {

      if (maxscore == -1) {
	// pwm[1a 1t 1c 1g 2a 2t 2c 2g....]
	
	// the maximum score is the sum of the maximum values at each position
	//print(getPwm());
	//printLogo(getPwm());
	
	double tot = 0;
	
	int i = 0;
	int maxindex = -1;
	
	while (i < pwm.length/4) {
	  double max = pwm[i*4]/at;
	  maxindex = 0;
	  
	  if (pwm[i*4 + 1]/at > max) {
	    max = pwm[i*4 + 1]/at;
	    maxindex = 1;
	  } 
	  if (pwm[i*4 + 2]/gc > max) {
	    max = pwm[i*4 + 2]/gc;
	    maxindex = 2;
	  } 
	  if (pwm[i*4 + 3]/gc > max) {
	    max = pwm[i*4 + 3]/gc;
	    maxindex = 3;
	  } 
	  tot += Math.log(max)/Math.log(2);
	  
	  i++;
	}
	maxscore = tot;
	
      }
      return maxscore;
      
    }

    public double getMinLogScore() {
		  if (minscore == -1) {
		    // pwm[1a 1t 1c 1g 2a 2t 2c 2g....]
		    
		    
		    // the minimum score is the sum of the minimum values at each position
		    
		    double tot = 0;
		    
		    int i = 0;
		    
		    while (i < pwm.length/4) {
		double min = pwm[i*4];
		
		if (pwm[i*4 + 1] < min) {
		  min = pwm[i*4 + 1];
		} 
		if (pwm[i*4 + 2] < min) {
		  min = pwm[i*4 + 2];
		} 
		if (pwm[i*4 + 3] < min) {
		  min = pwm[i*4 + 3];
		} 

		tot += Math.log(min/0.25)/Math.log(2);
		i++;
		    }
		    minscore = tot;
		  }
		  return minscore;

		}
	
    public String getName() {
		  return name;
		}

    public double[]  getPwm() {
		  return pwm;
		}

    public double[] getRevPwm() {
      int i = 0;
      
      double[] revmat = new double[pwm.length];
      int len = pwm.length/4;
      
      while (i < len) {
        
        // 0 -> 1
        // 1 -> 0
        // 2 -> 3
        // 3 -> 2
        
        revmat[(len-i-1)*4 + 0] = pwm[i*4 + 1];
        revmat[(len-i-1)*4 + 1] = pwm[i*4 + 0];
        revmat[(len-i-1)*4 + 2] = pwm[i*4 + 3];
        revmat[(len-i-1)*4 + 3] = pwm[i*4 + 2];
        i++;
        
      }
      
      return revmat;
    }
    public Pwm reverse() {
      double[] revpwm = getRevPwm();
      
      return new Pwm(revpwm,name);
    }

  public double scoreLogOdds(double[] pwm2) {

	  double[] lods = getLogOdds();

	  double score = 0;

	  int i = 0;
	  
	  while (i < lods.length) {
score += pwm2[i]*lods[i];
i++;
	  }

	  return score;
	}

  public void setChrRegion(ChrRegion r) {
    this.region = r;
  }


  public void setName(String name) {
    this.name = name;
	}

  public void setPwm(double[] pwm) {
    double[] logpwm = new double[pwm.length];

    int i = 0; 
    while (i < pwm.length) {
      if (pwm[i] == 0) {
	pwm[i] = 1e-6;
      }
      i++;
    }
    i = 0;

    double at = 0.3;
    double gc = 0.2;

    while (i < pwm.length/4) {
      logpwm[i*4+0] = Math.log(pwm[i*4+0]/at)/Math.log(10);
      logpwm[i*4+1] = Math.log(pwm[i*4+1]/at)/Math.log(10);
      logpwm[i*4+2] = Math.log(pwm[i*4+2]/gc)/Math.log(10);
      logpwm[i*4+3] = Math.log(pwm[i*4+3]/gc)/Math.log(10);
      i++;
    }
    this.logpwm = logpwm;
    this.pwm = pwm;
  }
  
  public static double[] trimPwmEnd(double[] pwm, int num) {
    
    double[] newpwm = new double[pwm.length - (num * 4)];

    int i = 0;

    while (i < (pwm.length/4 - num )) {
      int j = 0;
      while (j < 4) {
	newpwm[i*4+j] = pwm[i*4+j];
	j++;
      }
      i++;
    }

    return newpwm;
  }

  public static double[] trimPwmStart(double[] pwm, int num) {

    double[] newpwm = new double[pwm.length - (num * 4)];
    int i = num;

    while (i < pwm.length/4) {
      int j = 0;
      while (j < 4) {
	newpwm[(i-num)*4 + j] = pwm[i*4 + j];
	j++;
      }
      i++;
    }
    return newpwm;
  }

  public static double[] extendPwmEnd(double[] pwm, int num) {

    double[] newpwm = new double[pwm.length + num * 4];

    int i = 0;

    while (i < pwm.length) {
      newpwm[i] = pwm[i];
      i++;
    }

    while (i < newpwm.length) {
      newpwm[i] = 0.25;
      i++;

    }
    return newpwm;
  }

  public static double[] extendPwmStart(double[] pwm, int num) {
    double[] newpwm = new double[pwm.length + num * 4];

    int i = 0;

    while (i < num * 4) {

      newpwm[i] = 0.25;
      i++;
    }

    while (i < newpwm.length) {
      newpwm[i] = pwm[i - num * 4];
      i++;
    }
    return newpwm;
  }

  public static double[] extendPwm(double[] pwm, int num) {

    double[] newpwm = new double[pwm.length + num * 8];

    int i = 0;

    while (i < num) {
      int j = 0;

      while (j < 4) {
        newpwm[i * 4 + j] = 0.25;
        j++;
      }
      i++;
    }

    i = 0;

    while (i < pwm.length / 4) {
      int j = 0;
      while (j < 4) {
        newpwm[num * 4 + i * 4 + j] = pwm[i * 4 + j];
        j++;
      }
      i++;
    }

    i = 0;

    while (i < num) {
      int j = 0;

      while (j < 4) {
        newpwm[num * 4 + pwm.length + i * 4 + j] = 0.25;
        j++;
      }
      i++;
    }
    return newpwm;
  }

  public double[] trim(int start, int end) {
    double[] newpwm = new double[(end-start+1)*4];

    int i = 0;
    int len = end-start+1;

    while (i < len) {
      newpwm[i*4 + 0] = pwm[(i+start)*4 + 0];
      newpwm[i*4 + 1] = pwm[(i+start)*4 + 1];
      newpwm[i*4 + 2] = pwm[(i+start)*4 + 2];
      newpwm[i*4 + 3] = pwm[(i+start)*4 + 3];
      i++;
    }

    return newpwm;
  }

  public double[] revComp() {

    double[] newpwm = new double[pwm.length];

    int i = 0;

    while (i < pwm.length / 4) {
      newpwm[i * 4] = pwm[pwm.length - (i * 4) - 4];
      newpwm[i * 4 + 1] = pwm[pwm.length - (i * 4) - 3];
      newpwm[i * 4 + 2] = pwm[pwm.length - (i * 4) - 2];
      newpwm[i * 4 + 3] = pwm[pwm.length - (i * 4) - 1];
      i++;
    }

    i = 0;

    while (i < pwm.length / 4) {

      // A T C G
      // T A G C

      double tmpa = newpwm[i * 4];
      double tmpt = newpwm[i * 4 + 1];
      double tmpc = newpwm[i * 4 + 2];
      double tmpg = newpwm[i * 4 + 3];

      newpwm[i * 4] = tmpt; // A = T
      newpwm[i * 4 + 1] = tmpa; // C = G
      newpwm[i * 4 + 2] = tmpg; // T = A
      newpwm[i * 4 + 3] = tmpc; // G = C

      i++;
    }

    return newpwm;
  }



}




