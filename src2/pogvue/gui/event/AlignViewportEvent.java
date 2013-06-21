package pogvue.gui.event;

import pogvue.gui.AlignViewport;

import java.util.EventObject;

public final class AlignViewportEvent extends EventObject {
  private final AlignViewport    viewport;
  private final int              type;

  public static final int RESHAPE   = 1;
  public static final int LIMITS    = 2;
  public static final int COLOURING = 3;
  public static final int FONT      = 4;
  public static final int SHOW      = 5;
  public static final int ORDER     = 6;
  public static final int DELETE    = 7;
  public static final int THRESHOLD = 8;
  public static final int WRAP      = 9;
  public static final int HSCROLL   = 10;
  public static final int GLASS   = 11;

  public AlignViewportEvent(Object source, AlignViewport viewport, int type) {
    super(source);
    this.viewport = viewport;
    this.type = type;
  }

  public AlignViewport getAlignViewport() {
    return viewport;
  }
 
  public int getType() {
    return type;
  }

  public Object getSource() {
    return source;
  }
}
