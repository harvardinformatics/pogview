package pogvue.analysis;

import java.net.*;
import java.text.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import java.io.*;
import pogvue.datamodel.*;
import pogvue.datamodel.motif.*;
import pogvue.datamodel.comparer.*;
import pogvue.io.*;
import pogvue.gui.*;
import pogvue.gui.schemes.*;
import pogvue.analysis.*;
import pogvue.util.*;

//import org.jdesktop.swingworker.*;

public class SearchWorker extends JThread {
  Alignment align;

  Controller controller;
  AlignViewport viewport;

  Vector pwms;
  Vector matvect;
  Vector mats = null;
  Vector fullmats = null;

  Hashtable names;

  double threshold;
  double infContent;
  double infthresh = 0;
  String searchType = "search";
  int offset = 0;

  String str;

  private Pwm pwm;

  public SearchWorker(String str, int offset) {
    this.offset = offset;
    this.str = str;
  }

    public SearchWorker(Pwm pwm, Vector mats) {
    this.pwm = pwm;
    this.mats = mats;
    matvect = new Vector();

    for (int i = 0; i < mats.size(); i++) {
      TFMatrix tfm = (TFMatrix) mats.elementAt(i);
      matvect.addElement(tfm.getPwm());
    }
  }
  public SearchWorker(Pwm pwm, Vector mats, int offset) {
    this.pwm = pwm;
    this.mats = mats;
    this.offset = offset;
    matvect = new Vector();

    for (int i = 0; i < mats.size(); i++) {
      TFMatrix tfm = (TFMatrix) mats.elementAt(i);
      matvect.addElement(tfm.getPwm());
    }
  }

  public SearchWorker(Alignment align, Controller controller,
		      AlignViewport viewport) {
    this.align = align;
    this.controller = controller;
    this.viewport = viewport;
  }

  public void setViewport(AlignViewport viewport) {
    this.viewport = viewport;
  }

  public void setSearchType(String type) {
    this.searchType = type;
  }

  public String getSearchType() {
    return searchType;
  }

  public void setThreshold(double t) {
    this.threshold = t;
  }

  public void setInfContent(double inf) {
    this.infContent = inf;
  }
  public void run() {
    setProgress(0);

    if (str != null) {
      search(str);
    } else if (pwm != null) {
      searchMatrices(pwm,matvect);
    }

    done();
  }
  public void setNames(String namestr) {
    StringTokenizer str = new StringTokenizer(namestr);
    names = new Hashtable();
    while (str.hasMoreTokens()) {
      names.put(str.nextToken(), new Integer(1));
    }

    mats = null;
    matvect = null;
  }

  public void search(String str) {

    getTransfacMatrices();
    getSpliceMatrices();

    out = searchMatrices(str, matvect);

  }

  public Vector searchMatrices(String str, Vector pwms) {

    double[] seqvec = Correlation4.seqvec(str.toUpperCase(),0,str.length()-1);

    return searchMatrices(new Pwm(seqvec,"Pwm"),pwms);
  }
  public Vector searchMatrices(Pwm pwm, Vector pwms) {

    out = new Vector();

    Vector matches = new Vector();

    int pwmlen = pwm.getPwm().length/4;
    
    for (int j = 0; j < pwms.size(); j++) {

      String   name        = ((TFMatrix) mats.elementAt(j)).getName();
      Pwm      matpwm      = (Pwm) pwms.elementAt(j);
      double[] matpwmarray = matpwm.getPwm();
      int      len         = matpwmarray.length / 4;
      TFMatrix tfm         = (TFMatrix) mats.elementAt(j);

      int chunk = 1;

      int newval = (int)(100*j/pwms.size());
      
      setProgress(newval);

      if (name.indexOf("rich") < 0) {
        int i = 0;

        while (i < (pwmlen - len)) {

	  double[] tmppwmarray = pwm.trim(i,i+len-1);
	  Pwm      tmppwm      = new Pwm(tmppwmarray,"TMP");
	  String   tmpcons     = PwmCluster.getConsensus(tmppwmarray);

	  double forcorr = matpwm.getLogScore(tmppwm);
	  
            if (forcorr > threshold) {

              TFMatch match = new TFMatch((TFMatrix) mats.elementAt(j), tmppwm.getConsensus(), tmpcons, forcorr, 1);

              matches.addElement(match);
              System.out.println("Match " + name + "\t" + forcorr + "\t" + (tmpcons) + "\t" + matpwm.getConsensus());

              SequenceFeature sf = new SequenceFeature(null, "Transfac", i  + offset, i + len + offset, "");

              sf.setId(name);
              sf.setScore(forcorr);

              out.addElement(sf);

            }

	    tmppwmarray = tmppwm.revComp();
	    tmppwm = new Pwm(tmppwmarray,"TMP");
	    tmpcons = tmppwm.getConsensus();
            double revcorr = matpwm.getLogScore(tmppwm);

            if (revcorr > threshold) {

              TFMatch match = new TFMatch((TFMatrix) mats.elementAt(j), tmppwm.getConsensus(), tmpcons, revcorr, -1);

              matches.addElement(match);
              System.out.println("Match " + name + "\t" + revcorr + "\t"  + tmpcons + "\t" + matpwm.getConsensus());

              SequenceFeature sf = new SequenceFeature(null, "Transfac", i  + offset, i + len + offset, "");

              sf.setId(name);
              sf.setScore(revcorr);
              sf.setStrand(-1);

              out.addElement(sf);

            }
	
	    i += chunk;
	}
      }
    }
  
    Collections.sort(matches, new ReverseScoreComparer());
    Collections.sort(out, new SeqFeatureReverseScoreComparer());

    for (int i = 0; i < out.size(); i++) {
      TFMatch match = (TFMatch) matches.elementAt(i);
      SequenceFeature sf = (SequenceFeature) out.elementAt(i);
      System.out.println("Score " + sf.getScore());
      // match.print();
      // System.out.println("Coord is " + sf.getStart() + "\n");
    }
    return out;
  }

  public Vector getTransfacMatrices() {
    if (fullmats == null && viewport != null) {

      // fullmats =
      // viewport.getTransfacMatrices("/n/data1/fas_it/JCUFF/cvs/genpog/data/pwms_union.txt");
      fullmats = viewport.getTransfacMatrices();

      for (int i = 0; i < fullmats.size(); i++) {
        TFMatrix tfm = (TFMatrix) fullmats.elementAt(i);
        // System.out.println("Got matrix " + tfm.getName());
      }

      // fullmats =
      // viewport.getTransfacMatrices("/Users/mclamp/cvs/genpog/data/pwms_union.txt");
    }

    if (mats == null) {
      mats = new Vector();
      matvect = new Vector();

      if (names == null) {
        for (int i = 0; i < fullmats.size(); i++) {
          TFMatrix tfm = (TFMatrix) fullmats.elementAt(i);
          if (tfm.getPwm().getInfContent() > infthresh) {
            matvect.addElement(tfm.getPwm());
            mats.addElement(tfm);
          }

        }
      } else {
        for (int i = 0; i < fullmats.size(); i++) {
          TFMatrix tfm = (TFMatrix) fullmats.elementAt(i);

          Enumeration en = names.keys();
          while (en.hasMoreElements()) {
            String name = (String) en.nextElement();
            if (tfm.getName().indexOf(name) >= 0) {
              matvect.addElement(tfm.getPwm());
              mats.addElement(tfm);
            }
          }
        }
      }
    }
    return mats;

  }

  public Vector getSpliceMatrices() {

    if (pwms == null) {
      try {
        Pwm2File pwmfile = new Pwm2File("data/SpliceMotifs.txt", "File");

        pwms = pwmfile.getPwm();
      } catch (IOException e) {
        System.out.println("Can't read Splice File");
      }
    }
    return pwms;
  }

  private final static String getDateTime() {
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
    df.setTimeZone(TimeZone.getTimeZone("EST"));
    return df.format(new Date());
  }

  public final static String getHostName() {
    try {
      InetAddress addr = InetAddress.getLocalHost();
      String hostName = addr.getHostName();
      String hostAddress = addr.getHostName();

      return hostName;
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void main(String[] args) {

    try {
      // String chr = args[0];
      // int start = Integer.parseInt(args[1]);
      // int end = Integer.parseInt(args[2]);

      // String file = "/n/data1/fas_it/JCUFF/cvs/genpog/test." +
      // SearchWorker.getHostName() + "." + SearchWorker.getDateTime() + "." +
      // chr + "." + start + ".fa";
      // String file = "/tmp/test." + SearchThread.getHostName() + ".fa";
      // Get Fasta File

      // String cmd1 =
      // "/n/data1/fas_it/JCUFF/src/x86_64/nibFrag /n/data1/fas_it/JCUFF/src/" +
      // chr + ".nib " + start + " " + end + " + " + file;
      // String cmd1 =
      // "/Users/mclamp/bin/powerpc/nibFrag /Users/mclamp/hg18_fasta/" + chr +
      // ".nib " + start + " " + end + " + " + file;

      // Runtime runtime = Runtime.getRuntime();
      // Process proc = runtime.exec(cmd1);

      // System.out.println("Command " + cmd1);

      // try {
      // Thread.sleep(1000);
      // } catch (InterruptedException e) {
      // e.printStackTrace();
      // }
      String file = args[0];
      int start = 1;
      String chr = file;

      File f = new File(file);
      FastaFile ff = new FastaFile(file, "File");

      ff.parse();
      int count = 0;

      while (!f.exists() && count < 100) {

        try {
          Thread.sleep(100);
          count++;
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

      }

      Sequence[] seqs = ff.getSeqsAsArray();

      Alignment al = new Alignment(seqs);
      AlignViewport av = new AlignViewport(al);

      // SearchWorker st = new SearchWorker(seqs[0].getSequence(),0);

      for (int j = 0; j < seqs.length; j++) {
        SearchWorker st = new SearchWorker(seqs[j].getSequence(), 0);

        st.setViewport(av);
        st.setThreshold(0);
        st.start();

        while (st.DONE == false) {
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }

        Vector out = st.getOutput();

        j++;

        j = seqs.length;
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}
