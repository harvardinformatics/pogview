package pogvue.gui.hub;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.builder.DefaultFormBuilder;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: mclamp
 * Date: Aug 10, 2007
 * Time: 3:34:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class RegionPanel {

	/**
	 * Created by IntelliJ IDEA.
	 * User: mclamp
	 * Date: Aug 8, 2007
	 * Time: 3:57:34 PM
	 * To change this template use File | Settings | File Templates.
	 */

	Vector buttons;
	Vector bstr;

	ActionListener listener;
	//Font font = new Font("Helvetica",Font.PLAIN, 10);

	public RegionPanel(Vector bstr, ActionListener listener) {
		this.bstr = bstr;
		buttons = new Vector();
		this.listener = listener;
	}
	public void initComponents() {
		for (int i = 0; i < bstr.size(); i++) {
			JButton tmp = new JButton((String)bstr.elementAt(i));

			tmp.addActionListener(listener);
			tmp.setForeground(Color.darkGray);
			//tmp.setFont(font);

			buttons.addElement(tmp);
		}
	}
	public JPanel buildPanel() {
		initComponents();

		String rowstr = "pref:grow";

		for (int i = 0; i < bstr.size(); i++) {
			rowstr += (", pref");
			if (i < bstr.size()-1) {
				rowstr += ", 10dlu";
			}
		}

		rowstr += ", pref:grow";

		System.out.println("Row str " + rowstr);

		FormLayout layout = new FormLayout(
			  "pref:grow, pref, pref:grow",           // Columns
		    rowstr
				);

		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		CellConstraints cc = new CellConstraints();

		builder.setDefaultDialogBorder();

		 for (int i = 0; i < bstr.size(); i++) {
			System.out.println("Adding button in row " + i + " " + (2*i+2));
			builder.add((JButton)buttons.elementAt(i), cc.xy(2,2*i+2));
		}
		return builder.getPanel();
	}

	public void addListener(ActionListener comp) {
		for (int i = 0; i < bstr.size(); i++) {
			((JButton)bstr.elementAt(i)).addActionListener(comp);
		}
	}
}
