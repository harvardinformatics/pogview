package pogvue.gui.event;

import java.awt.*;
import java.util.EventObject;

public final class FontChangeEvent extends EventObject {
  private final Font font;

  public FontChangeEvent(Object source, Font font) {
    super(source);
    this.font = font;
  }

  public Font getFont() {
    return font;
  }

  public Object getSource() {
    return source;
  }
}
