package pogvue.gui;

import java.util.*;
import java.io.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;

import javax.swing.border.*;
import javax.swing.*;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import pogvue.datamodel.*;
import pogvue.datamodel.motif.*;
import pogvue.gui.event.*;
import pogvue.gui.hub.*;
import pogvue.analysis.*;
import pogvue.io.*;
import pogvue.util.*;

public final class AlignmentPanel extends ControlledPanel implements AlignViewportListener,
								     AdjustmentListener,
								     FontChangeListener,
								     MouseListener,
								     MouseMotionListener,
								     Printable {
  public IdCanvas         idPanel;
  public SeqCanvas        seqCanvas;
  public GlassCanvas      glassCanvas;
  public LayeredPanel     lp;
  public ScalePanel       scalePanel;
  public JScrollBar       hscroll;
  public JScrollBar       vscroll;
  public JSplitPane     jsp;
  public Alignment               align;
  
  private final AlignViewport av;
  
  int offx;
  int offy;
  
  int oldoffx;
  int oldoffy;
  
  int maxoffx;
  int maxoffy;
  
  public AlignmentPanel(Alignment al) {
    AlignViewport av = new AlignViewport(al);
    Controller    c  = new Controller();
    
    this.av = av;

    setController(c);
    
    this.align = al;
    
    componentInit();
  }
  
  public AlignmentPanel(AlignViewport av, Controller c) {
    this.av         = av;
    
    setController(c);
    
    align = (Alignment) av.getAlignment();
    
    componentInit();
    
  }
  
  public AlignViewport getAlignViewport() {
    return av;
  }
  
  public int print(Graphics g,PageFormat pf, int pi) throws PrinterException {
    if (pi >=1) {
      return Printable.NO_SUCH_PAGE;
    }
    paint(g);
    return Printable.PAGE_EXISTS;
  }
  
  private void componentInit() {
    
    idPanel    = new IdCanvas  (av,controller);
    seqCanvas  = new SeqCanvas (av,controller);
    //lp = new LayeredPanel(av,controller);
    //seqCanvas   = lp.getSeqCanvas();
    //glassCanvas = lp.getGlassCanvas();

    scalePanel = new ScalePanel(av,controller);
    
    hscroll = new JScrollBar(Scrollbar.HORIZONTAL);
    vscroll = new JScrollBar(Scrollbar.VERTICAL);
    
    hscroll.addAdjustmentListener(this);
    vscroll.addAdjustmentListener(this);
    
    seqCanvas.addMouseMotionListener(this);
    seqCanvas.addMouseListener(this);

    controller.addListener(seqCanvas);
    //controller.addListener(lp.getGlassCanvas());
    controller.addListener(this);
    
    // The window is split into two panels left and right
    
    JPanel leftPanel  = new JPanel();
    JPanel rightPanel = new JPanel();
    
    FormLayout leftLayout = new FormLayout("pref",
				   "30px, pref:grow, pref");
    
    FormLayout rightLayout = new FormLayout("pref:grow, pref",
					    "30px, pref:grow, pref");
    
    leftPanel.setLayout(leftLayout);
    rightPanel.setLayout(rightLayout);
    
    CellConstraints cc = new CellConstraints();
    
    leftPanel.add(idPanel,          cc.xy(1,2, "fill, fill"));

    rightPanel.add(scalePanel,      cc.xy(1,1, "fill, default"));
    rightPanel.add(seqCanvas,              cc.xy(1,2, "fill, fill"));
    //rightPanel.add(lp,              cc.xy(1,2, "fill, fill"));
    rightPanel.add(hscroll,         cc.xy(1,3, "fill, default"));
    rightPanel.add(vscroll,         cc.xy(2,2, "default, fill"));
    
    jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,leftPanel,rightPanel);
        
    jsp.setOneTouchExpandable(true);
    jsp.setDividerLocation(idPanel.getPreferredSize().width);
    
    setLayout(new BorderLayout());
    add("Center",jsp);
    setScrollValues(0,0);
  }
  
    public void setDividerLocation(double pos) {
	jsp.setDividerLocation(pos);
    }
  public boolean handleAlignViewportEvent(AlignViewportEvent e) {
	
    if (e.getType() != AlignViewportEvent.LIMITS) {
      setScrollValues(av.getStartRes(),av.getStartSeq());
      WindowUtil.invalidateComponents(this);
      repaint();
      //validate();
      //validateTree();
      revalidate();
    }
    revalidate();
    return true;
  }

  public boolean handleFontChangeEvent(FontChangeEvent e) {
    return true;
  }

  private void setScrollValues(int offx, int offy) {
	
    int width;
    int height;
    
    //Brings up error in netscape
    if (seqCanvas.getSize().width > 0) {
      width  = seqCanvas.getSize().width;
      height = seqCanvas.getSize().height;
    } else {
      width  = seqCanvas.getPreferredSize().width;
      height = seqCanvas.getPreferredSize().height;
    }
    
    //Make sure the maxima are right
    if (maxoffx != (align.getWidth())) {
      maxoffx = (align.getWidth());
    }
    if (maxoffy != (align.getHeight())) {
      maxoffy = (align.getHeight());
    }
    
    hscroll.setValues(offx, (int)(width/av.getCharWidth()),0,maxoffx);
    vscroll.setValues(offy,height/av.getCharHeight(),0,maxoffy);
    
    int hpageinc = (int)(0.3 * (av.getEndRes() - av.getStartRes()));
    int vpageinc = (int)(0.3 * (av.getEndSeq() - av.getStartSeq()));
    
    hscroll.setUnitIncrement(hpageinc/10 + 1);
    vscroll.setUnitIncrement(1);
    
    if (hpageinc < 1) {
      hpageinc = 1;
    }
    if (vpageinc < 1) {
      vpageinc = 1;
    }
    
    hscroll.setBlockIncrement(hpageinc);
    vscroll.setBlockIncrement(vpageinc);
  }
  
  public void adjustmentValueChanged(AdjustmentEvent evt) {
    // This gets called when the scrollbars are changed - 
    
    if (evt.getSource() == hscroll) {
      
      int offx = hscroll.getValue();
      
      // This should be n*bases per pixel

      int bpp = (int)(1.0/av.getCharWidth());

      if (bpp > 0) {
	offx = offx - offx%bpp;
      }
      //hscroll.setValue(offx);
      if (offx != oldoffx) {
	av.setStartRes(offx);
	av.setEndRes((int)(offx + (seqCanvas.size().width/av.getCharWidth())));
	
	controller.handleAlignViewportEvent(new AlignViewportEvent(this,av,AlignViewportEvent.HSCROLL));
	
	hscroll.revalidate();
      }
      oldoffx = offx;
    }
    
    if (evt.getSource() == vscroll) {
      int offy = vscroll.getValue();
      if (oldoffy != offy) {
	av.setStartSeq(offy);
	av.setEndSeq(offy + seqCanvas.getSize().height/av.getCharHeight());
	controller.handleAlignViewportEvent(new AlignViewportEvent(this,av,AlignViewportEvent.LIMITS));
      }
      oldoffy = offy;
    }
  }
    
  public void mouseEntered(MouseEvent evt) { }
  public void mouseExited (MouseEvent evt) { }
  public void mouseClicked(MouseEvent evt) { }
  public void mouseMoved  (MouseEvent evt) { 
  }
  public void mousePressed(MouseEvent evt) {
  }
  public void mouseDragged(MouseEvent evt) { }
  
  public void mouseReleased(MouseEvent evt) {
    int x = evt.getX();
    int y = evt.getY();
    
    int res = (int)(x/av.getCharWidth() + av.getStartRes());
    int seq = av.getIndex(y);
    
    //char resstr = align.getSequenceAt(seq).getSequence().charAt(res);

    
    int pos = align.getSequenceAt(0).findPosition(res);
    

    // Minipogness - we have the centre so we need to calculate the start and ends
    if (av.getMinipog() != null && SwingUtilities.isRightMouseButton(evt)) {
      AlignViewport av2 = av.getMinipog().getAlignViewport();
      
      int    width     = av2.getEndRes() - av2.getStartRes() + 1;
      double charwidth = av2.getCharWidth();
      int    startres  = res - width/2;
      int    endres    = startres + width;
      
      av2.setStartRes(startres);
      av2.setEndRes(endres);
      av.getMinipog().getController().handleAlignViewportEvent(new AlignViewportEvent(this,av2,AlignViewportEvent.RESHAPE));

    }


  }
    
  public void reshape(int x, int y, int width, int height) {
    super.reshape(x,y,width,height);
    setScrollValues(av.getStartRes(),av.getStartSeq());
  }
  
  
  public Dimension getPreferredSize() {
    return new Dimension(20,200);
  }
  
  public static void main(String[] args) {
    
    Alignment  al  = null;
    GFFFile    gff = null;
    GraphFile  grf = null;
    BlatFile   blt = null;
    PwmLineFile pf = null;
    Sequence[] s   = null;
    
    // Parse the command line options
    //
    // -seqfile <seqfile> -gfffile <gfffile> -graphfile <graphfile> -blatfile <blatfile>
    // -chr <chr> -chrstart <start> -chrend <end>
    
    Hashtable opts = GetOptions.get(args);
    
    String seqfile   = null;
    String gfffile   = null;
    String grffile   = null;
    String bltfile   = null;
    String pwmfile   = null;
    String showseq   = null;
    String chr       = null;
    int    chrstart  = -1;
    int    chrend    = -1;
    boolean gapped   = false;
    boolean conflate = false;
    int    charheight    = 10;


    if (opts.containsKey("-seqfile")) {
      seqfile  = (String)opts.get("-seqfile");
    }
    if (opts.containsKey("-gfffile")) {
      gfffile  = (String)opts.get("-gfffile");
    }        
    if (opts.containsKey("-graphfile")) {
      grffile  = (String)opts.get("-graphfile");
    }        
    if (opts.containsKey("-blatfile")) {
      bltfile  = (String)opts.get("-blatfile");
    }        
    if (opts.containsKey("-pwmfile")) {
	pwmfile  = (String)opts.get("-pwmfile");
    }        
    if (opts.containsKey("-chr")) {
      chr = (String)opts.get("-chr");
    }
    if (opts.containsKey("-start")) {
      chrstart = Integer.parseInt((String)opts.get("-start"));
    }
    if (opts.containsKey("-end")) {
      chrend = Integer.parseInt((String)opts.get("-end"));
    }

    if (opts.containsKey("-charheight")) {
      charheight = Integer.parseInt((String)opts.get("-charheight"));
    }

    if (opts.containsKey("-gapped")) {
      gapped = true;
    }
    if (opts.containsKey("-conflate")) {
      conflate = true;
    }

    int width  = 1500;
    int height = 1000;

    Dimension       dim = Toolkit.getDefaultToolkit().getScreenSize();

    
    if (chr != null && chrstart != -1 && chrend != -1) {
      
      al  = GenomeInfoFactory.requestRegion(chr,chrstart,chrend,new Vector(),null);
      
      double          cw  = (width-150)*1.0/al.getWidth();      
      //AlignSplitPanel asp = GenomeInfoFactory.makePanel(al,"Pogvue",cw,1,null,chrstart,chrstart,chrend,width-150);	    

      AlignmentPanel ap = new AlignmentPanel((Alignment)al);
      
      JFrame          jf  = new JFrame(chr + ":" + chrstart + "-" + chrend);
      
      jf.getContentPane().add(ap);
      
      jf.setLocation(dim.width  / 2 - width / 2,
		     dim.height / 2 - height / 2);

      jf.setSize(width,height);
      jf.setVisible(true);
      
    } else if (seqfile != null) {

      try {
	AlignFile ff = null;
	
	if (gapped == true) {
	  ff = new GappedFastaFile(seqfile,"File");
	} else {
	  ff = new FastaFile(seqfile,"File");
	}
	if (ff != null) {

	  s = ff.getSeqsAsArray();
	  
	  if (conflate) {
	    Sequence[] news = new Sequence[s.length];
	    String[] mammarr = EMFFile.mammarr;

	    Vector confseq = new Vector();

	    int i =0;
	    int count = 0;
	    while (i < mammarr.length) {
	      int j = 0;
	      while (j < s.length) {
		if (s[j].getName().equals(mammarr[i])) {
		  confseq.addElement(s[j]);
		}
		j++;
	      }
	      i++;
	    }

	    i = 0;
	    int paircount = 0;

	    Vector tmpseq = new Vector();

	    while (i < confseq.size()-1) {
	      Sequence s1 = (Sequence)confseq.elementAt(i);
	      Sequence s2 = (Sequence)confseq.elementAt(i+1);
	      if (s1.getName().equals(s2.getName())) {
		tmpseq.addElement(s1);
		tmpseq.addElement(s2);
		i++;
	      }
	      i++;
	    }
	    s = (Sequence[])tmpseq.toArray(new Sequence[tmpseq.size()]);
	    System.out.println("Paircount " + paircount);
	  }
	  System.out.println("S " + s.length);
	  al = new Alignment(s);

	  

	  if (pwmfile != null) {
	      pf = new PwmLineFile(pwmfile,"File");
	      pf.parse();

	      Vector pwms = pf.getTFMatrices();

	      for (int i = 0;i < pwms.size(); i++) {
		  TFMatrix tfm = (TFMatrix)pwms.elementAt(i);
		  System.out.println("TFM " + tfm.getPwm().getPwm());;
		  GFF tmpf = new GFF("PWM","", 1,tfm.getPwm().getPwm().length/4);

		  SequenceFeature sf = new SequenceFeature(null, tfm.getName(),1,tfm.getPwm().getPwm().length/4,"PWM");
		  sf.setId(tfm.getName());
		  sf.setTFMatrix(tfm);
		  tmpf.addFeature(sf);
		  al.addSequence(tmpf);
	      }
	  }

	  if (gfffile != null) {
	    LinkedHashMap typeorder = AlignViewport.readGFFConfig("http://www.broad.mit.edu/~mclamp/pogvue/gff.conf", "URL");


	    gff = new GFFFile(gfffile,"File");
	    
	    Vector tmpgenefeat = GFFFile.extractFeatures(gff.getFeatures(), "gene");
	    Vector tmpfeat = gff.getFeatures();
	    
	    tmpgenefeat = GFFFile.groupFeatures(tmpgenefeat, true);

      
	    Vector geneFeat = SequenceFeature.hashFeatures(tmpgenefeat, 0, typeorder, true);
	    Vector gffFeat  = SequenceFeature.hashFeatures(tmpfeat, 0, typeorder,false);
	    al.addSequences(geneFeat);
	    al.addSequences(gffFeat);
	  }

	  double          cw  = (width-150)*1.0/al.getWidth();      
	  AlignSplitPanel asp = GenomeInfoFactory.makePanel(al,"Pogvue",cw,1,chrstart,chrstart,chrend,width-150);	    

	  asp.getAlignmentPanel2().getAlignViewport().setCharHeight(charheight);
	  JFrame          jf  = new JFrame(chr + ":" + chrstart + "-" + chrend);
	  
	  jf.getContentPane().add(asp);
	  
	  jf.setLocation(dim.width  / 2 - width / 2,
			 dim.height / 2 - height / 2);
	  
	  jf.setSize(width,height);
	  jf.setVisible(true);
	} else {
	  
	  System.out.println("Can't read seqfile [" + seqfile +"]");
	}
      } catch (IOException e) {
	e.printStackTrace();
      }
    }
  }
}


