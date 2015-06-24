/*
 * FileSavePanel.java
 *
 * Created on August 21, 2007, 4:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pogvue.gui.hub;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import pogvue.datamodel.*;
import pogvue.gui.AlignViewport;
import pogvue.io.FastaFile;
import pogvue.io.GappedFastaFile;

/**
 *
 * @author mclamp
 */
public class FileSavePanel extends JPanel implements ActionListener {
	private DoubleButtonPanel dbp;
  
	private String       fileStr;
	private String       dirStr;
  
	private JLabel       fileLabel;
	private JTextField   fileField;
	private JLabel       dirLabel;
	private JLabel       dirField;
	private JButton      browseButton;
	private JFileChooser fileChooser;
	private JProgressBar progressBar;
	private JLabel       progressLabel;
  
	private FormLayout   layout1;
	private FormLayout   layout2;

	private AlignViewport viewport;
  
  public FileSavePanel(AlignViewport viewport) {
    this.viewport = viewport;
    buildPanel();
  }
  
  public void initComponents(){
    fileLabel     = new JLabel("Filename");
    fileField     = new JTextField();
    dirLabel      = new JLabel("Directory");
    dirField      = new JLabel(System.getProperty("user.dir"));
    browseButton  = new JButton("Browse...");
    fileChooser   = new JFileChooser();
    progressBar   = new JProgressBar();
    progressLabel = new JLabel();
  
    browseButton.addActionListener(this);
  
    dbp = new DoubleButtonPanel("Cancel","Ok");
    
    dbp.addActionListener(this,"Cancel");
    dbp.addActionListener(this,"Ok");
    
  }
  
  
  public void buildPanel() {
    initComponents();   
    
    layout1 = new FormLayout("pref",                // 1 column
			     "pref:grow, pref");    // 2 rows
    
    JPanel panel1 = new JPanel();
    
    layout2 = new FormLayout("pref, 3dlu, pref:grow, 5dlu, pref",   
			     "pref, 5dlu, pref:grow");
    setLayout(layout1);
    setBorder(Borders.DIALOG_BORDER);
    
    CellConstraints cc = new CellConstraints();
    
    panel1.setLayout(layout2);
    
    panel1.add(fileLabel,    cc.xy(1,3));
    panel1.add(fileField,    cc.xy(3,3));
    panel1.add(dirLabel,     cc.xy(1,1));
    panel1.add(dirField,     cc.xy(3,1));
            
    panel1.add(browseButton, cc.xy(5,3));
    
    add(panel1,  cc.xy(1,1));
    //add(dbp,     cc.xy(1,2));
    
            
  }
  
  public String getFile() {
    if (!(dirField.getText().equals("") || fileField.getText().equals(""))) {
      return dirField.getText() + "/" + fileField.getText();
    } else {
      return "";
    }
  }

  public void actionPerformed(ActionEvent e) {
    
    if (e.getSource() == browseButton) {
    
      int returnVal = fileChooser.showSaveDialog(this);
      
      if (returnVal == JFileChooser.APPROVE_OPTION) {

        System.out.println("You chose to save this file : " + fileChooser.getCurrentDirectory());

        dirField.setText(fileChooser.getCurrentDirectory().getAbsolutePath());
        fileField.setText(fileChooser.getSelectedFile().getName());
      }

      return;

    }
  }
}
  
