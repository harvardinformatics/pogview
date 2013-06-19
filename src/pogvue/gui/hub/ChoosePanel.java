package pogvue.gui.hub;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.debug.FormDebugPanel;
import com.jgoodies.forms.debug.FormDebugUtils;
import pogvue.gui.Main;


/**
 * Created by IntelliJ IDEA.
 * User: mclamp
 * Date: Aug 8, 2007
 * Time: 2:30:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChoosePanel implements ActionListener {
    
    private JFrame frame;
    
    private BannerHeader header;
    
    private JPanel footer;
    private JPanel region;
    private JPanel panel;
    
    private String heading    = "Pogvue";
    private String subheading = "Comparative genome browser.  Please choose an option";
    
    private String option1    = "View a genome region by specifying chromosome and coordinates";
    private String option2    = "View a genome region surrounding a gene";
    private String option3    = "Load a genome region from file";
    
    private JButton testButton;
    
    private FormLayout layout;
    
    private int selected;
    
    public ChoosePanel(JFrame parent) {
	this.frame = parent;
    }
    
    
    public void initComponents() {
	header = new BannerHeader(heading);
	footer = new ButtonFooter("Exit").buildPanel();
	
	panel = new JPanel();
	
	Vector buttons = new Vector();
	
	buttons.addElement(option1);
	buttons.addElement(option2);
	buttons.addElement(option3);
	
	region = new RegionPanel(buttons, this).buildPanel();

        testButton = new JButton("Click me!");
        
        testButton.addActionListener(this);
	}
    
    public JComponent buildPanel() {
	initComponents();
	// First the main layout
	
	layout = new FormLayout(
				"pref:grow",                      // Columns
				"pref, pref:grow, pref"
				);
	
	//DefaultFormBuilder builder = new DefaultFormBuilder(layout);//, new FormDebugPanel());
	
	CellConstraints cc = new CellConstraints();
	
	panel.setLayout(layout);
	
	panel.add(header, cc.xy(1,1));
	panel.add(region, cc.xy(1,2));
	panel.add(footer, cc.xy(1,3));
        
        //panel.add(testButton, cc.xy(1,4));
	
	return panel;
    }
    
    public void actionPerformed(ActionEvent evt) {
	System.out.println("Event " + evt);
	
        if (evt.getSource() == testButton) {         
          Main m = new Main();
        }
	if (evt.getSource() instanceof JButton) {
	    JButton tmp = (JButton)evt.getSource();
	    
	    int index = -1;
	    
	    if (tmp.getText().indexOf("gene") > 0) {
		index = 1;
	    } else if (tmp.getText().indexOf("chromosome") > 0) {
		index = 0;
	    } else if (tmp.getText().indexOf("file") > 0) {
		index = 2;
	    }
	    
	    selected = index;
	    
	    if (index != -1) {
		TabbedChoosePanel tcp = new TabbedChoosePanel(index);
		
		region.setVisible(false);
		footer.setVisible(false);
				
		CellConstraints cc = new CellConstraints();
		
		layout.setRowSpec(2,new RowSpec("pref:grow(1.0)"));
		panel.add(tcp, cc.xy(1,2));
		
		DoubleButtonPanel doubleButtonPanel = new DoubleButtonPanel("Cancel","Ok");
		
		doubleButtonPanel.addActionListener(tcp,"Ok");
		doubleButtonPanel.addActionListener(tcp,"Cancel");
		
		panel.add(doubleButtonPanel, cc.xy(1,3));
		
	    }
	}
    }
    
    public static void main(String[] args) {

	// Start the gui on the Event and Dispatch Thread and *not* the main thread.  
	// This leaves the main thread available to do other things.
	
	SwingUtilities.invokeLater(new Runnable() {
		public void run() {
		    
		    System.out.println("Gui Thread " + Thread.currentThread());
		    try {
		//	UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
		    } catch (Exception e) {
			// Likely PlasticXP is not in the class path. ignore.
		    }
		    
		    JFrame frame = new JFrame();
		
		    Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
		    
		    frame.setLocation(sd.width / 2 - 500 / 2,
				      sd.height / 2 - 500 / 2);
		    
		    frame.setTitle("Pogvue selection panel");
		    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		    
		    JComponent panel = new ChoosePanel(frame).buildPanel();
		    
		    panel.setPreferredSize(new Dimension(500,500));
		    frame.getContentPane().setLayout(new BorderLayout());
		    frame.getContentPane().add("Center",panel);
		    
		    frame.pack();
		    frame.setVisible(true);
		}
	    });
	
    }
}
				  
