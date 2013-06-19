package pogvue.gui;

import pogvue.datamodel.tree.NJTree;
import pogvue.datamodel.tree.Tree;
import pogvue.datamodel.Sequence;
import pogvue.datamodel.tree.SequenceNode;
import pogvue.gui.event.*;
import pogvue.util.Format;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class TreePanel extends JPanel implements ActionListener,
                                                ItemListener,
                                                SequenceSelectionListener,
                                                TreeSelectionListener,
                                                AlignViewportListener {

  private Tree tree;
  private Object parent;
  
  public TreeCanvas treeCanvas;

  Button close;
  Button output;

  private Checkbox cb;
  private Checkbox boot;
  private Choice   f;

  private PrintWriter bw;
  private PrintStream ps;
  private boolean makeString = false;
  private StringBuffer out;

  private Controller controller;
  private AlignViewport av;
  private Selection selected;

  RubberbandRectangle rubberband;

  public TreePanel(Object parent, Tree tree) {
    this.tree = tree;
    this.parent = parent;
    this.selected = new Selection();
    treeInit();
  }
  public TreePanel(Object parent, AlignViewport av, Controller c, Tree tree) {
    this.tree = tree;
    this.controller = c;
    this.av = av;
    this.selected = av.getSelection();
    this.parent = parent;
    
    treeInit();
    controller.addListener(this);
    controller.handleStatusEvent(new StatusEvent(this,"Finished calculating tree",StatusEvent.INFO));
    
  }
  private TreePanel(Object parent,AlignViewport av,Controller c,Sequence[] s,String treetype, String pairdist) {
    this.tree        = new NJTree(s,treetype,pairdist,0,s[0].getLength());
    this.controller = c;
    this.av         = av;
    this.selected   = av.getSelection();
    this.parent     = parent;

    controller.addListener(this);
    controller.handleStatusEvent(new StatusEvent(this,"Finished calculating tree",StatusEvent.INFO));

    this.parent = parent;
    treeInit();
  }

  public TreePanel(Object parent,AlignViewport av, Controller c,Sequence[] s) {
    this(parent,av,c,s,"AV","BL");
  }

  private void treeInit() {

    setLayout(new BorderLayout());

    treeCanvas = new TreeCanvas(this,tree,selected);

    treeCanvas.addTreeSelectionListener(this);

    JPanel p2 = new JPanel();

    p2.setLayout(new FlowLayout());

    cb = new Checkbox("Show distances");
    cb.setState(treeCanvas.getShowDistances());
    cb.addItemListener(this);

    boot = new Checkbox("Show bootstrap");
    boot.setState(treeCanvas.getShowBootstrap());
    boot.addItemListener(this);

    Label l = new Label("Font size");

    f = new Choice();

    int count = 1;
    while (count <= 30) {
	f.addItem(Integer.toString(count));
	count++;
    }
    f.select(treeCanvas.getFontSize());
    f.addItemListener(this);

    if (av != null && av.getCurrentTree() == null) {
	av.setCurrentTree(tree);
    }
    tree.reCount(tree.getTopNode());
    tree.findHeight(tree.getTopNode());

    p2.add(l);
    p2.add(f);
    p2.add(cb);
    p2.add(boot);

    add("Center",treeCanvas);
    add("South",p2);

    if (av != null) {
      av.setCurrentTree(tree);
    }
    //    rubberband  = new RubberbandRectangle(p);
    //    rubberband.setActive(true);
    //    rubberband.addListener(this);
  }

  public boolean handleAlignViewportEvent(AlignViewportEvent evt) {
    Sequence[] seqs = new Sequence[av.getAlignment().getSequences().size()];

    int i = 0;
    
    while (i < av.getAlignment().getSequences().size()) {
      seqs[i] = av.getAlignment().getSequenceAt(i);
      i++;
    }
    tree = new NJTree(seqs,"AV","PID",av.getStartRes(),av.getEndRes());
    av.setCurrentTree(tree);
    treeCanvas.setTree(tree);
    treeCanvas.repaint();

    return true;

  }

  public boolean handleSequenceSelectionEvent(SequenceSelectionEvent evt) {
      if (av != null) {
	  selected = av.getSelection();
      }
      treeCanvas.setSelected(selected);
      treeCanvas.repaint();
      return true;
  }


  public void itemStateChanged(ItemEvent evt) {

    if (evt.getSource() == f) {
      int size = Integer.parseInt(f.getSelectedItem());
      treeCanvas.setFontSize(size);
      treeCanvas.repaint();
    } else if (evt.getSource() == cb) {
	    treeCanvas.setShowDistances(cb.getState());
	    treeCanvas.repaint();

      if (parent instanceof JFrame) {
        ((JFrame)parent).validate();
      }
    } else if (evt.getSource() == boot) {
      treeCanvas.setShowBootstrap(boot.getState());
      treeCanvas.repaint();
    }

  }
  
  public boolean handleTreeSelectionEvent(TreeSelectionEvent evt) {
    if (av != null) {
      selected = av.getSelection();
      if (evt.getSequence() != null) {
	if (selected.contains(evt.getSequence())) {
	  selected.removeElement(evt.getSequence());
	} else {
	  selected.addElement(evt.getSequence());
	}
      }
      controller.handleSequenceSelectionEvent(new SequenceSelectionEvent(this,av.getSelection()));
      treeCanvas.setSelected(selected);
      treeCanvas.repaint();
    }
    
    return true;
    
  }
  
  public void actionPerformed(ActionEvent evt) {
    
  }
  
  public void setParent(Object parent) {
    this.parent = parent;
  }

  public String getText(String format) {
    return null;
  }
}



















