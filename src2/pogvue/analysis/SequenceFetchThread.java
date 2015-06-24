package pogvue.analysis;

import java.util.*;
import java.awt.event.*;
import java.io.*;

import pogvue.datamodel.*;
import pogvue.io.*;
import pogvue.gui.*;
import pogvue.gui.hub.*;

public class SequenceFetchThread extends Thread {
  private int chunk = 0;
  private Controller controller;
  private AlignViewport viewport;

  private ActionListener l;
  private boolean human = true;
  private boolean done = false;

  private int x1;
  private int x2;
  
  public SequenceFetchThread(Controller controller, AlignViewport viewport) {
    this.controller = controller;
    this.viewport = viewport;
  }
  public SequenceFetchThread(Controller controller, AlignViewport viewport, int x1 ,int x2) {
    this.controller = controller;
    this.viewport = viewport;
    this.x1 = x1;
    this.x2 = x2;
  }

  public void setActionListener(ActionListener l) {
    this.l = l;
  }

  public void setHuman(boolean flag) {
    this.human = flag;
  }

  public void run() {

    getSequence();

    if (l != null) {
      ActionEvent e = new ActionEvent(this, 0, "Done");

      l.actionPerformed(e);
    }
  }

  public void getSequence() {
    if (viewport.getAlignment().getChrRegion() != null) {
      ChrRegion r = viewport.getAlignment().getChrRegion();

      int start = r.getStart() + viewport.getStartRes() - chunk;
      int end = r.getStart() + viewport.getEndRes() + chunk;

      if (x1 > 0) {
        start = r.getStart() + x1;
        end   = r.getStart() + x2;
      }
      if (start < r.getStart()) {
        start = r.getStart();
      }
      if (end > r.getEnd()) {
        end = r.getEnd();
      }

      int tmpstart = start - r.getStart();
      int tmpend = end - r.getStart();

      String regstr = "query=" + r.getChr() + "&start=" + start + "&end=" + end
          + "&z=2";

      try {

        Hashtable nameHash = viewport.getAlignment().getNameHash();
        // GappedFastaFile ff = GenomeInfoFactory.getRegion(regstr,human);
        System.out.println("Getting region int SequenceFetchThread" + r + " " + r.getChr() + " " + start + " " + end);
        FastaFile ff = GenomeInfoFactory.getUngappedRegion(r.getChr(),start,end);
        //FastaFile ff = GenomeInfoFactory.getUngappedRegion(regstr);

        ff.parse();

        int j = 0;

        Sequence[] newseqs = ff.getSeqsAsArray();
        Vector seqs = viewport.getAlignment().getSequences();

        int count = 0;
        int lastseq = 0;
        Vector novelseq = new Vector();

        int len = newseqs.length;

        if (human) {
          //len = 1;
        }
        while (j < len) {
          if (nameHash.containsKey(newseqs[j].getName())) {
            Sequence s = (Sequence) nameHash.get(newseqs[j].getName());
            String seq = s.getSequence();

            if (seq.length() >= tmpend + 1) {
              String newseq = seq.substring(0, tmpstart)
                  + newseqs[j].getSequence() + seq.substring(tmpend + 1);
              s.setSequence(newseq);
            }
          } else {
            novelseq.addElement(newseqs[j]);
          }
          j++;

        }

        j = 0;

        while (j < seqs.size()) {
          Sequence s = (Sequence) seqs.elementAt(j);

          if (s.getSequence().length() > 0) {
            lastseq = j;
          }
          j++;
        }

        lastseq++;

        System.out.println("Human " + human + " " + novelseq.size());

        for (int i = 0; i < novelseq.size(); i++) {
          Sequence novseq = (Sequence) novelseq.elementAt(i);
          StringBuffer seqstr = new StringBuffer();

          for (int jj = 0; jj < viewport.getAlignment().getWidth(); jj++) {
            seqstr.append("X");
          }

          String str = seqstr.toString();
          String newseq = str.substring(0, tmpstart) + novseq.getSequence()
              + str.substring(tmpend);

          Sequence tmpseq = new Sequence(novseq.getName(), newseq, 1, newseq
              .length());

          //System.out.println("Inserting " + novseq.getName());
          seqs.insertElementAt(tmpseq, lastseq);

          lastseq++;
        }

        // viewport.setVisibleSequence(true);

        // for (int i = 0; i < viewport.getAlignment().getHeight(); i++) {
        // if (viewport.getAlignment().getSequenceAt(i).getSequence().length() >
        // 0) {
        // viewport.showSequence(viewport.getAlignment().getSequenceAt(i));
        // }
        // }
        done = true;
      } catch (IOException err) {
        err.printStackTrace();
        done = true;
      }
    }
  }

  public boolean isDone() {
    return done;
  }

}
