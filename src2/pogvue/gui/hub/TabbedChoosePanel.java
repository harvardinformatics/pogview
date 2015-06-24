package pogvue.gui.hub;

/**
 * Created by IntelliJ IDEA.
 * User: mclamp
 * Date: Aug 10, 2007
 * Time: 3:59:17 PM
 * To change this template use File | Settings | File Templates.
 */
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.factories.Borders;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class TabbedChoosePanel extends JPanel implements ActionListener {
    public final int REGION = 0;
    public final int GENE   = 1;
    public final int FILE   = 2;
    
    JTabbedPane            tabbedPane;
    
    GeneSelectPanel        genePanel;
    RegionSelectPanel      regionPanel;
    FileSelectPanel        filePanel;
    
    DoubleButtonPanel      doubleButtonPanel;
    
    public TabbedChoosePanel(int selected) {
	buildPanel(selected);
    }
    public void initComponents() {
	
	tabbedPane = new JTabbedPane();
	
	tabbedPane.putClientProperty("jgoodies.noContentBorder", Boolean.TRUE);
	
	doubleButtonPanel = new DoubleButtonPanel("Cancel","Ok");
	
	genePanel   = buildGenePanel();
	regionPanel = buildRegionPanel();
	filePanel   = buildFilePanel();

	doubleButtonPanel.addActionListener((ActionListener)this,"Ok");

    }
    public void buildPanel(int selected) {
	
	initComponents();
	
	tabbedPane.add(regionPanel,"Chromosome region");
	tabbedPane.add(genePanel,  "Gene region");
	tabbedPane.add(filePanel,  "Fasta file");
	
	tabbedPane.setSelectedIndex(selected);
	
	FormLayout layout = new FormLayout(
					   "pref",     			 // Columns
					   "pref:grow, pref" // Rows
					   );
	
	setLayout(layout);
	
	setBorder(Borders.DIALOG_BORDER);
	
	CellConstraints cc = new CellConstraints();
	
	add(tabbedPane,        cc.xy(1,1));    // column, row
    }

    public RegionSelectPanel buildRegionPanel() {
	return new RegionSelectPanel();
    }
    
    public GeneSelectPanel buildGenePanel() {
	return new GeneSelectPanel();
    }
    
    public FileSelectPanel buildFilePanel() {
	return new FileSelectPanel();
    }
    
    public void actionPerformed(ActionEvent e) {
	System.out.println("Action " + e);

	if (tabbedPane.getSelectedIndex() == 1) {
	    String idstr = genePanel.getGeneString();

	    genePanel.fetchGenes(idstr);

	}  else if (tabbedPane.getSelectedIndex() == 0) {
	    String regionStr = regionPanel.getRegionString();

	    regionPanel.fetchRegion(regionStr);
	} else if (tabbedPane.getSelectedIndex() == 2) {
            filePanel.fetchRegion();
        }
    }
}
