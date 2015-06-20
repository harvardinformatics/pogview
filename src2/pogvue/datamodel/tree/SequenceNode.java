package pogvue.datamodel.tree;

import java.awt.*;

public class SequenceNode extends BinaryNode {

  public int count;
  public float height;
  public float ycount;
  public Color color = Color.black;

  public SequenceNode() {
    super();
  }
  
  public SequenceNode(Object val, SequenceNode parent, float dist,String name) {
    super(val,parent,name);
    this.dist = dist;
  }
  public String toString() {
    return "Node-" + dist + "-" +  getName();
  }
}
