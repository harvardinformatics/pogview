package pogvue.gui;

import pogvue.datamodel.FeaturePair;
import pogvue.datamodel.Sequence;
import pogvue.datamodel.SequenceFeature;
import pogvue.datamodel.Sequence;
import pogvue.gui.schemes.ResidueProperties;
import pogvue.io.FastaFile;
import pogvue.io.GFFFile;
//import pogvue.io.MSPFile2;
import pogvue.util.QuickSort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public final class GeneDisplay2 extends JPanel implements WindowListener, MouseListener,MouseMotionListener {

  private final Vector    exons;
  private Hashtable pixelExons;
  private Sequence seq;
  private String    cdnaseq;
  private Vector    hits;
  private Hashtable hithash;
  private Hashtable hitlevel;
  private Hashtable exonhithash;
  private Hashtable exonhash;
  private Hashtable levelhit;
  private Hashtable msphash;

  private String chr;
  private int chrstart;
  private int chrend;

  private double bpp    = -1;
  private double newbpp = -1;

  private int prevx;
  private int prevy;

  private int width;
  private int height;
  private final int char_height = 10;
  private final int hit_height  = 4;
  private final int xoffset     = 50;
  private int cdnaoffset;
  private final int gap         = 20;

  private int ylevel;
  private int cdnalevel = -1;

  private String selectedHit = null;

  private final Color c1 = Color.pink;
  private final Color c2 = Color.lightGray;
  
  private final Color b1 = new Color(50,50,242);
  private final Color b2 = new Color(150,150,242);

  private final Color d1 = new Color(242,50,42);
  private final Color d2 = new Color(242,150,142);

  private GeneDisplay2(Vector exons) {

    int[]    arr = new int   [exons.size()];
    Object[] s   = new Object[exons.size()];

    for (int i=0; i < exons.size(); i++) {
      arr[i] = ((SequenceFeature)exons.elementAt(i)).getStart();
      s[i]   = exons.elementAt(i);
    }

    QuickSort.sort(arr,s);

    Vector newex = new Vector();

    SequenceFeature sf = (SequenceFeature)exons.elementAt(0);

    if (sf.getStrand() == 1) {
			      
      for (int i=0; i < exons.size(); i++) {
	SequenceFeature ex = (SequenceFeature)s[i];

	ex.setId(ex.getId() + "." + ex.getStart() + "-" + ex.getEnd());
	newex.addElement(ex);
      }
    } else {
      for (int i=exons.size()-1; i >=0; i--) {
	SequenceFeature ex = (SequenceFeature)s[i];

	ex.setId(ex.getId() + "." + ex.getStart() + "-" + ex.getEnd());
	newex.addElement(ex);

      }

    }
    this.exons = newex;


    addMouseListener(this);
    addMouseMotionListener(this);
  }

  private void setMspSeqs(Hashtable hash) {
    this.msphash = hash;
  }
  private void setChr(String chr) {
    this.chr = chr;
  }

  private void setChrStart(int chrstart) {
    this.chrstart = chrstart;
  }

  private void setChrEnd(int chrend) {
    this.chrend = chrend;
  }

  private void setGeneSequence(Sequence seq,int offset) {
     this.seq = seq;

     cdnaseq = "";

     int coord = 1;

     for (int i = 0; i < exons.size(); i++) {
       SequenceFeature sf = (SequenceFeature)exons.elementAt(i);
       //System.out.println("Coord\t" + (sf.getStart()-chrstart));

       String tmpseq = seq.getSequence().substring(sf.getStart()-chrstart,sf.getEnd()-chrstart+1);

       //System.out.println("Seq\t" + tmpseq);

       if (sf.getStrand() == -1) {
	 tmpseq = ResidueProperties.reverseComplement(tmpseq);
       }
       cdnaseq += tmpseq;

       //System.out.println("Coords   \t" + coord + "\t" + (coord+(sf.getEnd()-sf.getStart()+1)));

       coord += (sf.getEnd()-sf.getStart());

     }

     //System.out.println("Cdnaseq " + cdnaseq.length() + " " + cdnaseq);
  }

  private void setHits(Vector hits) {
     this.hits = hits;

     hithash  = new Hashtable();
     hitlevel = new Hashtable();
     levelhit = new Hashtable();

     for (int i = 0; i < hits.size(); i++) {
       FeaturePair fp = (FeaturePair)hits.elementAt(i);

       String hitname = fp.getHitId();

       if (hithash.get(hitname) == null) {
	 Vector h = new Vector();

	 hithash.put(hitname,h);

	 h.addElement(fp);
       } else {
	 Vector h = (Vector)hithash.get(hitname);

	 h.addElement(fp);
       }
     }

     Enumeration en = hithash.keys();
     int level = 1;

     while (en.hasMoreElements()) {
       String hitname = (String)en.nextElement();
       hitlevel.put(hitname, level);
       levelhit.put(level,hitname);
       level++;
     }


     // Now break up the hits into pieces.

     convertHits();
     
  }

  private void convertHits() {

    exonhithash = new Hashtable();
    exonhash    = new Hashtable();

    Enumeration en          = hithash.keys();

    while (en.hasMoreElements()) {
      String      name = (String)en.nextElement();
      Vector      hits = (Vector)hithash.get(name);
      Vector      newhits = new Vector();

      exonhithash.put(name,newhits);

      for (int i = 0; i < hits.size(); i++) {

	FeaturePair fp = (FeaturePair)hits.elementAt(i);
	
	boolean found_hit_start = false;
	boolean found_hit_end   = false;
	
	int exon_start = 1;
	int exon_end;
	
	int hit_start = 1;
	int hit_end;
	
	for (int j = 0; j < exons.size(); j++) {
	  
	  SequenceFeature sf = (SequenceFeature)exons.elementAt(j);
	  
	  exon_end = exon_start + sf.getLength() - 1;
	  
	  if (found_hit_start && !found_hit_end) {
	    
	    //  |********----|
	    
	    if (fp.getEnd() < exon_end) {
	      
	      FeaturePair fp1 = new FeaturePair();
	      
	      fp1.setStart(exon_start);
	      fp1.setEnd  (fp.getEnd());
	      fp1.setAlignString(cdnaseq.substring(exon_start-1,fp.getEnd()));

	      if (fp.getHstrand() == 1) {
		fp1.setHstart(hit_start);
		fp1.setHend  (hit_start + fp.getEnd() - exon_start);
		fp1.setHitAlignString(fp.getAlignString().substring(hit_start-fp.getHstart()));
	      } else {
		fp1.setHend  (hit_start);
		fp1.setHstart(hit_start - fp.getEnd() + exon_start);
	      }
	      
	      found_hit_end = true;

	      //System.out.println("Hit    \t" + fp1.getStart() + "\t" + fp1.getEnd() + "\t" + fp1.getHstart() + "\t" + fp1.getHend());

	      newhits.addElement(fp1);
	      exonhash.put(fp1,sf.getId());

	  } else {

	    // |***********|

	    FeaturePair fp1 = new FeaturePair();

	    fp1.setStart(exon_start);
	    fp1.setEnd  (exon_end);
	    fp1.setAlignString(cdnaseq.substring(exon_start-1,exon_end));

	    if (fp.getHstrand() == 1) {
	      fp1.setHstart(hit_start);
	      fp1.setHend  (hit_start + exon_end - exon_start);
	      fp1.setHitAlignString(fp.getAlignString().substring(hit_start-fp.getHstart(),exon_end-fp.getStart()+1));
	    } else {
	      fp1.setHend  (hit_start);
	      fp1.setHstart(hit_start - exon_end + exon_start);
	    }

	    hit_start = hit_start + (exon_end-exon_start+1);

	    //System.out.println("Hit    \t" + fp1.getStart() + "\t" + fp1.getEnd() + "\t" + fp1.getHstart() + "\t" + fp1.getHend());

	    newhits.addElement(fp1);
	    exonhash.put(fp1,sf.getId());

	  }



	} else if (!found_hit_start &&
		   fp.getStart() >= exon_start && 
		   fp.getStart() <= exon_end) {


	  found_hit_start = true;

	  if (fp.getHstrand() == 1) {
	    hit_start = fp.getHstart();
	  } else {
	    hit_start = fp.getHend();
	  }

	  //System.out.println("Cdnaelngth\t" + cdnaseq.length());

	  if (fp.getEnd() < exon_end) {
	    // |---*****----|
	    FeaturePair fp1 = new FeaturePair();

	    fp1.setStart(fp.getStart());
	    fp1.setEnd  (fp.getEnd());
	    fp1.setAlignString(cdnaseq.substring(fp.getStart()-1,fp.getEnd()));
	    fp1.setHitAlignString(fp.getAlignString());

	    fp1.setHstart(fp.getHstart());
	    fp1.setHend  (fp.getHend());
	    
	    //System.out.println("Hit    \t" + fp1.getStart() + "\t" + fp1.getEnd() + "\t" + fp1.getHstart() + "\t" + fp1.getHend());

	    newhits.addElement(fp1);
	    exonhash.put(fp1,sf.getId());

	    found_hit_end = true;
	  } else {
	    
	    // |---*********|
	    FeaturePair fp1 = new FeaturePair();

	    fp1.setStart(fp.getStart());
	    fp1.setEnd  (exon_end);
	    fp1.setAlignString(cdnaseq.substring(fp.getStart()-1,exon_end));
	    fp1.setHitAlignString(fp.getAlignString().substring(0,exon_end-fp.getStart()+1));

	    if (fp.getHstrand() == 1) {
	      fp1.setHstart(hit_start);
	      fp1.setHend  (hit_start + exon_end - fp.getStart());
	    } else {
	      fp1.setHend  (hit_start);
	      fp1.setHstart(hit_start - exon_end + fp.getStart());
	    }

	    if (fp.getHstrand() == 1) {
	      hit_start = hit_start + exon_end - fp.getStart() + 1;
	    } else {
	      hit_start = hit_start - exon_end + fp.getStart() - 1;
	    }
	    //System.out.println("Hit    \t" + fp1.getStart() + "\t" + fp1.getEnd() + "\t" + fp1.getHstart() + "\t" + fp1.getHend());

	    newhits.addElement(fp1);
	    exonhash.put(fp1,sf.getId());
	  }
	}
	exon_start = exon_end+1;
	}
      }
    }
  }
	  
  private void convertExons() {

    if (pixelExons == null) {
      pixelExons = new Hashtable();
    }

    width  = getSize().width;
    height = getSize().height;

    int cdnalen = 0;

    for (int i = 0; i < exons.size(); i++) {
       SequenceFeature sf = (SequenceFeature)exons.elementAt(i);
       cdnalen += sf.getLength() + gap;
    }

    bpp = (double)(cdnalen + 2*xoffset)/width;
    
    int pixel = (int)((1+xoffset)/bpp);

    //System.out.println("Cdnalen   \t" + cdnalen);
    //System.out.println("bpp       \t" + bpp);

    int cdna_start = 1;

    for (int i = 0; i < exons.size(); i++) {
      
      SequenceFeature sf = (SequenceFeature)exons.elementAt(i);

      FeaturePair psf = new FeaturePair();

      psf.setStart(pixel);

      pixel += (int)((sf.getLength()-1)/bpp);

      psf.setEnd(pixel);

      pixel += (int)((1+gap)/bpp);

      psf.setId(sf.getId());
      psf.setHstart(cdna_start);
      psf.setHend  (cdna_start + sf.getLength() - 1);

      pixelExons.put(sf.getId(),psf);

      cdna_start += sf.getLength();

      //System.out.println("    Exon\t" + i + "\t" + sf.getStart() + "\t" + sf.getEnd() + "\t" + psf.getStart() + "\t" + psf.getEnd());
    }
  }
    
  public void paintComponent(Graphics g) {

    convertExons();
    convertHits();

    ylevel = height - 50;

    g.setColor(Color.white);
    g.fillRect(0,0,width,height);

    // First draw genes

    for (int i = 0; i < exons.size(); i++) {

       Color c = c1;

       if ( i % 2 == 0) {
           c = c2;
       }

       SequenceFeature sf  = (SequenceFeature)exons.elementAt(i);
       FeaturePair     psf = (FeaturePair)pixelExons.get(sf.getId());

       int y1     = ylevel;

       int x1 = psf.getStart();
       int x2 = psf.getEnd();


       g.setColor(Color.black);
       g.drawRect(x1,y1,(x2-x1+1),char_height);

       g.setColor(c);
       g.fillRect(x1,y1,(x2-x1+1),char_height);


    }

    // Now the hits
    int y1 = ylevel;
    
    Enumeration en = exonhithash.keys();
    int ii = 0;
    while (en.hasMoreElements()) {
      String hitname = (String)en.nextElement();

      Vector h = (Vector)exonhithash.get(hitname);
      Color  c = b1; //new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));

      if (ii % 2 == 0) {
	c = b2;
      }

      g.setColor(c);

      for (int i = 0; i < h.size(); i++) {

	y1 = ylevel - hit_height * (Integer) hitlevel.get(hitname);

        FeaturePair     fp     = (FeaturePair)h.elementAt(i);
	String          exonid = (String)exonhash.get(fp);
	FeaturePair     pexon  = (FeaturePair)pixelExons.get(exonid);
	
	//System.out.println("FP\t" + fp + "\t" + exonid);

	int x1 = pexon.getStart() + (int)((fp.getStart()- pexon.getHstart())/bpp);
	int x2 = pexon.getStart() + (int)((fp.getEnd()  - pexon.getHstart())/bpp);
	
	//System.out.println("start\t" + x1 + "\t" + x2);

	g.setColor(Color.black);
	g.drawRect(x1,y1-2,(x2-x1+1),hit_height);
	g.setColor(c);
	g.fillRect(x1,y1-2,(x2-x1+1),hit_height);

      }
      ii++;
    }

    // Now the selected hit

    if (selectedHit == null) {
      return;
    }

    if (cdnalevel == -1) {
      cdnalevel = y1 - 10*char_height;
    }

    y1  = cdnalevel;

    Vector sel_hits = (Vector)exonhithash.get(selectedHit);

    // Should get this from fasta file

    int hitlength = 3000;

    if (msphash != null &&
	msphash.containsKey(selectedHit)) {
      Sequence seq = (Sequence)msphash.get(selectedHit);
      hitlength = seq.getSequence().length();
    }

    //System.out.println("Hitlength\t" + hitlength + "\t" + selectedHit);

    if (newbpp == -1) {
      newbpp = (double)(2*cdnaoffset + hitlength)/width;
    }
    g.setColor(Color.black);

    g.drawRect((int)((cdnaoffset+1)/newbpp),y1,(int)(hitlength/newbpp),char_height);

    for (int i = 0; i < sel_hits.size(); i++) {
      FeaturePair fp = (FeaturePair)sel_hits.elementAt(i);

      int x1 = (int)((cdnaoffset + fp.getHstart())/newbpp);
      int x2 = (int)((cdnaoffset + fp.getHend())/newbpp);


      Color c = d1;
      //Color  c = new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
      if (i%2 == 0) {
	c = d2;
      }

      g.setColor(Color.black);
      g.drawRect(x1,y1,(x2-x1),char_height);

      g.setColor(c);
      g.fillRect(x1,y1,(x2-x1),char_height);

      // Now draw the link to the hit

      String id = (String)exonhash.get(fp);
      FeaturePair pexon = (FeaturePair)pixelExons.get(id);

      //      System.out.println("Hit\t" + fp.getStart() + "\t" + fp.getEnd() + "\t" + fp.getHstart() + "\t" + fp.getHend() + "\t" + id);

      //System.out.println("Align   \t" + fp.getAlignString());
      //System.out.println("HitAlign\t" + fp.getHitAlignString());
      
      int x11 = pexon.getStart() + (int)((fp.getStart() - pexon.getHstart() + 1)/bpp);
      int x12 = pexon.getStart() + (int)((fp.getEnd()   - pexon.getHstart() + 1)/bpp);

      int y2 = 	ylevel - hit_height * (Integer) hitlevel.get(selectedHit) - 2;

      Polygon p = new Polygon();

      p.addPoint(x1,y1+char_height);
      p.addPoint(x11,y2);

      p.addPoint(x12+1,y2);
      p.addPoint(x2+1,y1+char_height);

      Graphics2D g2 = (Graphics2D)g;

      int type = AlphaComposite.SRC_OVER;

      AlphaComposite ac =  AlphaComposite.getInstance(type,(float)0.2);
      g2.setComposite(ac);
						     
      g2.fillPolygon(p);


      g2.setColor(Color.red);

      String qstr = fp.getAlignString();
      String hstr = fp.getHitAlignString();

      for (int iii = 0; iii < qstr.length(); iii++) {

	String q = qstr.substring(iii,iii+1);
	String h = hstr.substring(iii,iii+1);

	if (! q.equals(h)) {
	  g2.drawLine((int)((cdnaoffset + fp.getHstart() + iii)/newbpp),y1+char_height,pexon.getStart() + (int)((fp.getStart() - pexon.getHstart() + 1 + iii)/bpp),y2);
	}
		      
      }

      ac =  AlphaComposite.getInstance(type,(float)1.0);

      g2.setComposite(ac);


    }

  }

  public Dimension getPreferredSize() {
    return new Dimension(1400,300);
  }
  public void windowClosing(WindowEvent evt) {
    setVisible(false);
    System.exit(0);
  }
  public void windowOpened(java.awt.event.WindowEvent e){}
  public void windowClosed(java.awt.event.WindowEvent e){}
  public void windowIconified(java.awt.event.WindowEvent e){}
  public void windowDeiconified(java.awt.event.WindowEvent e){}
  public void windowActivated(java.awt.event.WindowEvent e){}
  public void windowDeactivated(java.awt.event.WindowEvent e){}
  
  public void mouseMoved  (MouseEvent e) { }
  public void mouseDragged(MouseEvent e) {
    int x = e.getX();
    int y = e.getY();

    cdnaoffset = cdnaoffset + (int)((x - prevx)*bpp);

    cdnalevel  = cdnalevel  + (y - prevy);

    prevx = x;
    prevy = y;

    repaint();
 }
  public void mouseClicked(MouseEvent e) { }
  public void mouseEntered(MouseEvent e) { }
  public void mouseExited (MouseEvent e) { }
  
  public void mousePressed(MouseEvent e) {

    int x = e.getX();
    int y = e.getY();

    prevx = x;
    prevy = y;

    // Find overlapping exon
    Enumeration en1  = pixelExons.keys();
    FeaturePair exon = null;

    while (en1.hasMoreElements()) {
      String      id = (String)en1.nextElement();
      FeaturePair fp = (FeaturePair)pixelExons.get(id);

      //System.out.println("Exon coords\t" + x + "\t" + fp.getStart() + "\t" + fp.getEnd());

      if (x >= fp.getStart() && 
	  x <= fp.getEnd()) {

	exon = fp;

	//System.out.println("Found overlap exon\t" + exon);
      }
    }

    if (exon == null) {
      return;
    }

    System.out.println("Found exon " + exon.getId());

    int    level = (ylevel - y)/hit_height + 1;
    String name  = (String)levelhit.get(new Integer(level));

    if (name == null) {
      return;
    }
    System.out.println("Name  = " + name);
    System.out.println("Level = " + level);

    Enumeration en = exonhithash.keys();

    Vector h = (Vector)exonhithash.get(name);

    for (int i = 0; i < h.size(); i++) {

      FeaturePair fp = (FeaturePair)h.elementAt(i);

      if (exonhash.containsKey(fp)) {
	String exonid = (String)exonhash.get(fp);

	//	System.out.println("Found hit exon " + exonid);

	if (exonid.equals(exon.getId())) {

	  int coord = exon.getHstart() + (int)((x - exon.getStart())*bpp) - 1;

	  if (coord >= fp.getStart() &&
	      coord <= fp.getEnd()) {

	      //System.out.println("Found hit\t" + fp.getStart() + "\t" + fp.getEnd());

	      //System.out.println("Align string\t" + fp.getAlignString());

	    selectedHit = name;
	    repaint();
	  }
	}
      }
    }
  }
  

  public void mouseReleased(MouseEvent e) {
  }

  public static void main(String[] args) {
     try {
	 JFrame jf       = new JFrame();

	 FastaFile fasta     = new FastaFile(args[1],"File");   // genomic fasta
	 FastaFile fasta_msp = new FastaFile(args[3],"File");   // hit fasta

	 Vector    seqs      = fasta.getSeqs();                 // genomic sequences

	 Hashtable msphash   = new Hashtable();                 // hit sequences

	 for (int i = 0; i < fasta_msp.getSeqs().size(); i++) {
	     Sequence seq = (Sequence)fasta_msp.getSeqs().elementAt(i);
	     msphash.put(seq.getName(),seq);
	 }
	 
	 System.out.println("Got genomic seq " + seqs.size());
	 System.out.println("Got hit seqs    " + msphash.size());
	 
	// MSPFile2 msp    = new MSPFile2(args[2],"File");       // Blast hits in msp format
	 Vector   hits   = null;//msp.getFeatures();                  // Blast features

	 System.out.println("Got blast hits " + hits.size());

	 for (int i = 0; i < hits.size(); i++) {
	     FeaturePair sf = (FeaturePair)hits.elementAt(i);
	     System.out.println("Feat " + sf.getStart() + "\t" + sf.getEnd() + "\t" + sf.getHitId() + "\t" + sf.getHstart() + "\t" + sf.getHend() + "\t" + sf.getScore() + "\t" + sf.getHstrand());
	 }

	 Sequence seq = (Sequence)seqs.elementAt(0);
	 String id = seq.getName();

	 String chr = "Un";
	 
	 int    chrstart = -1;
	 int    chrend   = -1;

	 if (id.indexOf(".") > 0) {
	     chr = id.substring(0,id.indexOf("."));

	     if (id.indexOf("-") > 0) {
		 chrstart = Integer.parseInt(id.substring(id.indexOf(".")+1,id.indexOf("-")));
		 chrend   = Integer.parseInt(id.substring(id.indexOf("-")+1));
	     }
	 }
	 System.out.println("Genomic region is " + chr + ":" + chrstart + "-" + chrend);

	 GFFFile gfffile = new GFFFile(args[0],"File");       // Exon file
	 Vector exons = gfffile.getFeatures();                // exons

	 System.out.println("Number of Exons " + exons.size());
	 
	 GeneDisplay2 gd = new GeneDisplay2(exons);

	 gd.setChr(chr);
	 gd.setChrStart(chrstart);
	 gd.setChrEnd(chrend);
	 gd.setMspSeqs(msphash);
       
	 JScrollPane jsp = new JScrollPane();

	 gd.setGeneSequence(seq,chrstart);
	 gd.setHits(hits);
	 gd.setSize(768,768);

	 jsp.setViewportView(gd);
	 jf.addWindowListener(gd);
	 jf.getContentPane().add(jsp);
	 jf.setSize(768,500);
	 jf.pack();
	 jf.setVisible(true);

     } catch (Exception e) {
       System.out.println("Exception " + e);
     }
  }
}

