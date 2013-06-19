package pogvue.util;

import java.awt.*;

public class WindowUtil {

  public  static Window getWindowAncestor(Component c) {
    for(Container p = c.getParent(); p != null; p = p.getParent()) {
      if (p instanceof Window) {
        return (Window)p;
      }
    }
    return null;
  }

  private static void removeComponents(Container cont) {
    Component[] components = cont.getComponents();
    Component comp;

		for (Component component : components) {
			comp = component;
			if (comp != null) {
				cont.remove(comp);
				if (comp instanceof Container)
					removeComponents((Container) comp);
			}
		}
	}
  public static void invalidateComponents(Container cont) {
    Component[] components = cont.getComponents();
    Component comp;
 
    cont.invalidate();
		for (Component component : components) {
			comp = component;
			if (comp != null) {
				if (comp instanceof Container)
					invalidateComponents((Container) comp);
				else
					comp.invalidate();
			}
		}
	}
}
