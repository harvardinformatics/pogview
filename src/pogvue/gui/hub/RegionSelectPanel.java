package pogvue.gui.hub;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.RowSpec;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.io.*;
import pogvue.datamodel.Alignment;
import pogvue.datamodel.Sequence;
import pogvue.datamodel.Sequence;
//import pogvue.gui.AlignFrame;
import pogvue.gui.AlignViewport;
import pogvue.gui.AlignmentPanel;
import pogvue.gui.Controller;
import pogvue.gui.menus.MenuManager;
import pogvue.gui.renderer.ConsensusRenderer;
import pogvue.gui.renderer.GraphRenderer;
import pogvue.io.FileParse;
import java.util.*;


/**
 * Created by IntelliJ IDEA.
 * User: mclamp
 * Date: Aug 10, 2007
 * Time: 4:51:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class RegionSelectPanel extends JPanel implements DocumentListener, ActionListener {
  String chrstr   = "";
  String startstr = "";
  String endstr   = "";

    String idstr = "";
    Sequence[] s;

  JComboBox  chr;
  JTextField chrstart;
  JTextField chrend;
  
  JTextField   region_str;
  JProgressBar progressBar;
  JLabel       progressLabel;
  FormLayout   layout;

  JFrame       jf = null;
  JPanel       jp = null;
  
  GetRegionThread t;
    GetFeatureThread ft;
  public RegionSelectPanel() {
    buildPanel();
  }
  
  public void initComponents(){
    chr      = createChrComboBox();
    
    chrstart = new JTextField();
    chrend   = new JTextField();
    
    region_str = new JTextField();
    
    region_str.setEditable(false);
    
    chr       .addActionListener(this);
    chrstart  .getDocument().addDocumentListener(this);
    chrend    .getDocument().addDocumentListener(this);
    
  }
  
  public JComboBox createChrComboBox() {
    
    chrstr = "-";
    
    return new JComboBox( new String[] {"-","chr1","chr2","chr3","chr4","chr5","chr6","chr7","chr8","chr9",
    "chr10","chr11","chr12","chr13","chr14","chr15","chr16","chr17","chr18",
    "chr19","chr20","chr21","chr22","chrX","chrY"});
  }
  
  public void buildPanel() {
    initComponents();
    
    layout = new FormLayout(
            "right:max(40dlu;pref), 3dlu, max(150dlu;pref)",
            "p, 3dlu, p , 3dlu, p, 3dlu, p, 3dlu, p, "+
            "5dlu, 10dlu, 5dlu, p,3dlu,p");
    
    setLayout(layout);
    setBorder(Borders.DIALOG_BORDER);
    
    CellConstraints cc = new CellConstraints();
    
    add(new JLabel("Chromosome"),          cc.xy(1,3));
    add(chr,                               cc.xy(3,3));
    add(new JLabel("Start"),               cc.xy(1,5));
    add(chrstart,                          cc.xy(3,5));
    add(new JLabel("End"),                 cc.xy(1,7));
    add(chrend,                            cc.xy(3,7));
    add(new JLabel("OR"),                  cc.xy(3,11));
    add(new JLabel("Region string"),       cc.xy(1,13));
    add(region_str,                        cc.xy(3,13));
    
  }
  
  public Component createSeparator(String textWithMnemonic) {
    return DefaultComponentFactory.getInstance().createSeparator(
            textWithMnemonic);
    
  }
  
  public void insertUpdate(DocumentEvent e) {
    updateLog(e, "inserted into");
  }
  public void removeUpdate(DocumentEvent e) {
    updateLog(e, "removed from");
  }
  public void changedUpdate(DocumentEvent e) {
    //Plain text components do not fire these events
  }
  public void updateLog(DocumentEvent e, String action) {
    Document d = e.getDocument();
    
    
    System.out.println("Document " + d);
    
    if (d == chrstart.getDocument()) {
      startstr = chrstart.getText();
    } else if (d == chrend.getDocument()) {
      endstr   = chrend.getText();
    }
    
    setRegionStr();
    
  }
  public void keyPressed(KeyEvent evt) {}
  
  public void keyReleased(KeyEvent evt) {}
  
  public void setRegionStr() {
    String tmpstr = chrstr + "." + startstr + "-" + endstr;
    
    region_str.setText(tmpstr);
  }
  
  public String getRegionString() {
    return  "query=" + chrstr + "&start=" + startstr + "&end=" + endstr + "&z=2";
  }
  
  public void createProgressBar() {
    // Change layout to include a progress bar and a label
    
    layout.appendRow(new RowSpec("5dlu"));
    layout.appendRow(new RowSpec("pref"));
    
    layout.appendRow(new RowSpec("2dlu"));
    layout.appendRow(new RowSpec("pref"));
    
    CellConstraints cc = new CellConstraints();
    
    // Insert progress bar
    
    progressBar = new JProgressBar();
   
    progressLabel = new JLabel();
    progressLabel.setFont(new Font("Helvetica",Font.PLAIN,10));
    
    add(progressBar, cc.xy(3,15));
    add(progressLabel, cc.xy(3,17));
    progressBar.setIndeterminate(true);
   
    // This is the only thing that makes the progressbar appear
    actionPerformed(new ActionEvent(this,0,"Wait")); 
    
  }
  
  public void fetchRegion(String idstr) {
      this.idstr = idstr;
    createProgressBar();
    
    // CREATE thread that fetches the genes (???)
    
    t = new GetRegionThread(chrstr,startstr,endstr,this,null);
    
    t.start();
    
  }
    public void fetchFeatures(String idstr) {

	ft  = new GetFeatureThread(chrstr,startstr,endstr,this,true,true,true);

	ft.start();
    }
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == chr) {
      chrstr = (String)chr.getSelectedItem();
      setRegionStr();
      return;
    }
    
    if (e.getActionCommand().equals("Wait")) {
        
        //progressBar.setIndeterminate(true);
        
        progressLabel.setText("Connecting to server...");
        repaint();
        return;
    }
    if (e.getSource() instanceof FileParse) {
      if (e.getActionCommand().equals("Wait")) {
        
        //progressBar.setIndeterminate(true);
        
        progressLabel.setText("Connecting to server...");
        
        
        
      } else {
        if (progressBar.isIndeterminate()) {
          progressBar.setIndeterminate(false);
          
          int size = (int)t.getSize();
          
          progressBar.setMinimum(0);
          progressBar.setMaximum(size);
          progressBar.setValue(0);
          
          progressLabel.setText("Loading...");
        }
        
        
        
        if (e.getActionCommand().indexOf("Len=") == 0) {
          
          int size = (int)t.getSize();
        
          int val = Integer.parseInt(e.getActionCommand().substring(4));
        
          progressBar.setValue(val);
          progressLabel.setText("Loaded " + progressBar.getValue() + " of " + size);
        }
      }
    }
    if (e.getSource() instanceof GetRegionThread && e.getActionCommand().equals("Done")) {
	//progressBar.setVisible(false);
	progressLabel.setText("Creating alignment...");
	
	fetchFeatures(idstr);
	
    }
    if (e.getSource() instanceof GetFeatureThread && e.getActionCommand().equals("Done")) {
      progressBar.setVisible(false);
      progressLabel.setText("Creating alignment...");
      
      Sequence[] s = t.getOutput();
      Alignment  al = new Alignment(s);
      

      Vector feat = ft.getOutput();

      for (int i = 0; i < feat.size(); i++) {
	  al.addSequence((Sequence)feat.elementAt(i));
      }

      

      if (jf == null) {	
        jf = new JFrame("Alignment: " + chrstr + "." + startstr + "-" + endstr);
      } else {
	jf.setTitle("Alignment: " + chrstr + "." + startstr + "-" + endstr);
        jf.getContentPane().remove(jp);
      }

      jp = GenomeInfoFactory.makePanel(al,jf.getTitle(),0.1,10,0,0,1000,650);
      jf.getContentPane().add(jp);
      
      Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
      
      jf.setLocation(sd.width / 2 - 1000 / 2,
		     sd.height / 2 - 700 / 2);
      
      jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

      jf.setSize(1000,700);
      jf.setVisible(true);
      
      // This is needed when the panel is brought up from a Choosepanel button.
      //ap.repaint();
      //ap2.repaint();
      
      progressLabel.setText("");
      progressLabel.hide(); 
    }

  }
}
  
