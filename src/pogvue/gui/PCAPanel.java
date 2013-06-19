package pogvue.gui;

import pogvue.analysis.PCA;
import pogvue.datamodel.Sequence;
import pogvue.datamodel.tree.SequencePoint;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

public final class PCAPanel extends Panel implements ActionListener,
                                                     ItemListener {
  private final PCA pca;
  private final Object parent;
  private final int top;
  private final RotatableCanvas rc;
  private final Choice cx;
  private final Choice cy;
  private final Choice cz;
  private final Button b;
  private final AlignViewport av;
  private final Controller controller;

  public PCAPanel(Object parent, AlignViewport av, Controller c, PCA pca) {
    this(parent,av,c,pca,null);
  }

  public PCAPanel(Object parent, AlignViewport av, Controller c, PCA pca, Sequence[] s) {

    this.parent = parent;
    this.pca = pca;
    this.av = av;
    this.controller = c;

    Panel p1  = new Panel();

    Panel p2  = new Panel();
    Panel p3  = new Panel();

    Panel p4 = new Panel();

    Label l1 = new Label("x = ");
    Label l2 = new Label("y = ");
    Label l3 = new Label("z = ");

    b = new Button("Close");

    b.addActionListener(this);

    cx = new Choice();
    cy = new Choice();
    cz = new Choice();

    addItems(cx);
    addItems(cy);
    addItems(cz);

    cx.select(0);
    cy.select(1);
    cz.select(2);

    cx.addItemListener(this);
    cy.addItemListener(this);
    cz.addItemListener(this);

    top = pca.getM().rows-1;

    Vector points = new Vector();
    float[][] scores = pca.getComponents(top-1,top-2,top-3,100);

    for (int i =0; i < pca.getM().rows; i++ ) {
      SequencePoint sp = new SequencePoint(s[i],scores[i]);
      points.addElement(sp);
    }
    rc = new RotatableCanvas(parent,av,controller,points,pca.getM().rows);

    rc.printPoints();

    p1.setLayout(new BorderLayout());
    p1.add("Center",rc);

    p2.setLayout(new FlowLayout());
    p2.add(l1);
    p2.add(cx);

    p2.add(l2);
    p2.add(cy);

    p2.add(l3);
    p2.add(cz);

    p3.add(b);

    p4.setLayout(new GridLayout(2,1));
    p4.add(p2);
    p4.add(p3);

    setLayout(new BorderLayout());

    add("Center",p1);
    add("South",p4);
  }

  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == b) {
      this.hide();
      if (this.getParent() instanceof Frame) {
        ((Frame)this.getParent()).dispose();
      }
    }
  }


  public void itemStateChanged(ItemEvent evt) {
    boolean newdim = false;

    if (evt.getSource() == cx ||
        evt.getSource() == cy ||
        evt.getSource() == cz) {

      int dim1 = top - cx.getSelectedIndex();
      int dim2 = top - cy.getSelectedIndex();
      int dim3 = top - cz.getSelectedIndex();

      float[][] scores  = pca.getComponents(dim1,dim2,dim3,100);
      for (int i=0; i < pca.getM().rows; i++) {
        ((SequencePoint)rc.points.elementAt(i)).coord = scores[i];
      }

      rc.img = null;
      rc.rotmat.setIdentity();
      rc.initAxes();
      rc.paint(rc.getGraphics());
    }
  }

//  public boolean keyPressed(KeyEvent evt) {
//    if (evt.getKeyCode() Event.UP || evt.getKeyCkey == Event.DOWN) {
//      return  rc.keyDown(evt,key);
//    } else if (key == 's') {
//      return rc.keyDown(evt,key);
//    } else {
//      return super.keyDown(evt,key);
//    }
//  }

  private void addItems(Choice c) {
    c.addItem("dim 1");
    c.addItem("dim 2");
    c.addItem("dim 3");
    c.addItem("dim 4");
    c.addItem("dim 5");
    c.addItem("dim 6");
    c.addItem("dim 7");
  }
}
