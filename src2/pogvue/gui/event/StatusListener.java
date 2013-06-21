package pogvue.gui.event;

import java.util.EventListener;

public interface StatusListener extends EventListener {

  public boolean handleStatusEvent(StatusEvent evt);
}
