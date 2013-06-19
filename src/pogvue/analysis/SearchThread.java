package pogvue.analysis;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;

import pogvue.datamodel.*;
import pogvue.datamodel.motif.*;
import pogvue.datamodel.comparer.*;
import pogvue.gui.AlignViewport;
import pogvue.gui.Controller;
import pogvue.gui.schemes.ResidueProperties;
import pogvue.io.FastaFile;
import pogvue.io.Pwm2File;

public class SearchThread extends JThread {
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
  
  public boolean DONE = false;
  
  public SearchThread(String str, int offset, ActionListener l) {
    super(l);
    this.offset = offset;
    this.str = str;
  }
  
  public SearchThread(Alignment align, Controller controller,
		      AlignViewport viewport) {
    this.align      = align;
    this.controller = controller;
    this.viewport   = viewport;
    
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
    
    if (str != null) {
      search(str);
    } else {
      searchViewport();
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
  
  public void searchViewport() {
    
    out = new Vector();
    
    getTransfacMatrices();
    getSpliceMatrices();
    
    int start = viewport.getStartRes();
    int end = viewport.getEndRes();
    
    if (searchType.equals("Transfac")) {
      search(viewport.getAlignment().getSequenceAt(0).getSequence(start, end));
    } else if (searchType.equals("Splices")) {
      search(viewport.getAlignment().getSequenceAt(0).getSequence(start, end));
    }
    
  }
  
  public void search(String str) {
    
    getTransfacMatrices();
    getSpliceMatrices();
    
    out = searchMatrices(str, matvect);
    
  }
  
  public Vector searchMatrices(String str, Vector pwms) {
    
    out = new Vector();
    
    Vector matches = new Vector();
    
    for (int j = 0; j < pwms.size(); j++) {
      String name = ((TFMatrix) mats.elementAt(j)).getName();
      Pwm pwm = (Pwm) pwms.elementAt(j);
      double[] pwmarray = pwm.getPwm();
      int len = pwmarray.length / 4;
      TFMatrix tfm = (TFMatrix) mats.elementAt(j);

      if (name.indexOf("rich") < 0) {
	// System.out.println("Searching " + name);
	int i = 0;

	while (i < (str.length() - len)) {

	  String sub = str.substring(i, i + len).toUpperCase();
	  if (sub.indexOf("N") < 0) {
	    char[] subchar = sub.toCharArray();

	    // String revsub = ResidueProperties.reverseComplement(sub);

	    double forcorr = pwm.getLogScore(subchar);

	    if (forcorr > threshold) {

	      TFMatch match = new TFMatch((TFMatrix) mats.elementAt(j), pwm
					  .getConsensus(), sub, forcorr, 1);

	      matches.addElement(match);

	      SequenceFeature sf = new SequenceFeature(null, "Transfac", i
						       + offset, i + len + offset, "");
	      sf.setId(name);
	      // sf.setScore(tfm.getPwm().getInfContent()*forcorr);
	      sf.setScore(100 * forcorr);
	      out.addElement(sf);

	    }

	    char[] revsubchar = ResidueProperties.reverseComplement(subchar);

	    double revcorr = pwm.getLogScore(revsubchar);

	    if (revcorr > threshold) {

	      TFMatch match = new TFMatch((TFMatrix) mats.elementAt(j), pwm
					  .getConsensus(), new String(revsubchar), revcorr, -1);

	      matches.addElement(match);

	      SequenceFeature sf = new SequenceFeature(null, "Transfac", i
						       + offset, i + len + offset, "");
	      sf.setId(name);
	      // sf.setScore(tfm.getPwm().getInfContent()*revcorr);
	      sf.setScore(100 * revcorr);
	      sf.setStrand(-1);

	      out.addElement(sf);

	    }
	  }
	  i++;
	}
      }
    }
    Collections.sort(matches, new ScoreComparer());
    Collections.sort(out, new SeqFeatureScoreComparer());

    for (int i = 0; i < matches.size(); i++) {
      TFMatch match = (TFMatch) matches.elementAt(i);
      SequenceFeature sf = (SequenceFeature) out.elementAt(i);
      match.print();
      // System.out.println("Coord is " + sf.getStart() + "\n");
    }
    return out;
  }

  public Vector getTransfacMatrices() {
    if (fullmats == null && viewport != null) {

      // fullmats =
      // viewport.getTransfacMatrices("/n/data1/fas_it/JCUFF/cvs/genpog/data/pwms_union.txt");
      fullmats = viewport.getTransfacMatrices("./data/pwms_union.txt");

      for (int i = 0; i < fullmats.size(); i++) {
	TFMatrix tfm = (TFMatrix) fullmats.elementAt(i);
	System.out.println("Got matrix " + tfm.getName());
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
      String chr = args[0];
      int start = Integer.parseInt(args[1]);
      int end = Integer.parseInt(args[2]);

      // String file = "/n/data1/fas_it/JCUFF/cvs/genpog/test." +
      // SearchThread.getHostName() + "." + SearchThread.getDateTime() + "." +
      // chr + "." + start + ".fa";
      String file = "/tmp/test." + SearchThread.getHostName() + ".fa";
      // Get Fasta File

      // String cmd1 =
      // "/n/data1/fas_it/JCUFF/src/x86_64/nibFrag /n/data1/fas_it/JCUFF/src/" +
      // chr + ".nib " + start + " " + end + " + " + file;
      String cmd1 = "/Users/mclamp/bin/powerpc/nibFrag /Users/mclamp/hg18_fasta/"
	+ chr + ".nib " + start + " " + end + " + " + file;

      Runtime runtime = Runtime.getRuntime();
      Process proc = runtime.exec(cmd1);

      System.out.println("Command " + cmd1);

      try {
	Thread.sleep(1000);
      } catch (InterruptedException e) {
	e.printStackTrace();
      }

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

      SearchThread st = new SearchThread(seqs[0].getSequence(), 0, null);

      st.setViewport(av);
      st.setThreshold(0.85);
      st.run();

      // while (st.DONE == false) {
      // try {
      // Thread.sleep(1000);
      // }
      // catch (InterruptedException e) {
      // e.printStackTrace();
      // }
      // }

      Vector out = st.getOutput();

      for (int i = 0; i < out.size(); i++) {
	SequenceFeature sf = (SequenceFeature) out.elementAt(i);
	sf.setStart(sf.getStart() + start - 1);
	sf.setEnd(sf.getEnd() + start - 1);

	System.out.println("Out\t" + sf.toGFFString() + "\t" + chr);
      }

      // Make sure the file or directory exists and isn't write protected
      if (f.exists()) {
	boolean success = f.delete();

	if (!success) {
	  throw new IllegalArgumentException("Delete: deletion failed");
	}
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}
