package pogvue.gui.event;

import pogvue.datamodel.Sequence;

import java.util.EventObject;

public final class TreeSelectionEvent extends EventObject {
    private final Sequence seq;

  public TreeSelectionEvent(Object source, Sequence seq) {
    super(source);
    this.seq = seq;
  }

  public Sequence  getSequence() {
    return seq;
  }

  public Object getSource() {
    return source;
  }
}
