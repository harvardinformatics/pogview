package pogvue.io;


import java.util.*;
import java.io.*;

import pogvue.datamodel.*;
import pogvue.datamodel.motif.*;
import pogvue.analysis.*;

public class OmegaPwmFile extends FileParse {
  Vector pwms;

  public OmegaPwmFile(String name,String type) throws IOException {
    super(name,type);
    
    pwms = new Vector();

    parse();
  }
  private void parse() throws IOException {

    String id;
    String line;

    while ((line = nextLine()) != null) {


      if (line.indexOf(">") == 0) {
	id = line.substring(1);

	// First line is A

	line = nextLine();

	StringTokenizer str = new StringTokenizer(line,"\t");

	int len = str.countTokens();

	// Create the pwm

	double[] pwm = new double[len*4];

	// Fill in the A's

	int i = 0;

	while (i < len) {
	  String tmp = str.nextToken();

	  double val = Double.parseDouble(tmp);

	  pwm[i*4] = val;

	  i++;

	}

	// Now C's

	line = nextLine();

	str = new StringTokenizer(line,"\t");

	// Fill in the C's

	i = 0;

	while (i < len) {
	  String tmp = str.nextToken();

	  double val = Double.parseDouble(tmp);

	  pwm[i*4+2] = val;

	  i++;

	}

	// Now G's

	line = nextLine();

	str = new StringTokenizer(line,"\t");

	// Fill in the G's

	i = 0;

	while (i < len) {
	  String tmp = str.nextToken();

	  double val = Double.parseDouble(tmp);

	  pwm[i*4+3] = val;

	  i++;

	}

	// Now T's

	line = nextLine();

	str = new StringTokenizer(line,"\t");

	// Fill in the T's

	i = 0;

	while (i < len) {
	  String tmp = str.nextToken();

	  double val = Double.parseDouble(tmp);

	  pwm[i*4+1] = val;

	  i++;

	}

	Pwm tmppwm = new Pwm(pwm,id);

	pwms.addElement(tmppwm);

      }
    }
  }

  public Vector getPwms() {
    return pwms;
  }

  public static void main(String[] args) {
    
    try {
      OmegaPwmFile opf = new OmegaPwmFile(args[0],"File");

      Vector pwms = opf.getPwms();
      
      for (int i = 0; i < pwms.size(); i++) {

	Pwm pwm = (Pwm)pwms.elementAt(i);
	
	System.out.println(pwm.getName() + " " + PwmCluster.getConsensus(pwm.getPwm()));
      
	Correlation4.printPWM(pwm.getPwm(),true);
	System.out.println();
	
	Pwm.printLogo(pwm.getPwm());
	
      System.out.println();
      }
    } catch (IOException e) {
      System.out.println("Exception " + e);
    }
  }

}


  
