package pogvue.gui.hub;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.builder.DefaultFormBuilder;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: mclamp
 * Date: Aug 10, 2007
 * Time: 3:21:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class ButtonFooter {

	JButton button;
	String  bstr;

	public ButtonFooter(String bstr) {
		this.bstr = bstr;
	}
	public void initComponents() {
		button = new JButton(bstr);
	}
	public JPanel buildPanel() {
	  initComponents();
		
	  FormLayout layout = new FormLayout(
					     "pref:grow, pref",                      // Columns
					     "pref"                                  // Rows
					     );

	  DefaultFormBuilder builder = new DefaultFormBuilder(layout);
	  CellConstraints cc = new CellConstraints();
	  
	  builder.setDefaultDialogBorder();
	  builder.add(button, cc.xy(2,1));
	  
	  return builder.getPanel();
	}
}
