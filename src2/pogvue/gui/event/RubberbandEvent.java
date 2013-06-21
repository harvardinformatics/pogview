package pogvue.gui.event;

import java.awt.*;
import java.util.EventObject;

public final class RubberbandEvent extends EventObject {
    private final Rectangle bandBounds;
    
    public RubberbandEvent(Object source,Rectangle Bounds) {
	super(source);
	this.bandBounds = Bounds;
    }

    public Rectangle getBounds() {
	return bandBounds;
    }

    public Object getSource() {
	return source;
    }
}

	
