package pogvue.io;

import pogvue.analysis.*;
import pogvue.datamodel.*;
import pogvue.datamodel.expression.*;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class ExpFile extends AlignFile {

	private Vector levels;
	private Vector tissues;

	private int    maxlevel;
	private int    minlevel;

  public ExpFile(String inFile, String type) throws IOException {
    super(inFile,type,true);
  }

  public ExpFile(String inFile, String type, boolean parse) throws IOException {
    super(inFile,type,parse);
  }

  private  int getMaxLevel() {
    return maxlevel;
  }
  private  int getMinLevel() {
    return minlevel;
  }

  public void parse() {
    levels  = new Vector();
    tissues = new Vector();

    String line  = null;
    int    i     = 0;

    maxlevel = 0;
    minlevel = (int)1e10;

    try {
      while ((line = nextLine()) != null) {

	StringTokenizer str = new StringTokenizer(line,"\t");

	if (i == 0) {

	  str.nextToken();
	  str.nextToken();
	  str.nextToken();

	  while (str.hasMoreTokens()) {
	    tissues.addElement(str.nextToken());
	  }
	}  else {

	  str.nextToken();

	  String gene      = str.nextToken();
	  Vector tmplevels = new Vector();

	  str.nextToken();
	  int j = 0;

	  while (str.hasMoreTokens()) {
	    
	    double val1 = Double.parseDouble((String)str.nextToken());
	    //val1 = Math.log(val1);
	    int    val  = (int)(val1);

	    if (val >= maxlevel) {
	      maxlevel = val;
	    }

	    if (val <= minlevel) {
	      minlevel = val;
	    }

	    String tissue = (String)tissues.elementAt(j);

	    ExpressionLevel level = new ExpressionLevel(gene,tissue,val);

	    int pos = 0;
	    if (gene.indexOf("_") > 0) {

	      Pattern p  = Pattern.compile("_");

	      String[] result = p.split(gene);


	      level.setEnsg(result[0]);
	      level.setType(result[1]);
	      level.setName(result[3]);
	      level.setCluster(result[result.length-1]);

	      String pfam = result[4];

	      
	      int ii = 5;
	      while (ii < result.length-1) {
		pfam = pfam + "_" + result[ii];
		ii++;
	      }

	      level.setPfam(pfam);
	    }
	    
	    tmplevels.addElement(level);

	    j++;
	  }

	  levels.addElement(tmplevels);
	}
	i++;
      }
    } catch (IOException e) {
      System.out.println("Exception parsing fastafile");
    }
  }

  public Vector getExpLevels() {
    return levels;
  }

  private  Vector getTissues() {
    return tissues;
  }

  public static void write(OutputStream os,Sequence seq) throws IOException {
  }
  private static String print(Sequence[] s) {
    return print(s,72);
  }
  private static String print(Sequence[] s, int len) {
    return print(s,len,true);
  }
  private static String print(Sequence[] s, int len,boolean gaps) {
    return "";
  }

  public String print() {
    return print(getSeqsAsArray());
  } 
}



