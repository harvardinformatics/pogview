package pogvue.gui.hub;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.factories.Borders;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: mclamp
 * Date: Aug 11, 2007
 * Time: 2:05:45 PM
 * To change this template use File | Settings | File Templates.
 */

public class DoubleButtonPanel extends JPanel {
    
    JButton button1;
    String  bstr1;
    JButton button2;
    String  bstr2;
    
    public DoubleButtonPanel(String bstr1, String bstr2) {
	this.bstr1 = bstr1;
	this.bstr2 = bstr2;
	
	buildPanel();
    }
    public void initComponents() {
	button1 = new JButton(bstr1);
	button2 = new JButton(bstr2);
    }
    public void buildPanel() {
	initComponents();
	
	FormLayout layout = new FormLayout(
					   "pref:grow, pref, pref",                // Columns
					   "pref, 10dlu"                            // Rows
					   );
	
	setLayout(layout);
	setBorder(Borders.DIALOG_BORDER);

	CellConstraints cc = new CellConstraints();
	
	add(button1, cc.xy(2,1));
	add(button2, cc.xy(3,1));
	
    }
    public void addActionListener(ActionListener l, String bstr) {
	if (bstr.equals(bstr1)) {
	    button1.addActionListener(l);
	}
	if (bstr.equals(bstr2)) {
	    button2.addActionListener(l);
	}
    }
}




