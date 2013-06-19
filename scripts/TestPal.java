import java.util.*;
import java.io.*;

import pal.alignment.*;
import pal.tree.*;
import pal.treesearch.*;
import pal.substmodel.*;

import pogvue.datamodel.*;
import pogvue.io.*;


public class TestPal {

  public static void main(String[] args) {
    
    try {
      FastaFile ff = new FastaFile(args[0],"File");
      
      
      pogvue.datamodel.Sequence[] seqs = ff.getSeqsAsArray();
      
      
      pal.alignment.AlignmentBuilder ab = new pal.alignment.AlignmentBuilder(seqs.length);


      char[][] chars = new char[seqs.length][1000];

      for (int i = 0; i < seqs.length; i++) {
	Sequence seq = seqs[i];
	
	for (int j = 0; j < 1000; j++) {

	  chars[i][j] = seq.getCharAt(j);
	}
      }

      int[][] states = pal.alignment.DataTranslator.toStates(chars,new pal.datatype.Nucleotides());

      
      for (int i = 0; i < seqs.length; i++) {
	ab.addSequence(states[i],((Sequence)seqs[i]).getName());
      }
      
      pal.alignment.Alignment al = ab.generateAlignment(new pal.datatype.Nucleotides());

      PrintWriter pw = new PrintWriter(System.out);

      pal.alignment.AlignmentUtils.print(al,pw);
      pw.flush();
      
      String treestr = "(((((((Human: 0.003731, Chimp: 0.005501): 0.013010, RheMac: 0.031571): 0.058623, (MouseLemur: 0.084110, Bushbaby: 0.145437): 0.033956): 0.008977, TreeShrew: 0.203975): 0.001109, ((((Rat: 0.109421, Mouse: 0.104920): 0.231729, Squirrel: 0.176404): 0.004461, Cavia: 0.252149): 0.019799, (Rabbit: 0.139029, Pika: 0.219638): 0.091246): 0.003896): 0.015639, ((Shrew: 0.309867, Hedgehog: 0.279121): 0.023929, (((Dog: 0.114682, Cat: 0.098674): 0.048296, Bat: 0.160442): 0.002783, Cow: 0.162368): 0.007170): 0.014246): 0.003381, (Armadillo: 0.178799, (Elephant: 0.110021, Tenrec: 0.265218): 0.046339): 0.016734);";
      
      
      StringReader sr = new StringReader(treestr);
      
      pal.tree.Tree tree = pal.tree.TreeTool.readTree(sr);

      pal.treesearch.TreeSearchTool ptt = new pal.treesearch.TreeSearchTool();

      
      double[] freqs = pal.alignment.AlignmentUtils.estimateFrequencies(al);
      double a = 4;
      double b = 1;
      double c = 3;
      double d = 10;
      double e = 2;

      pal.substmodel.SubstitutionModel subst =  pal.substmodel.SubstitutionTool.createGTRModel(a,b,c,d,e,freqs);
	

      double ll = ptt.calculateLogLikelihood(tree,al,subst);
      

      System.out.println("Log likelihood " + ll);

      pal.tree.TreeUtils.printNH(tree,pw);

      pw.flush();
      pw.close();



    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
  
