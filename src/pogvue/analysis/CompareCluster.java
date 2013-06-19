package pogvue.analysis;

import pogvue.io.*;
import pogvue.datamodel.*;
import pogvue.datamodel.motif.*;

import java.util.*;
import java.io.*;

public class CompareCluster {

    public static void main(String[] args) {

	try {

	    String    pwmfile1 = args[0];
            String    pwmfile2 = args[1];
            
            int len = Integer.valueOf(args[2]);
            
            double    thresh  = Double.valueOf(args[3]);

	    System.out.println("Thresh " + thresh);
	    
            PwmLineFile pf1    = new PwmLineFile(args[0],"File");
	    PwmLineFile pf2    = new PwmLineFile(args[1],"File");
	    
            Vector      clus  = new Vector();

            Pwm pwm;

            Vector pwm2 = new Vector();
            
            while ((pwm = pf2.nextPwm()) != null) {
                pwm2.addElement(pwm);
            }
            
	    while ((pwm = pf1.nextPwm()) != null) {

		double [] pwm1 = pwm.getPwm();

		System.out.println("Consensus pwm1 " + PwmCluster.getConsensus(pwm1));

		int found = 0;

		double maxcorr = 0;
		Pwm maxpwm = null;
		int    maxstrand = 0;
	
                for (int k = 0;k < pwm2.size(); k++) {
                    Pwm p2 = (Pwm)pwm2.elementAt(k);
                
                    double[] pp2 = p2.getPwm();
                    
                    double   tmpcorr = Correlation4.get(pwm1,pp2);
			
			if (tmpcorr > thresh && tmpcorr >= maxcorr) {
			    maxcorr   = tmpcorr;
			    maxpwm    = p2;
			   
			    maxstrand = 1;
			    found = 1;
                        }

			pp2 = Correlation4.revcompPwm(pp2);

			tmpcorr =  Correlation4.get(pwm1,pp2);

			if (tmpcorr > thresh && tmpcorr >= maxcorr) {
			    maxcorr   = tmpcorr;
			    maxpwm    = p2;
			    
			    maxstrand = -1;
			    found = 1;

			    System.out.println("Found reverse corr " + maxcorr + " " + PwmCluster.getConsensus(maxpwm.getPwm()));
			}
                }
            
	       
		if (found == 1) {
                     System.out.println("Found matching pwm " + pwm.getChrRegion() + "\t" + PwmCluster.getConsensus(pwm1) + "\t" + maxpwm.getChrRegion() + "\t" + PwmCluster.getConsensus(maxpwm.getPwm()) + " " + maxcorr);
                      //Correlation4.printPWM(maxpwm.getPwm(),false);
			
                    Pwm.printLogo(pwm.getPwm());
                    System.out.println();
                     Pwm.printLogo(maxpwm.getPwm());
			
                    System.out.println();
		    
		} else {
		    System.out.println("No matching pwm " + PwmCluster.getConsensus(pwm1));
                    
                    Pwm.printLogo(pwm1);
		} 
        
            }	    
	} catch (IOException e) {
	    System.out.println("Exception " + e);
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
