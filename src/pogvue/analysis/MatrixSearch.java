package pogvue.analysis;

import java.util.*;
import java.io.*;

import pogvue.datamodel.*;
import pogvue.datamodel.motif.*;
import pogvue.io.*;



public class MatrixSearch {


    public static Vector alignSearch(Vector matrices,Alignment al) {
	
	Vector out = new Vector();

	for (int i = 0; i < matrices.size(); i++) {
	    TFMatrix tf = (TFMatrix)matrices.elementAt(i);

	    double[][] vals = tf.getValues();

	    double log_base2 = 1.0/Math.log(2.0);

	    // j is position in motif
	    
	    System.out.println("Matrix " + tf.getName() + " " + tf.getConsensus() + " " + tf.getDesc());

	    for (int k = 0; k < al.getWidth() - tf.getRows(); k++) {

		int species  = 0;
		double totscore = 0;

		for (int s = 0; s < al.getHeight(); s++) {

		    Sequence seq = al.getSequenceAt(s);
		    double score = 0;
		    int count = 0;
		    for (int j = 0; j < tf.getRows(); j++) {

			// Cols go A C GT

			double tot = vals[j][0] + vals[j][1] + vals[j][2] + vals[j][3];
			
			char c       = seq.getCharAt(k+j);

			// Assumes background .25, .25, .25, .25 for now
			
			double val = vals[j][0]/tot;

			if (c == 'A') { val = vals[j][0]/tot;
			} else if (c == 'C') { val = vals[j][1]/tot;
			} else if (c == 'G') { val = vals[j][2]/tot;
			} else if (c == 'T') { val = vals[j][3]/tot;}
			
			if (val > 0) {
			    score += Math.log(val/0.25)*log_base2;
			    count++;
			}
		    }
		    if (count+1 >= tf.getRows()) {
			    species++;
			    totscore += score;
			    //	    System.out.println("Score for matrix " + tf.getName() + " " + k + " "  + seq.getSequence(k,k+tf.getRows()) + " " + score + " " + tf.getRows() + " " + count);
		    }
		    
		}
		if (species > 0) {
		    totscore = totscore/(species * tf.getRows());
		    if (totscore > 1) {
			System.out.println("Score for alignment " + tf.getName() + " " + k + " " + al.getSequenceAt(0).getSequence(k,k+tf.getRows()) + " " + species + " " + totscore);
		    }
		} else {
		    totscore = 0;
		}

	    }
	}
	return out;
    }
    public static void main(String[] args) {
	try {
	    String seqfile = args[0];
	    String matfile = args[1];
	    
	    //GappedFastaFile f = new GappedFastaFile(seqfile,"File");
	    FastaFile ff = new FastaFile(seqfile,"File");
	    
	    Sequence[] s  = ff.getSeqsAsArray();
	    
	    Alignment al   = new Alignment(s);

	    TFMatrixFile tff   = new TFMatrixFile(matfile,"File");
	    
	    Vector matrices = tff.getMatrices();
	    
	    for (int i = 0 ; i < s.length; i++) {
	        Sequence[] tmp = new Sequence[1];
                tmp[0] = s[i];
		Vector pos = MatrixSearch.alignSearch(matrices,new Alignment(tmp));
		
	    }
	} catch (IOException e) {
	    System.out.println("Error:" + e);
	}
    }
}
	
