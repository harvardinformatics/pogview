package pogvue.gui;

import java.io.*;
import java.util.*;

import pogvue.io.*;
import pogvue.datamodel.*;

import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.*;

import java.awt.event.*;
import java.awt.*;
import java.awt.print.*;
import javax.swing.JTree;
import javax.swing.tree.*;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import java.net.URL;
import java.io.IOException;
import java.awt.Dimension;
import java.awt.GridLayout;

public class FeatureTreePanel extends JPanel implements TreeSelectionListener {

  private static boolean playWithLineStyle    = false;
  private static String  lineStyle            = "Horizontal";
  private static boolean useSystemLookAndFeel = false;

  private JTree tree;
  private Hashtable gff;
  private JEditorPane htmlPane;
  private URL helpURL;
  private boolean DEBUG = true;

  Hashtable feat;

  DefaultMutableTreeNode top;

  public FeatureTreePanel(Hashtable feat) {
    super(new GridLayout(1,0));

    this.feat = feat;

    createNodesByHitId();

    tree = new JTree(top);

    JScrollPane treeView = new JScrollPane(tree);

    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.addTreeSelectionListener(this);

    htmlPane = new JEditorPane();
    htmlPane.setEditable(false);

    JScrollPane htmlView = new JScrollPane(htmlPane);

    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

    splitPane.setTopComponent(treeView);

    splitPane.setBottomComponent(htmlView);

    Dimension minimumSize = new Dimension(100, 50);

    htmlView.setMinimumSize(minimumSize);
    treeView.setMinimumSize(minimumSize);
    splitPane.setDividerLocation(600); 
    splitPane.setPreferredSize(new Dimension(500, 700));

    add(splitPane);
  }


  // This is what happens when a node is selected

  public void valueChanged(TreeSelectionEvent e) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

    if (node == null) {
      return;
    }

    Object nodeInfo = node.getUserObject();

    if (node.isLeaf()) {
      System.out.println("Feature " + node.getUserObject());
      //Do something with the feature
    } else {
      //Something else here
    }

  }

  
  //The code creates an instance of DefaultMutableTreeNode to serve as the root node for the tree. 
  //It then creates the rest of the nodes in the tree. After that, it creates the tree, specifying
  // the root node as an argument to the JTree constructor. Finally, it puts the tree in a scroll pane, 
  //a common tactic because showing the full, expanded tree would otherwise require too much space.

  private void createNodesByHitId() {

    
    top =  new DefaultMutableTreeNode("Features");

    DefaultMutableTreeNode feattype  = null;
    DefaultMutableTreeNode feature   = null;
    
    Enumeration typeen = feat.keys();

    while (typeen.hasMoreElements()) {
      String type = (String)typeen.nextElement();
      GFF    gff =  (GFF)feat.get(type);
      Hashtable hitnodes = new Hashtable();

      feattype = new DefaultMutableTreeNode(gff.getType());

      top.add(feattype);

      Vector f = gff.getFeatures();

      for (int i = 0; i < f.size(); i++) {

	SequenceFeature sf = (SequenceFeature)f.elementAt(i);
	String id = sf.getId();

	if (sf.getHitFeature() != null) {
	  id = sf.getHitFeature().getId();
	}

	DefaultMutableTreeNode n;// = findNodeByName(tree,top,id);

	if (hitnodes.get(id) == null) {
	    n = new DefaultMutableTreeNode(id);
	    feattype.add(n);
	    hitnodes.put(id,n);
	} else {
	    n = (DefaultMutableTreeNode)hitnodes.get(id);
	}

	feature = new DefaultMutableTreeNode(sf);
	n.add(feature);

      }
    }
  }
  private void createNodesByType() {
    DefaultMutableTreeNode feattype  = null;
    DefaultMutableTreeNode feature   = null;
      
    Enumeration en = feat.keys();

    top = new DefaultMutableTreeNode("Features");

    while (en.hasMoreElements()) {
      String type = (String)en.nextElement();

      feattype = new DefaultMutableTreeNode(type);

      System.out.println("Adding type " + type);
      top.add(feattype);
    
      GFF  gff = (GFF)feat.get(type);

      Vector f = gff.getFeatures();

      for (int i = 0; i < f.size(); i++) {
	SequenceFeature sf = (SequenceFeature)f.elementAt(i);

	feature = new DefaultMutableTreeNode(sf);

	feattype.add(feature);
      }
    }
  }


  // Finds the path in tree as specified by the array of names. The names array is a
  // sequence of names where names[0] is the root and names[i] is a child of names[i-1].
  // Comparison is done using String.equals(). Returns null if not found.

  public DefaultMutableTreeNode findNodeByName(JTree tree, DefaultMutableTreeNode node, String name) {
    return find2(tree,node,name);

  }

  private DefaultMutableTreeNode find2(JTree tree, DefaultMutableTreeNode node, String name) {
    String   o    = node.toString();

    if (o.equals(name)) {
      // If at end, return match
      return node;
      
    }
      
    // Traverse children
    if (node.getChildCount() > 0) {

	for (Enumeration e = node.children(); e.hasMoreElements(); ) {
	    DefaultMutableTreeNode n = (DefaultMutableTreeNode)e.nextElement();
	    
	    
	    if (n.equals(name)) {
		return n;
	    }
	    
	    DefaultMutableTreeNode result = find2(tree, n, name);
	    
	    // Found a match
	    if (result != null) {
		
		return result;
		
	    }
	} 
	
    }
	// No match at this branch
	return null;

  }

  private static void createAndShowGUI(Hashtable feat) {
    if (useSystemLookAndFeel) {
      try {
	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception e) {
	System.err.println("Couldn't use system look and feel.");
      }
    }

    JFrame frame = new JFrame("TreeDemo");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(new FeatureTreePanel(feat));
    frame.pack();
    frame.setVisible(true);
  }

  public static void main(String[] args) {
    try {
      final GFFFile gfffile = new GFFFile(args[0],"File");
      
      javax.swing.SwingUtilities.invokeLater(new Runnable() {
	  public void run() {
	    Hashtable feat = gfffile.getGFFHashtable();

	    createAndShowGUI(feat);
	  }
	});
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
