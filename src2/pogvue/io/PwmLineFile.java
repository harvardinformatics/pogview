package pogvue.io;

import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import pogvue.datamodel.*;
import pogvue.datamodel.motif.*;

public class PwmLineFile extends FileParse {

  private Vector pwm = null;
    int count = 0;
  public PwmLineFile(String name,String type) throws IOException {
    super(name,type);
    pwm = new Vector();
  }
  public  void parse()  {
      count = 0;
    Pwm p;
    while ((p = nextPwm()) != null) {
      //System.out.println("Pwm " + p);
      pwm.addElement(p);
    }
  }
  public Pwm nextPwm() { 
    String line = "";

    try {

      Pwm p = null;

      while (p == null && (line = nextLine()) != null) {
	//	System.out.println("Line " + line);
	try {
	  count++;

	  int i = 0;

	  StringTokenizer str = new StringTokenizer(line,"\t");
	  
	  int    len = (str.countTokens()-3)/4;
	  
	  if (len > 0) {
	    double[] tmppwm = new double[len*4];
	    
	    String chr = str.nextToken();
	    int    start = Integer.parseInt(str.nextToken());
	    int    end   = Integer.parseInt(str.nextToken());
	    
	    while (str.hasMoreTokens()) {
	      double tmp = Double.parseDouble(str.nextToken());
	      
	      tmppwm[i] = tmp;
	      
	      i++;
	    }
	    p = new Pwm(tmppwm,"PWM");

	    if (chr.indexOf("chr") == 0) {
	      ChrRegion r = new ChrRegion(chr,start,end,1);
	      p.setChrRegion(r);
	    } else {
	      p.setName(chr);
	    }
	    return p;
	  }

	} catch (Exception e) {
	  System.out.println("Got exception reading matrix " + e + " " + line);
	  e.printStackTrace();
	}
      }
    } catch (IOException e) {
      e.printStackTrace();
      return nextPwm();
    }

    return null;
  }
  
  public Vector getPwmMatrices() {
    return pwm;
  }

  public Vector getMatrices() {
    Vector mat = new Vector();

    for (int i = 0; i < pwm.size(); i++) {
      mat.addElement(((Pwm)pwm.elementAt(i)).getPwm());
    }

    return mat;
  }
  public Vector getTFMatrices() {
    Vector out = new Vector();

    for (int i = 0; i < pwm.size(); i++) {
      Pwm p = (Pwm)pwm.elementAt(i);

      double[][] newmat = new double[p.getPwm().length/4][4];

      for (int j = 0; j < p.getPwm().length/4; j++) {
	int k = 0;
	while (k < 4) {

	  newmat[j][k] = p.getPwm()[j*4+k];
	  k++;
	}
      }
      TFMatrix tfm = new TFMatrix(newmat,p.getPwm().length/4,4);

      if (p.getChrRegion() != null) {
	  tfm.setName(p.getChrRegion().toString());
      } else {
	  tfm.setName(p.getName());
      }
      out.addElement(tfm);
    }

    return out;
  }
    public static void print(Pwm p) {

	System.out.print(p.getName() + "\t100\t100");
	
	
	for (int j = 0; j < p.getPwm().length/4; j++) {
	    int k = 0;
	    
	    while (k < 4) {
		
		System.out.print("\t" + p.getPwm()[j*4+k]);
		
		k++;
	    }
	}
	System.out.println();
	    
    }
  public static void main(String[] args) {
    try {
      PwmLineFile pf = new PwmLineFile(args[0],"File");
      Pwm pwm;

      while ((pwm = pf.nextPwm()) != null) {
	//Pwm.printLogo(pwm.getPwm());
	
	//System.out.println();
	System.out.println(PwmCluster.getConsensus(pwm.getPwm()));
      }
    } catch (IOException e) {
      System.out.println("Error : " + e);
    }
  }
}




