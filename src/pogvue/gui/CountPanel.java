package pogvue.gui;

// I *think* this was written for the kmer x kmer matrix

import pogvue.datamodel.tree.SequenceNode;
import pogvue.io.FileParse;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

public final class CountPanel extends JPanel {
  public Vector v;
  private Vector counts;

  private int    rows;
  private int    cols;

  private int[][]    score;

  private int    mini;
  private int    minj;

  private Vector cluster = new Vector();

  private int[] done;
  private int noseqs;
  private int noClus;

  private int ycount;

  private int ri;
  private int rj;

  private SequenceNode maxdist;
  private SequenceNode top;

  private int maxheight;
  private int maxDistValue;
  private Vector node;

  public CountPanel(Vector counts,int rows,int cols) {
    this.counts = counts;
    this.rows   = rows;
    this.cols   = cols;
  }

  public void paint(Graphics g) {
    if (v == null) {
      cluster();
    }

    g.setColor(Color.white);
    g.fillRect(0,0,getSize().width,getSize().height);

    for (int i = 0; i < v.size();i++) {

      Vector c = (Vector)((SequenceNode)v.elementAt(i)).element();

      for (int j = 0; j < c.size(); j++) {

	int k = (Integer) c.elementAt(j);
	
	if (k > 20) {
	  g.setColor(Color.blue);
	} else if (k>10) {
	  g.setColor(Color.red);
	} else if (k > 5) {
	  g.setColor(Color.orange);
	} else if (k > 3) {
	  g.setColor(Color.yellow);
	} else {
	  g.setColor(Color.pink);
	}
	if (k > 0) {
	  g.fillRect(j*2,i*2,2,2);
	}
      }
    }
  }

  public void cluster() {
    node = new Vector();
    makeLeaves();

    score = new int[rows][rows];

    System.out.println("Rows " + rows);
    int num = counts.size();

    for (int i = 0; i < num-1; i++) {

      for (int k = i+1; k < num-1; k++) {
	
	Vector v1 = (Vector)counts.elementAt(i);
	Vector v2 = (Vector)counts.elementAt(k);

	score[i][k] = find_dist(v1,v2);

      }
    }

    noClus = num;

    int mind;

    done = new int[num];
    
    int ii = 0;

    while (ii < rows) {
      done[ii] = 0;
      ii++;
    }

    while (noClus > 2) {
      findMinDistance();
      System.out.println("No " + noClus + " " + mini + " " + minj);
      Cluster c = joinClusters(mini,minj);
      
      done[minj] = 1;
      
      cluster.setElementAt(null,minj);
      cluster.setElementAt(c,mini);
      
      noClus--;
    }
    
    boolean onefound = false;

    int one = -1;
    int two = -1;

    for (int i=0; i < rows; i++) {
      if (done[i] != 1) {
        if (!onefound) {
          two = i;
          onefound = true;
        } else {
          one = i;
        }
      }
    }

    Cluster c = joinClusters(one,two);
    top = (SequenceNode)(node.elementAt(one));

    reCount(top);
    findHeight(top);
    findMaxDist(top);
    
    //printNode(top);

    v = new Vector();

    orderLeaves(top,v);
  }

  private void orderLeaves(SequenceNode node,Vector v) {
    
    if (node == null) {
      return;
    }

    if (node.left() == null && node.right() == null) {
      v.addElement(node);
      //System.out.println("Leaf = " + node.element());
      //System.out.println("Dist " + node.dist);
      //System.out.println("Boot " + node.getBootstrap());
    } else {
      //System.out.println("Dist " + node.dist);
      orderLeaves((SequenceNode)node.left(),v);
      orderLeaves((SequenceNode)node.right(),v);
    }

  }
  private void reCount(SequenceNode node) {
    ycount = 0;
    _reCount(node);
  }
  private void _reCount(SequenceNode node) {
    if (node == null) {
      return;
    }
    
    if (node.left() != null && node.right() != null) {
      _reCount((SequenceNode)node.left());
      _reCount((SequenceNode)node.right());
      
      SequenceNode l = (SequenceNode)node.left();
      SequenceNode r = (SequenceNode)node.right();

      node.count  = l.count + r.count;
      node.ycount = (l.ycount + r.ycount)/2;

    } else {
      node.count = 1;
      node.ycount = ycount++;
    }

  }

  private void findMinDistance() {

  	int min = 10000000;
  	
  	for (int i = 0; i < rows-1; i++) {
  		for (int j = i+1; j < rows-1; j++) {
  			if (done[i] != 1 && done[j] != 1 && score[i][j] < min) {
  				mini = i;
  				minj = j;
  				
  				min = score[i][j];
  			}
      }
    }
  }
  private int find_dist(Vector v1, Vector v2) {
    int score = 0;

    for (int i = 0 ; i < v1.size(); i++) {

      int n1 = (Integer) v1.elementAt(i);
      int n2 = (Integer) v2.elementAt(i);

      score += (n1-n2)*(n1-n2);
	
    }
    score = (int)(Math.sqrt(score));
    return score;
  }

  private void makeLeaves() {
    cluster = new Vector();

    for (int i=0; i < rows; i++) {
      SequenceNode sn = new SequenceNode();

      sn.setElement(counts.elementAt(i));
      //sn.setName(counts.elementAt(i).toString());
      Integer in = i;
      sn.setName(in.toString());
      node.addElement(sn);

      int[] value = new int[1];
      value[0] = i;

      Cluster c = new Cluster(value);
      cluster.addElement(c);
    }	
  }


  private Cluster joinClusters(int i, int j) {
    //System.out.println("Joining " + i + " " + j);
    int dist = score[i][j];

    int noi = ((Cluster)cluster.elementAt(i)).value.length;
    int noj = ((Cluster)cluster.elementAt(j)).value.length;

    int[] value = new int[noi + noj];

    for (int ii = 0; ii < noi;ii++) {
      value[ii] =  ((Cluster)cluster.elementAt(i)).value[ii];
    }

    for (int ii = noi; ii < noi+ noj;ii++) {
      value[ii] =  ((Cluster)cluster.elementAt(j)).value[ii-noi];
    }

    Cluster c = new Cluster(value);
    
    ri = findr(i,j);
    rj = findr(j,i);

    findClusterDistance(i,j);

    SequenceNode sn = new SequenceNode();

    sn.setLeft((SequenceNode)(node.elementAt(i)));
    sn.setRight((SequenceNode)(node.elementAt(j)));

    SequenceNode tmpi = (SequenceNode)(node.elementAt(i));
    SequenceNode tmpj = (SequenceNode)(node.elementAt(j));

    findNewDistances(tmpi,tmpj,dist);

    tmpi.setParent(sn);
    tmpj.setParent(sn);

    node.setElementAt(sn,i);

    return c;
  }

  private int findr(int i, int j) {

    int tmp = 1;

    for (int k=0; k < rows;k++) {
      if (k!= i && k!= j && done[k] != 1) {
        tmp = tmp + score[i][k];
      }
    }

    if (noClus > 2) {
      tmp = tmp/(noClus - 2);
    }

    return tmp;
  }

  private void findClusterDistance(int i, int j) {

    int noi = ((Cluster)cluster.elementAt(i)).value.length;
    int noj = ((Cluster)cluster.elementAt(j)).value.length;

    // New distances from cluster to others
    int[] newdist = new int[rows];

    for (int l = 0; l < rows; l++) {
      if ( l != i && l != j) {
        newdist[l] = (score[i][l] * noi + score[j][l] * noj)/(noi + noj);
      } else {
        newdist[l] = 0;
      }
    }

    for (int ii=0; ii < noseqs;ii++) {
      score[i][ii] = newdist[ii];
      score[ii][i] = newdist[ii];
    }
  }

  private void findNewDistances(SequenceNode tmpi,SequenceNode tmpj,int dist) {

    float ih = 0;
    float jh = 0;

    SequenceNode sni = tmpi;
    SequenceNode snj = tmpj;

    while (sni != null) {
      ih = ih + sni.dist;
      sni = (SequenceNode)sni.left();
    }

    while (snj != null) {
      jh = jh + snj.dist;
      snj = (SequenceNode)snj.left();
    }

    tmpi.dist = (dist/2 - ih);
    tmpj.dist = (dist/2 - jh);
  }


  public static void main(String[] args) {
    try {
      Vector counts = new Vector();
      
      int i=0;
      int j=0;
      
      FileParse fp = new FileParse(args[0],"File");
      String line;
      
      while ((line = fp.nextLine()) != null) {
      	
      	Vector c = new Vector();
      	
      	StringTokenizer st = new StringTokenizer(line);
      	
      	while (st.hasMoreTokens()) {
      		c.addElement(Integer.parseInt(st.nextToken()));
      		if (i == 0) {
      			j++;
      		}
      	}
      	i++;
      	counts.addElement(c);
      }
      
      JFrame     f  = new JFrame();
      ScrollPane sp = new ScrollPane();
      CountPanel cp = new CountPanel(counts,i,j);
      
      //System.out.println("Size " + i + " " +j);
      cp.setPreferredSize(new Dimension(2*j,2*i));
      sp.add(cp);
      f.getContentPane().add(sp);
      
      f.setSize(400,400);
      f.setVisible(true);

    } catch (IOException e) {
      System.out.println("Exception " + e);
    }
  }

  private int findHeight(SequenceNode node) {

    if (node == null) {
      return maxheight;
    }

    if (node.left() == null && node.right() == null) {
      node.height = ((SequenceNode)node.parent()).height + node.dist;

      if (node.height > maxheight) {
        return (int)node.height;
      } else {
        return maxheight;
      }
    } else {
      if (node.parent() != null) {
        node.height = ((SequenceNode)node.parent()).height + node.dist;
      } else {
        maxheight = 0;
        node.height = (float)0.0;
      }

      maxheight = findHeight((SequenceNode)(node.left()));
      maxheight = findHeight((SequenceNode)(node.right()));
    }
    return maxheight;
  }

  private void findMaxDist(SequenceNode node) {
    if (node == null) {
      return;
    }
    if (node.left() == null && node.right() == null) {

      int dist = (int)((SequenceNode)node).dist;
      if (dist > maxDistValue) {
	  maxdist      = node;
	  maxDistValue = dist;
      }
    } else {
      findMaxDist((SequenceNode)node.left());
      findMaxDist((SequenceNode)node.right());
    }
  }

  private void printNode(SequenceNode node) {
    if (node == null) {
      return;
    }
    if (node.left() == null && node.right() == null) {
      System.out.println("Leaf = " + node.element());
      System.out.println("Dist " + node.dist);
      System.out.println("Boot " + node.getBootstrap());
    } else {
      System.out.println("Dist " + node.dist);
      printNode((SequenceNode)node.left());
      printNode((SequenceNode)node.right());
    }
  }

}

      
final class Cluster {
  final int[] value;
  
  public Cluster(int[] value) {
    this.value = value;
  }
}
