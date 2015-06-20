package pogvue.datamodel.motif;


import java.util.*;
import pogvue.analysis.*;
import pogvue.datamodel.*;

public class PwmCluster {

    public static String getConsensus(double[] pwm) {
	StringBuffer cons = new StringBuffer();

	int i = 0;
	
	while (i < pwm.length/4) {
	    double maxval = 0.25;
	    String maxch  = "-";
	    
	    if (pwm[i*4] > maxval) {
		maxval = pwm[i*4];
		maxch = "A";
	    } 

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
  Vector pwms;
  double pwm[];
  
  int seqlen;
  
  Vector matches = null;
  String name;

  public PwmCluster(Pwm pwm) {
    pwms = new Vector();
    
    addPwm(pwm);
    
  }

  public String getName() {
    name = "";

    Hashtable stubs = new Hashtable();

    int maxcount = 0;
    String maxstub = "";
    
    for (int i = 0;i < pwms.size(); i++) {
      Pwm pwm = (Pwm)pwms.elementAt(i);

      String s = pwm.getName().substring(0,3);

      // Names like PM23.01 etc
      if (pwm.getName().indexOf("PM") == 0) {
	s = pwm.getName().substring(0,pwm.getName().indexOf("."));
      }

      // Names like LM23.01 etc
      if (pwm.getName().indexOf("LM") == 0) {
	s = pwm.getName().substring(0,pwm.getName().indexOf("."));
      }

      // Names like UTRM23_3.15 etc
      if (pwm.getName().indexOf("UTR") == 0 && pwm.getName().indexOf("_") > 0) {
	s = pwm.getName().substring(0,pwm.getName().indexOf("_"));
      }
      if (stubs.containsKey(s)) {
	int count = ((Integer)stubs.get(s)).intValue();
	count++;
	stubs.put(s,(Integer)count);
	if (count > maxcount) {
	  maxcount = count;
	  maxstub = s;
	}
      } else {
	if (maxcount == 0) {
	  maxcount = 1;
	  maxstub = s;
	}
	stubs.put(s,1);
      }
    }

    name = maxstub + "." + pwms.size();
    return name;
  }
  public void addMatch(TFMatrix tfm) {
    if (matches == null) {
      matches = new Vector();
    }
    if (!matches.contains(tfm)) {
      matches.addElement(tfm);
    }
  }
  public void addPwm(Pwm newpwm) {
    
    if (contains(newpwm)) {
      System.out.println("cluster contains pwm");
      return;
    }
    
    
    //System.out.println("Adding Pwm " + newpwm.getPwm().length/4 + " " + PwmCluster.getConsensus(newpwm.getPwm()));
    
    if (pwms.size() == 0) {
      seqlen = newpwm.getPwm().length/4;
      pwm = new double[newpwm.getPwm().length];
    }
    
    int num = pwms.size();
    
    int i = 0;
    
    while (i < seqlen) {
      int j = 0;
      
      while (j < 4) {
	pwm[i*4+j] *= num;
	pwm[i*4+j] += newpwm.getPwm()[i*4+j];
	pwm[i*4+j] /= (num+1);
	j++;
      }
      i++;
    }
    pwms.addElement(newpwm);
  }
  public boolean contains(Pwm pwm) {
    
    for (int i = 0; i < pwms.size(); i++) {
      Pwm tmp = (Pwm)pwms.elementAt(i);
      
      ChrRegion reg1 = tmp.getChrRegion();
      ChrRegion reg2 = pwm.getChrRegion();
      
      if (!tmp.getName().equals(pwm.getName())) {
	return false;
      }
      if (reg1 != null && reg2 != null) {
	
	if (reg1.getChr().equals(reg2.getChr()) &&
	    reg1.getStart() == reg2.getStart() &&
	    reg1.getEnd()   == reg2.getEnd()) {
	  
	  System.out.println("Found matching pwm " + reg1.toString() + " " + reg2.toString());
	  return true;
	}
      }
    }
    return false;
  }
  public double[] getPwm() {
    return pwm;
  }
  
  public Vector getPwms() {
    return pwms;
  }
  
  public void print() {
    Vector  pwms = getPwms();
    
    System.out.print("Cluster\t" + pwms.size() + "\t");
    
    String matchstr = "";
    
    if (matches != null) {
      for (int i = 0;i < matches.size(); i++) {
	matchstr += "_" + ((TFMatrix)matches.elementAt(i)).getName();
      }
    } else {
      matchstr = "-";
    }
    
    System.out.print(matchstr + "\n");
    
    //System.out.print(getConsensus(getPwm()));
    
    for (int j = 0; j < pwms.size(); j++) {
      Pwm p = (Pwm)pwms.elementAt(j);
      System.out.printf("%25s\t%s\n",p.getName(),PwmCluster.getConsensus(p.getPwm()));
    }
    
		  System.out.print("\n");
		  System.out.print("Cluster\t1\t1\t");
		  Correlation4.printPWM(getPwm(),true);
		  System.out.println();
		  //Pwm.printLogo(getPwm());
		  
    //		  System.out.println();
    //		  System.out.println(PwmCluster.getConsensus(getPwm()));
    //		  System.out.println();

  }

  public int size() {
    return pwms.size();
  }
}


    
