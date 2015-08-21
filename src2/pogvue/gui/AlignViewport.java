package pogvue.gui;

import pogvue.datamodel.*;
import pogvue.datamodel.motif.*;
import pogvue.datamodel.tree.*;
import pogvue.analysis.*;
import pogvue.io.*;
import pogvue.gui.renderer.*;
import pogvue.analysis.SequenceFetchThread;
import pogvue.gui.event.*;
import java.beans.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;

public final class AlignViewport implements ActionListener, KeyListener {

  public static final Color stripeColor = Color.white;//new Color(240,240,240);
  private boolean     showLogoLabels = true;

  private JFrame     treeframe;
  private TreePanel  treepanel;
  private boolean    showtree = false;
  
  private JFrame       sframe;
  private ProgressPanel progressPanel;
  
  private int newres;
  private int newseq;
  
  private AlignmentPanel minipog = null;

  private int xpos;
  private int ypos;
  public Hashtable mathash;
  public Vector matrices;

  public boolean showGaps = true;
  public boolean shownGFFConfigError = false;

  public String tffile = "data/tf9.4.matrix.dat";
  public Sequence[] tffasta = null;

  public Vector hide;
  public Vector bamfiles = new Vector();

  private int startRes;
  private int endRes;
  public double charWidth;
  private int startSeq;
  private int endSeq;

  private boolean showCpG = false;

  private Hashtable names;
  private RendererI renderer = new ConsensusRenderer();

  private String  gapCharacter = "-";
  private int             charHeight;
  private int             chunkWidth;
  private int             chunkHeight;
  private Color           backgroundColour;
  private Font            font;
  private Font            idFont;
  private Alignment       alignment;

  private Selection       sel    = new Selection();

  private Rectangle       pixelBounds     = new Rectangle();
  public  String          gff_config_file = "data/gff.conf";
  private String          gffFileType     = "File";
  public  LinkedHashMap   gff_config;
  private boolean         GFFHeightByScore = true;

  private int threshold;
  private int increment;

  private pogvue.datamodel.tree.Tree  tree = null;
  private boolean human       = true;

  private int window   = 20;
  private int baseline = 70;

  private SequenceFetchThread sft;
  private int offset = 0;
  private Vector kmers;
  public boolean showSequence = false;
  private Controller controller;
  
    private int mousePos = -1;
    private int mouseRes = -1;
    private int mouseSeq = -1;

    private int lastres;
    private int lastseq;
    
    private RegionFetchThread rft;
    
    private Image[][] images;
    // These should be in another class that stores urls/data sourcs
  private static final String fasta_url = "http://www.broadinstitute.org/~mclamp/fetchmam_ungapped.php?";
  private static final String gff_url   = "http://www.broadinstitute.org/~mclamp/fetchmamgff.php?";
  private static final String grf_url   = "http://www.broadinstitute.org/~mclamp/fetchmamgraph.php?";
  private static final String blt_url   = "http://www.broadinstitute.org/~mclamp/fetch_mrna.php?";
  private static final String info_url   = "http://www.broadinstitute.org/~mclamp/fetch_geneinfo.php?";
  
  //private static final String fasta_url = "http://srv/~mclamp/fetchmam.php?";
  //private static final String gff_url   = "http://srv/~mclamp/fetchmamgff.php?";
  //private static final String grf_url   = "http://srv/~mclamp/fetchmamgraph.php?";
  //private static final String blt_url   = "http://srv/~mclamp/fetch_mrna.php?";
  //private static final String info_url   = "http://srv/~mclamp/fetch_geneinfo.php?";

   //private static final String fasta_url = "http://localhost:8080/~mclamp/fetchmam.php?";
   //private static final String gff_url   = "http://localhost:8080/~mclamp/fetchmamgff.php?";
   //private static final String grf_url   = "http://localhost:8080/~mclamp/fetchmamgraph.php?";
   //private static final String blt_url   = "http://localhost:8080/~mclamp/fetch_mrna.php?";
   //private static final String info_url   = "http://localhost:8080/~mclamp/fetch_geneinfo.php?";


  public void addBamFile(String file) {
      this.bamfiles.addElement(file);
  }
  public boolean useImage = true;

    public AlignViewport(Alignment da) {
    this(0,da.getWidth()-1,0,da.getHeight()-1);
    
    setAlignment(da);


  }

  public AlignViewport() {
    this(0,0,0,0);
  }
  public AlignViewport(int startRes, int endRes,
			int startSeq, int endSeq) {

    this.startRes = startRes;
    this.endRes   = endRes;
    this.startSeq = startSeq;
    this.endSeq   = endSeq;

    setFont(new Font("Helvetica",Font.PLAIN,12));
    setIdFont(new Font("Helvetica",Font.PLAIN,12));

    setCharHeight(20);
    setCharWidth(1,"AlignViewport");

    hide = new Vector();
    kmers = new Vector();
  }
  

  public void showLogoLabels(boolean flag) {
    this.showLogoLabels = flag;
  }
  public boolean showLogoLabels() {
    return showLogoLabels;
  }
   
  public void scanLogos() {

    alignment.scanLogos(getTransfacMatrices());
  }
    public void setImages(Image[][] img) {
	this.images = img;
    }
    public Image[][] getImages() {
	return images;
    }
  // I suspect these can be retired once the data is in the right place
  public int getNewRes() {
  	return newres;
  }
  public int getNewSeq() {
  	return newseq;
  }
  
  
  public void decreaseTrackHeight() {
  	if (getCharHeight() > 1) {
			setCharHeight(getCharHeight()-1);
  	}
  }
  public void expandRegion() {
    if (minipog != null) {
      System.out.println("Expanding region");
      int width = getEndRes() - getStartRes() + 1;
      
      // The region needs to be requested - to be quicker we should keep the middle piece and only fetch the end pieces but let's start simple.
      getAlignment().getChrRegion().expand(width/2);
      
      rft = new RegionFetchThread(getAlignment().getChrRegion());
      rft.setActionListener(this);    /// Hmm - in the alignment ?
      rft.start();
    }
  }
  public void moveLeft() {
    int width    = getEndRes()- getStartRes() +1;
    int newstart = getStartRes() - width/2;
    
    if (newstart < 0) {
      newstart = 0;
    }
    
    setStartRes(newstart);
    setEndRes(newstart+width);
  }
  public void moveToStart() {
    setStartRes(0);
  }
  
  public void moveToEnd() {
    int width = getEndRes() - getEndRes() +1;
    setStartRes(getAlignment().getWidth()-width);
  }
  public void moveRight() {
    int width    = getEndRes()- getStartRes() +1;
    int newstart = getStartRes() + width/2;
    
    if (newstart < 0) {
      newstart = 0;
    }
    
    setStartRes(newstart);
    setEndRes(newstart+width);
  }
  public void showTrackSelectionFrame() {
  	TrackSelectionFrame tsf = new TrackSelectionFrame(getAlignment().getSequences(),getGFFConfig(),this);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		tsf.setLocation(dim.width/5,
				dim.height/10);
		
		tsf.pack();
    tsf.setVisible(true);
  }
  public void increaseCharHeight() {
  	setCharHeight(getCharHeight()+1);
  }
  public void setMediumText() {
//	Set to char width of 7
    int size = getEndRes()-getStartRes()+1;
    int mid  = (getEndRes()+getStartRes())/2;

    setCharWidth((double)7.0,"reset");
    setCharHeight(10);
    setFont(new Font(getFont().getName(),getFont().getStyle(),10));
  }
  public void hideTrack(int seqnum) {
  	//Hide track under mouse
    
  	if (seqnum >=0 && seqnum <= getAlignment().getHeight()) {
  		Sequence seq = (Sequence)getAlignment().getSequenceAt(seqnum-1);

  		String type = seq.getName();

  		for (int i = 0; i < getAlignment().getHeight();i++) {
  			Sequence tmp = getAlignment().getSequenceAt(i);
  
  			if (tmp.getName().equals(type)) {
  				hideSequence(tmp);
  			}
  		}
  	}
  }
  public void showAllTracks() {
  	hiddenSequences().removeAllElements();
  	
    if (getVisibleSequence() == false) {
    	for (int i = 0; i < getAlignment().getHeight() ; i++) {
    		if (getAlignment().getSequenceAt(i).getLength() > 0) {
    			hideSequence(getAlignment().getSequenceAt(i));
    		}
    	}
    }
  }
  public void collapseAllTracks() {
    Vector tmp = GFFFile.collapseGFF(getAlignment().getSequences());

    getAlignment().setSequences(tmp);

  }
  public void zoomOut() {
  	int size = getEndRes()-getStartRes()+1;
    int mid  = (getEndRes()+getStartRes())/2;

    if (getCharWidth() >  1) {
    	setCharWidth(getCharWidth()-1,"pog");
    } else {
    	setCharWidth(getCharWidth()*0.9,"pog");
    }
  }
  
  
  public void zoomIn() {
  	int size   = getEndRes()-getStartRes()+1;
    int mid    = (getEndRes()+getStartRes())/2;
    int pixels = (int)(size/getCharWidth());
    int end    = getEndRes();

    if (getCharWidth() > 1) {
    	setCharWidth(getCharWidth()+1,"pog");
    } else {
    	setCharWidth(getCharWidth()*1.1,"pog");
    }
    int new_end = getStartRes() + (int)(getCharWidth()*pixels);

    if (getCharWidth() >= 3) {
    	setFont(new Font(getFont().getName(),getFont().getStyle(),(int)getCharWidth()+3));
    } else {
    	setFont(new Font(getFont().getName(),getFont().getStyle(),0));
    }
  }
  public void toggleHuman() {
    setHuman(!getHuman());
  }
  public void hideGappySequences() {
    int start = getStartRes();
    int end   = getEndRes();

    for (int i = 0; i < getAlignment().getHeight(); i++) {
    	Sequence seq = (Sequence)getAlignment().getSequenceAt(i);

    	if (seq.getSequence().length() > 0) {
    		int count = 0;
    		int j = start;
    		while (j <= end) {
    			if (seq.getSequence().substring(j,j+1).equals("-")) {	    

    				count++;
    			}
    			j++;
    		}
    		if (100*count/(end-start+1) > 30) {
    			hideSequence(seq);
    		} else {
    			showSequence(seq);
    		}
    	}
    }
  }
  public void toggleSequence() {
    if (getVisibleSequence() == true) {
      
      setVisibleSequence(false);
      
      for (int i = 0; i < getAlignment().getHeight(); i++) {
        Sequence s = (Sequence)getAlignment().getSequenceAt(i);
        
        if (getAlignment().getSequenceAt(i).getSequence().length() > 0) {
          hideSequence(getAlignment().getSequenceAt(i));
        }
      }
    } else {
      setVisibleSequence(true);

      if (charWidth > .1) {
	System.out.println("Region is " + getStartRes() + " " + getEndRes() + " " + charWidth);

	getSequence(getStartRes(),getEndRes());
  	    
	for (int i = 0; i < getAlignment().getHeight(); i++) {
	  Sequence s = (Sequence)getAlignment().getSequenceAt(i);
  	  
	  if (getAlignment().getSequenceAt(i).getSequence().length() > 0) {
	    showSequence(getAlignment().getSequenceAt(i));
	  }
	}
  	 
	System.out.println("Seq " + getAlignment().getSequenceByName("Logo"));
	if (getAlignment().getSequenceByName("Logo") == null) {
	  Sequence tmp = new Sequence("Logo","",1,1000);
  	      
	  Sequence tmp2 = new Sequence("","",1,1);

	  int pos   = 0;
	  int found = 0;

	  while (pos < getAlignment().getHeight()) {
	    Sequence s = getAlignment().getSequenceAt(pos);

	    System.out.println("name " + s.getName());

	    if (s.getName().indexOf("blat") >= 0) {
	      found = pos;
	      pos = getAlignment().getHeight();
	    }
	    pos++;
	  }
	  if (found == 0) {
	    getAlignment().addSequence(tmp2);
	    getAlignment().addSequence(tmp2);
	    getAlignment().addSequence(tmp2);
	    getAlignment().addSequence(tmp2);
	    getAlignment().addSequence(tmp2);
	    getAlignment().addSequence(tmp);
	  }else {
	    getAlignment().insertSequenceAt(tmp,found);
	    getAlignment().insertSequenceAt(tmp2,found);
	    getAlignment().insertSequenceAt(tmp2,found);
	    getAlignment().insertSequenceAt(tmp2,found);
	    getAlignment().insertSequenceAt(tmp2,found);
	    getAlignment().insertSequenceAt(tmp2,found);
	    
	  }
	}
      }
    }
  }
  public void setLargeText() {
  	setCharHeight(12);
    setCharWidth(12,"Text size");
    setFont(new Font(getFont().getName(),getFont().getStyle(),(int)getCharHeight()+3));
  }
  
  // So this is taking mouse input  - ViewableFeatureSet could be a mouseListener
  public void setPositions(int xpos, int ypos) {
  	this.xpos  = xpos;
  	this.ypos = ypos;
  	
  	newres = (int)(xpos/getCharWidth() + getStartRes());
    newseq = getIndex(ypos) -1;

  }
  public void setXPosition(int x) {
  	xpos = x;
  }
  public int getXPosition() {
  	return xpos;
  }
  public void setYPosition(int y) {
  	ypos = y;
  }
  public int getYPosition() {
  	return ypos;
  }
  public void setLastRes(int res) {
  	lastres = res;
  }
  public int getLastRes() {
  	return  lastres;
  }
  public void setLastSeq(int seq) {
  	lastseq = seq;
  }
  public int getLastSeq() {
  	return lastseq;
  }
  
  public void setGFFHeightByScore(boolean b) {
    this.GFFHeightByScore = b;
  }
  public boolean getGFFHeightByScore() {
    return GFFHeightByScore;
  }
  public void setHuman(boolean human) {
    this.human = human;
  }
  public boolean getHuman() {
    return human;
  }
    public void setMinipog(AlignmentPanel minipog) {
      this.minipog = minipog;
    }
    public AlignmentPanel getMinipog() {
	return minipog;
    }

  public void setVisibleSequence(boolean show) {
    this.showSequence = show;
  }
  public boolean getVisibleSequence() {    
    return showSequence;
  }
  public void showSequence(Sequence seq) {
    if (alignment != null) {
      if (hide.contains(seq)) {
	hide.removeElement(seq);
      }
    }
  }
  public void setMousePos(int pos) {
    this.mousePos = pos;
  }
  public int getMousePos() {
    return mousePos;
  }
  public void setMouseRes(int res) {
    this.mouseRes = res;
  }
  public int getMouseRes() {
    return mouseRes;
  }
  public void setMouseSeq(int seq) {
    this.mouseSeq = seq;
  }
  public int getMouseSeq() {
    return this.mouseSeq;
  }
  public boolean useImage() {
    return useImage;
  }
  
  public void useImage(boolean flag) {
    useImage = flag;
  }
  
  // So this is sequence stuff - keep here for now but should be moved
  public void getSequence(int x1, int x2) {
    Vector  tmpseq     = new Vector();
    
    if (sft != null) {
      return;
    }
    
    if (charWidth < .1 || x2 - x1 > 2000 || showSequence == false) {
      return;
    }
    if (getAlignment().getSequenceAt(0).getSequence(x1,x2).indexOf("X") >= 0 ||
        (getAlignment().countSequenceEntries() ==1 && getHuman() == false)) {

      sft = new SequenceFetchThread(controller,this,x1,x2);
      sft.setHuman(getHuman());
      sft.setActionListener(this);
      sft.start();
    }
  }
  
  // Not here
  public void search() {
    getAlignment().search(this);
  }
  
  public void setFont(Graphics g, Font f) {
  	
    if (g != null) {
      
    	FontMetrics fm = g.getFontMetrics(f);
      
    	int charWidth  = fm.charWidth('W');
    	int charHeight = fm.getHeight();
      
      setCharHeight(getCharHeight());
      
    } else {
    	setCharWidth(10,"SeqCanvas2");
    	setCharHeight(11);
    }
    
    setFont(f);
    
  }

  public void updateRegion(int width, int height){
  	int startres = getStartRes();
    int startseq = getStartSeq();
    
    int endres   = startres + (int)(width/getCharWidth()-1);
    int endseq   = startseq + height/getCharHeight();
    
    if (endres > getAlignment().getWidth()) {
      endres = getAlignment().getWidth();
      
      startres = endres - (int)(width/getCharWidth());
      endres   = getAlignment().getWidth();
      
      if (startres < 0) {
        startres = 0;
      }
    }
    
    if (endseq > getAlignment().getHeight()) {
      endseq = getAlignment().getHeight();
      startseq = endseq - (int)(width/getCharWidth());
      
      if (startseq < 0) {
        startseq = 0;
      }
      
    }
    
    setStartRes(startres);
    setStartSeq(startseq);
    setEndRes(endres);
    setEndSeq(endseq);
    
  }
  
  // Viewable FeatureSet
  public void hideType(String type) {
  	for (int i = 0;i < alignment.getHeight(); i++) {
  		Sequence seq = alignment.getSequenceAt(i);
  		
  		if (seq instanceof GFF) {
  			GFF gff = (GFF)seq;
  			
  			if (gff.getType() != null && gff.getType().equals(type)) {
  				hideSequence(seq);
  			}
  		}
    }
  }
  
  public void showType(String type) {
    for (int i = 0;i < alignment.getHeight(); i++) {
  	  Sequence seq = alignment.getSequenceAt(i);
  		
  		if (seq instanceof GFF) {
  		  GFF gff = (GFF)seq;
  			
  			if (gff.getType() != null && gff.getType().equals(type)) {
  				showSequence(seq);
  			}
      }
    }
  }

  public void hideSequence(Sequence seq) {
    if (alignment != null) {
      if (!hide.contains(seq)) {
	hide.addElement(seq);
      }
    }
  }
  public void setShowCpG(boolean cpg) {
    this.showCpG = cpg;
  }
  public boolean showCpG() {
     return showCpG;
  }
  public void showGaps(boolean show) {
    showGaps = show;
  }

  public boolean showGaps() {
    return showGaps;
  }
  public Vector hiddenSequences() {
    return hide;
  }
  public void hiddenSequences(Vector seq) {
    hide = seq;
  }
  public boolean isHiddenType(String type ) {
    for (int i = 0;i < hide.size(); i++) {
      Sequence seq = (Sequence)hide.elementAt(i);

      if (seq instanceof GFF) {
	GFF gff = (GFF)seq;

	if (gff.getType() != null && gff.getType().equals(type)) {
	  return true;
	}
      }
    }
    return false;
  }

  public int getStartRes() {
    return startRes;
  }

  public int getEndRes() {
    return endRes;
  }

  public int getStartSeq() {
    return startSeq;
  }

  public void setController(Controller c) {
    this.controller = c;
  }
  public Controller getController() {
    return controller;
  }
  public void setPixelBounds(Rectangle rect) {
    pixelBounds = rect;
  }

  private Rectangle getPixelBounds() {
    return pixelBounds;
  }

  public void setStartRes(int res) {
    this.startRes = res;
  }
  public void setStartSeq(int seq) {
    this.startSeq = seq;
  }
  public void setEndRes(int res) {

    // Alignment would have to change - getWidth
    if (res > alignment.getWidth()-1) {
      res = alignment.getWidth() -1;
    }
    if (res < 0) {
      res = 0;
    }
    this.endRes = res;
  }
  public void setEndSeq(int seq) {
    if (seq > alignment.getHeight()) {
      seq = alignment.getHeight();
    }
    if (seq < 0) {
      seq = 0;
    }
    this.endSeq = seq;
  }
  public int getEndSeq() {
    return endSeq;
  }
  private void setIdFont(Font f) {
    this.idFont = f;
  }
  public Font getIdFont() {
    return idFont;
  }
  public void setFont(Font f) {
      this.font = f;
  }
  public Font getFont() {
    return font;
  }
  public void setCharWidth(double w, String from) {
      //System.out.println("Width " + w);

    if (w < .1 && w > 0) {
      w = 1.0/(int)(1/w);
    }

    //System.out.println("Char width is " + w);
      // Only change if different
      if (w != charWidth) {
	  int startRes = getStartRes();
	  int endRes   = getEndRes();
	  
	  double charWidth = getCharWidth();
	  
	  int currentWidth = (int)((endRes-startRes+1)*charWidth);
	  int centreRes = (startRes + endRes)/2;
	  
	  //System.out.println("\nPrevious start end " + startRes + " " + endRes);
	  
	  int prevstart = startRes;

	  startRes = centreRes - (int)(currentWidth/(2*w));
	  endRes   = centreRes + (int)(currentWidth/(2*w));
	  
	  if (startRes < 0) {
	      startRes = 0;
	      endRes = startRes + (int)(currentWidth/w);
	  }

	  if (getCharWidth() < 1) {
	    int bpp = (int)(1.0/getCharWidth());
	    this.startRes = startRes - startRes%bpp;
	    this.endRes   = endRes   - startRes%bpp;
	  }
	  //this.startRes = prevstart + 1000;

	  this.charWidth = w;
      }
  }
  public double getCharWidth() {
    return charWidth;
  }
  public void setCharHeight(int h) {
    this.charHeight = h;
  }
  public int getCharHeight() {
    return charHeight;
  }
  public void setChunkWidth(int w) {
    this.chunkWidth = w;
  }
  public int getChunkWidth() {
    return chunkWidth;
  }
  public void setChunkHeight(int h) {
    this.chunkHeight = h;
  }
  public int getChunkHeight() {
    return chunkHeight;
  }
  public Alignment getAlignment() {
    return alignment;
  }
  public  void setAlignment(Alignment align) {
    this.alignment = align;

    if (align.getChrRegion() != null) {
      
      setOffset(align.getChrRegion().getStart());
      System.out.println("Offset is " + getOffset());
    }

    // Transfer the hidden sequence types to the new alignment
    Vector types = new Vector();

    for (int i = 0; i < hide.size(); i++) {
      Sequence seq = (Sequence)hide.elementAt(i);

      if (seq instanceof GFF) {
	types.addElement((String)((GFF)seq).getType());
      }

    }

    hide.removeAllElements();

    for (int i = 0; i < align.getHeight(); i++) {
      Sequence seq = align.getSequenceAt(i);

      if (seq instanceof GFF) {
	GFF gff = (GFF)seq;
	if (types.contains(gff.getType())) {
	  hide.addElement(seq);
	}

      }

      if (seq.getSequence().length() > 0 &&
	  showSequence == false) {
	hide.addElement(seq);
      }
    }
    
    // Estimate the rough basesperpixel

    int size = alignment.getWidth();

    int bpp = (int)(alignment.getWidth()/700);

    startRes = 0;

    charWidth = 1.0/bpp;

  }
  public void setOffset(int offset) {
    this.offset = offset;
    if (minipog != null) {
      minipog.getAlignViewport().setOffset(offset);
    }
  }
  public int getOffset() {
    return this.offset;
  }

  public void setThreshold(int thresh) {
    threshold = thresh;
  }
  public int getThreshold() {
    return threshold;
  }
  public void setIncrement(int inc) {
    increment = inc;
  }
  public int getIncrement() {
    return increment;
  }
  
  //  Hmm - ViewableFeatureSet
  public int getIndex(int y) {
    int y1     = 0;
    
    int starty = getStartSeq();
    int endy   = getEndSeq();
    
    int i     = 0;
    int count = 0;
    
    while (i <= alignment.getHeight()) {
      
      if (!hide.contains(alignment.getSequenceAt(i))) {
        count++;
        
        if (count >= starty &&
            count <= endy) {
          
          int y2 = y1+getCharHeight();
          
          if (y >= y1 && y <= y2) {
            return i+1;
          }
          
          y1  = y2;
        }
      }
      i++;
    }
    return -1;
  }
  
  
  public Selection getSelection() {
    return sel;
  }
  public void resetSeqLimits() {
    setStartSeq(0);
    setEndSeq(getPixelBounds().height/getCharHeight()); 
  }

  public void keyPressed(KeyEvent e) {}
  public void keyReleased(KeyEvent e) {}
  public void keyTyped(KeyEvent e) {}

    
  public void setCurrentTree(pogvue.datamodel.tree.Tree tree) {
      this.tree = tree;
  }

  public pogvue.datamodel.tree.Tree getCurrentTree() {

    if (tree == null) {
      try {
	//TreeFile tf = new TreeFile("http://www.broad.mit.edu/~mclamp/pogvue/hiram.nh","URL");
	//TreeFile2 tf = new TreeFile2("data/ratites.nh","File");
	TreeFile2 tf = new TreeFile2("data/galGal4.test.nh","File");
	tree = tf.getTree();
      } catch (IOException e) {
	e.printStackTrace();
      }
    }
    return tree;
  }

  public void toggleTree() {
    showtree = !showtree;
    showTree(mousePos+1);
  }
  public void findMutations() {
    if (tree != null) {
      tree.findMutations(getStartRes(),getEndRes());

      treepanel.treeCanvas.repaint();
    }
  }
  public void showTree(int site) {
    
    if (showtree) {
      
      tree      = getCurrentTree();
      tree.setSequences(getAlignment().getSequenceArray());
      tree.setSite(site);
      tree.calcLikelihood();
      
      if (treeframe == null) {
	treeframe = new JFrame();
	treepanel = new TreePanel(null,tree);
	
	tree.setSequences(getAlignment().getSequenceArray());
	
	treeframe.setLayout(new BorderLayout());
	treeframe.add("Center",treepanel);
	treeframe.setSize(500,500);
	
	Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	
	treeframe.setLocation(dim.width/5,
			      dim.height/10);
	
	treeframe.setVisible(true);
      }
      
      treepanel.treeCanvas.repaint();
    }
  }

  public void setRenderer(RendererI rend) {
    this.renderer = rend;
  }
  
  public RendererI getRenderer() {
    return renderer;
  }
    public LinkedHashMap getGFFConfig() {
      return readGFFConfig();
    }
    public void setGFFConfig(LinkedHashMap hash) {
	this.gff_config = hash;
    }
    public void setGFFFileType(String type) {
	gffFileType = type;
    }
    public String getGFFFileType() {
	return gffFileType;
    }
    public void setGFFConfigFile(String file) {
	gff_config_file = file;
    }
    public String getGFFConfigFile() {
	return gff_config_file;
    }
    public static String getFastaURL() {
	return fasta_url;
    }
    public static String getGFFURL() {
	return gff_url;
    }
    public static String getGRFURL() {
	return grf_url;
    }
    public static String getBLTURL() {
	return blt_url;
    }
    public static String getInfoURL() {
	return info_url;
    }

  public void writeGFFConfig() {
    //  try {
      //PrintWriter pw  = new PrintWriter(new FileWriter(gff_config_file));
      PrintWriter pw = new PrintWriter(System.out,true);
      Iterator    str = gff_config.keySet().iterator();

      while (str.hasNext()) {
	String name = (String)str.next();
	Color  val  =   (Color)gff_config.get(name);
	
	pw.println(name + " " + val.getRed() + "," + val.getGreen() + "," + val.getBlue());
      }

      pw.flush();
      pw.close();
      
      // } catch (IOException e) {
      //if (shownGFFConfigError == false) {
      //System.out.println("Can't write to config file " + gff_config_file);
      //}
      //shownGFFConfigError = true;
      //}
  }

  public LinkedHashMap readGFFConfig() {
    if (gff_config == null) {
      gff_config =  readGFFConfig(gff_config_file,gffFileType);
    }

    return gff_config;
  }

  public static LinkedHashMap readGFFConfig(String file,String type) {
    
    String line = "";

    LinkedHashMap out = new LinkedHashMap();      
      
    try {
      FileParse fp = new FileParse(file,type);
      
      while ((line = fp.nextLine()) != null) {
        StringTokenizer str = new StringTokenizer(line);
      
	  if (str.countTokens() == 2) {
      	    String name = str.nextToken();
      	    String val  = str.nextToken();
      	    StringTokenizer str2 = new StringTokenizer(val,",");
      	    int red   = Integer.parseInt(str2.nextToken());
      	    int green = Integer.parseInt(str2.nextToken());
      	    int blue  = Integer.parseInt(str2.nextToken());
      
      	    Color c = new Color(red,green,blue);
      
      	    out.put(name,c);
      
      	  } else {
	    System.out.println("Wrong format for gff config file " + line);
	  }
	}
      
      } catch (IOException e) {
      	System.out.println("Exception reading gff config file " + e);
      } catch (NumberFormatException e) {
      	System.out.println("Exception reading gff config line " + line + e);
      }
      
    
    
    return out;
  }
  public void setFullSpan(int pixelwidth) {
    setCharWidth(pixelwidth/getAlignment().getWidth(),"Setting to full span");
  }
  
  // this shouldn't be here - or in ViewableFeatureSet
  public Vector getTransfacMatrices(String file) {
    if (matrices == null) {
      mathash = new Hashtable();
      try {
        
        String type = "File";
        
        if (file.indexOf("http:") == 0) {
          type = "URL";
        }
       
	if (file.indexOf(".pwm") > 0) {
	  PwmLineFile pwm = new PwmLineFile(file,"File");
	  pwm.parse();
	  matrices = pwm.getTFMatrices();
	  System.out.println("MAtrix");
	} else {
	  OrmatFile tffile = new OrmatFile(file,type);
	  
	  matrices = tffile.getMatrices();
	}
	for (int i = 0;i < matrices.size(); i++) {
	  TFMatrix mat = (TFMatrix)matrices.elementAt(i);
	  mathash.put(mat.getName(),mat);
	  System.out.println("Matrix " + mat.getName());
	}

      } catch (IOException e) {
        System.out.println("Can't read TFMatrix File");
      }
      
    }
    return matrices;
  }
  
  public int getPixelHeight(int i, int j,int charHeight) {
    
    int h=0;
    
    while (i < j) {
      h += charHeight;
      i++;
    }
    return h;
  }


  // More transfac stuff
  public Vector getTransfacMatrices() {
    
    if (matrices == null) {
	//getTransfacMatrices("http://www.broadinstitute.org/~mclamp/pogvue/clus.pwm");
	
	getTransfacMatrices("data/clus.pwm");
    }
    
    return matrices;
  }
  
  // More transfac stuff
  public TFMatrix getMatrix(String name) {
    
    if (matrices == null) {
      getTransfacMatrices();
    }
    
    if (name != null && mathash != null && mathash.containsKey(name)) {
      return (TFMatrix)mathash.get(name);
    }
    return null;
    
  }

 
 
  // This is sequence fetching - not here but in FeatureSet probably
  public void actionPerformed(ActionEvent e) {
    
    if (e.getSource() == sft) {
      if (sft.isDone()) {
	controller.handleAlignViewportEvent(new AlignViewportEvent(this,this,AlignViewportEvent.RESHAPE));
	sft = null;
      }
    }
    if (e.getSource() == rft) {
      
      Alignment      al = rft.getOutput();
      ChrRegion sr = al.getChrRegion();
      setAlignment(al);
      minipog.getAlignViewport().setAlignment(al);
      controller.handleAlignViewportEvent(new AlignViewportEvent(this,this,AlignViewportEvent.RESHAPE));
      rft = null;
    }
  }
}
