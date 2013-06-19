package pogvue.io;

import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import pogvue.datamodel.motif.*;

public class Pwm2File extends FileParse {

    private Vector pwm = null;

    public Pwm2File(String name,String type) throws IOException {
	super(name,type);
	parse();
    }

  private void parse()  {
    try {

      String line;
      int    linenum = 0;
      boolean newpwm = true;
      double []tmppwm = null;
      String name     = null;
      
      
      int i = 0;
      
      while ((line = nextLine()) != null) {
	
	if (line.indexOf(">") == 0) {
	  if (pwm == null) {
	    pwm   = new Vector();
	  }
	  
	  name = line.substring(1);
	  
	  newpwm = true;
	  tmppwm = null;
	  
	  
	  int j = 0;

	  while (j < 4) {
	    
	    String tmpline = nextLine();
	    
	    StringTokenizer str = new StringTokenizer(tmpline,"\t");
	    
	    int len = str.countTokens();
	    
	    if (tmppwm == null) {
	      tmppwm = new double[len*4];
	    }
	    
	    int k = 0;
	    
	    while (str.hasMoreTokens()) {
	      if (j == 0) {
		tmppwm[4*k + j] = Double.parseDouble(str.nextToken());   // A0 -> A0
	      } else if (j == 1) {
		tmppwm[4*k + 2] = Double.parseDouble(str.nextToken());   // C1 -> C2
	      } else if (j == 2) {
		tmppwm[4*k + 3] = Double.parseDouble(str.nextToken());   // G2 -> G3
	      } else if (j == 3) {
		tmppwm[4*k + 1] = Double.parseDouble(str.nextToken());   // T3 => T1
	      }
	      k++;
	    }
	    j++;
	  }
	  
	  if (tmppwm != null) {
	    pwm.addElement(new Pwm(tmppwm,name));
	  }
	}
      }

    } catch (Exception e) {
      System.out.println("Got exception reading matrix " + e);
      e.printStackTrace();
    }
  }

    public Vector getPwm() {
	return pwm;
    }
    public static void main(String[] args) {
      try {
	Pwm2File pwmfile = new Pwm2File(args[0],"File");
	
	Vector   pwm     = pwmfile.getPwm();
	
	for (int i = 0; i < pwm.size(); i++) {
	  Pwm p = (Pwm)pwm.elementAt(i);
	  
	  //	  p.printLogo(p.getPwm());
	  printPWM(p,true);


	  System.out.println("\nConsensus\t" + p.getConsensus());
	  
	}
      } catch (IOException e) {
	System.out.println("Got exception " + e);
	e.printStackTrace();
      }
    }

    public static void printPWM(Pwm pwm, boolean oneline) {

	int i = 0;

	double [] tmppwm = pwm.getPwm();

	if (pwm.getChrRegion() != null) {
	  System.out.print(pwm.getChrRegion().toString() + "\t");
	} else if (pwm.getName() != null) {
	  System.out.print(pwm.getName() + "\t0\t0\t");
	} else {
	  System.out.print("PWM\t0\t0\t");
	}

	while (i < tmppwm.length/4) {
	    int j = 0;

	    while (j < 4) {
		System.out.printf("%10.2f\t",tmppwm[i*4+j]);
		j++;
	    }
	    if (oneline == false) {
		System.out.println();
	    }
	    i++;
	}
	
    }

}




