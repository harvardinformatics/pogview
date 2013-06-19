package pogvue.gui.hub;

//import java.awt.Desktop;
import java.net.URI;
import java.net.URL;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.*;

import javax.swing.text.Document;
import javax.swing.event.*;

import pogvue.gui.*;
import pogvue.gui.event.*;
import pogvue.gui.renderer.TrackRenderer;
import pogvue.io.*;
import pogvue.util.*;
import pogvue.datamodel.*;
import pogvue.analysis.*;
import javax.swing.table.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class GFFTableModel extends AbstractTableModel implements ActionListener, DocumentListener {
  private Vector           feat;
  private FeatureSelection fsel;

  private String[] colnames = {"Name","Type1","Start","End","Score","Strand","HitName","Pogvue","UCSC"};
  
  private JTable     table;
  private JButton    button;
  private JButton    ucsc;
  private JTextField tf;
  private JLabel     jl;
  private Vector     tmpfeat;

  public GFFTableModel(Vector feat) {
    this.feat = feat;
    
    button = new JButton("pogvue");
    ucsc   = new JButton("ucsc");
  }
  
  public void setTextField(JTextField tf) {
  	this.tf = tf;
  }
  
  public JTextField getTextField() {
    return tf;
  }
  
  public void setJLabel(JLabel jl) {
    this.jl = jl;
  }
  
  public int getRowCount() {
    if (fsel.getFeatures().size() > 0) {
      
      return fsel.getFeatures().size();
      
    } else {
      return feat.size();
    }
  }
  public int getColumnCount() {
  	return colnames.length;
  }
  public String getColumnName(int col) {
    return colnames[col];
  }
  public Class getColumnClass(int col) {
    return getValueAt(0,col).getClass();
  }
  public void setFeatureSelection(FeatureSelection fsel){
    this.fsel = fsel;
  }
  public Vector getAllFeatures() {
    return feat;
  }

  public void actionPerformed(ActionEvent e) {
  	
  	if (tf != null) {
  		if (e.getSource() instanceof JButton ||
  				tf.getText().indexOf("ch") != 0) {
  		  // Search the FeatureSet
  		  searchTable(tf.getText());
  		}
  	}
  }

  public void searchTable(String str) {
  	// This is functionality that should be in the FeatureSet
  	
  	if (str.length() < 3) {
      fsel.removeAllFeatures();
      jl.setText("No features");
      return;
    }
  	
    Vector found = new Vector();

    if (str.indexOf("chr") == 0 &&
    		str.indexOf(":") > 0    &&
    		str.indexOf("-") > 0) {
    	System.out.println("This is a region string - deal");
    	ChrRegion sr = parseRegionString(str);

    	if (sr != null) {
    		Vector tmpfeat = sr.overlaps(feat);
    		fsel.removeAllFeatures();
    		jl.setText(tmpfeat.size() + " features");
    		
    		for (int i = 0; i > tmpfeat.size(); i++) {
    			SequenceFeature tmpf = ((SequenceFeature)tmpfeat.elementAt(i)).clone();
    			fsel.addFeature(tmpf);
    		}
    		//fsel.addFeatures(tmpfeat);
    	}
    	
    	return;
    }

    for (int i = 0;i < feat.size(); i++) {
      SequenceFeature f = (SequenceFeature)feat.elementAt(i);
      
      if (f.searchId(str) == true) { 
        found.addElement(f);
      }
    }
    if (found.size() > 0) {
    	fsel.removeAllFeatures();
    	jl.setText(found.size() + " features");
    	fsel.addFeatures(found);
    }
  }
  
  
  public Object getValueAt(int row, int col) {
  	SequenceFeature f = (SequenceFeature)feat.elementAt(row);
  	
  	if (fsel.getFeatures().size() > 0) {
  		f = (SequenceFeature)fsel.getFeatures().elementAt(row);
    }
    
  	// We could move this into feature but we'd have to have knowledge of the columns
  	
  	if (col == 0) {
      return f.getId();
    } else if (col == 1) {
      return f.getType();
    } else if (col == 2) {
      return f.getStart();
    } else if (col == 3) {
      return f.getEnd();
    } else if (col == 4) {
      return f.getScore();
    } else if (col == 5) {
      return f.getStrand();
    } else if (col == 6) {
      if (f.getHitFeature() != null){
      	return f.getHitFeature().getId();
      } else {
      	return "";
      }
    } else if (col == 7) {
      return button;
    } else if (col == 8) {
      return ucsc;
    }
    return "";
  }
  
  public void setTable(JTable t) {
    this.table = t;
  }
  
  public Vector getFeatures() {
    System.out.println("Selection " + fsel.getFeatures());
    
  	if (fsel.getFeatures().size() > 0) {
  		return fsel.getFeatures();
  	} else {
  		return feat;
  	}
  }
  
  public void insertUpdate(DocumentEvent e) {
    updateLog(e, "inserted into");
    //searchTable(tf.getText());
  }
  public void removeUpdate(DocumentEvent e) {
    updateLog(e, "removed from");
    //searchTable(tf.getText());
  }
  public void changedUpdate(DocumentEvent e) {
    updateLog(e, "changed from");
  }
  public void updateLog(DocumentEvent e, String action) {
    Document d = e.getDocument();
    
    System.out.println("Document " + d);
    
    if (d == tf.getDocument()) {
      String str = tf.getText();
      System.out.println("Text string is " + str + " " + action);
    }
    
  }

  public static void main(String[] args) {
    System.out.println("Args " + args);
    
    try {
	    
      Hashtable opts = GetOptions.get(args);
      System.out.println("Opts " + opts);
      String lenfile     = null;
      String gfffile     = null;	  
      String bandfile    = null;
      boolean dens     = false;
      boolean linked   = false;

      int    binsize   = 1000000;

      if (opts.containsKey("-lenfile")) {
      	lenfile  = (String)opts.get("-lenfile");
      }
      if (opts.containsKey("-bandfile")) {
      	bandfile  = (String)opts.get("-bandfile");
      }
      if (opts.containsKey("-gfffile")) {
      	gfffile  = (String)opts.get("-gfffile");
      }        
      
      if (opts.containsKey("-density")) {
      	dens  = true;
      }        
      if (opts.containsKey("-linked")) {
      	linked  = true;
      }        
      if (opts.containsKey("-binsize")) {
      	binsize = Integer.parseInt((String)opts.get("-binsize"));
      }
      
      // First the chromosomes

      // So the file parsing shouldn't be done here - from a factory
      // The chromosomes should be features
      
      FileParse    file         = new FileParse(lenfile,"File");
      Vector       chromosomes = new Vector();
      CytoBandFile cbf         = new CytoBandFile(bandfile,"File");
      Hashtable bands          = cbf.getBands();
      
      String    line;
      
      while ((line = file.nextLine()) != null) {
      	
      	StringTokenizer str  = new StringTokenizer(line,"\t" );
      	String          name = str.nextToken();
      	int             len  = Integer.parseInt(str.nextToken());
      	
      	if (name.indexOf("_random") == -1 &&
      			name.indexOf("_NT_") == -1) {
      		Chromosome chr = new Chromosome(len,(Vector)bands.get(name),name);
      		chromosomes.addElement(chr);
      	}
      }
      
      // Adding features to chromosomes should be somewhere else
      GFFFile gff  = new GFFFile(gfffile,"File");
      
      Vector  feat = gff.getFeatures();
      
      Hashtable chrhash = new Hashtable();
      
      for (int i = 0 ; i < chromosomes.size(); i++) {
      	Chromosome chr = (Chromosome)chromosomes.elementAt(i);
      	if (feat.size() > 0) {
      		chr.setDensity(dens); 
      		chr.addFeatures(feat,true);
      		chr.featureDensity(binsize);
      	} else {
      		chromosomes.removeElement(chr);
      	}
      }
      
      System.out.println("Chromosomes " + chromosomes.size());
      
      KaryotypePanel kp   = new KaryotypePanel(chromosomes);
      JScrollPane    jsp2 = new JScrollPane(kp);
      JTextField     tf   = new JTextField();
      JButton        bt   = new JButton("Search");
      
      kp.setLinkedMaxcount(linked);
      kp.setScrollPane(jsp2);
	  
      Vector topfeat = new Vector();
      Vector lowfeat = new Vector();
	  
      for (int i = 0; i < feat.size(); i++) {
      	if (((SequenceFeature)feat.elementAt(i)).getType().equals("gene")) {
      		topfeat.addElement(feat.elementAt(i));
	      
      	} else {
      		lowfeat.addElement(feat.elementAt(i));
	    }
      }
      for (int i = 0 ; i < lowfeat.size();i++) {
      	topfeat.addElement(lowfeat.elementAt(i));
      }
	  
      GFFTableModel    tm    = new GFFTableModel(topfeat);
      JTable           table = new JTable(tm);
      
      // This doesn't look right
      FeatureSelection fs    = new FeatureSelection(kp,table); 
	  
      bt.addActionListener(tm);
      tf.getDocument().addDocumentListener(tm);
	  
      kp.setFeatureSelection(fs);
      tm.setFeatureSelection(fs);
	  
      table.addMouseListener(fs);
	  
      //Now the renderer

      TableCellRenderer renderer = table.getDefaultRenderer(JButton.class);

      table.setDefaultRenderer(JButton.class, new GFFTableButtonRenderer(renderer));
      table.addMouseListener(new GFFTableButtonMouseListener(table));
	  
      //table.setFillsViewportHeight(true);
      //table.setAutoCreateRowSorter(true);
      table.getSelectionModel().addListSelectionListener(fs);
      
      tm.setTable(table);
      
      // The Pfam renderer
      TableColumnModel tcm = table.getColumnModel();
      TableColumn tc = tcm.getColumn(7);
      //tc.setCellRenderer(new PfamRenderer());
          
      
      // So now we have the frame stuff
      JFrame      jf  = new JFrame();
      JScrollPane sp  = new JScrollPane(table);
      
      JPanel contain1 = new JPanel();
      JLabel jl = new JLabel("");
      
      tm.setTextField(tf);
      tm.setJLabel(jl);

      contain1.setLayout(new BorderLayout());

      contain1.add("Center",tf);
      contain1.add("East",jl);
      contain1.add("West",bt);
	  
      JPanel contain2 = new JPanel();
      contain2.setLayout(new BorderLayout());

      contain2.add("Center",sp);
      contain2.add("North",contain1);

      JSplitPane  jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT,jsp2,contain2);
	  
      jsp.setOneTouchExpandable(true);
      jsp.setDividerLocation(400);
      
      jf.getContentPane().add(jsp);
          
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	  
      int width = (int)(0.5*dim.width);
      if (width > dim.width) {
      	width = dim.width;
      }
      int height = (int)(0.7*dim.height);
      if (height > dim.height) {
      	height = dim.height;
      }
      jf.setLocation(dim.width  / 2 - width / 2,
      		dim.height / 2 - height / 2);
      
      
      jf.setSize(width,height);
	  
      jf.setVisible(true);
	  
    } catch (IOException e) {
	    System.out.println("ERROR: " + e);
    }
	
  }
  public ChrRegion parseRegionString(String str) {
  	int pos1 = str.indexOf(":");
  	int pos2 = str.indexOf("-",pos1);
    
  	if (pos1 > 0 && pos2 > 0 &&
  			str.length() > pos2+1) {
  		System.out.println("String " + str.length() + " " + pos2);
  		String chr = str.substring(0,pos1);
  		System.out.println(str.substring(pos1+1,pos2));
  		System.out.println(str.substring(pos2+1));
  		Integer start = Integer.parseInt(str.substring(pos1+1,pos2));
  		Integer end   = Integer.parseInt(str.substring(pos2+1));
  		
  		ChrRegion sr = new ChrRegion(chr,start,end);
  		
      return sr;
  	}
  
  	return null;
  }
}

class GFFTableButtonRenderer implements TableCellRenderer {
    private TableCellRenderer renderer;
    
    public GFFTableButtonRenderer(TableCellRenderer renderer) {
    	this.renderer = renderer;
    }
    
    public Component getTableCellRendererComponent(JTable table, 
						   Object value,
						   boolean isSelected,	
						   boolean hasFocus,
						   int row, 
						   int column) {
    	
    	if (value instanceof Component){
    		return (Component)value;
    	} else { 
    		return renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    	}
    }
}   

class GFFTableButtonMouseListener implements MouseListener, ActionListener {
	private  JTable table;
	private  
	JFrame jf;
	public   CustomGlassPane cgp;
	private  AlignSplitPanel asp;
	public   ProgressPanel progressPanel;
	public   RegionFetchThread rft;
	
	// Hmm - this stuff shouldn't be here
	// We get the coords - just pass it on to the Drawing component
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == rft) {

			Alignment      al = rft.getOutput();
			ChrRegion sr = al.getChrRegion();

			updateFrame(al,sr.getStart(),sr.getStart(),sr.getEnd());
		}
	}

	private void forwardEventToButton(MouseEvent e) {
    TableColumnModel columnModel = table.getColumnModel();
    
    int              column      = columnModel.getColumnIndexAtX(e.getX());
    int              row         = e.getY() / table.getRowHeight();
    Object           value;
    JButton          button;
    MouseEvent       buttonEvent;
    
    if (row >= table.getRowCount() || 
    		row < 0 ||
    		column >= table.getColumnCount() || 
    		column < 0) {
    	return;
    }   
	
    value = table.getValueAt(row, column);
    
    if (!(value instanceof JButton)) {
      return;
    }  
    
    button = (JButton)value;
    
    buttonEvent =      (MouseEvent)SwingUtilities.convertMouseEvent(table, e, button);
    button.dispatchEvent(buttonEvent);
    
    // This is necessary so that when a button is pressed and released
    // it gets rendered properly.  Otherwise, the button may still appear
    // pressed down when it has been released.
    
    table.repaint();  
    table.revalidate();
  }
	
  
  public GFFTableButtonMouseListener(JTable table) {
    this.table = table;
  }
  
  public void mouseClicked(MouseEvent e) {
    int row = table.getSelectedRow();
    int col = table.getSelectedColumn();
    
    GFFTableModel tm = (GFFTableModel)table.getModel();
    
    String chr  = (String)tm.getValueAt(row,0);
    
    int    start = Integer.valueOf((Integer)tm.getValueAt(row,2));
    int    end   = Integer.valueOf((Integer)tm.getValueAt(row,3));
    
    int tmpstart = start;
    int tmpend   = end;

    ChrRegion sr = new ChrRegion(chr,tmpstart,tmpend);
    
    if (col == 7) {	
      if (end-start < 500000) { 
      tmpstart = start - 20000  - 1;
      tmpend   = end   + 20000;
      } 
      if (tmpstart < 1) {
      	tmpstart = 1;
      }
      
      start = tmpstart;
      end   = tmpend;

      sr = new ChrRegion(chr,tmpstart,tmpend);
      
      Alignment      al      = null;
      Vector         newfeat = null;
      Vector         tmpfeat = null;

      // So the overlaps should be after the fetch - addOverlappingFeatures
    
      if (tm.getTextField().getText().length() > 0) {
        ChrRegion sr2      = tm.parseRegionString(tm.getTextField().getText());
        if (sr2 != null) {
          sr = sr2;
        }
      }
      
      tmpfeat = sr.overlaps(tm.getFeatures());
      newfeat = new Vector();
      
      for (int i = 0; i < tmpfeat.size(); i++) {
        SequenceFeature sf1 = (SequenceFeature)tmpfeat.elementAt(i);
        SequenceFeature sf2 = sf1.clone();
        
        newfeat.addElement(sf2);
      }

      // Hmm - not here
      // Start off a fetch
      // Send a 'new region event'  to the Controller
      
      System.out.println("Region is " + sr);

      rft = new RegionFetchThread(sr.getChr(),sr.getStart(),sr.getEnd(),newfeat);
      rft.setActionListener(this);
      rft.start();
      
    } else if (col == 8) {
    	// show ucsc page in browser
    	tmpstart = start - 10000;
    	if (tmpstart < 1) {
    		tmpstart = 1;
    	}
    	
    	String url = "http://genome.ucsc.edu/cgi-bin/hgTracks?db=hg18&position=" + chr + ":" + tmpstart + "-" + (end+10000);
    	
    	try {
    		
    		showInBrowser(new URL(url));
    		
    	} catch (java.io.IOException e2) {
    		System.out.println("IO Error for " + url);
    		e2.printStackTrace();
    	}
    }
  }
  
  public void updateFrame(Alignment al, int offset, int start, int end) {
    int width  = 1500;
    int height = 1000;
    
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    String title = "Pogvue";
    
    if (al.getChrRegion() != null) {
      title = al.getChrRegion().toString();
    } 
    if (jf == null) {
      jf = new JFrame("title");
      
      cgp = new CustomGlassPane();
      cgp.setOpaque(false);
      
      jf.setGlassPane(cgp);
      jf.getGlassPane().setVisible(true); 
      
      System.out.println("Width " + dim.width + " Height " + dim.height);
      
      if (width > dim.width) {
	width = (int)(0.8*dim.width);
      }
      if (height > dim.height) {
	height = (int)(0.8*dim.height);
      }
      jf.setSize(width,height);
    } 
    
    double cw = (width-150)*1.0/al.getWidth();
    
    if (asp != null) {
      asp.setVisible(false);
      asp.setAlignment(al);
    } else {
      System.out.println("Making panel");
      
      asp = GenomeInfoFactory.makePanel(al,"Pogvue",cw,3,offset,start,end,width-150);	    
      System.out.println("Done1");
      jf.getContentPane().add(asp);
      System.out.println("Done2");
      asp.setVisible(false);
      System.out.println("Done3");
      
      jf.setTitle(title);
      System.out.println("Done31");
      jf.setLocation(dim.width  / 2 - width / 2,
		     dim.height / 2 - height / 2);
      System.out.println("Done32");
      jf.setVisible(true);
      System.out.println("Done4");
    }
    
    System.out.println("Done5");
    int mid = al.getWidth()/2;
    
    asp.getAlignmentPanel2().getAlignViewport().setStartRes(mid - (width-150)/(2*3));
    asp.getAlignmentPanel1().getAlignViewport().setCharWidth(cw,"set");
    asp.getAlignmentPanel2().getAlignViewport().setCharWidth(3,"set");

    asp.setVisible(true);

    cgp.setAlignViewport(asp.getAlignmentPanel1().getAlignViewport());
    cgp.setController(   asp.getAlignmentPanel1().getAlignViewport().getController());
    
    jf.setTitle(title);
    
    System.out.println("Done6");

    AlignViewport av = asp.getAlignmentPanel2().getAlignViewport();

    System.out.println("Done7");
    
    if (asp != null) {
      asp.setVisible(true);
    }
    System.out.println("Done8");
  }
  
  public void mouseEntered(MouseEvent e) {
  	forwardEventToButton(e);
  }
  public void mouseExited(MouseEvent e) {
  	forwardEventToButton(e);  }
  public void mousePressed(MouseEvent e) {
  	forwardEventToButton(e);
  }
  public void mouseReleased(MouseEvent e) {
  	forwardEventToButton(e);
  }
  
  final static void showInBrowser(URL url) {
    try {
      //if (microsoftBrowser && windowsOS)
      //	Runtime.getRuntime().exec("iexplore.exe " + url);
      //      else if (firefoxBrowser && windowsOS)
      //Runtime.getRuntime().exec("firefox.exe \"" + url + "\"");
      //else if (firefoxBrowser && macOS) 

      String os = System.getProperty("os.name");
      System.out.println("OS name " + os);

      if (os.equals("Linux")) {
      	String cmd = "firefox -url " + url.toString();
      	Process p = Runtime.getRuntime().exec(cmd);
      } else if (os.equals("Mac OS X")) {
      	Runtime.getRuntime().exec(new String[] {"open", url.toString()});
      	//Runtime.getRuntime().exec(new String[] {"open", "-a", "Firefox.app", url.toString()});
      	//else 
      	//appletContext.showDocument(url, "_blank");
      }
    } catch (Exception e){
    	System.out.println("Couldn't show in browser: " + e);
    }
  }
  
}

// Hmm - this is really nasty
class CustomGlassPane extends JComponent implements AlignViewportListener, MouseListener, MouseMotionListener { 
    Image         img;
    AlignViewport av;
    Controller    c;
    Graphics2D    gg;
    boolean       display = false;
    AlignViewport internal_av;
    int           mousex;
    int           mousey;

    public void setAlignViewport(AlignViewport av) {
    	this.av = av;
    	internal_av = new AlignViewport(av.getAlignment());
    	internal_av.setCharWidth(1.0,"Glass");
    }
    public void setController(Controller c) {
    	this.c = c;
    	c.addListener(this);
    }
    public Controller getController() {
    	return c;
    }
    public Object getControlledWindow() {
    	return this;
    }
    public boolean handleAlignViewportEvent(AlignViewportEvent e) {
    	
    	if (e.getType() == AlignViewportEvent.GLASS && av.getMouseRes() != -1) {
    		repaint();
    	}
    	
    	return true;
    }
    public void mouseEntered(MouseEvent evt) { }
    public void mouseExited (MouseEvent evt) { }
    public void mouseClicked(MouseEvent evt) { }
    public void mouseMoved  (MouseEvent evt) {
    	
    	mousex = evt.getX();
    	mousey = evt.getY();
    	
    }
    public void mousePressed(MouseEvent evt) { }
    public void mouseDragged(MouseEvent evt) { }
    public void mouseReleased(MouseEvent evt) { }
    
    protected void paintComponent(Graphics g) { 
    	if (av != null && av.getMouseRes() != -1) {
    		Rectangle clip = g.getClipBounds(); 
    		
    		if (img == null) {
    			System.out.println("Size " + size().width + " " + size().height);
    			img = createImage(size().width,size().height);
    			
    			gg = (Graphics2D)img.getGraphics();
    		}
    		
    		//Color alphaWhite = new Color(1.0f, 1.0f, 1.0f, 0.65f); 
    		
    		//g.setColor(alphaWhite); 
    		
    		//g.fillRect(clip.x, clip.y, clip.width, clip.height); 
    		
    		// mousex pixels represents  mousex/charWidth residues
    		// startres will be av.getStartRes() +  (av.getMouseRes - mousex/charWidth);
    		
    		
    		double glassCharWidth = 3;
    		
    		int    panelResWidth  = (av.getEndRes()-av.getStartRes()+1);
    		int    panelPixWidth  = (int)(panelResWidth*av.getCharWidth());
    		
    		int    glassPixWidth  = (int)(2*panelPixWidth/3);
    		
    		int    glassStartRes  = (int)(av.getMouseRes() - glassPixWidth/(glassCharWidth*2));
    		int    glassEndRes    = (int)(glassStartRes + glassPixWidth/glassCharWidth);
    		
    		int    glassResWidth  = glassEndRes-glassStartRes+1;
    		
    		int    pixHeight      = (int)(av.getEndSeq()-av.getStartSeq()+1)*av.getCharHeight();
    		
    		int    offx           = (int)(av.getMousePos() - glassCharWidth*glassResWidth/2);
    		
    		internal_av.setCharHeight(av.getCharHeight());
    		internal_av.setCharWidth(glassCharWidth,"Glass");
    		internal_av.setGFFConfig(av.getGFFConfig());
    		internal_av.hiddenSequences(av.hiddenSequences());
    		
    		//System.out.println("New residue width is " + glassResWidth);
    		//System.out.println("Start residue is " + glassStartRes);
    		//System.out.println("End residue is   " + glassEndRes);
    		//System.out.println("Projected width is " + (glassEndRes-glassStartRes+1)*internal_av.getCharWidth() + " " + glassPixWidth);
    		//System.out.println("Pixel height is " + pixHeight);
	    
    		//System.out.println("Rect " + offx + " " + glassPixWidth);
    		
    		TrackRenderer.drawTracks((Graphics2D)g,
    				glassStartRes,       // Start residue
    				glassEndRes,         // End residue
    				av.getStartSeq(),    // Start seq  
    				av.getEndSeq(),      // End seq
    				glassStartRes,       // Start residue at origin
    				av.getStartSeq(),    // Start sequence at origin
    				offx + 100,          // Offsetx to start drawing
    				45,                  // Offsety to start drawing
    				internal_av);
    		
    		g.setColor(Color.black);
    		g.drawRect(offx+100,24,glassPixWidth,pixHeight+22);
    		g.drawRect(offx+101,25,glassPixWidth,pixHeight+22);
    		
    		//g.drawImage(img,0,0,this);
    	} 
    }
} 

