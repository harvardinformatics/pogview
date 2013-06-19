package pogvue.gui;

import pogvue.analysis.PCA;
import pogvue.datamodel.Sequence;
import pogvue.gui.event.StatusEvent;

import java.awt.*;
import java.util.Vector;

public class PCAThread extends Thread {
  private Sequence[] s;
  private Object parent;
  private PCA pca;
  private PCAPanel p;

  private Controller controller;
  private AlignViewport av;

  private boolean calculated = false;

  public PCAThread(Object parent, AlignViewport av, Controller c,Vector seqs) {
    s = new Sequence[seqs.size()];
    for (int i=0; i<seqs.size(); i++) {
      s[i] = (Sequence)seqs.elementAt(i);
    }
    init(parent,av,c,s);
  }
  public PCAThread(Object parent, AlignViewport av, Controller c,Sequence[] s) {
    init(parent,av,c,s);
  }
  private void init(Object parent, AlignViewport av, Controller c,Sequence[] s) {
    this.s = s;
    this.parent = parent;
    this.av = av;
    this.controller = c;
  }

  public void run() {
    pca = new PCA(s);
    pca.run();
    calculated = true;

    controller.handleStatusEvent(new StatusEvent(this,"Finished PCA calculation",StatusEvent.INFO));

    // Now find the component coordinates
    int ii=0;
    while (ii < s.length && s[ii] != null) {
      ii++;
    }

    double[][] comps = new double[ii][ii];

    for (int i=0; i < ii; i++ ) {
      if (pca.getEigenvalue(i) > 1e-4) {
        comps[i]  = pca.component(i);
      }
    }

    PCAFrame f = new PCAFrame("PCA results",parent);
    f.setLayout(new BorderLayout());
    p  = new PCAPanel(parent,av,controller,pca,s);
    f.add("Center",p);
    f.resize(400,400);

    f.show();
  }
}
