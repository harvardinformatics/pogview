package pogvue.gui;


import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import java.io.*;
import java.beans.*;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import pogvue.datamodel.*;
import pogvue.datamodel.motif.*;
import pogvue.io.*;
import pogvue.gui.*;
import pogvue.gui.hub.*;
import pogvue.gui.event.*;
import pogvue.analysis.*;
import pogvue.util.*;

import org.jdesktop.swingworker.*;

public class SearchPanel extends JPanel implements ActionListener, PropertyChangeListener {
  Container    parent;
  
  JPanel    topPanel;
  JPanel    midPanel;
  JPanel    botPanel;
  
  JTextArea textArea;

  JButton   okButton;

  JCheckBox  tfsearch1;
  JCheckBox  tfsearch2;
  JTextField textField;
  JTextField threshField;
  JTextField infField;
  
  Alignment     align;
  
  Controller    controller;
  AlignViewport viewport;
  
  Vector mats;
  
  SearchThread searchThread = null;
  SearchWorker st           = null;
  
  public ProgressPanel progressPanel;
  
  public JFrame searchFrame;    // This is the progressbar frame

  public SearchPanel(Alignment align, AlignViewport av, Container parent) {
    this.align      = align;
    this.controller = av.getController();
    this.viewport   = av;
    this.parent     = parent;

    topPanel  = new JPanel();
    midPanel  = new JPanel();
    botPanel  = new JPanel();
    
    FormLayout topLayout = new FormLayout("pref:grow",               // Columns
                                          "pref:grow");              // Rows
    
    FormLayout midLayout = new FormLayout("pref,pref:grow",
                                          "pref,pref,pref,pref,pref");

    FormLayout botLayout = new FormLayout("pref:grow, pref, pref:grow",  // Columns
		                               			  "pref");                       // Rows
    
    topPanel.setLayout(topLayout);
    midPanel.setLayout(midLayout);
    botPanel.setLayout(botLayout);
    
    CellConstraints cc = new CellConstraints();
    
    textArea     = new JTextArea();

    tfsearch1    = new JCheckBox();
    tfsearch1.setSelected(true);

    textField   = new JTextField();
    threshField = new JTextField("0.1");
    infField    = new JTextField("0");
    
    JLabel tfLabel1     = new JLabel("Search transfac");
    JLabel searchLabel = new JLabel("Restrict search to matrix name (e.g. Sp1)");
    JLabel threshLabel = new JLabel("Information threshold");
    

    okButton = new JButton("Ok");
    
    
    topPanel.add(textArea,        cc.xy(1,1, "fill, fill"));   // For user input of kmers

    midPanel.add(tfLabel1,        cc.xy(1,1, "fill, fill"));   // Label for transfac searching
    midPanel.add(tfsearch1,       cc.xy(2,1, "fill, fill"));   // Checkbox for searching transfac
    
    midPanel.add(searchLabel,     cc.xy(1,3, "fill, fill"));   // Label for transfac names
    midPanel.add(textField,       cc.xy(2,3, "fill, fill"));   // Transfac names to search
    
    midPanel.add(threshLabel,     cc.xy(1,4, "fill, fill"));   // Label for threshold
    midPanel.add(threshField,     cc.xy(2,4, "fill, fill"));   // User input for threshold

    botPanel.add(okButton,        cc.xy(2,1, "fill, fill"));   // Col 2 , Row 1  - ok button
    
    setLayout(new BorderLayout());
    
    add("North", topPanel);
    add("Center",midPanel);
    add("South", botPanel);
    
    okButton.addActionListener(this);
  }

  public void propertyChange(PropertyChangeEvent evt) {
    if ("progress" == evt.getPropertyName()) {
      int progress = (Integer) evt.getNewValue();
      progressPanel.setValue(progress);
    }
    
    if ("done" == evt.getPropertyName()) {
      progressPanel.setIndeterminate();
      Vector out = st.getOutput();
      
      if (out != null && out.size() > 0) {
        Vector gffvect = getGFFByName(viewport.getAlignment(),"Transfac");
        
        System.out.println("Transfac " + gffvect);
        
        for (int g = 0; g < gffvect.size(); g++) {
          viewport.getAlignment().deleteSequence((Sequence)gffvect.elementAt(g));	
        }
        
        GFF top = new GFF("Transfac","",1,2);
        GFF dum = new GFF("Transfac","",1,2);
        
        for (int i = 0 ; i < out.size(); i++) {
          SequenceFeature sf = (SequenceFeature)out.elementAt(i);
          System.out.println("Feature is " + sf.getStart() + " " + sf.getEnd());
          top.addFeature(sf);
        }
        
        Vector newgff = GFFFile.bumpGFF_nosort(top);
        
        for (int i = 0 ; i < newgff.size(); i++) {
          GFF tmpgff = (GFF)newgff.elementAt(i);
          viewport.getAlignment().addSequence(dum);
          viewport.getAlignment().addSequence(dum);
          viewport.getAlignment().addSequence(dum);
          viewport.getAlignment().addSequence(tmpgff);
        }
        
        searchFrame.setVisible(false);
        getTopLevelAncestor().setVisible(false);
        
        controller.handleAlignViewportEvent(new AlignViewportEvent(this,viewport,AlignViewportEvent.COLOURING));
      }
      st = null;
    }
  }


 public void actionPerformed(ActionEvent e) {
   if (e.getSource() == okButton) {
     StringTokenizer str = new StringTokenizer(textArea.getText(),"\n");
     
     while (str.hasMoreTokens()) {
	    String kmer = str.nextToken();
	    
	    Vector kmers = findKmers(align.getSequenceAt(0),kmer,viewport.getStartRes(), viewport.getEndRes());
	    Vector gffvect = getGFFByName(align,kmer);		
	    
	    for (int g = 0; g < gffvect.size(); g++) {
	      viewport.getAlignment().deleteSequence((Sequence)gffvect.elementAt(g));	
	    }
	    
	    GFF top = new GFF(kmer,"",1,2);
	    GFF dum = new GFF(kmer,"",1,2);
	    
	    for (int i = 0 ; i < kmers.size(); i++) {
	      int j = ((Integer)kmers.elementAt(i)).intValue();
	      top.addFeature(new SequenceFeature(null,kmer,j,j+kmer.length(),""));	  
	    }
	    
	    Vector newgff = GFFFile.bumpGFF_nosort(top);
	    
	    for (int i = 0 ; i < newgff.size(); i++) {
	      GFF tmpgff = (GFF)newgff.elementAt(i);
	      viewport.getAlignment().addSequence(tmpgff);
	    }
	    
	    controller.handleAlignViewportEvent(new AlignViewportEvent(this,viewport,AlignViewportEvent.COLOURING));
	    
	  }
	  
	  if (tfsearch1.isSelected()) {
	    
	    double thresh = Double.parseDouble(threshField.getText());
	    double inf    = Double.parseDouble(infField.getText());
	    
	    progressPanel = new ProgressPanel(0,100,this);
	    
	    searchFrame= new JFrame("Search Transfac");
	    searchFrame.getContentPane().setLayout(new BorderLayout());
	    searchFrame.getContentPane().add(progressPanel, BorderLayout.PAGE_START);
	    
	    
	    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	    
	    searchFrame.setLocation(dim.width  / 2 - 100 / 2,
	        dim.height / 2 - 50 / 2);
	    
	    searchFrame.pack();
	    searchFrame.setVisible(true);

	    String searchString = viewport.getAlignment().getSequenceAt(0).getSequence(viewport.getStartRes(),viewport.getEndRes());
	    System.out.println("Search offset " + viewport.getStartRes());
	    
	    //st = new SearchWorker(searchString,viewport.getStartRes());
	    st = new SearchWorker(viewport.getAlignment().getPwm(viewport.getStartRes(),viewport.getEndRes()),viewport.getTransfacMatrices(),viewport.getStartRes());
	    st.addPropertyChangeListener(this);
	    st.setViewport(viewport);
	    st.setThreshold(thresh);
	    
	    if (!textField.getText().equals("")) {
	      st.setNames(textField.getText());
	    }
	    
	    st.start();
	    
	  } 
   }
 }
    
    
  public static Vector getGFFByName(Alignment align,  String name) {

    int i = 0;
    Vector out = new Vector();
    
    while (i < align.getHeight()) {
      if (align.getSequenceAt(i) instanceof GFF &&
          align.getSequenceAt(i).getName().equals(name)) {
        GFF gff = (GFF)align.getSequenceAt(i);
        out.addElement(gff);
      }
      i++;
    }
    return out;

  }
	
  public static Vector findKmers(Sequence seq, String kmer,int start, int end) {
	Vector out = new Vector();

	String str = seq.getSequence();

	int pos = start;
	//System.out.println("Kmer is " + kmer);
	while (pos < end && pos != -1) {
	    pos++;
	    pos = str.indexOf(kmer,pos);

	    if (pos >=1) {
	      out.addElement(pos);
	    }

	}

	str = pogvue.gui.schemes.ResidueProperties.reverseComplement(str);
	pos = start;

	int len = str.length();

	while (pos < end && pos != -1) {
	    pos++;
	    pos = str.indexOf(kmer,len-pos);

	    if (pos > 1) {
	      out.addElement(pos);
	    }

	}

	return out;
    }

    public Hashtable searchConstrainedRegions() {

	Hashtable out = new Hashtable();

	if (mats == null) {
		mats = viewport.getTransfacMatrices();
	}

	int start = 0;
	System.out.println(" " + align.getWidth());

	while (start < align.getWidth() - 1000) {
	    int[][] intpid =  AAFrequency.calculatePID_test(align.getSequenceAt(0),
							    align.getSequences(),
							    50,start,start+1000);

	    int winstart = start;

	    int mousenum = -1;

	    int i = 0;

	    while (i < align.getSequences().size()) {
		Sequence seq = align.getSequenceAt(i);

		if (seq.getName().indexOf("ouse") > 0 ||
		    seq.getName().indexOf("mm") >= 0) {
		    mousenum = i;
		}

		i++;

	    }
		
	    if (mousenum == -1) {
		return out;
	    }
	    double thresh = 0.8;

	    int tmpstart = 25;//(int)(viewport.getPIDWindow()/2);

	    double window_mult = 100.0/50;//(viewport.getPIDWindow()+1);

	    while (tmpstart < 1000 - 50) {

	      if (intpid[tmpstart][mousenum] >= thresh*50) {//iewport.getPIDWindow()) {
		    System.out.println("Found window " + tmpstart + " " + start + " " + intpid[tmpstart][mousenum]);

		    Hashtable tmpout = searchTransfac(tmpstart+start, tmpstart+start+50);//viewport.getPIDWindow());

		    Enumeration en = tmpout.keys();

		    while (en.hasMoreElements()) {
			String el = (String)en.nextElement();
			if (!out.containsKey(el)) {
			    Vector tmpv = new Vector();
			    out.put(el,tmpv);
			}

			Vector tmpv = (Vector)out.get(el);

			Vector tmp2 = (Vector)tmpout.get(el);

			for (int kk = 0; kk < tmp2.size(); kk++) {
			    tmpv.addElement(tmp2.elementAt(kk));
			}
		    }

		    tmpstart += 50;//iewport.getPIDWindow();
		} else {
		    tmpstart++;
		}
	    }
	    start += 1000;
	}
	return out;
    }


    public Hashtable searchTransfac(int start, int end) {
      Hashtable out = new Hashtable();

      double thresh = 0.8;
      
      for (int i = start; i <= end; i++) {
        for (int j = 0; j < mats.size(); j++) {
          
          TFMatrix tfm = (TFMatrix)mats.elementAt(j);
          
          double [] pwm1 = tfm.getPwm().getPwm();
          
          if (i+pwm1.length/4 < align.getWidth() && pwm1.length/4 > 8) {
            
		    String tmp = align.getSequenceAt(0).getSequence().substring(i,i+pwm1.length/4);

		    double[] seqvec = Correlation4.seqvec(new Sequence(tmp,tmp,1,tmp.length()));

		    Pwm tmppwm  = new Pwm(seqvec,"");
		    PwmCluster pwmclus = new PwmCluster(tmppwm);

		    for (int k = 1; k < align.getHeight(); k++) {
			if (align.getSequenceAt(k).getSequence().length() < i+pwm1.length/4 && 
			    align.getSequenceAt(k).getSequence().length() > 0) {
			    System.out.println("Align " + align.getSequenceAt(k).getSequence() + " "  + i);
			    tmp = align.getSequenceAt(k).getSequence().substring(i,i+pwm1.length/4);
			    seqvec = Correlation4.seqvec(new Sequence(tmp,tmp,1,tmp.length()));
			    if (tmp.indexOf("-") < 0) {
				pwmclus.addPwm(new Pwm(seqvec,""));
			    }
			}
		    } 

		    double   tmpcorr = Correlation4.get(pwm1,pwmclus.getPwm());


		    if (tmpcorr > thresh) {
			if (!out.containsKey(tfm.getName())) {
			    Vector tmpv = new Vector();
			    out.put(tfm.getName(),tmpv);
			}

			Vector tmpv = (Vector)out.get(tfm.getName());

			tmpv.add(i);

			System.out.print("Matrix " +  tfm.getName() + " " + tfm.getConsensus() + " " + i);
			System.out.print(" Consensus mat " + PwmCluster.getConsensus(pwm1));
			System.out.print(" Consensus aln " + PwmCluster.getConsensus(pwmclus.getPwm()));
			System.out.println(" Corr " + tmpcorr);	
			//System.out.println("logo\n");
			//		Pwm.printLogo(pwm1);
			//System.out.println("\nlogo\n");
			//Pwm.printLogo(pwmclus.getPwm());
			//System.out.println("\nlogo\n");
		    }
		}
	    }

	}
	return out;
    }
    public static void main(String[] args) {
	
// 	try {
// 	    FastaFile ff = new FastaFile(args[0],"File");
	    
// 	    ff.parse();
	    
// 	    Sequence[] seqs = ff.getSeqsAsArray();
	    
// 	    Alignment align = new Alignment(seqs);

// 	    JPanel jp = GenomeInfoFactory.makePanel(al,"Pogvue",.1,10,null);
// 	    JFrame jf2 = new JFrame("Pogvue");

// 	    jf2.getContentPane().add(jp);
// 	    jf2.setSize(1000,700);
// 	    jf2.setVisible(true);

	    
// 	    JFrame jf = new JFrame("Test SearchPanel");
	    
// 	    SearchPanel sp = new SearchPanel(align,);
	    
// 	    jf.getContentPane().setLayout(new BorderLayout());
	    
// 	    jf.getContentPane().add("Center",sp);
	    
// 	    jf.setSize(500,500);
	    
// 	    jf.setVisible(true);
// 	} catch (IOException e) {
// 	    System.out.println("IOException " + e);
// 	}
    }

}
