package pogvue.gui;

import pogvue.datamodel.Alignment;
import pogvue.datamodel.Sequence;

import java.util.Vector;

public final class Selection {
  private final Vector selected = new Vector();

  public void addElement(Sequence seq) {
    selected.addElement(seq);
  }

  public void clear() {
    selected.removeAllElements();
  }

  public void removeElement(Sequence seq) {
    if (selected.contains(seq)) {
      selected.removeElement(seq);
    } else {
      System.err.println("WARNING: Tried to remove Sequence NOT in Selection");
    }
  }

  public boolean contains(Sequence seq) {
    return selected.contains(seq);
  }

  public Sequence sequenceAt(int i) {
    return (Sequence)selected.elementAt(i);
  }

  public int size() {
    return selected.size();
  }

  public Vector asVector() {
    return selected;
  }

  public void selectAll(Alignment align) {
    for (int i=0;i<align.getSequences().size();i++) {
      Sequence seq = align.getSequenceAt(i);
      if (!contains(seq)) {
        addElement(seq);
      }
    }
  }
}
