package pogvue.gui.event;

import java.util.EventListener;

public interface AlignViewportListener extends EventListener {

  public boolean handleAlignViewportEvent(AlignViewportEvent evt);

}
