package pogvue.io;

import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import pogvue.datamodel.*;
import pogvue.datamodel.motif.*;

public class PwmFile extends FileParse {

    private Vector pwm = null;
    private int   size;
    public PwmFile(String name,String type,int size) throws IOException {
	super(name,type);
	this.size = size;
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

		  if (tmppwm != null && name != null) {
		      pwm.addElement(new Pwm(tmppwm,name));
		  }
		  name = line.substring(1);
		  
		  newpwm = true;
		  tmppwm = null;

	      } else {

		  StringTokenizer str = new StringTokenizer(line,"\t");

		  if (newpwm == true) {
		      tmppwm = new double[size*4];
		      
		      i = 0;

		      newpwm= false;
		  }
		  while (str.hasMoreTokens()) {
		      double tmp = Double.parseDouble(str.nextToken());
		      
		      tmppwm[i] = tmp;
		      
		      i++;
		  }

	      } 
	      linenum++;

	  }
	  if (tmppwm != null) {
	      pwm.addElement(new Pwm(tmppwm,name));
	  }

      } catch (Exception e) {
	  System.out.println("Got exception reading matrix " + e);
	  e.printStackTrace();
      }
  }

    public Vector getPwm() {
	return pwm;
    }
    public int getSize() {
	return size;
    }
}




