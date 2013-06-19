package pogvue.gui.hub;

import com.jgoodies.forms.layout.*;
import com.jgoodies.forms.factories.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import pogvue.datamodel.*;
import pogvue.datamodel.motif.*;
import pogvue.gui.*;
import pogvue.gui.renderer.*;
import pogvue.gui.menus.*;
import pogvue.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: mclamp
 * Date: Aug 10, 2007
 * Time: 4:51:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileSelectPanel extends JPanel implements ActionListener {
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
	private FormLayout   layout;
  
	private GetFileThread t;
  
  public FileSelectPanel() {
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
  }
  
  
  public void buildPanel() {
    initComponents();
    
    layout = new FormLayout(
          "pref, 3dlu, pref:grow, 3dlu, pref",
          "pref, 5dlu, pref");
    
    setLayout(layout);
    setBorder(Borders.DIALOG_BORDER);
    
    CellConstraints cc = new CellConstraints();
    
    add(fileLabel,    cc.xy(1,3));
    add(fileField,    cc.xy(3,3));
    add(dirLabel,     cc.xy(1,1));
    add(dirField,     cc.xy(3,1));
            
    add(browseButton, cc.xy(5,3));
  }
  
  public void createProgressBar() {
    CellConstraints cc = new CellConstraints();
    
    // Insert progress bar
    layout.appendRow(new RowSpec("5dlu"));
    layout.appendRow(new RowSpec("pref"));
    layout.appendRow(new RowSpec("2dlu"));
    layout.appendRow(new RowSpec("pref"));
    
    progressBar.setIndeterminate(false);
   
    add(progressBar, cc.xy(3,5));
    
    progressLabel = new JLabel("Reading file...");
    progressLabel.setFont(new Font("Helvetica",Font.PLAIN,10));
    
    add(progressLabel, cc.xy(3,7));
   
    actionPerformed(new ActionEvent(this,0,"Wait"));
    
  }
  
  public void fetchRegion() {
    createProgressBar();
    
    dirStr  = dirField.getText();
    
    fileStr = fileField.getText();
    
    System.out.println("File is " + dirStr + " " + fileStr);
    
    t = new GetFileThread(dirStr + "/" + fileStr,this);
    
    t.start();
    
  }
  
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == browseButton) {
      // Create the fileChooser - get the file and update the textField
    
      int returnVal = fileChooser.showOpenDialog(this);
      
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        System.out.println("You chose to open this file : " + fileChooser.getCurrentDirectory());
        dirField.setText(fileChooser.getCurrentDirectory().getAbsolutePath());
        fileField.setText(fileChooser.getSelectedFile().getName());
      }
      return;
    }
    
    if (e.getActionCommand().equals("Wait")) {
        
        progressBar.setIndeterminate(true);
        progressLabel.setText("Opening file...");
        repaint();
        return;
    }
    
    if (e.getSource() instanceof FileParse) {
      
      System.out.println("Aciton event " + e.getActionCommand());
      
      if (e.getActionCommand().equals("Wait")) {
        
        progressBar.setIndeterminate(true);
        progressLabel.setText("Opening file...");
        repaint();
      } else if (e.getActionCommand().indexOf("Size") == 0) {
        int length = Integer.parseInt(e.getActionCommand().substring(5));
      
        System.out.println("Got Size " + length);
        progressBar.setIndeterminate(false);
        progressBar.setMaximum(length);
        progressBar.setValue(progressBar.getValue());
        repaint();
      
     } else if (e.getActionCommand().indexOf("Len") == 0) {
        
        int curlen = Integer.parseInt(e.getActionCommand().substring(4));
        
        System.out.println("Setting length " + curlen);
        progressBar.setValue(curlen);
        progressLabel.setText("Got " + curlen + " of " + progressBar.getMaximum());
        repaint();
     }
    }
        
    if (e.getActionCommand().equals("Done")) {
        progressBar.setVisible(false);
        progressLabel.setText("Creating alignment...");
      
        Sequence[] s = t.getOutput();
        Alignment  al = new Alignment(s);

	JPanel jp = GenomeInfoFactory.makePanel(al,fileStr,.01,10,0,0,1000,650);
	JFrame jf = new JFrame(fileStr);
	jf.getContentPane().add(jp);
	jf.setSize(1000,700);
	jf.setVisible(true);

	progressLabel.setText("");
	progressLabel.setVisible(false); 
    }
    
  }
}
  
