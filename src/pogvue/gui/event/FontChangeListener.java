package pogvue.gui.event;

import java.util.EventListener;

public interface FontChangeListener extends EventListener {

  public boolean handleFontChangeEvent(FontChangeEvent evt);

}
