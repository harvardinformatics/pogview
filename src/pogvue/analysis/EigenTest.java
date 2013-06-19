package pogvue.analysis;
      
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


public class EigenTest implements KeyListener {
  private Sequence[] s;
  private TreePanel  p;
  private Frame      f;
  private String     treefile;
  private Vector     leaves;
  private Vector     leafnames;
  private Tree       tree;
  private int        num = 0;
  private int        len = 0;

  public int        start;

  private double[] background = new double[] { 0.3, 0.2, 0.2, 0.3};
  private double[][] R = new double[][] {
    {0,   0.25, 0.5, 0.25},
    {0.25, 0,   0.25, 0.5},
    {0.5, 0.25, 0,   0.25},
    {0.25, 0.5, 0.25, 0.0}};
  
  private double[][] Q;


  public static void main(String[] args) {

    EigenTest et = new EigenTest(args[0]);

    et.start = Integer.parseInt(args[1]);
    et.calc();
  }

  public EigenTest(String treefile) {
    this.treefile = treefile;

    Q = Matrix.mult(R,background);    

    Matrix.scaleQ(Q,background);
    Matrix.printQ(Q,background);
  }

  public void calc() {
    try {
      //TreeFile tf   = new TreeFile(treefile,"File");

      //tree = tf.getTree();
      AlignViewport av = new AlignViewport();

      tree = av.getCurrentTree();

    String chr   = "chr1";
    int    len1   = 240000000;
    //start = (int)(Math.random()*len1);
    int    end   = start + 50000;

    len  = (end-start+1);


    end   = start + 10000;
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

      if (valid >= 20) {
	found = true;
      } else {
	num++;
      }
    }

    if (found == true) {
      System.out.println("\n\nSite " + (start+num-1) + " " + tree.getTopNode());
      
      tree.removeProbs(site);
      tree.calcLikelihood(tree.getTopNode(),Q,background,num);
      tree.assignBases(tree.getTopNode(),site);
      p.treeCanvas.repaint();
    }
  }
}
