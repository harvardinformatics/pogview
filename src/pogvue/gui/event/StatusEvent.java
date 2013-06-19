package pogvue.gui.event;

import java.util.EventObject;

public final class StatusEvent extends EventObject {

  public static final int INFO    = 1;
  public static final int WARNING = 2;
  public static final int ERROR   = 3;
  public static final int FNAME   = 4;
  public static final int FSCORE  = 5;
  public static final int POSITION = 6;

  private final String text;
  private final int    type;

  public StatusEvent(Object source, String text, int type) {
    super(source);
    this.text = text;
    this.type = type;
  }

  public Object getSource() {
    return source;
  }

  public String getText() {
    return text;
  }
 
  public int getType() {
    return type;
  }
}
