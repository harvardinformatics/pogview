package pogvue.analysis;

import pogvue.io.*;
import pogvue.datamodel.*;
import java.util.*;
import java.io.*;
import pogvue.gui.schemes.*;

public class Correlation {


    public static double get(double x[], double y[]) {

	double sum_sq_x = 0.0;
	double sum_sq_y = 0.0;
	double sum_coproduct = 0.0;
	double mean_x = x[0];
	double mean_y = y[0];

	for (int i = 1; i < x.length; i++) {
	    double sweep = (i - 1.0) / i;
	    double delta_x = x[i] - mean_x;
	    double delta_y = y[i] - mean_y;
	    sum_sq_x += delta_x * delta_x * sweep;
	    sum_sq_y += delta_y * delta_y * sweep;
	    sum_coproduct += delta_x * delta_y * sweep;
	    mean_x += delta_x / i;
	    mean_y += delta_y / i;
	}
	double pop_sd_x = Math.sqrt( sum_sq_x / x.length );
	double pop_sd_y = Math.sqrt( sum_sq_y / x.length );
	double cov_x_y = sum_coproduct / x.length;
	return cov_x_y / (pop_sd_x * pop_sd_y);
    }

    public static double[] shift(double[] seq, int num) {
	double[] newseq = new double[seq.length + num*4];

	int i = 0;

	while (i < num) {
	    newseq[i*4]   = 0.25;
	    newseq[i*4+1] = 0.25;
	    newseq[i*4+2] = 0.25;
	    newseq[i*4+3] = 0.25;
	    i++;
	}
	i = i * 4;
	while (i < seq.length+num) {
	    newseq[i] = seq[i-num*4];
	    i++;
	}
	return newseq;
    }

    public static Hashtable get_with_shift(double[] vec1, double[] vec2,String seq,double thresh) {
	double[] off1 = Correlation.shift(vec1,1);
	double[] off2 = Correlation.shift(vec1,2);
	double[] off3 = Correlation.shift(vec2,1);
	double[] off4 = Correlation.shift(vec2,2);
	
	double corr = Correlation.get(vec1,vec2);
	double corr1 = Correlation.get(vec2,off1);
	double corr2 = Correlation.get(vec2,off1);
	double corr3 = Correlation.get(vec1,off3);
	double corr4 = Correlation.get(vec1,off4);
		    


	double maxcorr = corr;
	int    maxstr  = 0;
	
	if (corr1 > corr) {
	    if (corr2 > corr1) {
		maxstr = 2;
		maxcorr = corr2;
	    } else {
		maxstr = 1;
		maxcorr = corr1;
	    }
	}
	
	if (corr3 > maxcorr) {
	    if (corr4 > corr3) {
		maxstr = 4;
		maxcorr = corr4;
	    } else {
		maxstr = 3;
		maxcorr = corr3;
	    }
	}
	
	if (maxstr == 1) {
	    seq = "---" + seq + "-";
	} else if (maxstr == 2) {
	    seq = "----" + seq;
	} else if (maxstr == 3) {
	    seq = "-" + seq + "--";
	} else if (maxstr == 0) {
	    seq = "--" + seq + "--";
	} else if (maxstr == 4) {
	    seq = seq + "----";
	}
	Hashtable out = new Hashtable();

	out.put("maxcorr",new Double(maxcorr));
	out.put("seq",seq);

	return out;
    }

    public static void main(String[] args) {

	try {
	    FastaFile ff = new FastaFile(args[0],"File");
	    
	    double thresh = Double.valueOf(args[1]);

	    Vector seqs = ff.getSeqs();
	    
	    Vector clus = new Vector();

	    for (int i = 0; i < seqs.size(); i++) {

		String seq = ((Sequence)seqs.elementAt(i)).getSequence();		
		double[] vec1 = Correlation.seqvec((Sequence)seqs.elementAt(i));
		
		int found = 0;

		for (int j = 0; j < clus.size(); j++) {
		    Vector clusseqs = (Vector)clus.elementAt(j);

		    double[] vec2 = Correlation.seqvec((Sequence)clusseqs.elementAt(0));
		    
		    Hashtable res = Correlation.get_with_shift(vec1,vec2,seq,thresh);

		    double maxcorr = ((Double)res.get("maxcorr")).doubleValue();
		    String newseq  = (String)res.get("seq");

		    String revstr = ResidueProperties.reverseComplement(seq);

		    double[] revvec1 = Correlation.seqvec(new Sequence(revstr,revstr,1,revstr.length()));

		    Hashtable revres = Correlation.get_with_shift(revvec1,vec2,revstr,thresh);

		    
		    double revmaxcorr = ((Double)revres.get("maxcorr")).doubleValue();
		    String revnewseq  = (String)revres.get("seq");

		    if (revmaxcorr > maxcorr) {
			maxcorr = revmaxcorr;
			newseq  = revnewseq;
		    }
		    if (maxcorr > thresh) {
			System.out.println("Add to cluster " + newseq + "\t" + ((Sequence)clusseqs.elementAt(0)).getSequence() + "\t" + maxcorr + "\t" + clusseqs.size());
			found = 1;
			clusseqs.addElement(new Sequence(newseq,newseq,1,newseq.length()));
			j = clus.size();
		    }
		}

		if (found == 0) {
		    Vector newclus = new Vector();
		    newclus.addElement(seqs.elementAt(i));
		    clus.addElement(newclus);
		} 

	    }

	    for (int i = 0; i < clus.size(); i++) {
		Vector cseqs = (Vector)clus.elementAt(i);

		System.out.print("Cluster\t" + cseqs.size());

		for (int j = 0; j < cseqs.size(); j++) {
		    if (j == 0) {
			System.out.print("\t--" + ((Sequence)cseqs.elementAt(j)).getSequence());
		    } else {
			System.out.print("\t" + ((Sequence)cseqs.elementAt(j)).getSequence());
		    }
		}
		System.out.println();
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
	    }
	}
	return seqvec;
    }
}
