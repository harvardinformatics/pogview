package pogvue.io;

import java.io.IOException;
import java.util.*;

import pogvue.datamodel.*;
import pogvue.datamodel.motif.*;


public class OrmatFile extends FileParse {
    Vector  matrices;

    public OrmatFile(String name,String type) throws IOException {
	super(name,type);

	matrices = new Vector();

	parse();
    }
    public void parse() throws IOException {


	  String line;
	  int    linenum = 0;

	  while ((line = nextLine()) != null) {
	    String name = line;

	    String desc  = nextLine();
	    String name2 = nextLine();

	    int j = 0;   // File cols are positions, rows are bases  acgt
		  
	    String as = nextLine();
	    String cs = nextLine();
	    String gs = nextLine();
	    String ts = nextLine();

	    nextLine();

	    StringTokenizer astr = new StringTokenizer(as," ");
	    StringTokenizer cstr = new StringTokenizer(cs," ");
	    StringTokenizer gstr = new StringTokenizer(gs," ");
	    StringTokenizer tstr = new StringTokenizer(ts," ");

	    int numpos = astr.countTokens();

	    double tmp[] = new double[4];

	    double[][] mat = new double[numpos][4];

	    int k = 0;

	    while (k < numpos) {
	      
	      mat[k][0] = Double.parseDouble(astr.nextToken());   // a
	      mat[k][1] = Double.parseDouble(tstr.nextToken());   // t
	      mat[k][2] = Double.parseDouble(cstr.nextToken());   // c
	      mat[k][3] = Double.parseDouble(gstr.nextToken());   // g

		k++;
	    }

	    TFMatrix tf = new TFMatrix(mat,numpos,4);
		  
	    tf.setAcc(name2);
	    tf.setId(name);

	    int inf = (int)(tf.getPwm().getInfContent());

	    tf.setName(name + "." + inf);
	    tf.setDesc(desc);
	    tf.setConsensus(Pwm.getConsensus(tf.getPwm().getPwm()));

	    matrices.addElement(tf);
	    
	  }
    }

    public Vector getMatrices() {
	return matrices;
    }
    public Vector getTFMatrices() {
	return matrices;
    }
    public static void main(String[] args) {
        try {
	    OrmatFile tf = new OrmatFile(args[0],"File");
	    
	    Vector m = tf.getMatrices();
	    
	    String seq  = "CATGCACTTTTATTACTTTAGGTATAAACATATTTTTTAACTCATTAAAATTTTGATTTCAATTTCTAAGTGATTATAAGTTCCAGTGGACTAAATGCATTCTAGTTGATCTTGTTTTGTGATCACTCTACTGCTGATTAGAATCTCTACTAGAAATTTTGTATTTTCAAGAGGAGGAACAAAGAGCAGAAATGGGATAAGTAAAATTTTAACTATCTTCTTGGGTTCAATTGTATTGCTCTGGTGAGAGAACTCAGCAGCAGGCAGAAGAGGACAAAGGCATGATGCATATTCAATGAGTAATGCACTTATAAGTAAGT----TTCTTTAAATATAAAACATATTGAAGATAATAATACTCTATTCCTAGCTAAGGTTATAGTCTTGTCTTCTCATGAGAGTAAATATCCTTGAGGACAGGTACCACCTCTGTTCAAAATTGATCCTTCCCAACCCCTACTTCCACACTACCCCATACATTTGTAGGCAGGCACTAGATTGAGCTCAATAAATGTTTTGAGGAGTAACTAACAGAGTTATCATCACAACAGCTTCAGTTAAACTTAGAGCTATTTCTACTTGTCTGATGAGTGTCATTTTTTGTTCTGTAGTCTCTCCCTAATGAATCCTTCTTTTTAGTAATTCTTAAAATTCTATTGGAACTCTAAAACAGTTGAAAAGAGAGCAGGCATAGGGCCATACCAGTCATTTCCAAGATAAAAATGGTCTCAGATAAGATTCTAAAGAAAGTCCACTCCTCTTTGTATGTTTCAAAATGGCTTCAAGTGTTGTTTTGCCATTTAAGTGTGAATGTACTTGGCTTATGAAAGTAGATAGAAACTCAAAATTATTACTAATAGTGAAGTCATGATTTTTTAAGAGATTGAGACAGCAAATAAAATTGGAGAAAAATTCTTCTTTATTTAAAAATACCAGTAATACTGACAGACTTCAAAAGCAATTCACGCTTCCAGAATACAAAGTACTTAATACATATTTTCAAACCTGTTTGCATTTCAAACAAAGTTAGCGTTTTTGTAAATCAAATTTGATAACCCGACTAAAAATATTTTCCAGCTTTATTATTTAAGGAGCTGCACAGCCTTTAAAGTGGGGACCAGGAGGCAGGCAGAGGCAGAGAGACTGAATGCACCCAGGACTGCGCAGCAGTCTACAGCAACATGTCCCACAACTTTGGTGCTGGAAACACAAGTAATGCACAAGACAGCTGCCCTCCAGTGTCAGGATCCTGTGAAACAGCATATCAAAAGATCGCCAGCTTCTTATAATTTACACACTTTCATTTAGGATTGCTTTTTGAAGAAAATCTTTAAGAATGCCATTTTAATTTAATATCCAGAACCCTG";

	    for (int i = 0 ; i < m.size() ; i++) {
		
		TFMatrix tfm = (TFMatrix)m.elementAt(i);
		
		System.out.println("Matrix " + tfm.getName() + " " + tfm.getConsensus() + " " + tfm.getDesc() + tfm.getPwm().getPwm().length );
		PwmLineFile.print(tfm.getPwm());

		int len = tfm.getPwm().getLength();

		int j = 0;
		while (j < seq.length()-len) {
		  String tmp = seq.substring(j,j+len);
		  double score = tfm.getLogScore(tmp);

		  System.out.println("Score\t" + tfm.getName() + "\t" + tfm.getConsensus() + "\t" + tmp + "\t" + score);
		  j++;
		}
		
	    }
	} catch (IOException e) {
	    System.out.println("Exception " + e);
	}
   }
}




