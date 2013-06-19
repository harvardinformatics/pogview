package pogvue.datamodel.tree;

import pogvue.datamodel.tree.BinaryNode;
import pogvue.datamodel.Sequence;
import pogvue.gui.schemes.ResidueProperties;
import pogvue.util.Comparison;
import pogvue.analysis.*;
import pogvue.math.Matrix;

import java.util.Vector;

public class Tree {

  public Sequence[] sequence;
  
  public BinaryNode maxdist;
  public  BinaryNode top;

  public  float      maxDistValue;
  public  float      maxheight;
  
  public  int        ycount;
  
  public Object     found = null;
  public Object     leaves = null;

  public Vector     groups;

  public int        site = 0;

  private boolean findMutations = true;

  private double[] background = new double[] { 0.3, 0.2, 0.2, 0.3};

  private double[][] R = new double[][] {
    {0,   0.25, 0.5, 0.25},
    {0.25, 0,   0.25, 0.5},
    {0.5, 0.25, 0,   0.25},
    {0.25, 0.5, 0.25, 0.0}};
  

  public Tree() {
  }
  public Tree(BinaryNode node) {
    top = node;
    maxheight = findHeight(top);
  }	

  public void setSite(int site) {
    this.site = site;
  }
  public int getSite() {
    return site;
  }
  public void setSequences(Sequence[] s) {
    this.sequence = s;
  }

  public Vector getLeaves() {
    Vector leaves = new Vector();
    return _getLeaves(getTopNode(),leaves);

  }
  private Vector _getLeaves(BinaryNode node, Vector leaves) {

    if (node.left() == null &&
	node.right() == null) {
      leaves.addElement(node);
      return leaves;
    } else {
      leaves = _getLeaves(node.left(),leaves);
      leaves = _getLeaves(node.right(),leaves);
      return leaves;
    }
  }

  public Vector findLeaves(BinaryNode node, Vector leaves) {
    if (node == null) {
      return leaves;
    }
    
    if (node.left() == null && node.right() == null) {
      leaves.addElement(node);
      return leaves;
    } else {
      findLeaves((BinaryNode)node.left(),leaves);
      findLeaves((BinaryNode)node.right(),leaves);
    }
    return leaves;
  }	

  public Object findLeaf(BinaryNode node, int count) {
    found = _findLeaf(node,count);

    return found;
  }
  
  private Object _findLeaf(BinaryNode node,int count) {
    if (node == null) {
      return null;
    }
    if (node.ycount == count) {
      found = node.element();
      return found;
    } else {
      _findLeaf((BinaryNode)node.left(),count);
      _findLeaf((BinaryNode)node.right(),count);
    }

    return found;
  }
  
  public void printNode(BinaryNode node) {
    if (node == null) {
      return;
    }
    System.out.println("Node " + node);
    if (node.left() == null && node.right() == null) {
      System.out.println("Leaf = " + ((Sequence)node.element()).getName());
      System.out.println("Dist " + node.dist);
      System.out.println("Boot " + node.getBootstrap());
    } else {
      System.out.println("Dist " + node.dist);
      printNode(node.left());
      printNode(node.right());
    }
  }

  public  void findMaxDist(BinaryNode node) {
    if (node == null) {
      return;
    }
    if (node.left() == null && node.right() == null) {

      float dist = node.dist;
      if (dist > maxDistValue) {
	  maxdist      = node;
	  maxDistValue = dist;
      }
    } else {
      findMaxDist(node.left());
      findMaxDist(node.right());
    }
  }
  public float getMaxHeight() {
    return maxheight;
  }
  public float findHeight(BinaryNode node) {

    if (node == null) {
      return maxheight;
    }

    if (node.left() == null && node.right() == null) {
      node.height = (node.parent()).height + node.dist;

      if (node.height > maxheight) {
        return node.height;
      } else {
        return maxheight;
      }
    } else {
      if (node.parent() != null) {
        node.height = (node.parent()).height + node.dist;
      } else {
        maxheight = 0;
        node.height = (float)0.0;
      }

      maxheight = findHeight((BinaryNode)(node.left()));
      maxheight = findHeight((BinaryNode)(node.right()));
    }
    return maxheight;
  }
  
  public BinaryNode root(BinaryNode node) {

    // The rerooting occurs half up the branch above this node


    // The parent node becomes the right node and the node the left node.

    // Recursing down the tree

    //              |--------@
    //       |------X
    //   %---#   ^
    //   |   |------------&
    //   |

    // Node X becomes left node - nothing needs to change
    // Node # (parent) becomes right node
    //
    // The parent to # becomes the parents left node (the right stays the same

    // Recurse up the parent (before swapping of course) and swap the parent and the  left node

    // When the parent is the top node - discard the top node - keep the right node

    BinaryNode newtop = new BinaryNode();

    newtop.setLeft(node.left());
    newtop.setRight(node);

    BinaryNode left  = newtop.left();
    BinaryNode right = newtop.right();

    left.dist  = left.dist/2;
    right.dist = left.dist;

    //System.out.println("Creating new top node " + newtop.left() + " " + newtop.right());

    BinaryNode tmp = node.parent;

    node.parent = newtop;
    node.setLeft(tmp);

    while (tmp.parent != null) {

      // System.out.println("Node " + tmp + " " + tmp.parent);
      BinaryNode tmp2 = tmp.left();
    
      //System.out.println("Tmp2 " + tmp2);

      tmp.setLeft(tmp.parent);
      tmp.parent = tmp2;

      tmp = tmp.left();

      // System.out.println("Node " + tmp + " " + tmp.parent);

    }

    // Cut out the top node and pin onto the right
    
    //pogvue.io.TreeFile.checkNode((BinaryNode)tmp.right());
    //System.out.println("Left node is " + tmp.left());
    BinaryNode tmp3 = tmp.left();

    tmp3.setLeft(tmp.right());
    tmp.right().parent = tmp3;
    (tmp.right()).dist = (tmp.right()).dist + (tmp3).dist;

    //System.out.println("Right node is " + tmp.right());

    pogvue.io.TreeFile.checkNode(newtop);
    top = newtop;
    reCount(top);
    findHeight(top);

    return top;
  }

  public void deleteNode(BinaryNode node) {


    // Remove node #
    //      -------1
    //   ---#                    
    //---@  -------2             
    //   ------3               
    //
    //   ---X           New leaf node
    //---@
    //   ------3
    //


    node.setLeft(null);
    node.setRight(null);

    node.setName("NewNode");
    node.setElement(new Sequence("NewNode","",0,0));

    reCount(top);
    findHeight(top);
  }
    
  public void addNode(BinaryNode node) {


    //      -------1
    //------#
    //   ^  -------2
    //
    //      -------1
    //   ---#                    
    //---@  -------2              newnode1
    //   ------3                  newnode2
    //
    //
    //  3 and @ are new nodes
    


    // Make the 2 new nodes
    BinaryNode newnode1 = new BinaryNode();
    BinaryNode newnode2 = new BinaryNode();


    if (node.parent.left() == node) {
      node.parent.setLeft(newnode1);
    } else {
      node.parent.setRight(newnode1);
    }
      
    newnode2.setName("NewNode");
    newnode2.dist   = node.dist;
    newnode2.parent = newnode1;
    newnode2.setElement(new Sequence("NewNode","",0,0));
    newnode1.parent = node.parent;
    newnode1.dist = node.dist/2;
    newnode1.setLeft(node);
    newnode1.setRight(newnode2);

    node.dist = node.dist/2;
    node.parent = newnode1;

    reCount(top);
    findHeight(top);

    pogvue.io.TreeFile.checkNode(top);
  }

  public void reCount(BinaryNode node) {
    ycount = 0;
    _reCount(node);
  }
  private void _reCount(BinaryNode node) {
    if (node == null) {
      return;
    }

    if (node.left() != null && node.right() != null) {
      _reCount((BinaryNode)node.left());
      _reCount((BinaryNode)node.right());

      BinaryNode l = (BinaryNode)node.left();
      BinaryNode r = (BinaryNode)node.right();

      node.count  = l.count + r.count;
      node.ycount = (l.ycount + r.ycount)/2;

    } else {
      node.count = 1;
      node.ycount = ycount++;
    }

  }
    public void swapNodes(BinaryNode node) {
	if (node == null) {
	    return;
	}
	BinaryNode tmp = (BinaryNode)node.left();

	node.setLeft(node.right());
	node.setRight(tmp);
    }
  public void setMaxDist(BinaryNode node) {
    this.maxdist = maxdist;
  }
  public BinaryNode getMaxDist() {
    return maxdist;
  }
  public BinaryNode getTopNode() {
    return top;
  }
  
  public int getMax(double[] p) {
    double max = 0;
    int maxnum = 0;

    int i = 0;

    while (i < 4) {
      if (p[i] > max) {
	max = p[i];
	maxnum = i;
      }
      i++;
    }
    return maxnum;
  }
  
  public void assignBases(BinaryNode node, int site) {
    if (sequence == null) {
      return;
    }
    if (node.left()  == null &&
	node.right() == null) {
      
      System.out.println("Leaf node " + node + " " + node.getMaxChar() + " " + node.getParent().getMaxChar());
      
      return;
   
    } else if (node.parent() == null) {

      int top = getMax(node.getMaxProb(site));

      node.setMaxChar(site, top);

      System.out.println("Top Base for " + node + " at " + site + " is " + top);

    }
    
    if (node.left().getMaxChar(site) == -1) {
      
      int parentchar = node.getMaxChar(site);

      System.out.println("Parent for " + node + " at " + site + " is " + parentchar);

      int childchar = node.left().getMaxBase(site)[parentchar];

      if (parentchar != childchar && findMutations) {

	node.left().mutationCount++;
      }

      //System.out.println("Parent is " + parentchar + " child is " + childchar);
      // Set the left node to Cx[i]

      node.left().setMaxChar(site,childchar);

      //System.out.println("Assigning from " + parentchar + " to " + node.left().getMaxChar());

      assignBases(node.left(),site);
    } 

    if (node.right().getMaxChar(site) == -1) {
      int parentchar = node.getMaxChar(site);

      int childchar = node.right().getMaxBase(site)[parentchar];

      if (parentchar != childchar && findMutations) {

	node.right().mutationCount++;
      }
      // Set the left node to Cx[i]
      //System.out.println("Assigning from " + parentchar + " to " + node.left().getMaxChar());

      node.right().setMaxChar(site,childchar);

      assignBases(node.right(),site);
      
    }
  }

  public void calcLikelihood() {

    double[][] Q = Matrix.mult(R,background);    

    Matrix.scaleQ(Q,background);

    int i = site;
    initMutationCount(getTopNode());
    while (i < site+10) {
      removeProbs(i);
      calcLikelihood(getTopNode(),Q,background,i);
      assignBases(getTopNode(),i);
      i++;
    }
  }

  private void initMutationCount(BinaryNode node) {
    
    node.mutationCount = 0;

    if (node.left() != null) {
      initMutationCount(node.left());
    }
    if (node.right() != null) {
      initMutationCount(node.right());
    }
  }

  private void normalizeMutationCounts(BinaryNode node, int size) {
    
    node.mutationCount  = (double)((int)(100*node.mutationCount/(size*node.dist)));

    //System.out.println("Mutation count " + node.mutationCount);

    if (node.left() != null) {
      normalizeMutationCounts(node.left(),size);
    }
    if (node.right() != null) {
      normalizeMutationCounts(node.right(),size);
    }
  }

  public void findMutations(int start, int end) {
    int i = start;

    System.out.println("Finding mutations");

    initMutationCount(getTopNode());

    double[][] Q = Matrix.mult(R,background);    

    Matrix.scaleQ(Q,background);

    findMutations = true;

    while (i <= end) {
      site = i;
      removeProbs(site);
      calcLikelihood(getTopNode(),Q,background,site);
      assignBases(getTopNode(),site);

      i++;
    }

    normalizeMutationCounts(getTopNode(),end-start+1);
    findMutations = false;
  }
  public void calcLikelihood(BinaryNode node,double[][] mat, double[] pi, int site) {

    if (sequence == null) {
      return;
    }
    
    //System.out.println("Node " + node + " " + node.left() + " " + node.right());
    //This calculation is for internal node reconstruction

    if (node.left()  == null &&
	node.right() == null) {
      
      // This is a leaf

      Sequence  s    = null;
      String    name = "";

      int i = 0;

      while (i < sequence.length) {
	String n = sequence[i].getName();
	if (node.getName().equals(n)) {
	  s = sequence[i];
	  name = n;
	  i = sequence.length;
	}
	i++;
      }

      char base = '-';

      if (s != null) {
	base = s.getSequence().toCharArray()[site];
      }

      if (base == '-') {
	node.setProb(new double[] {0.25,0.25,0.25,0.25});
	node.setMaxProb(new double[] {0.25,0.25,0.25,0.25});
	node.setMaxBase(new int[]{0,1,2,3});

	node.setProb(site,new double[] {0.25,0.25,0.25,0.25});
	node.setMaxProb(site,new double[] {0.25,0.25,0.25,0.25});
	node.setMaxBase(site,new int[]{0,1,2,3});
      }

      double[] p1  = new double[] { 1,0,0,0};
      double[] p2  = new double[] { 0,1,0,0};
      double[] p3  = new double[] { 0,0,1,0};
      double[] p4  = new double[] { 0,0,0,1};
      
      int[] c1  = new int[] { 1,0,0,0};
      int[] c2  = new int[] { 0,1,0,0};
      int[] c3  = new int[] { 0,0,1,0};
      int[] c4  = new int[] { 0,0,0,1};

      int[] b1 = new int[]{0,0,0,0};
      int[] b2 = new int[]{1,1,1,1};
      int[] b3 = new int[]{2,2,2,2};
      int[] b4 = new int[]{3,3,3,3};
      double     tz = node.getDist();

      //System.out.printf("Leaf node dist %7.4f\n",tz);
      double[][] PZ = Matrix.exponent(mat,tz);      

      double[] maxprob = new double[] { 0,0,0,0};

      if (base == 'A') {
	node.setProb(p1);
	node.setMaxBase(b1);

	node.setProb(site,p1);
	node.setMaxBase(site,b1);

	maxprob[0] = PZ[0][0];
	maxprob[1] = PZ[1][0];
	maxprob[2] = PZ[2][0];
	maxprob[3] = PZ[3][0];

	node.setMaxProb(maxprob);

	node.setMaxProb(site,maxprob);

      } else if (base == 'C'){
	node.setProb(p2);
	node.setMaxBase(b2);

	node.setProb(site,p2);
	node.setMaxBase(site,b2);

	maxprob[0] = PZ[0][1];
	maxprob[1] = PZ[1][1];
	maxprob[2] = PZ[2][1];
	maxprob[3] = PZ[3][1];

	node.setMaxProb(maxprob);

	node.setMaxProb(site,maxprob);

      } else if (base == 'G'){
	node.setProb(p3);
	node.setMaxBase(b3);

	node.setProb(site,p3);
	node.setMaxBase(site,b3);

	maxprob[0] = PZ[0][2];
	maxprob[1] = PZ[1][2];
	maxprob[2] = PZ[2][2];
	maxprob[3] = PZ[3][2];

	node.setMaxProb(maxprob);

	node.setMaxProb(site,maxprob);


      } else if (base == 'T'){
	node.setProb(p4);
	node.setMaxBase(b4);

	node.setProb(site,p4);
	node.setMaxBase(site,b4);

	maxprob[0] = PZ[0][3];
	maxprob[1] = PZ[1][3];
	maxprob[2] = PZ[2][3];
	maxprob[3] = PZ[3][3];

	node.setMaxProb(maxprob);

	node.setMaxProb(site,maxprob);


      } else {
	node.setProb(new double[] { 0.25,0.25,0.25,0.25});
	node.setMaxProb(new double[] { 0.25,0.25,0.25,0.25});
	node.setMaxBase(new int[]{0,1,2,3});

	node.setProb(site,new double[] { 0.25,0.25,0.25,0.25});
	node.setMaxProb(site,new double[] { 0.25,0.25,0.25,0.25});
	node.setMaxBase(site,new int[]{0,1,2,3});
      }
      // ***** POG ************
      double[] m = node.getMaxProb(site);
      int[]    c = node.getMaxBase(site);

      //System.out.printf("MaxProb for leaf %10s %c is %5d\t%7.4f\t%7.4f\t%7.4f\t%7.4f\n",node.getName(),base,i , m[0],m[1],m[2],m[3]);

      i = 0;
      while (i < 4)  {
	//System.out.printf("If parent is %d - leaf is %d\n",i,c[i]);
	i++;

      }

      calcLikelihood(node.getParent(),mat,pi,site);
    } else if (node.left().getProb(site) == null) {
      calcLikelihood(node.left(),mat,pi,site);

    } else if (node.right().getProb(site) == null)  {
      calcLikelihood(node.right(),mat,pi,site);

    } else {

      // Calculate the max prob and char assignments
      //                 |   tz
      //                Z|
      //           -------------
      //          |            |
      //    t1    |            |    t2   
      //          |            |
      //          X            Y


      double z[] = new double[4];

      int base1 = 0;
      int base2 = 0;
      
      double t1 = node.left().getDist();    //  Get these from the daughter nodes
      double t2 = node.right().getDist();
      double tz = node.getDist();

      int i = 0;

      double[] dprob1 = node.left().getMaxProb(site);
      double[] dprob2 = node.right().getMaxProb(site);

      double[][] PZ = Matrix.exponent(mat,tz);

      // For each i
      // so Lz(i)  = maxj Pij(tz)  * Lx(j) * Ly(j)

      int[]    maxbase = new int[4];
      double[] maxprob = new double[4];

      while (i < 4) {
	int j = 0;

	int    maxb = -1;
	double maxl = -1;

	//System.out.printf("\nFinding max for %7.4f\t%5d\t%7.4f\t%7.4f\t%7.4f\n",tz,i ,PZ[i][j], dprob1[j],dprob2[j]);

	while (j < 4) {

	  double prob = PZ[i][j]  * dprob1[j] * dprob2[j];

	  //System.out.println("Prob " + prob);

	  if (prob > maxl) {
	    maxb = j;
	    maxl    = prob;
	  }

	  j++;

	}

	//System.out.println("\nMax base is " + maxb + " " + maxl);

	maxbase[i] = maxb;
	maxprob[i] = maxl;
	
	i++;
      }
      
      node.setMaxProb(maxprob);
      node.setProb(maxprob);
      node.setMaxBase(maxbase);
      
      node.setMaxProb(site,maxprob);
      node.setProb(site,maxprob);
      node.setMaxBase(site,maxbase);
      
      if (node.getParent() != null) {
	calcLikelihood(node.getParent(),mat,pi,site);
      } else {
	// Top node.
	double max = 0;
	int    num = 0;
	int maxnum = 0;

	while (num < 4) {
	  double p = pi[num] * node.left().getMaxProb(site)[num] * node.right().getMaxProb(site)[num];
	  if (p > max) {
	    max = p;
	    maxnum = num;
	  }
	  num++;
	}

	if (maxnum == 0) {
	  node.setMaxProb(site, new double[]{1,0,0,0});
	} else if (maxnum == 1) {
	  node.setMaxProb(site, new double[]{0,1,0,0});
	} else if (maxnum == 2) {
	  node.setMaxProb(site, new double[]{0,0,1,0});
	} else if (maxnum == 3) {
	  node.setMaxProb(site, new double[]{0,0,0,1});
	}
	return;
      }
    }
  }

  public void removeProbs(int site) {
    _removeProbs(getTopNode(),site);
    }
    
  private void _removeProbs(BinaryNode node,int site) {
      
      if (node.isLeaf()) {
    node.setProb(null);
    node.setMaxProb(null);
    node.setMaxBase(null);
    node.setMaxChar(-1);

    node.setProb(site,null);
    node.setMaxProb(site,null);
    node.setMaxBase(site,null);
    node.setMaxChar(site,-1);

  } else {
    node.setProb(null);
    node.setMaxProb(null);
    node.setMaxBase(null);
    node.setMaxChar(-1);

    node.setProb(site,null);
    node.setMaxProb(site,null);
    node.setMaxBase(site,null);
    node.setMaxChar(site,-1);

    _removeProbs(node.left(),site);
    _removeProbs(node.right(),site);
      }
    }

    public Vector getGroups() {
	return groups;
    }
  public void  groupNodes(BinaryNode node, float threshold) {
    if (node == null) {
      return;
    }

    if (node.height/maxheight > threshold) {
      groups.addElement(node);
    } else {
      groupNodes((BinaryNode)node.left(),threshold);
      groupNodes((BinaryNode)node.right(),threshold);
    }
  }


}

