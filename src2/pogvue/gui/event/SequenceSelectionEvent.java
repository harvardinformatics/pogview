package pogvue.gui.event;

import pogvue.gui.Selection;

import java.util.EventObject;

public final class SequenceSelectionEvent extends EventObject {
  private final Selection sel;

  public SequenceSelectionEvent(Object source, Selection sel) {
    super(source);
    this.sel = sel;
  }

  public Selection getSelection() {
    return sel;
  }

  public Object getSource() {
    return source;
  }
}
