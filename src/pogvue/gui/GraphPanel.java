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

public final class GraphPanel extends ControlledPanel implements AlignViewportListener,
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
  
  public GraphPanel(Alignment al) {
    AlignViewport av = new AlignViewport(al);
    Controller    c  = new Controller();
    
    this.av = av;

    setController(c);
    
    this.align = al;
    
    componentInit();
  }
  
  public GraphPanel(AlignViewport av, Controller c) {
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

    Hashtable opts = GetOptions.get(args);
    
    String gfffile    = null;
    String grffile    = null;
    String chr        = null;
    int    chrstart   = -1;
    int    chrend     = -1;
    int    charheight = 10;


    if (opts.containsKey("-gfffile")) {
      gfffile  = (String)opts.get("-gfffile");
    }        
    if (opts.containsKey("-graphfile")) {
      grffile  = (String)opts.get("-graphfile");
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

    int width  = 1000;
    int height = 800;

    Dimension       dim = Toolkit.getDefaultToolkit().getScreenSize();

    System.out.println("GFFFile " + gfffile);

    try {
      if (gfffile != null) {
	LinkedHashMap typeorder = AlignViewport.readGFFConfig("data/gff.conf", "File");
	LinkedHashMap neworder  = new LinkedHashMap();

	Vector allfeat = new Vector();

	StringTokenizer str = new StringTokenizer(gfffile,",");

	while (str.hasMoreTokens()) {
	  String file = str.nextToken();

	  System.out.println("File " + file);
	  gff = new GFFFile(file,"File");
	
	  Vector gffFeat = gff.getFeatures();

	  for (int i = 0;i < gffFeat.size();i++) {
	    SequenceFeature sf = (SequenceFeature)gffFeat.elementAt(i);
	    String type = sf.getType();
	    
	    if (type.indexOf("::") > 0) {
	      type = type.substring(0,type.indexOf("::"));
	    }
	    sf.setType(type);

	    Color c = (Color)(typeorder.get(type));
	  
	    if (neworder.get(type) == null) {
	      neworder.put(type,c);
	      
	      System.out.println("Putting " + type);
	    }
	  }

	  gffFeat  = SequenceFeature.hashFeatures(gffFeat, 0, neworder,false);//typeorder,false);

	  for (int i = 0;i < gffFeat.size();i++) {
	    GFF sf = (GFF)gffFeat.elementAt(i);
	    String type = sf.getType();
	    System.out.println("Type " + type);
	  }
	  System.out.println("Features " + gffFeat.size());

	  for (int i = 0; i < gffFeat.size();i++) {
	    allfeat.addElement(gffFeat.elementAt(i));
	  }
	}
	al = Alignment.getDummyAlignment("Top","1",1,35000);
	al.addSequences(allfeat);
	System.out.println("Width " + al.getWidth());
	
	double          cw  = (width-150)*1.0/al.getWidth();      
	AlignSplitPanel asp = GenomeInfoFactory.makePanel(al,"Pogvue",cw,cw*10,1,15000,35000,width-150);	    
	
	charheight = 45;
	
	asp.getAlignmentPanel2().getAlignViewport().setCharHeight(charheight);
	asp.getAlignmentPanel1().getAlignViewport().setCharHeight(35);
	
	asp.getAlignmentPanel2().getAlignViewport().gff_config = neworder;
	asp.getAlignmentPanel1().getAlignViewport().gff_config = neworder;
	JFrame          jf  = new JFrame(gfffile);
	
	jf.getContentPane().add(asp);
	
	jf.setLocation(dim.width  / 2 - width / 2,
		       dim.height / 2 - height / 2);
	
	jf.setSize(width,height);
	jf.setVisible(true);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    
  }
}


