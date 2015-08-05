package pogvue.io;

import pogvue.datamodel.tree.*;
import pogvue.datamodel.Sequence;
import pogvue.gui.TreePanel;

import java.awt.*;
import java.io.IOException;

public class TreeFile extends FileParse {
  private Tree tree;

  private SequenceNode maxdist;
  private SequenceNode top;

  private int          ycount    = 0;
  private int          ch        = 0;
  private int          linecount = 0;

  private String line;

  public TreeFile(SequenceNode sn) {
    top = sn;
  }

  public TreeFile(String inStr) {
    parse();
  }

  public TreeFile(String inFile, String type)  throws IOException {
    super(inFile,type);
    parse();
  }

  public Tree getTree() {
    return tree;
  }
  
  private void parse() {
    try {

      ycount = 0;

      StringBuffer str = new StringBuffer();
      String line;

      while ((line = nextLine()) != null) {
	  str.append(line);
      }

      top = readTree(str.toString());

      tree = new Tree(top);
      tree.setMaxDist(maxdist);

    } catch (IOException e) {
      System.out.println("Error parsing treefile " + e);
    }
  }

  private String getChar() throws IOException {
    if (ch >= line.length()) {
      line = nextLine();
      linecount++;
      ch = 0;
    }

    String str = line.substring(ch,ch+1);
    ch++;
    return str;
  }

  private String createTree(BinaryNode node,String str) throws IOException {
    String name = "";
    String dist = "";

    if (str.equals("(")) {

      //System.out.println("New left node on " + node);

      SequenceNode sn = new SequenceNode();
      sn  .setParent(node);
      node.setLeft(sn);

      str = createTree(sn,getChar());

      if (str.equals(",")) {

        //System.out.println("New right node on " + node);
        SequenceNode sn2 = new SequenceNode();
        sn2 .setParent(node);
        node.setRight(sn2);

        str = createTree(sn2,getChar());

        // This means an unrooted tree - adding a parent to the top node
        if (str.equals(",")) {
          //System.out.println("Here " + node.parent());
          //System.out.println(((SequenceNode)node).dist + " " + ((SequenceNode)node.right()).dist);
          //System.out.println(sn2.dist);
          //New node for top
          SequenceNode sn3 = new SequenceNode();
          top = sn3;
          sn3.setLeft(node);
          node.setParent(sn3);

          // dummy distance?
          ((SequenceNode)node).dist = (float).001;
          SequenceNode sn4 = new SequenceNode();
          sn4.setParent(sn3);
          sn3.setRight(sn4);
          createTree(sn4,getChar());
        }

        // Tot up the height
        SequenceNode l = (SequenceNode)node.left();
        SequenceNode r = (SequenceNode)node.right();

        ((SequenceNode)node).count = l.count + r.count;
        ((SequenceNode)node).ycount = (l.ycount + r.ycount)/2;
        str = getChar();
      }
    } else {
      // Leaf node
      while (!(str.equals(":") || str.equals(",") ||str.equals(")"))) {
        name = name + str;
        str = getChar();
      }

      node.setElement(new Sequence(name,"",0,0));
      node.setName(name);
      ((SequenceNode)node).count = 1;
      ((SequenceNode)node).ycount = ycount++;
    }

    // Read distance
    if (!str.equals(";")) {
      String bootstrap = "";
      while (!str.equals(":")) {
        bootstrap = bootstrap + str;
        str = getChar();
      }
      //System.out.println("Boot :" + bootstrap + ":");
      if (!bootstrap.equals("")) {
        //System.out.println("Setting bootstrap " + bootstrap);
        node.setBootstrap(Integer.parseInt(bootstrap));
        //System.out.println("Getting bootstrap " + node.getBootstrap());
      }
      str=getChar();
      while (!(str.equals(":") || str.equals(",") ||str.equals(")"))) {
        dist = dist + str;
        str = getChar();
      }

      if (node instanceof SequenceNode) {
        ((SequenceNode)node).dist  = Float.valueOf(dist);
        //System.out.println("Distance = " + ((SequenceNode)node).dist);

        if ( maxdist == null || Float.valueOf(dist) > maxdist.dist) {
	    maxdist = (SequenceNode)node;
        }

      }
    }
    return str;
  }


  public static  void checkNode(BinaryNode node) {


    //System.out.println("\nChecking node..... " + node);

	if (node == null) {
	    return;
	}
	if (node.parent == null) {

	    if (node.left() != null && node.right()!= null) {
		System.out.println("Top node - children exist!!");
	    } else {
		System.out.println("ERROR: Top node - children don't exist!! " + node.left() + " " + node.right());
	    }
	} else if (node.getName() != null) {
	    if (node.dist == 0) {
		System.out.println("ERROR: Leaf node : name " + node.getName() + " zero distance " + node.dist);
	    } else {
		System.out.println("Leaf node : name " + node.getName() + " non-zeno distance " + node.dist);
	    }

	    if (node.left() != null ||
		node.right() != null) {
		System.out.println("ERROR: Leaf node : name " + node.getName() + "  non-null children nodes " + node.left() + " " + node.right());
	    }
	} else {
	    // This is an internal node

	    if (node.dist == 0) {
		System.out.println("ERROR: Internal node distance is zero " + node.dist);
	    } 
	    if (node.left() == null || node.right() == null) {
		System.out.println("ERROR: Internal node children are null " + node.left() + " " + node.right());
	    }
	}

	if (node.left() != null) {
	  System.out.println("Down to left node " + node.left() +  "   - parent " +  node);
	    checkNode((SequenceNode)node.left());
	}
	if (node.right() != null) {
	  System.out.println("Down to right node " + node.right() + "   - parent " + node);
	    checkNode((SequenceNode)node.right());
	}
    }
	    

	
    private SequenceNode readTree(String str) {
	
	int pos = 0;
	
	SequenceNode curr_node;
	SequenceNode top_node = null;

	curr_node = top_node;

	while (pos < str.length()) {

	    String s = str.substring(pos,pos+1);

	    //System.out.println();


	    //System.out.println();


	    //System.out.println("String is " + s);
	    //System.out.println("Full string is " + str.substring(0,pos+1));

	    if (s.equals("(")) {

		// This takes us down a node
		//       |X
		// #-----|
		//       |

		if (curr_node == null) {

		  //System.out.println("New top node");
		    
		    top_node   = new SequenceNode();
		    curr_node  = top_node;

		} else {

		    // Add in a new left node

		    SequenceNode tmpnode = new SequenceNode();

		    tmpnode.parent = curr_node;

		    curr_node.setLeft(tmpnode);
		    curr_node = tmpnode;

		    //System.out.println("New left node");
		    
		}
		
		pos++;

	    } else if (s.equals(",")) {
		// Move up and over to the right node

		//       |-----#
		//  -----|
		//       |X

		SequenceNode parent_node = (SequenceNode)curr_node.parent;
		SequenceNode tmpnode     = new SequenceNode();

		// If no parent node - we are moving the top - create new top

		if (parent_node == null) {

		  //System.out.println("New top node for ,");
		    SequenceNode tmp = new SequenceNode();

		    curr_node.parent = tmp;
		    top_node         = tmp;
		    parent_node      = tmp;
		    
		    top_node.setLeft(curr_node);
		}
		
		tmpnode.parent = parent_node;
		parent_node.setRight(tmpnode);

		curr_node = tmpnode;

		pos++;

		//System.out.println("New right node");

	    } else if (s.equals(":")) {

		pos++;

		// Read the distance

		StringBuffer diststr = new StringBuffer();
		
		while (! (str.substring(pos,pos+1).equals(",") ||
			  str.substring(pos,pos+1).equals(")"))) {
		    
		    diststr.append(str.substring(pos,pos+1));
		    
		    pos++;
		}

		//System.out.println("Adding distance " + diststr);

		double dist = Double.parseDouble(diststr.toString());

		// Set current nodes distance

		curr_node.dist = (float)dist;

	    } else if (s.equals(")")) {
		
		// Move up to the parent

		//       |------#
		// ------X
		//       |

		SequenceNode parent_node = (SequenceNode)curr_node.parent;

		// If no parent node - we are moving the top - create new top

		if (parent_node == null) {
		    SequenceNode tmp = new SequenceNode();

		    curr_node.parent = tmp;
		    top_node         = tmp;

		    top_node.setRight(curr_node);


		    //  System.out.println("Resetting top node");
		}

		curr_node = parent_node;
		//print_node(curr_node);
		
		//System.out.println("Up to parent");
		

		pos++;

	    } else if (s.equals(";")) {
	      //System.out.println("Pos is " + pos);


	      //System.out.println("Top " + top_node.left());
	      //System.out.println("Top " + top_node.right());

	      //System.out.println("Top " + top_node.dist);


	      //print_node(top_node);
	      //	checkNode(top_node);
		return top_node;
	    } else if (! s.equals(" ")) {

		// Read org name
		StringBuffer org = new StringBuffer();
		
		while (! (str.substring(pos,pos+1).equals(":") ||
			  str.substring(pos,pos+1).equals(";"))) {



		    org.append(str.substring(pos,pos+1));
		    
		    pos++;
		}

		System.out.println("Org " + org);
		// Set node id and isLeaf

		curr_node.setName(org.toString());
		curr_node.setElement(new Sequence(org.toString(),"",0,0));
		curr_node.setLeft(null);
		curr_node.setRight(null);


	    } else {
	    	pos++;
            }
	    //System.out.println("Pos " + pos);
	}



	return top_node;
    }


    private void print_node(SequenceNode node) {

	if (node == null) {
	    return;
	}

	if (node.dist == 0 && node.getName() == null) {
	    
	    System.out.println("Empty node");;
	} else {

	    if (node.getName() == null) {
		System.out.println("Interior node " + node.dist);
		System.out.println("Left/right " + node.left() + " " + node.right());
		System.out.println();
	    } else {
		System.out.println("Leaf mnode " + node.dist + " " + node.getName());
		System.out.println("Left/right " + node.left() + " " + node.right());
		System.out.println();
	    }
	}


	if (node.left() != null) {
	    print_node((SequenceNode)node.left());
	}
	if (node.right() != null) {
	    print_node((SequenceNode)node.right());
	}
    }
		
		
  public float max(float l, float r) {
    if (l > r) {
      return l;
    } else {
      return r;
    }
  }
    public static void main(String[] args) {
	try {
	  TreeFile tf = new TreeFile(args[0],"File");
	  Tree tree = tf.getTree();

	  tree.printNode(tree.getTopNode());

	  TreePanel p = new TreePanel(null,tree);
	  Frame     f = new Frame("Tree : " + args[0]);
	  
	  f.setLayout(new BorderLayout());
	  f.add("Center",p);
	  f.setSize(500,500);
	  f.show();

	} catch (IOException e) {
	    System.out.println("Exception parsing treefile " + args[0] + " " + e);
	}
    }
}




