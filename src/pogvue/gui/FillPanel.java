package pogvue.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mclamp
 * Date: Aug 6, 2007
 * Time: 3:14:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class FillPanel extends JPanel {
	private int height;

	public FillPanel(int height) {
		setBackground(Color.white);
		this.height = height;
	}
	public Dimension getMinimumSize() {
		return new Dimension(0,height);
	}

	public Dimension getPreferredSize() {
		return getMinimumSize();
	}
	public void setHeight(int height) {
		this.height = height;
	}
}
