package pogvue.datamodel.tree;

import pogvue.datamodel.tree.BinaryNode;
import pogvue.datamodel.Sequence;
import pogvue.gui.schemes.ResidueProperties;
import pogvue.util.Comparison;
import java.util.Vector;
import pogvue.analysis.AlignSeq;

public class NJTree extends Tree {

  private Vector cluster;
  
  private int[] done;
  private int noseqs;
  private int noClus;
  
  private float[][] distance;
  
  private int mini;
  private int minj;
  private float ri;
  private float rj;
  
  private final Vector groups = new Vector();

  private String type;
  private String pwtype;

  private Object found = null;
  final Object leaves = null;

  private int start;
  private int end;

  public Vector node;

  public NJTree(Sequence[] sequence,int start, int end) {
    this(sequence,"NJ","BL",start,end);
  }
  
  public NJTree(Sequence[] sequence,String type,String pwtype,int start, int end ) {

    this.sequence = sequence;
    this.node     = new Vector();
    this.type     = type;
    this.pwtype   = pwtype;
    this.start    = start;
    this.end      = end;

    if (!(type.equals("NJ"))) {
      type = "AV";
    }
    
    if (!(pwtype.equals("PID"))) {
      type = "BL";
    }

    int i=0;
    
    done = new int[sequence.length];

    
    while (i < sequence.length  && sequence[i] != null) {
      done[i] = 0;
      i++;
    }
    
    noseqs = i++;

    distance = findDistances();
    
    makeLeaves();
    
    noClus = cluster.size();

    cluster();

  }

  private void cluster() {

    while (noClus > 2) {
      if (type.equals("NJ")) {
        float mind = findMinNJDistance();
      } else {
        float mind = findMinDistance();
      }

      
      TreeCluster c = joinClusters(mini,minj);


      done[minj] = 1;

      cluster.setElementAt(null,minj);
      cluster.setElementAt(c,mini);

      noClus--;
    }

    boolean onefound = false;

    int one = -1;
    int two = -1;

    for (int i=0; i < noseqs; i++) {
      if (done[i] != 1) {
        if (!onefound) {
          two = i;
          onefound = true;
        } else {
          one = i;
        }
      }
    }

    TreeCluster c = joinClusters(one,two);
    top = (BinaryNode)(node.elementAt(one));

    reCount(top);
    findHeight(top);
    findMaxDist(top);
    
  }	
  private TreeCluster joinClusters(int i, int j) {
    //System.out.println("Size " + i + " " + j + " " + distance);
    float dist = distance[i][j];

    int noi = ((TreeCluster)cluster.elementAt(i)).value.length;
    int noj = ((TreeCluster)cluster.elementAt(j)).value.length;

    int[] value = new int[noi + noj];

    for (int ii = 0; ii < noi;ii++) {
      value[ii] =  ((TreeCluster)cluster.elementAt(i)).value[ii];
    }

    for (int ii = noi; ii < noi+ noj;ii++) {
      value[ii] =  ((TreeCluster)cluster.elementAt(j)).value[ii-noi];
    }

    TreeCluster c = new TreeCluster(value);
    
    ri = findr(i,j);
    rj = findr(j,i);

    if (type.equals("NJ")) {
      findTreeClusterNJDistance(i,j);
    } else {
      findTreeClusterDistance(i,j);
    }

    BinaryNode sn = new BinaryNode();

    sn.setLeft((BinaryNode)(node.elementAt(i)));
    sn.setRight((BinaryNode)(node.elementAt(j)));

    BinaryNode tmpi = (BinaryNode)(node.elementAt(i));
    BinaryNode tmpj = (BinaryNode)(node.elementAt(j));

    if (type.equals("NJ")) {
      findNewNJDistances(tmpi,tmpj,dist);
    } else {
      findNewDistances(tmpi,tmpj,dist);
    }

    tmpi.setParent(sn);
    tmpj.setParent(sn);

    node.setElementAt(sn,i);
    return c;
  }

  private void findNewNJDistances(BinaryNode tmpi, BinaryNode tmpj, float dist) {

    float ih = 0;
    float jh = 0;

    BinaryNode sni = tmpi;
    BinaryNode snj = tmpj;

    tmpi.dist = (dist + ri - rj)/2;
    tmpj.dist = (dist - tmpi.dist);

    if (tmpi.dist < 0) {
      tmpi.dist = 0;
    }
    if (tmpj.dist < 0) {
      tmpj.dist = 0;
    }
  }

  private void findNewDistances(BinaryNode tmpi,BinaryNode tmpj,float dist) {

    float ih = 0;
    float jh = 0;

    BinaryNode sni = tmpi;
    BinaryNode snj = tmpj;

    while (sni != null) {
      ih = ih + sni.dist;
      sni = (BinaryNode)sni.left();
    }

    while (snj != null) {
      jh = jh + snj.dist;
      snj = (BinaryNode)snj.left();
    }

    tmpi.dist = (dist/2 - ih);
    tmpj.dist = (dist/2 - jh);
  }



  private void findTreeClusterDistance(int i, int j) {

    int noi = ((TreeCluster)cluster.elementAt(i)).value.length;
    int noj = ((TreeCluster)cluster.elementAt(j)).value.length;

    // New distances from cluster to others
    float[] newdist = new float[noseqs];

    for (int l = 0; l < noseqs; l++) {
      if ( l != i && l != j) {
        newdist[l] = (distance[i][l] * noi + distance[j][l] * noj)/(noi + noj);
      } else {
        newdist[l] = 0;
      }
    }

    for (int ii=0; ii < noseqs;ii++) {
      distance[i][ii] = newdist[ii];
      distance[ii][i] = newdist[ii];
    }
  }

  private void findTreeClusterNJDistance(int i, int j) {

    int noi = ((TreeCluster)cluster.elementAt(i)).value.length;
    int noj = ((TreeCluster)cluster.elementAt(j)).value.length;

    // New distances from cluster to others
    float[] newdist = new float[noseqs];

    for (int l = 0; l < noseqs; l++) {
      if ( l != i && l != j) {
        newdist[l] = (distance[i][l] + distance[j][l] - distance[i][j])/2;
      } else {
        newdist[l] = 0;
      }
    }

    for (int ii=0; ii < noseqs;ii++) {
      distance[i][ii] = newdist[ii];
      distance[ii][i] = newdist[ii];
    }
  }

  private float findr(int i, int j) {

    float tmp = 1;
    for (int k=0; k < noseqs;k++) {
      if (k!= i && k!= j && done[k] != 1) {
        tmp = tmp + distance[i][k];
      }
    }

    if (noClus > 2) {
      tmp = tmp/(noClus - 2);
    }

    return tmp;
  }

  private float findMinNJDistance() {

    float min = 100000;

    for (int i=0; i < noseqs-1; i++) {
      for (int j=i+1;j < noseqs;j++) {
        if (done[i] != 1 && done[j] != 1) {
          float tmp = distance[i][j] - (findr(i,j) + findr(j,i));
          if (tmp < min) {
            
            mini = i;
            minj = j;

            min = tmp;

          }	
        }
      }
    }
    return min;
  }

  private float findMinDistance() {

    float min = 100000;

    for (int i=0; i < noseqs-1;i++) {
      for (int j = i+1; j < noseqs;j++) {
        if (done[i] != 1 && done[j] != 1) {
          if (distance[i][j] < min) {
            mini = i;
            minj = j;

            min = distance[i][j];
          }
        }
      }
    }
    return min;
  }

  private float[][] findDistances() {

    float[][] distance = new float[noseqs][noseqs];

    if (pwtype.equals("PID")) {
      for (int i = 0; i < noseqs-1; i++) {
        for (int j = i; j < noseqs; j++) {
          if (j==i) {
            distance[i][i] = 0;
          } else {
            distance[i][j] = 100-Comparison.compare(sequence[i],sequence[j],start,end);
            distance[j][i] = distance[i][j];
          }
        }
      }
    } else if (pwtype.equals("BL")) {
      int   maxscore = 0;

      for (int i = 0; i < noseqs-1; i++) {
        for (int j = i; j < noseqs; j++) {
          int score = 0;
          for (int k=0; k < sequence[i].getLength(); k++) {
            score += ResidueProperties.getBLOSUM62(sequence[i].getSequence(k,k+1),
                                                   sequence[j].getSequence(k,k+1));
          }
          distance[i][j] = score;
          if (score > maxscore) {
            maxscore = score;
          }
        }
      }
      for (int i = 0; i < noseqs-1; i++) {
        for (int j = i; j < noseqs; j++) {
          distance[i][j] =  maxscore - distance[i][j];
          distance[j][i] = distance[i][j];
        }
      }
    } else if (pwtype.equals("SW")) {
      float max = -1;
      for (int i = 0; i < noseqs-1; i++) {
        for (int j = i; j < noseqs; j++) {
          AlignSeq as = new AlignSeq(sequence[i],sequence[j],"pep");
          as.calcScoreMatrix();
          as.traceAlignment();
          as.printAlignment();
          distance[i][j] = as.maxscore;
          if (max < distance[i][j]) {
            max = distance[i][j];
          }
        }
      }
      for (int i = 0; i < noseqs-1; i++) {
        for (int j = i; j < noseqs; j++) {
          distance[i][j] =  max - distance[i][j];
          distance[j][i] = distance[i][j];
        }
      }
    }
    
    return distance;
  }

  private void makeLeaves() {
    cluster = new Vector();

    for (int i=0; i < noseqs; i++) {
      BinaryNode sn = new BinaryNode();

      sn.setElement(sequence[i]);
      sn.setName(sequence[i].getName());
      node.addElement(sn);

      int[] value = new int[1];
      value[0] = i;

      TreeCluster c = new TreeCluster(value);
      cluster.addElement(c);
    }	
  }

    
}

class TreeCluster {
  
  final int[] value;

  public TreeCluster(int[] value) {
    this.value = value;
  }

}

