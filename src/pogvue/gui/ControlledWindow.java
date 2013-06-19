package pogvue.gui;

import java.awt.*;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: mclamp
 * Date: Aug 6, 2007
 * Time: 3:17:34 PM
 * To change this template use File | Settings | File Templates.
 */
public final class ControlledWindow {
		private final Window window;
		private final Vector<Object> children;

		public ControlledWindow(Window w) {
			this.window = w;
			children = new Vector<Object>();
		}

		public void addChild(Object child) {
			children.addElement(child);
		}

		public void removeChild(Object child) {
			children.removeElement(child);
		}

		public Vector<Object> getChildren() {
			return children;
		}

		public boolean contains(Object child) {
			return children.contains(child);
		}

		public Window getWindow() {
			return window;
		}

	}
