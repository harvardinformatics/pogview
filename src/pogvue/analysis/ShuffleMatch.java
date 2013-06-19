package pogvue.analysis;

import pogvue.io.*;
import pogvue.datamodel.*;
import pogvue.datamodel.motif.*;

import java.util.*;

public class ShuffleMatch {

  public static double[] shuffle(double[] mat){

    int len = mat.length/4;

    int[] done = new int[len];
    double[] newmat = new double[mat.length];

    int i = 0;

    while (i < len) {
      done[i] = 0;
      i++;
    }

    i = 0;

    while (i < len) {
      int r = (int)(Math.random()*len);

      while (done[r] == 1) {
	r = (int)(Math.random()*len);

      }
      newmat[i*4 + 0] = mat[r*4 + 0];   
      newmat[i*4 + 1] = mat[r*4 + 1];   
      newmat[i*4 + 2] = mat[r*4 + 2];   
      newmat[i*4 + 3] = mat[r*4 + 3];   

      done[r] = 1;
      
      i++;
    }
    return newmat;
  }
    public static double get(double x[], double y[]) {

	double sum_sq_x = 0.0;
	double sum_sq_y = 0.0;
	double sum_coproduct = 0.0;
	double mean_x = x[2];
	double mean_y = y[2];

        int len = x.length;

	if (y.length < x.length) {
	  len = y.length;
	  }

        len -= 2;
	for (int i = 3; i < len; i++) {
	    double sweep = (i - 1.0) / i;
	    double delta_x = x[i] - mean_x;
	    double delta_y = y[i] - mean_y;
	    sum_sq_x += delta_x * delta_x * sweep;
	    sum_sq_y += delta_y * delta_y * sweep;
	    sum_coproduct += delta_x * delta_y * sweep;
	    mean_x += delta_x / i;
	    mean_y += delta_y / i;
	}
	double pop_sd_x = Math.sqrt( sum_sq_x / len);
	double pop_sd_y = Math.sqrt( sum_sq_y / len );
	double cov_x_y = sum_coproduct / len;
	return cov_x_y / (pop_sd_x * pop_sd_y);
    }

    public static double[] shift(double[] seq, int num) {
	double[] newseq = new double[seq.length + num*4];

	int i = 0;

	while (i < num) {
	    newseq[i*4]   = 0.25;
	    newseq[i*4+1] = 0.25;
	    newseq[i*4+2] = 0.25;
	    newseq[i*4+3] = 0.25;
	    i++;
	}
	i = i * 4;
	while (i < seq.length+num) {
	    newseq[i] = seq[i-num*4];
	    i++;
	}
	return newseq;
    }

    public static Hashtable get_with_shift(double[] vec1, double[] vec2,double thresh) {
	double[] off1 = Correlation4.shift(vec1,1);
	double[] off2 = Correlation4.shift(vec1,2);
	double[] off3 = Correlation4.shift(vec2,1);
	double[] off4 = Correlation4.shift(vec2,2);
	
	double corr =  Correlation4.get(vec1,vec2);
	double corr1 = Correlation4.get(vec2,off1);
	double corr2 = Correlation4.get(vec2,off1);
	double corr3 = Correlation4.get(vec1,off3);
	double corr4 = Correlation4.get(vec1,off4);
		    

	double maxcorr = corr;
	int    maxstr  = 0;
	
	if (corr1 > corr) {
	    if (corr2 > corr1) {
		maxstr = 2;
		maxcorr = corr2;
	    } else {
		maxstr = 1;
		maxcorr = corr1;
	    }
	}
	
	if (corr3 > maxcorr) {
	    if (corr4 > corr3) {
		maxstr = 4;
		maxcorr = corr4;
	   } else {
		maxstr = 3;
		maxcorr = corr3;
	    }
	}

	Hashtable out = new Hashtable();
	out.put("maxcorr",new Double(maxcorr));	
	if (maxstr == 1) {
	    //seq = "-" + seq; 
	    out.put("maxpwm",off1);
	} else if (maxstr == 2) {
	    //seq = "--" + seq;
	    out.put("maxpwm",off2);
	} else if (maxstr == 3) {
	    // seq = seq.substring(1) + "-";
	    double [] maxpwm = Correlation4.extendPwmEnd(vec1,1);
	    maxpwm = Pwm.trimPwmStart(maxpwm,1);

	    out.put("maxpwm",maxpwm);
	} else if (maxstr == 0) {
	//    seq = "--" + seq + "--";
	    out.put("maxpwm",vec1);
	} else if (maxstr == 4) {
	    // seq = seq.substring(2) + "--";
	    double[] maxpwm = Correlation4.extendPwmEnd(vec1,2);
	    maxpwm = Pwm.trimPwmStart(maxpwm,2);

	    out.put("maxpwm",maxpwm);
	}

	return out;
    }


    public static double[] trimPwmEnd(double[] pwm, int num) {

	double[] newpwm = new double[pwm.length - num*4];

	int i = 0;

	while (i < pwm.length - num*4) {

	    newpwm[i] = pwm[i];
	    i++;
	}

	return pwm;
    }

    public static double[] trimPwmStart(double[] pwm, int num) {

	double [] newpwm = new double[pwm.length - num*4];

	int i = 0;

	while (i < pwm.length - num*4) {

	    newpwm[i] = pwm[i + num*4];
	    i++;
	}
	return newpwm;
    }
    
    public static double[] extendPwmEnd(double[] pwm, int num) {

	double [] newpwm = new double[pwm.length + num*4];

	int i = 0;

	while (i < pwm.length) { 
	    newpwm[i] = pwm[i];
	    i++;
	}

	while ( i < newpwm.length) {
	    newpwm[i] = 0.25;
	    i++;

	}
	return newpwm;
    }
    public static double[] extendPwmStart(double[] pwm, int num) {
	double[] newpwm = new double[pwm.length + num*4];

	int i = 0;

	while (i < num*4) {

	    newpwm[i] = 0.25;
	    i++;
	}

	while (i < newpwm.length) {
	    newpwm[i] = pwm[i - num*4];
	    i++;
	}
	return newpwm;
    }

    public static double[] extendPwm(double[] pwm, int num) {

	double[] newpwm = new double[pwm.length + num*8];

	int i = 0;

	while (i < num) {
	    int j = 0;

	    while (j < 4) {
		newpwm[i*4+j] = 0.25;
		j++;
	    }
	    i++;
	}

	i = 0;

	while (i < pwm.length/4) {
	    int j = 0;
	    while (j < 4) {
		newpwm[num*4 + i*4 + j] = pwm[i*4+j];
		j++;
	    }
	    i++;
	}

	i = 0;

	while (i < num) {
	    int j = 0;

	    while (j < 4) {
		newpwm[num*4 + pwm.length + i*4+j] = 0.25;
		j++;
	    }
	    i++;
	}
	return newpwm;
    }
	
			   
    public static double[] revcompPwm(double[] pwm) {

	double[] newpwm = new double[pwm.length];

	//Correlation3.printPWM(pwm,false);
	//Pwm.printLogo(pwm);
	//System.out.println();
	int i = 0;

	while (i < pwm.length/4) {
	    newpwm[i*4]   = pwm[pwm.length - (i*4) - 4];
	    newpwm[i*4+1] = pwm[pwm.length - (i*4) - 3];
	    newpwm[i*4+2] = pwm[pwm.length - (i*4) - 2];
	    newpwm[i*4+3] = pwm[pwm.length - (i*4) - 1];
	    i++;
	}

	i = 0;

	//Correlation3.printPWM(newpwm,false);
	//Pwm.printLogo(newpwm);
	//System.out.println();
	while (i < pwm.length/4) {

	    // A T C G
	    // T A G C

	    double tmpa = newpwm[i*4];
	    double tmpt = newpwm[i*4+1];
	    double tmpc = newpwm[i*4+2];
	    double tmpg = newpwm[i*4+3];

	    newpwm[i*4]   = tmpt;          // A = T
	    newpwm[i*4+1] = tmpa;          // C = G
	    newpwm[i*4+2] = tmpg;          // T = A
	    newpwm[i*4+3] = tmpc;          // G = C

	    i++;
	}

	//Correlation3.printPWM(newpwm,false);
	//Pwm.printLogo(newpwm);
	//System.out.println();
	return newpwm;
    }
    public static void main(String[] args) {

	try {
	    OrmatFile   pf     = null;

	    double    thresh = Double.valueOf(args[1]);
	    String    name   = args[2];

	    pf = new OrmatFile(args[0],"File");
	    pf.parse();

	    Vector    clus   = new Vector();
	    Vector    mats   = pf.getMatrices();


	    int maxlen = 0;

	    TFMatrix shuffmat = null;

	    for (int i = 0; i < mats.size(); i++) {

	      TFMatrix tfm = (TFMatrix)mats.elementAt(i);

	      if (tfm.getName().indexOf(name) == 0) {

		shuffmat = tfm;
	      }
	      int len = tfm.getPwm().getPwm().length/4;

	      if (len > maxlen) {
		maxlen = len;
	      }
	    }
	    if (shuffmat == null) {
	      System.out.println("Can't find name " + name);
	      System.exit(0);
	    }

	    Vector newmats  = new Vector();
	    
	    int num = Integer.valueOf(args[3]);

	    int x= 0;
	    while (x  < num) {
	      int      shufflen = shuffmat.getPwm().getPwm().length/4;
	      int      shuffdiff = maxlen - shufflen + 2;
	      double[] newshuff = ShuffleMatch.shuffle(shuffmat.getPwm().getPwm());
	      
	      newshuff = Correlation4.extendPwm(newshuff,shuffdiff/2);
	      
	      TFMatrix shufftf   = new TFMatrix(newshuff,newshuff.length/4,4);
	      
	      shufftf.setAcc(shuffmat.getAcc());
	      shufftf.setId(shuffmat.getId());
	      shufftf.setName(shuffmat.getName());      
	      shufftf.setDesc(shuffmat.getDesc());
	      shufftf.setConsensus(Pwm.getConsensus(shuffmat.getPwm().getPwm()));
	      
	      
	      for (int i = 0; i < mats.size(); i++) {
		TFMatrix tfm = (TFMatrix)mats.elementAt(i);
		
		int len = tfm.getPwm().getPwm().length/4;
		
		int diff = maxlen - len + 2;
		
		if (diff % 2 == 0) {
		  double[] newpwm = Correlation4.extendPwm(tfm.getPwm().getPwm(),diff/2);
		  
		  TFMatrix tf = new TFMatrix(newpwm,newpwm.length/4,4);
		  
		  tf.setAcc(tfm.getAcc());
		  tf.setId(tfm.getId());
		  tf.setName(tfm.getName());      
		  tf.setDesc(tfm.getDesc());
		  tf.setConsensus(Pwm.getConsensus(tf.getPwm().getPwm()));
		  
		  //System.out.println("Name is " + tfm.getName() + " " + tfm.getPwm().getName());
		  newmats.addElement(tf);
		} else {
		  double[] newpwm = Correlation4.padPwm(tfm.getPwm().getPwm(),(diff-1)/2,(diff-1)/2 + 1);
		  
		  TFMatrix tf = new TFMatrix(newpwm,newpwm.length/4,4);
		  
		  tf.setAcc(tfm.getAcc());
		  tf.setId(tfm.getId());      
		  tf.setName(tfm.getName());      
		  
		  tf.setDesc(tfm.getDesc());
		  tf.setConsensus(Pwm.getConsensus(tf.getPwm().getPwm()));
		  //System.out.println("Name is " + tfm.getName() + " " + tfm.getPwm().getName());
		  newmats.addElement(tf);
		  
		}
	      }
	      System.out.println("Shuffled mat " + PwmCluster.getConsensus(shuffmat.getPwm().getPwm()) + "\t" + PwmCluster.getConsensus(shufftf.getPwm().getPwm()));

	      for (int i = 0; i < newmats.size(); i++) {
		TFMatrix tfm = (TFMatrix)newmats.elementAt(i);
		Pwm pwm  = tfm.getPwm();
		
		double [] pwm1 = pwm.getPwm();
		double [] pwm2 = shufftf.getPwm().getPwm();
		
		pwm1 = Correlation4.extendPwm(pwm1,2);
		pwm2 = Correlation4.extendPwm(pwm2,2);
		
		Hashtable res = Correlation4.get_with_shift(pwm1,pwm2,thresh);
		
		double maxcorr = ((Double)res.get("maxcorr")).doubleValue();
		double[] maxpwm  = (double[])res.get("maxpwm");
		String newseq  = (String)res.get("seq");
		
		double[] revpwm1 = Correlation4.revcompPwm(pwm1);
		double[] revpwm2 = Correlation4.revcompPwm(pwm2);
		
		//System.out.println("Reversed pwm " + PwmCluster.getConsensus(revpwm1) + " " + PwmCluster.getConsensus(pwm1));
		//System.out.println("Reversed pwm " + PwmCluster.getConsensus(revpwm2) + " " + PwmCluster.getConsensus(pwm2));
		
		Hashtable revres = Correlation4.get_with_shift(revpwm1,pwm2,thresh);
		double revmaxcorr = ((Double)revres.get("maxcorr")).doubleValue();
		
		if (revmaxcorr > maxcorr) {
		  maxcorr = revmaxcorr;
		  maxpwm  = (double[])revres.get("maxpwm");
		}
		
		if (maxcorr > thresh) {
		  System.out.println("Found match " + tfm.getName() + "\t" + PwmCluster.getConsensus(tfm.getPwm().getPwm()) + "\t" + PwmCluster.getConsensus(pwm2) + "\t" + maxcorr);
		}
	      }
	      x++;
	    }
	} catch (Exception e) {
	  System.out.println("Exception " + e);
	  e.printStackTrace();
	  
	}
	
    }
    
    public static double[] seqvec(Sequence seq) {
	
	double seqvec[] = new double[seq.getLength()*4];
	
	for (int j = 0; j < seq.getLength(); j++) {
	    char c = seq.getCharAt(j);
	    
	    seqvec[j*4] = 0;
	    seqvec[j*4+1] = 0;
	    seqvec[j*4+2] = 0;
	    seqvec[j*4+3] = 0;
	    if (c == 'A') {
		seqvec[j*4] = 1;
	    } else if (c == 'T') {
		seqvec[j*4+1] = 1;
	    } else if (c == 'C') {
		seqvec[j*4+2] = 1;
	    } else if (c == 'G') {
		seqvec[j*4+3] = 1;
	    } else if (c == '-') {
	        seqvec[j*4] = 0.25;
		seqvec[j*4+1] = 0.25;
		seqvec[j*4+2] = 0.25;
		seqvec[j*4+3] = 0.25;
		}
	}
	return seqvec;
    }
    public static String vec2seq(double[] vec) {

	StringBuffer str = new StringBuffer();

	int i = 0;
	
	while (i < vec.length/4) {
	    int max = maxOfFour(vec,i);

	    if (i == 0) {
		str.append("A");
	    } else if (i == 1) {
		str.append("T");
	    } else if (i == 2) {
		str.append("C");
	    } else if (i ==1) {
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
	    if (vec[offset+i] >= max) {
		max = vec[offset+i];
		maxindex = i;
	    }
	    i++;
	}
	return maxindex;
    }
    public static void printPWM(double[] pwm, boolean oneline) {

	int i = 0;

	while (i < pwm.length/4) {
	    int j = 0;

	    while (j < 4) {
		System.out.printf("%10.2f\t",pwm[i*4+j]);
		j++;
	    }
	    if (oneline == false) {
		System.out.println();
	    }
	    i++;
	}
	
    }
}
