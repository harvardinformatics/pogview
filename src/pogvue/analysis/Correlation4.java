package pogvue.analysis;

import pogvue.io.*;
import pogvue.util.*;
import pogvue.datamodel.*;
import pogvue.datamodel.motif.*;
import pogvue.datamodel.comparer.*;

import java.util.*;

public class Correlation4 {
  Vector clus;
  int    len;
  int    win;
  double thresh;

  public Correlation4(int len, int win, double thresh) {
    this.len = len;
    this.win = win;
    this.thresh = thresh;

    clus = new Vector();
  }

  public static double[] shift(double[] seq, int num) {
    double[] newseq = new double[seq.length + num * 4];

    int i = 0;

    while (i < num) {
      newseq[i * 4] = 0.25;
      newseq[i * 4 + 1] = 0.25;
      newseq[i * 4 + 2] = 0.25;
      newseq[i * 4 + 3] = 0.25;
      i++;
    }
    i = i * 4;
    while (i < seq.length + num) {
      newseq[i] = seq[i - num * 4];
      i++;
    }
    return newseq;
  }

  public static Hashtable get_with_shift(double[] vec1, double[] vec2,
      double thresh) {
    double[] off1 = Correlation4.shift(vec1, 1);
    double[] off2 = Correlation4.shift(vec1, 2);
    double[] off3 = Correlation4.shift(vec2, 1);
    double[] off4 = Correlation4.shift(vec2, 2);

    double corr = Correlation4.get(vec1, vec2);
    double corr1 = Correlation4.get(vec2, off1);
    double corr2 = Correlation4.get(vec2, off1);
    double corr3 = Correlation4.get(vec1, off3);
    double corr4 = Correlation4.get(vec1, off4);

    double maxcorr = corr;
    int maxstr = 0;

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
    out.put("maxcorr", new Double(maxcorr));
    if (maxstr == 1) {
      // seq = "-" + seq;
      out.put("maxpwm", off1);
    } else if (maxstr == 2) {
      // seq = "--" + seq;
      out.put("maxpwm", off2);
    } else if (maxstr == 3) {
      // seq = seq.substring(1) + "-";
      double[] maxpwm = Correlation4.extendPwmEnd(vec1, 1);
      maxpwm = Pwm.trimPwmStart(maxpwm, 1);

      out.put("maxpwm", maxpwm);
    } else if (maxstr == 0) {
      // seq = "--" + seq + "--";
      out.put("maxpwm", vec1);
    } else if (maxstr == 4) {
      // seq = seq.substring(2) + "--";
      double[] maxpwm = Correlation4.extendPwmEnd(vec1, 2);
      maxpwm = Pwm.trimPwmStart(maxpwm, 2);

      out.put("maxpwm", maxpwm);
    }

    return out;
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

  public void addPwm(Pwm pwm) {

    double[] pwm1 = pwm.getPwm();

    //System.out.println("Consensus pwm1 " + PwmCluster.getConsensus(pwm1));

    int found = 0;

    double maxcorr = 0;
    double[] maxpwm = null;
    int maxcoord = 0;
    int maxstrand = 0;
    String maxname;

    PwmCluster maxclus = null;

    double inf1 = pwm.getInformationContent();
    for (int j = 0; j < clus.size(); j++) {
      PwmCluster c = (PwmCluster) clus.elementAt(j);
      Pwm t = new Pwm(c.getPwm(),"Pog");
      double inf2 = t.getInformationContent();
      double[] pwm2 = c.getPwm();

      int len = pwm2.length/4;

      win = pwm1.length/4;
      //System.out.println("Length " + len + " " + win + " " + pwm1.length);

      double diff = (inf1-inf2);
      if (diff < 0) {
	diff = -diff;
      }
      if (diff < 10) {
	//System.out.println("Comparing " + PwmCluster.getConsensus(pwm1) + " to " + PwmCluster.getConsensus(pwm2) + " " + inf1 + " " + inf2);
	int start = (len-win)/2 - 2;
	int end   = start+4;;

	
	if (start < 0) {start = 0;}
	if (end   > len-win) {end = len-win;}
	  
	for (int k = start; k < end; k++) {
	  double[] winpwm = Correlation4.substring(pwm2, k, k + win);
	  double tmpcorr = Correlation4.get(winpwm, pwm1);
	  
	  //System.out.println("Strings\t" + PwmCluster.getConsensus(winpwm) + "\t" + PwmCluster.getConsensus(pwm1) + "\t" + tmpcorr);
	  if (tmpcorr > thresh && tmpcorr >= maxcorr) {
	    maxcorr     = tmpcorr;
	    maxpwm      = winpwm;
	    maxcoord    = k;
	    maxstrand   = 1;
	    maxclus     = c;
	    found       = 1;
	    
	    k = len - win;
	    j = clus.size();
	    
	    System.out.println("Found forward corr " + tmpcorr + " "   + PwmCluster.getConsensus(maxpwm) + " " + PwmCluster.getConsensus(pwm1));
	  }
	  
	  winpwm = Correlation4.revcompPwm(winpwm);
	  
	  tmpcorr = Correlation4.get(winpwm, pwm2);
	  
	  if (tmpcorr > thresh && tmpcorr >= maxcorr) {
	    maxcorr    = tmpcorr;
	    maxpwm     = winpwm;
	    maxcoord   = k;
	    maxstrand  = -1;
	    maxclus    = c;
	    found      = 1;
	    
	    k = len - win;
	    j = clus.size();
	    
	    System.out.println("Found reverse corr " + maxcorr + " " + PwmCluster.getConsensus(maxpwm) + " " + PwmCluster.getConsensus(pwm1));
	  }
	}
      } else {
	///System.out.println ("Skipping - difference too big " + inf1 + " " + inf2);
      }
    }

    if (found == 1) {
      System.out.println("window  cons " + maxclus.getName() + " " + PwmCluster.getConsensus(maxpwm));

      Pwm tmppwm = new Pwm(maxpwm, pwm.getName());

      // Change the coords depending on k
      if (pwm.getChrRegion() != null) {
        ChrRegion reg = pwm.getChrRegion();
        ChrRegion oldreg = new ChrRegion(reg.getChr(), reg.getStart(), reg
            .getEnd(), 1);

        if (maxstrand == 1) {
          reg.setStart(reg.getStart() - ((len - win) / 2) + maxcoord);
          reg.setEnd(reg.getEnd() - ((len - win) / 2) + maxcoord);
        } else {
          reg.setStart(reg.getStart() - ((len - win) / 2) + maxcoord);
          reg.setEnd(reg.getEnd() - ((len - win) / 2) + maxcoord);
        }

        tmppwm.setChrRegion(reg);
      }
      
      // Pad the ends to match the cluster length   - maxcoord is k
      //    ------------------------------------
      //    <   k       >#########< len-k-pwmlen>

      pwm1 = padPwm(pwm1,maxcoord,maxclus.getPwm().length/4-pwm1.length/4-maxcoord);
      //      System.out.println("Padded " + PwmCluster.getConsensus(pwm1) );
      //System.out.println("Padded " + PwmCluster.getConsensus(maxclus.getPwm()) );

      tmppwm.setPwm(pwm1);

      maxclus.addPwm(tmppwm);

    } else if (found == 0) {
      // System.out.println("Len " + len + " " + pwm1.length/4);
      //double[] newpwm1 = Correlation4.substring(pwm1, (len - win) / 2,
      //    (len - win) / 2 + win);

      System.out.println("Making new cluster");

      double[] tmpp = padPwm(pwm1,10,10);
      Pwm tmppwm = new Pwm(tmpp, pwm.getName());
      tmppwm.setChrRegion(pwm.getChrRegion());
      PwmCluster newclus = new PwmCluster(tmppwm);

      clus.addElement(newclus);
    }

  }

  public static double get_inf(double x[], double y[], double thresh) {

    double inf = 0.0;
    int xstart = -1;

    while (inf <= thresh) {
      xstart += 1;

      int j = 0;
      while (j < 4) {
        if (x[xstart * 4 + j] > 0) {
          inf += x[xstart * 4 + j] * Math.log(x[xstart * 4 + j]) / Math.log(2);
        }
        j++;
      }

      inf += 2;
      // System.out.println("Inf " + inf + " " + xstart + " " + thresh);

    }

    // System.out.println("Final inf " + inf + " " + xstart);

    double sum_sq_x = 0.0;
    double sum_sq_y = 0.0;
    double sum_coproduct = 0.0;
    double mean_x = x[xstart * 4];
    double mean_y = y[xstart * 4];

    int len = x.length / 4;
    int num = 1;

    int j = 1;

    while (j < 4) {

      double sweep = (num - 1.0) / num;
      double delta_x = x[xstart * 4 + j] - mean_x;
      double delta_y = y[xstart * 4 + j] - mean_y;
      sum_sq_x += delta_x * delta_x * sweep;
      sum_sq_y += delta_y * delta_y * sweep;
      sum_coproduct += delta_x * delta_y * sweep;
      mean_x += delta_x / num;
      mean_y += delta_y / num;

      // System.out.println("Num " + num + " " + mean_x);

      num++;
      j++;
    }

    if (y.length < x.length) {
      len = y.length / 4;
    }

    for (int i = xstart + 1; i < len; i++) {

      // First the inf.

      inf = 0.0;

      j = 0;
      while (j < 4) {
        if (x[i * 4 + j] > 0) {
          inf += x[i * 4 + j] * Math.log(x[i * 4 + j]) / Math.log(2);
        }
        j++;
      }

      inf += 2;

      // System.out.println("Int inf " + inf + " " + num);

      if (inf >= thresh) {
        j = 0;
        while (j < 4) {
          double sweep = (num - 1.0) / num;
          double delta_x = x[i * 4 + j] - mean_x;
          double delta_y = y[i * 4 + j] - mean_y;
          sum_sq_x += delta_x * delta_x * sweep;
          sum_sq_y += delta_y * delta_y * sweep;
          sum_coproduct += delta_x * delta_y * sweep;
          mean_x += delta_x / num;
          mean_y += delta_y / num;

          // System.out.println("Num " + num + " " + mean_x + " " + i + " " +
          // len);

          num++;
          j++;
        }
      }
    }
    // System.out.println("Means inf " + mean_x + " " + mean_y + " " + num);
    double pop_sd_x = Math.sqrt(sum_sq_x / num);
    double pop_sd_y = Math.sqrt(sum_sq_y / num);
    double cov_x_y = sum_coproduct / (num);

    // System.out.println("Cov inf  " + cov_x_y + " " + pop_sd_x + " " +
    // pop_sd_y);
    if (num > 4) {
      return cov_x_y / (pop_sd_x * pop_sd_y);
    } else {
      return 0;
    }
  }

  public static double get(double x[], double y[]) {

    double sum_sq_x = 0.0;
    double sum_sq_y = 0.0;
    double sum_coproduct = 0.0;
    double mean_x = x[0];
    double mean_y = y[0];

    int len = x.length;

    if (y.length < x.length) {
      len = y.length;
    }

    for (int i = 1; i < len; i++) {
      double sweep = (i - 1.0) / i;
      double delta_x = x[i] - mean_x;
      double delta_y = y[i] - mean_y;
      sum_sq_x += delta_x * delta_x * sweep;
      sum_sq_y += delta_y * delta_y * sweep;
      sum_coproduct += delta_x * delta_y * sweep;
      mean_x += delta_x / i;
      mean_y += delta_y / i;

    }

    // System.out.println("Means orig " + mean_x + " " + mean_y + " " + len);
    double pop_sd_x = Math.sqrt(sum_sq_x / len);
    double pop_sd_y = Math.sqrt(sum_sq_y / len);
    double cov_x_y = sum_coproduct / len;
    // System.out.println("Cov orig " + cov_x_y + " " + pop_sd_x + " " +
    // pop_sd_y);
    return cov_x_y / (pop_sd_x * pop_sd_y);
  }

  public static double[] substring(double[] seq, int start, int end) {
    double[] newseq = new double[(end - start + 1) * 4];

    int i = start;

    //System.out.println("Length " + (end-start+1) + " " + seq.length/4);

     if (seq.length/4 < end) {
       System.out.println("ERROR: " + (seq.length/4) + " " + end);
     }
    while (i <= end) {
      //System.out.println("I " + i);
      int j = 0;

      while (j < 4) {
	//System.out.println("Coord " + ((i-start)*4+j) + " " + i + " " +(i*4+j));
        newseq[(i - start) * 4 + j] = seq[i * 4 + j];
        j++;
      }
      i++;
    }

    return newseq;
  }

  public static double[] revcompPwm(double[] pwm) {

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

  public static double[] padPwm(double[] pwm, int pre, int pos) {

    double[] newpwm = new double[pwm.length + (pre + pos) * 4];

    int i = 0;

    while (i < pre) {
      int j = 0;

      while (j < 4) {
        newpwm[i * 4 + j] = 0.25;
        j++;
      }

      i++;

    }

    i = 0;
    while (i < pwm.length) {
      newpwm[pre * 4 + i] = pwm[i];
      i++;
    }

    i = 0;

    while (i < pos) {
      int j = 0;

      while (j < 4) {

        newpwm[pre * 4 + pwm.length + i * 4 + j] = 0.25;
        j++;
      }
      i++;
    }
    return newpwm;
  }

  public void sortClusters() {
    Collections.sort(clus, new PwmClusterComparer());
  }

  public static void print_help() {
    System.out
        .println("\nUsage: java pogvue.gui.Correlation4 -pwmfile <pwmfile> -seqlen <len> -window <windowlen> -thresh <double> -matrixfile <tffile> -matrixformat <format> -noseed\n");

  }

  public static void main(String[] args) {

    try {

      Hashtable opts = GetOptions.get(args);

      if (opts.containsKey("-help")) {
        Correlation4.print_help();
        System.exit(0);
      }

      String pwmfile = (String) opts.get("-pwmfile");
      String matfile = (String) opts.get("-matrixfile");
      int len = Integer.parseInt((String) opts.get("-seqlen"));
      int win = Integer.parseInt((String) opts.get("-window"));
      double thresh = Double.parseDouble((String) opts.get("-thresh"));
      String format = (String) opts.get("-format");

      boolean noseed = false;

      if (opts.containsKey("-noseed") == true) {
        noseed = true;
      }
      if (pwmfile == null || len <= 0 || win > len || thresh < 0 || thresh > 1) {
        Correlation4.print_help();
        System.exit(0);
      }

      PwmLineFile pf = new PwmLineFile(pwmfile, "File");
      Correlation4 corr4 = new Correlation4(len, win, thresh);

      Vector matrices = null;

      if (matfile != null) {
        if (format.equals("transfac")) {
          TFMatrixFile tfm = new TFMatrixFile(matfile, "File");
          matrices = tfm.getMatrices();
        } else if (format.equals("pwmline")) {
          PwmLineFile plf = new PwmLineFile(matfile, "File");
          plf.parse();
          matrices = plf.getTFMatrices();
          System.out.println("Mat " + matrices.size());
        } else if (format.equals("ormat")) {
          OrmatFile plf = new OrmatFile(matfile, "File");
          plf.parse();
          matrices = plf.getTFMatrices();
          System.out.println("Mat " + matrices.size());
        } else {
          System.out.println("No format specified - should be one of transfac,pwmline");
          Correlation4.print_help();
          System.exit(0);
        }

        if (noseed == false) {
          for (int i = 0; i < matrices.size(); i++) {
            TFMatrix tf = (TFMatrix) matrices.elementAt(i);
            int tflen = tf.getPwm().getPwm().length / 4;

            //if (tflen >= 6 && tflen < len) {
              Pwm tmp = new Pwm(tf.getPwm().getPwm(),tf.getName());
	      System.out.println("Adding " + i + " " + tf.getName() + " " + PwmCluster.getConsensus(tf.getPwm().getPwm()));
              tmp.setChrRegion(new ChrRegion(tf.getName(), 1, 100, 1));
              corr4.addPwm(tmp);

	      //}
          }
        }
      }

      corr4.sortClusters();

      Vector clus = corr4.getClusters();

      for (int i = 0; i < clus.size(); i++) {
	PwmCluster c = (PwmCluster) clus.elementAt(i);
	System.out.println("\nCluster number " + i + " " + c.getName());
	c.print();

	Pwm p = new Pwm(c.getPwm(),c.getName());
	double[] trimpwm = p.trimByInfContent(0.1);
	p.setPwm(trimpwm);
	p.printPwmLine();

	Pwm.printLogo(p.getPwm());

      }
    } catch (Exception e) {
      System.out.println("\nException " + e);
      e.printStackTrace();

      print_help();
      System.exit(0);
    }

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
