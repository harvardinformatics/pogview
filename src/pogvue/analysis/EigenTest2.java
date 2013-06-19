package pogvue.analysis;
      
import cern.colt.matrix.*;

import cern.colt.function.DoubleFunction;
import cern.colt.matrix.linalg.Algebra;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import pogvue.analysis.*;
import pogvue.io.*;
import pogvue.gui.*;
import pogvue.gui.hub.*;
import pogvue.datamodel.Sequence;
import pogvue.datamodel.tree.*;
import pogvue.math.Matrix;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;


public class EigenTest2 implements KeyListener {
  private Sequence[] s;
  private TreePanel  p;
  private Frame      f;
  private String     treefile;
  private Vector     leaves;
  private Vector     leafnames;
  private Tree       tree;
  private int        num = 0;
  private int        len = 0;
  
  private double[] background = new double[] { 0.3, 0.2, 0.2, 0.3};
  private double[][] R = new double[][] {
    {0,   0.25, 0.5, 0.25},
    {0.25, 0,   0.25, 0.5},
    {0.5, 0.25, 0,   0.25},
    {0.25, 0.5, 0.25, 0.0}};
  
  private double[][] Q;


  public static void main(String[] args) {

    EigenTest2 et = new EigenTest2(args[0]);
    
    et.calc();
  }
  public EigenTest2(String treefile) {
    this.treefile = treefile;

    Q = Matrix.mult(R,background);    

    Matrix.scaleQ(Q,background);
    Matrix.printQ(Q,background);
  }

  public void calc() {
    try {
    TreeFile tf   = new TreeFile(treefile,"File");

    tree = tf.getTree();

    String chr   = "chr1";
    int    len1   = 240000000;
    int    start = (int)(Math.random()*len1);
    int    end   = start + 50000;

    len  = (end-start+1);
    String regionStr = "query=" + chr + "&start=" + start + "&end=" + end  + "&z=2";

    FastaFile ff = GenomeInfoFactory.getHumanRegion(regionStr);

    ff.parse();
    s = ff.getSeqsAsArray();

    tree.setSequences(s);
    
    leaves    = tree.getLeaves();
    leafnames = new Vector();

    for (int i = 0; i < leaves.size(); i++) {
      BinaryNode node = (BinaryNode)leaves.elementAt(i);
      leafnames.addElement(node.getName());
    }

    p = new TreePanel(null,tree);
    p.treeCanvas.addKeyListener(this);
    f = new Frame();
    
    f.setLayout(new BorderLayout());
    f.add("Center",p);
    f.setSize(500,500);
    f.show();

    } catch(IOException e) {
      e.printStackTrace();
    }
  }
  public void keyTyped(KeyEvent e) { }
  public void keyReleased(KeyEvent e) { }
  
  public void keyPressed(KeyEvent e) {

    char c = e.getKeyChar();

    num++;

    int     site  = num;
    boolean found = false;

    System.out.println("Num " + num + " " + len);
    while (num < len && found == false) {
      int     gaps  = 0;
      int     i     = 0;
      int     valid = 0;

      while (i < s.length) {
	if (leafnames.contains(s[i].getName())) {
	  if (s[i].getCharAt(num) == '-') {
	    gaps++;
	  } else {
	    valid++;
	  }
	}
	i++;
      }
      System.out.println("Valid for " + num + " " + valid);
      if (valid >= 20) {
	found = true;
      } else {
	num++;
      }
    }

    if (found == true) {
      System.out.println("\n\nSite " + num + " " + tree.getTopNode());
      
      tree.removeProbs(site);
      tree.calcLikelihood(tree.getTopNode(),Q,background,num);
      tree.assignBases(tree.getTopNode(),site);
      p.treeCanvas.repaint();
    }
  }



  public static double[][] expMatrix(double[][] mat, final double t) {

    DoubleFactory2D factory2D = DoubleFactory2D.sparse;
    Algebra         algebra = Algebra.DEFAULT;
    

    DoubleMatrix2D Q = factory2D.make(mat);

    //System.out.println(Q);

    EigenvalueDecomposition eigenDecomp = new EigenvalueDecomposition(Q);

    DoubleMatrix1D eigenValue  = factory2D.diagonal(eigenDecomp.getD());
    DoubleMatrix2D eigenVector = eigenDecomp.getV();

    eigenValue.assign(new DoubleFunction() {
	public final double apply(double a) {
	  return Math.exp(a * t);
	}
      }
      );

      DoubleMatrix2D P = algebra.mult(algebra.mult(eigenVector,
						   factory2D.diagonal(eigenValue)),
				      algebra.inverse(eigenVector));
      //System.out.println(P);

      return P.toArray();
  }

  public static void test2() {
    DoubleFactory2D factory2D = DoubleFactory2D.sparse;
    Algebra algebra = Algebra.DEFAULT;
 
    final double t = 0.01;
    //DoubleMatrix2D Q = factory2D.make(new double[]{1,2,3,2,4,5,3,5,6}, 3);
    DoubleMatrix2D Q = factory2D.make(new double[]{1,0,0,0,4,0,0,0,6}, 3);
    System.out.println(Q);
 
    EigenvalueDecomposition eigenDecomp = new EigenvalueDecomposition(Q);
    DoubleMatrix1D eigenValue = factory2D.diagonal(eigenDecomp.getD());
    DoubleMatrix2D eigenVector = eigenDecomp.getV();
 
    eigenValue.assign(
       new DoubleFunction() {
          public final double apply(double a) { return Math.exp(t * a); }
       }
    );
 
    DoubleMatrix2D P = algebra.mult(
            algebra.mult(eigenVector, factory2D.diagonal(eigenValue)),
            algebra.inverse(eigenVector));
 
    System.out.println(P);
  }

  public static void test(String treefile) {

    try {
      TreeFile tf = new TreeFile(treefile,"File");
      Tree tree = tf.getTree();

      double alpha    = 0.4;
      double beta     = 0.3;
      
      double[] background = new double[] { 0.3, 0.2, 0.2, 0.3};
      
      // R matrix is :
      
      double[][] R = new double[][] {
	{0,   0.25, 0.5, 0.25},
	{0.25, 0,   0.25, 0.5},
	{0.5, 0.25, 0,   0.25},
	{0.25, 0.5, 0.25, 0.0}};
      
      double[][] Q = mult(R,background);
      
      scaleQ(Q,background);
      
      System.out.println("\nQ Matrix\n");
      printmat(Q);
      
      printQ(Q,background);
      
      double t = 0;
      
      String str1 = "CCAT";
      String str2 = "CCAG";
      
      String chr = "chr1";
      
      int len = 240000000;

      int start = (int)(Math.random()*len);
      int    end   = start + 50000;
    
    String regionStr = "query=" + chr + "&start=" + start + "&end=" + end
        + "&z=2";

    FastaFile ff = GenomeInfoFactory.getHumanRegion(regionStr);
    ff.parse();
    Sequence[] s = ff.getSeqsAsArray();
    tree.setSequences(s);
    
    Vector leaves = tree.getLeaves();
    Vector leafnames = new Vector();
    for (int i = 0; i < leaves.size(); i++) {
      BinaryNode node = (BinaryNode)leaves.elementAt(i);
      leafnames.addElement(node.getName());
    }

    System.out.println("Leaves " + leaves);

    TreePanel p = new TreePanel(null,tree);
    Frame     f = new Frame();
    
    f.setLayout(new BorderLayout());
    f.add("Center",p);
    f.setSize(500,500);
    f.show();

    int num = 0;
    while (num < (end-start+1)) {
      int gaps = 0;
      int i = 0;
      while (i < s.length) {
	//System.out.println("Char " + s[i].getCharAt(num) + " " + gaps);
	if (leafnames.contains(s[i].getName()) && s[i].getCharAt(num) == '-') {
	  //if ( s[i].getCharAt(num) == '-') {
	  gaps++;
	}
	i++;
      }

      if (gaps < 10) {

	System.out.println("\n\nSite " + num + " " + tree.getTopNode());

	tree.removeProbs(num);
	tree.calcLikelihood(tree.getTopNode(),Q,background,num);
	tree.assignBases(tree.getTopNode(),num);
	p.treeCanvas.repaint();
	
	
	try {
	  Thread.sleep(2000);
	} catch(InterruptedException e) {
	  e.printStackTrace();
	}
      }
      num++;
    }

    while (t < .5) {
      double[][] expq = expMatrix(Q,t);

      
      double dist = getDist(str1,str2,expq,background);

      System.out.println("Distance\t" + t + "\t" + dist);
      System.out.println();
      t += .1;
    }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public static double[][] constmult(double[][] mat, double c) {
    double[][] newmat = copy(mat);
    
    int i = 0;
    while (i < 4) {
      int j = 0;
      while (j < 4) {
	newmat[i][j] *= c;
	j++;
      }
      i++;
    }
    return newmat;
  }
      
  public static void printQ(double[][] mat, double[] background) {
    double sum = 0;
    
    int i = 0;
    while (i < 4) {
      int j = 0;
      while (j < 4) {
	System.out.printf("%7.4f\t", mat[i][j]*background[i]);
	j++;
      }
      System.out.println();
      i++;
    }
    System.out.println();
  }

  public static int[] str2arr(String str) {
    int[]  c = new int[str.length()];
    char[] a = str.toUpperCase().toCharArray();

    int i = 0;
    while (i < str.length()) {
      if (a[i] == 'A') {
	c[i] = 0;
      } else if (a[i] == 'C') {
	c[i] = 1;
      } else if (a[i] == 'G') {
	c[i] = 2;
      } else if (a[i] == 'T') {
	c[i] = 3;
      }
      i++;
    }
    return c;
  }

  public static double getDist(String str1, String str2,double[][] mat, double[] background) {

    int[] c1 = str2arr(str1);
    int[] c2 = str2arr(str2);

    int i = 0;
    double dist = 1;

    while (i < c1.length) {
      int base1 = c1[i];
      int base2 = c2[i];

      
      dist  *= background[base1] * mat[base1][base2];
      i++;
    }
    return dist;
  }
    
  public static void scaleQ(double[][] mat, double[] background) {
    double sum = 0;

    int i = 0;

    while (i < 4) {

      int j = 0;

      while (j < 4) {
	if (i != j) {
	  sum += mat[i][j]*background[i];
	}
	j++;
      }
      i++;
    }
    i = 0;
    while (i < 4) {
      int j = 0;
      double rowsum = 0;
      while (j < 4) {
	if (i != j) {
	  mat[i][j] /= (sum);
	  rowsum += mat[i][j];
	}
	j++;
      }
      mat[i][i] = -1 * rowsum;
      i++;
    }
  }

  public static double[][] mult(double[][] mat, double[] vect) {
    double[][] newmat = copy(mat);

    int i = 0;
    while (i < 4) {
      int j = 0;
      while (j < 4) {

	newmat[i][j] *= vect[j];

	j++;
      }
      i++;
    }
    return newmat;
  }

  public static double[][] divide(double[][] mat, double[] vect) {
    double[][] newmat = copy(mat);

    int i = 0;
    while (i < 4) {
      int j = 0;
      while (j < 4) {

	newmat[i][j] /= vect[i];

	j++;
      }
      i++;
    }
    return newmat;
  }

  public static void printmat(double[][] mat) {
    int i = 0;
    while (i < 4) {
      int j = 0;
      while (j < 4) {

	System.out.printf("%7.4f",mat[i][j]);

	if (j < 4) {
	  System.out.print("\t");
	}
	j++;

      }
      System.out.println();
      i++;
    }
    System.out.println();
  }

  public static double[][] copy(double[][] mat){ 
    double[][] newmat = new double[4][4];

    int i = 0;
    while (i < 4) {
      int j = 0;
      while (j < 4) {

	newmat[i][j] = mat[i][j];

	j++;
      }
      i++;
    }
    return newmat;
  }
}
