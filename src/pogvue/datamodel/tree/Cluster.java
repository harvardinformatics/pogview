package pogvue.datamodel.tree;

import pogvue.datamodel.*;
import java.util.*;
import pogvue.analysis.*;

//This is used in clustering tree nodes
public class Cluster {

	Vector seqs;
	double pwm[];
	int seqlen;

    public Cluster(Sequence seq) {
	seqs = new Vector();

	pwm = new double[seq.getLength() * 4];

	addSequence(seq);

    }
    public void addSequence(Sequence seq) {
        //System.out.println("Adding sequence " + seq.getSequence() + " " + seq.getLength()  + " " + pwm.length/4);
	if (seqs.size() == 0) {
	    pwm = Correlation4.seqvec(seq);
	    seqlen = seq.getLength();
	} else {
	    double[] tmpvec = Correlation4.seqvec(seq);

	    int num = seqs.size();

	    int i = 0;

	    while (i < seqlen) {
		int j = 0;
		
		while (j < 4) {
		    pwm[i*4+j] *= num;
		    pwm[i*4+j] += tmpvec[i*4+j];
		    pwm[i*4+j] /= (num+1);
		    j++;
		}
		i++;
	    }
	}
	seqs.addElement(seq);
    }
    public String getConsensus() {
	StringBuffer cons = new StringBuffer();

	int i = 0;
	
	while (i < seqlen) {
	    double maxval = pwm[i*4];
	    String maxch  = "A";
	    
	    if (pwm[i*4+1] > maxval) {
		maxval = pwm[i*4+1];
		maxch  = "T";
	    } else if (pwm[i*4+2] > maxval) {
		maxval = pwm[i*4+2];
		maxch  = "C";
	    } else if (pwm[i*4+3] > maxval) {
		maxval = pwm[i*4+3];
		maxch  = "G";
	    }
	    cons.append(maxch);
	    i++;
	}
	return cons.toString();
    }
    public double[] getPwm() {
	return pwm;
    }
    public Vector getSeqs() {
	return seqs;
    }
    public void printPwm() {
       int i = 0;

       while (i < seqlen) {
           System.out.println((int)(100*pwm[i*4])/10 + "\t" + (int)(100*pwm[i*4+1])/10 + "\t" + (int)(100*pwm[i*4+2])/10 + "\t" + (int)(100*pwm[i*4+3])/10);
	   i++;
        }
    }
    public int size() {
	return seqs.size();
    }

}


    
