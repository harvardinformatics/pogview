package pogvue.gui.event;

import java.util.EventListener;

public interface TreeSelectionListener extends EventListener {

  public boolean handleTreeSelectionEvent(TreeSelectionEvent evt);

}
