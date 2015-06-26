package pogvue.datamodel.tree;

import java.awt.Color;
import java.util.Hashtable;

public class BinaryNode {

  private Object element;
  private String name;
  private BinaryNode left;
  private BinaryNode right;
  public BinaryNode parent;
  private int bootstrap;
  private double[] prob;
  public float dist;
  private int[] maxbase;
  private double[] maxprob;
  private int maxchar = -1;
  public float ycount;
  public int count;
  public float height;
  public double mutationCount = 0;

  public int num;
  public int depth;

  public Color color = Color.black;


  private Hashtable probhash;
  private Hashtable maxbasehash;
  private Hashtable maxprobhash;
  private Hashtable maxcharhash;

  public BinaryNode() {
    left = right = parent = null;

    probhash    = new Hashtable();
    maxbasehash = new Hashtable();
    maxprobhash = new Hashtable();
    maxcharhash = new Hashtable();

  }

  public BinaryNode(Object element, BinaryNode parent,String name) {
    this.element = element;
    this.parent  = parent;
    this.name    = name;

    probhash    = new Hashtable();
    maxbasehash = new Hashtable();
    maxprobhash = new Hashtable();
    maxcharhash = new Hashtable();

    left=right=null;
  }

  public double getDist() {
    return dist;
  }
  public Object element() {
    return element;
  }

  public int getBootstrap() {
    //System.out.println("Getting bootstrap :" + bootstrap + ":");
    return bootstrap;
  }

  public String getName() {
return this.name;
	}

  public boolean isLeaf() {
    return (left == null) && (right == null);
  }

  public BinaryNode left() {
    return left;
  }

  public BinaryNode parent() {
    return parent;
  }

  public BinaryNode right() {
    return right;
  }

  public void setBootstrap(int boot) {
    this.bootstrap = boot;
    //System.out.println("Node bootstrap :" + bootstrap + ":");
	}

  public Object setElement(Object v) {
    return element=v;
  }

    public BinaryNode setLeft(BinaryNode n) {
		  return left=n;
		}
    public void setName(String name) {
	  this.name = name;
    }
	public BinaryNode setParent(BinaryNode n) {
    return parent=n;
  }
  public BinaryNode setRight(BinaryNode n) {
    return right=n;
  }

  public BinaryNode getParent() {
    return parent;
  }
  
  public double[] getProb() {
    return prob;
  }
  public void setProb(double[] p) {
    this.prob = p;
  }
  public double[] getProb(int site) {
    if (probhash.containsKey(site)) {
      return (double[])(probhash.get(site));
    } else {
      return null;
    }
  }
    
  public void setProb(int site, double[] p) {
    if (p != null) {
      probhash.put(site,p);
    } else {
      probhash.remove(site);
    }
  }
  public void setMaxBase(int[] c) {
    this.maxbase = c;
  }
  
  public int[] getMaxBase() {
    return maxbase;
  }
  public int[] getMaxBase(int site) {
    if (maxbasehash.containsKey(site)) {
      return (int[])(maxbasehash.get(site));
    } else {
      return null;
    }
  }

  public void setMaxBase(int site,int[] c) {
    if (c != null) {
      maxbasehash.put(site,c);
    } else {
      maxbasehash.remove(site);
    }
  }
  public void setMaxProb(double[] p) {
    this.maxprob = p;
  }
  public double[] getMaxProb() {
    return maxprob;
  }
  public double[] getMaxProb(int site) {
    if (maxprobhash.containsKey(site)) {
      return (double[])(maxprobhash.get(site));
    } else {
      return null;
    }
  }
  public void setMaxProb(int site, double[] c) {
    if (c != null) {
      maxprobhash.put(site,c);
    } else {
      maxprobhash.remove(site);
    }
  }

  public int getMaxChar() {
    return maxchar;
  }
  public void setMaxChar(int i) {
    this.maxchar = i;
  }

  public void setMaxChar(int site, int c) {
    maxcharhash.put(site, new Integer(c));
  }

  public int getMaxChar(int site) {
    if (maxcharhash.containsKey(site)) {
      return ((Integer)(maxcharhash.get(site))).intValue();
    } else {
      return -1;
    }
  }
  
}

