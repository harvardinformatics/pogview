package pogvue.gui;

import pogvue.datamodel.*;
import pogvue.datamodel.genedisplay.Region;
import pogvue.gui.schemes.ResidueProperties;
import pogvue.io.FastaFile;
import pogvue.io.GFFFile;
//import pogvue.io.MSPFile2;
import pogvue.util.QuickSort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

public final class GeneDisplay extends JPanel implements WindowListener, MouseListener,MouseMotionListener {
  private final int       genomic_gap = 10000;
  private String    geneid;
  private final Vector    exons;
  private Vector    genhits;
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
  private final Hashtable gcdshash = new Hashtable();
  private Hashtable genhithash;
  private Hashtable genhitlevel;
  private Hashtable levelgen;

  private Vector    regions;

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
  private final int hit_height  = 10;
  private final int xoffset     = 500;
  private int cdnaoffset;
  private final int gap         = 200;

  private int ylevel;
  private int cdnalevel = -1;

  private String selectedHit = null;
  private String selectedGenHit = null;

  private Hashtable regionhash;
  private Hashtable pixelRegions;

  private final Color c1 = Color.pink;
  private final Color c2 = Color.lightGray;
  
  private final Color b1 = new Color(50,50,242);
  private final Color b2 = new Color(150,150,242);

  private final Color d1 = new Color(242,50,42);
  private final Color d2 = new Color(242,150,142);

  private final Color g1 = new Color(246,197,94);
  private final Color gr2 = new Color(246,197,40);
  private final Color gr3 = new Color(200,150,40);

  private GeneDisplay(Vector exons) {

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
  private void setGeneId(String id) {
    this.geneid = id;
  }
    
  private void setMspSeqs(Hashtable hash) {
    this.msphash = hash;
  }
  private void setChr(String chr) {
    this.chr = chr;
  }

  private void setChrStart(int chrstart) {
    this.chrstart = chrstart;

    for (int i = 0; i < exons.size(); i++) {
					    
      SequenceFeature sf = (SequenceFeature)exons.elementAt(i);

      //sf.setStart(sf.getStart() - chrstart + 1);
      //sf.setEnd  (sf.getEnd() - chrstart + 1);
    }

  }

  private void setChrEnd(int chrend) {
    this.chrend = chrend;
  }

  private void setGenomicHits(Vector genhits) {
    this.genhits = genhits;
  }

  private void setGenomicLevels() {
    
    // Hash the hit ids

    genhithash = new Hashtable();
    levelgen   = new Hashtable();

    for (int i = 0; i < regions.size(); i++) {
      Region r = (Region)regions.elementAt(i);

      Vector f = r.features;

      for (int j = 0; j < f.size(); j++) {
	
	if (f.elementAt(j) instanceof FeaturePair) {
	 
	  FeaturePair sf = (FeaturePair)f.elementAt(j);

	  if (!genhithash.containsKey(sf.getHitId())) {
	    Vector newv  = new Vector();
	    newv.addElement(sf);
	    genhithash.put(sf.getHitId(),newv);
	    
	  } else {
	    
	    Vector v = (Vector)genhithash.get(sf.getHitId());
	    v.addElement(sf);
	  }
	}
      }
    }

    genhitlevel = new Hashtable();

    int i = 0;

    Enumeration en = genhithash.keys();

    while (en.hasMoreElements()) {
      String key = (String)en.nextElement();

      genhitlevel.put(key, i);
      System.out.println("putting level " + i);
      levelgen.put(i,key);
      i++;
    }
  }
  private void clusterGenomicRegions() {

    System.out.println("Cluster");

    regions  = new Vector();

    int num = exons.size();

    Vector newgenhits = new Vector();

    System.out.println("hash " + hithash);

    for (int i = 0; i < genhits.size(); i++) {
      FeaturePair fp = (FeaturePair)genhits.elementAt(i);

      if (hithash.get(fp.getHitId()) != null) {
	newgenhits.addElement(fp);
      }
    }

    genhits = newgenhits;

    num += genhits.size();

    int             starts[] = new int[num];
    Object          things[] = new Object[num];

    int j = 0;
    for (int i = 0; i < exons.size(); i++) {
      SequenceFeature sf = (SequenceFeature)exons.elementAt(i);

      starts[j] = sf.getStart() - 1;
      things[j] = sf;
      j++;
    }

    for (int i = 0; i < genhits.size(); i++) {
      FeaturePair sf = (FeaturePair)genhits.elementAt(i);

      starts[j] = sf.getStart();
      things[j] = sf;
      j++;
    }

    QuickSort.sort(starts,things);

    j = 0;

    while (j < num) {

      SequenceFeature sf = (SequenceFeature)things[j];

      //      System.out.println("Feature " + sf.getStart() + "\t" + sf.getEnd());

      boolean found_region = false;

      for (int i = 0;i < regions.size(); i++) {

	Region r = (Region)regions.elementAt(i);

	//System.out.println("   - REgion " + r.getStart() + "\t" + r.getEnd());

	if (!(r.getStart() > (sf.getEnd() + genomic_gap)||
	      r.getEnd()  < (sf.getStart()-genomic_gap))) {

	  //  System.out.println("Found region\n");

	  found_region = true;
	  

	  if (sf.getStart() < r.getStart()) {
	    r.setStart(sf.getStart());
	  }
	  if (sf.getEnd() > r.getEnd()) {
	    r.setEnd(sf.getEnd());
	  }

	  r.features.addElement(sf);
	}
      }

      if (! found_region) {
	Region r = new Region();

	r.setStart(sf.getStart());
	r.setEnd  (sf.getEnd());

	r.features  = new Vector();

	r.features.addElement(sf);

	regions.addElement(r);
      }

      j++;
    }

    regionhash  = new Hashtable();

    for (int i = 0; i < regions.size(); i++) {
      Region r = (Region)regions.elementAt(i);

      //System.out.println("Region\t" + r.getStart() + "\t" + r.getEnd() + "\t" + r.features.size());

      String id = chr + "." + (chrstart + r.getStart()-1) + "-" + (chrstart + r.getEnd() - 1);

      r.setId(id);

      for (j = 0; j < r.features.size(); j++) {


	if (! (r.features.elementAt(j) instanceof FeaturePair)) {
	  //System.out.println("Found exon " + r.features.elementAt(j));
	} else {
	  FeaturePair fp = (FeaturePair)r.features.elementAt(j);
	  regionhash.put(fp,id);
	  //System.out.println("Putting\t" + fp + "\t" + id);
	}
      }
    }

  }
	


  private void setGeneSequence(Sequence seq,int offset) {
     this.seq = seq;

     cdnaseq = "";

     int coord = 1;

     for (int i = 0; i < exons.size(); i++) {
       SequenceFeature sf = (SequenceFeature)exons.elementAt(i);
       System.out.println("Coord\t" + (sf.getStart() + "\t" + sf.getEnd()));

       System.out.println("Seq " + seq.getSequence().length());
       String tmpseq = seq.getSequence().substring(sf.getStart()-offset,sf.getEnd()-offset);

       System.out.println("Seq\t" + tmpseq);

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

       getFullSequence(hitname);

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

     System.out.println("Converting");

     // Now break up the hits into pieces.

     convertHits(chrstart);
     
  }


  private void getFullSequence(String id1) {

    if (gcdshash.containsKey(id1)) {
      return;
    }
    try {

      String id = id1;

      if (id.indexOf("|") > 0) {
	id = id.substring(id.indexOf("|")+1);
      }
      if (id.indexOf("|") > 0) {
	id = id.substring(0,id.indexOf("|"));
      }
      
      String command = "fastacmd -s " + id + " -d /Volumes/Data/gene_killing/refseq.200405/vmam.fa";
      
      System.out.println("Comand " + command);

      Runtime r = Runtime.getRuntime();
      
      Process p = r.exec(command);
      
      BufferedInputStream is = new BufferedInputStream(p.getInputStream());
      int len;
      byte buf[] = new byte[5000];
      
      while((len = is.read(buf)) != -1 ) {
	String st = new String(buf,0,0,len);
	


	if (st.indexOf(">") == 0) {
	  System.out.println(st);
	  StringTokenizer str = new StringTokenizer(st);

	  String idval = str.nextToken();

	  idval = idval.substring(1);

	  int lenval = Integer.parseInt(str.nextToken());

	  String  div  = str.nextToken();
	  String  org  = str.nextToken();

	  int cds_start = Integer.parseInt(str.nextToken());
	  int cds_end   = Integer.parseInt(str.nextToken());

	  int codon_start = Integer.parseInt(str.nextToken());

	  String translation = str.nextToken();
	  String description =  str.nextToken();
	  String comment     = str.nextToken();

	  GeneCDS gcds = new GeneCDS(id,cds_start,cds_end,translation,codon_start,org,div,description,comment,"");

	  System.out.println("Sequence " + id1);
	  gcdshash.put(id1,gcds);
	} 
	
      }
      
      System.out.println("Command thread is done");
      
    } catch( java.io.EOFException eof ) {
      System.out.println("Exception : " + eof);
    } catch (java.io.IOException e) {
      System.out.println("Exception : " + e);
      
    }
  }

  private void convertHits(int offset) {

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

	  exon_end = exon_start + sf.getEnd()-sf.getStart();

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
	  
  public void convertExons() {

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
  private void convertExons2() {

    if (pixelExons == null) {
      pixelExons = new Hashtable();
    }

    pixelRegions = new Hashtable();

    width  = getSize().width;
    height = getSize().height;

    int cdnalen = 0;

    for (int i = 0; i < regions.size(); i++) {
       SequenceFeature sf = (SequenceFeature)regions.elementAt(i);
       cdnalen += sf.getEnd() - sf.getStart() + 1 + gap;
    }

    bpp = (double)(cdnalen + 2*xoffset)/width;
    
    int pixel = (int)((1+xoffset)/bpp);

    System.out.println("Cdnalen   \t" + cdnalen);
    System.out.println("bpp       \t" + bpp);

    int cdna_start = 1;

    for (int i = 0; i < regions.size(); i++) {
      
      Region r = (Region)regions.elementAt(i);
      Vector f = r.features;

      for (int ii = 0; ii < f.size(); ii++) {
	SequenceFeature sf = (SequenceFeature)f.elementAt(ii);

	if (!(sf instanceof FeaturePair)) {
	
	  
	  FeaturePair psf = new FeaturePair();

	  psf.setStart(pixel + (int)((sf.getStart() - r.getStart()-1)/bpp));
	  psf.setEnd  (pixel + (int)((sf.getEnd()   - r.getStart()-1)/bpp));

	  psf.setId(sf.getId());
	  psf.setHstart(cdna_start);
	  psf.setHend  (cdna_start + sf.getLength() - 1);

	  cdna_start += sf.getLength();

	  //System.out.println("Found exon\t" + sf.getStart() + "\t" + sf.getEnd() + "\t" + psf.getStart() + "\t" + psf.getEnd());

	  pixelExons.put(sf.getId(),psf);
	}
      }

      r.setHstart(pixel);
      pixel += (int)((r.getEnd() - r.getStart() -1)/bpp);
      r.setHend(pixel);

      pixelRegions.put(r.getId(),r);

      pixel += (int)((1+gap)/bpp);

      //      System.out.println("pixel\t" + r.getStart() + "\t" + r.getEnd() + "\t" + pixel);


    }
  }
    
    
  public void paintComponent(Graphics g) {

    Font f = new Font("Courier",Font.PLAIN,8);
    g.setFont(f);
    convertExons2();
    convertHits(chrstart);

    ylevel = height - 50;

    g.setColor(Color.white);
    g.fillRect(0,0,width,height);

    // First draw genes

    g.setColor(Color.black);

    for (int i = 0; i > regions.size(); i++) {
      FeaturePair r = (FeaturePair)regions.elementAt(i);

      int y1 = ylevel + 5;

      int x1 = r.getHstart();
      int x2 = r.getHend();
      
      g.drawRect(x1,y1,(x2-x1+1),5);
    }

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
       g.drawString(geneid,10,y1+char_height);
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

      y1 = ylevel - hit_height * (Integer) hitlevel.get(hitname);

      g.setColor(Color.black);
      g.drawString(hitname,10,y1+hit_height);
      g.setColor(c);

      for (int i = 0; i < h.size(); i++) {



        FeaturePair     fp     = (FeaturePair)h.elementAt(i);
	String          exonid = (String)exonhash.get(fp);
	FeaturePair     pexon  = (FeaturePair)pixelExons.get(exonid);
	
	//System.out.println("FP\t" + fp + "\t" + exonid);

	int x1 = pexon.getStart() + (int)((fp.getStart()- pexon.getHstart())/bpp);
	int x2 = pexon.getStart() + (int)((fp.getEnd()  - pexon.getHstart())/bpp);
	
	//System.out.println("start\t" + x1 + "\t" + x2);

	g.setColor(Color.black);
	g.drawRect(x1,y1-2,(x2-x1+1),hit_height-2);

	if (hitname.equals(selectedHit)) {
	  g.setColor(Color.red);
	} else {
	  g.setColor(c);
	}
	g.fillRect(x1,y1-2,(x2-x1+1),hit_height-2);

      }
      ii++;
    }

    // Now the genomic hits

    y1 -= 2*char_height;

    int maxnum = 0;

    for (int i = 0; i < regions.size(); i++) {
      Region r = (Region)regions.elementAt(i);

      for (int j = 0; j < r.features.size(); j++) {
	if (r.features.elementAt(j) instanceof FeaturePair) {
	  FeaturePair fp = (FeaturePair)r.features.elementAt(j);

	  int level = (Integer) genhitlevel.get(fp.getHitId());

	  int yy = y1 - hit_height*level;

	  int x1 = r.getHstart() + (int)((fp.getStart()-r.getStart())/bpp);
	  int x2 = r.getHstart() + (int)((fp.getEnd()  - r.getStart())/bpp);
	    
	  g.setColor(Color.black);
	  g.drawRect(x1,yy,(x2-x1+1),hit_height-2);
	  //g.drawString(fp.getHitId(),x1,yy+2*hit_height);

	  if (msphash.containsKey(fp.getHitId())) {
	    g.setColor(g1);
	  } else {
	    g.setColor(Color.lightGray);
	  }

	  if (fp.getHitId().equals(selectedHit)) {
	    g.setColor(Color.red);
	  }
	  g.fillRect(x1,yy,(x2-x1+1),hit_height-2);


	  // Check for coding region

	  System.out.println("G " + fp.getHitId());

	  GeneCDS gcds = (GeneCDS)gcdshash.get(fp.getHitId());

	  if (gcds != null) {
	    System.out.println("GeneGDS " + fp.getHitId() + " " + gcds.cds_start + " " + gcds.cds_end);
	    System.out.println("Start   " + fp.getHend() + " " + fp.getHstart());
	    
	    int cds_start = gcds.cds_start;
	    int cds_end   = gcds.cds_end;
	    

	    int base_start = 1;
	    int base_end   = fp.getHend() - fp.getStart() + 1;

	    int qbase_start = fp.getStart();
	    int qbase_end   = fp.getEnd();
	    
	    if (!(cds_start > fp.getHend() || 
		  cds_end   < fp.getHstart())){
	      
	      System.out.println("Coords " + x1  + " " + x2);
	      System.out.println("Colouring");
	   
	      if (fp.getHstrand() == 1) {
		if (cds_start >= fp.getHstart()) {
		  x1 = r.getHstart() + (int)((fp.getStart() - r.getStart() + cds_start - fp.getHstart())/bpp);

		  base_start = cds_start - fp.getHstart()+1;
		  qbase_start = fp.getStart() + cds_start-fp.getHstart();

		}
		
		if (cds_end <= fp.getHend()) {
		  x2 = r.getHstart() + (int)((fp.getStart() - r.getStart() + cds_end  - fp.getHstart())/bpp);
		  base_end = cds_end - fp.getHend() + 1;
		  qbase_end = fp.getStart() + cds_end-fp.getHstart();
		}

		System.out.println("Bases " + qbase_start + " " + qbase_end + " "+ fp.getHitId());

		System.out.println("hitaln " + fp.getAlignString());

		System.out.println("Coords " + x1  + " " + x2);
		if (fp.getHitId().equals(selectedHit)) {
		  g.setColor(Color.magenta);
		} else {
		  g.setColor(gr3);
		}
		g.fillRect(x1,yy,(x2-x1+1),hit_height-2);

		int base = qbase_start;
		int hbase = base_start;

		if (cds_start < fp.getHstart()) {
		  int offset = (fp.getHstart() - cds_start+1)%3;
		  base += offset;
		  hbase += offset;
		}
		while (base < qbase_end) {
		  String qcodon  = seq.getSequence().substring(base-1,base+2).toUpperCase();
		  String hcodon  = fp.getAlignString().substring(hbase-1,hbase+2).toUpperCase();

		  String qres = ResidueProperties.codonTranslate(qcodon);
		  String hres = ResidueProperties.codonTranslate(hcodon);

		  if (qres.equals("*")) {
		    g.setColor(Color.green);
		    g.drawLine(x1 + (int)((base-fp.getStart())/bpp),yy,x1+(int)((base-fp.getStart())/bpp),yy+char_height-1);
		  } 
		  System.out.println("Codons " + qcodon + " " + hcodon + " " + qres + " " + hres);
		  base += 3;
		  hbase += 3;
		}
	      } else {
		if (cds_start >= fp.getHstart()) {
		  x2 = r.getHstart() + (int)((fp.getStart() - r.getStart() + cds_start - fp.getHstart())/bpp);
		}
		
		if (cds_end <= fp.getHend()) {
		  x1 = r.getHstart() + (int)((fp.getStart() - r.getStart() + cds_end  - fp.getHstart())/bpp);
		}

		System.out.println("Coords " + x1  + " " + x2);

		if (fp.getHitId().equals(selectedHit)) {
		  g.setColor(Color.magenta);
		} else {
		  g.setColor(gr3);
		}
		g.fillRect(x1,yy,(x2-x1+1),hit_height-2);

	      }

	    }
	  }
	    
	}
      }
    }	



    // Now the selected hit

    if (selectedHit != null) {

      if (cdnalevel == -1) {
	cdnalevel = y1 - 10*char_height;
      }
      
      y1  = cdnalevel;
      
      Vector sel_hits = (Vector)exonhithash.get(selectedHit);
      
      Vector gen_hits = (Vector)genhithash.get(selectedHit);

      if (gen_hits != null) {
	System.out.println("Got gen hits " + gen_hits.size());
      }
      // Should get this from fasta file
      
      int hitlength = 3000;
      
      if (msphash != null &&
	  msphash.containsKey(selectedHit)) {
	Sequence seq = (Sequence)msphash.get(selectedHit);
	hitlength = seq.getSequence().length();
      }
      
      System.out.println("Hitlength\t" + hitlength + "\t" + selectedHit);
      
      if (newbpp == -1) {
	//newbpp = (double)(2*cdnaoffset + hitlength)/width;
	newbpp = bpp;
      }
      
      g.setColor(Color.black);
      
      g.drawRect((int)((cdnaoffset+1)/newbpp),y1,(int)(hitlength/newbpp),char_height);

      GeneCDS gcds = (GeneCDS)gcdshash.get(selectedHit);

      g.setColor(Color.gray);
      g.fillRect((int)((cdnaoffset+gcds.cds_start)/newbpp),y1-char_height/2,(int)((gcds.cds_end-gcds.cds_start+1)/newbpp),char_height/2);

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
	
	//System.out.println("Hit\t" + fp.getStart() + "\t" + fp.getEnd() + "\t" + fp.getHstart() + "\t" + fp.getHend() + "\t" + id);
	
	//System.out.println("Align   \t" + fp.getAlignString());
	//System.out.println("HitAlign\t" + fp.getHitAlignString());
	
	int x11 = pexon.getStart() + (int)((fp.getStart() - pexon.getHstart() + 1)/bpp);
	int x12 = pexon.getStart() + (int)((fp.getEnd()   - pexon.getHstart() + 1)/bpp);
	
	int ymid =      ylevel - hit_height * hitlevel.size() - char_height;
	int y2 = 	ylevel - hit_height * (Integer) hitlevel.get(selectedHit) - 2;
	
	Polygon p = new Polygon();
	
	p.addPoint(x1,y1+char_height);
	p.addPoint(x11,ymid);
	
	p.addPoint(x12+1,ymid);
	p.addPoint(x2+1,y1+char_height);
	
	Polygon p2 = new Polygon();
	
	p2.addPoint(x11,ymid);
	p2.addPoint(x11,ylevel);
	
	p2.addPoint(x12,ylevel);
	p2.addPoint(x12,ymid);

	//p.addPoint(x2+1,y1+char_height);
	
	Graphics2D g2 = (Graphics2D)g;
	
	
	int type = AlphaComposite.SRC_OVER;

	g2.setColor(Color.black);	

	g2.drawString(selectedHit,10,y1+10);
	g2.drawString(selectedHit,10,y2+10);
	AlphaComposite ac =  AlphaComposite.getInstance(type,(float)0.2);

	g2.setComposite(ac);
	g2.fillPolygon(p);
	g2.fillPolygon(p2);
	g2.setColor(Color.red);	
	
	String qstr = fp.getAlignString();
	String hstr = fp.getHitAlignString();
	
	qstr = qstr.toUpperCase();
	hstr = hstr.toUpperCase();
	
	for (int iii = 0; iii < qstr.length(); iii++) {
	  
	  String q = qstr.substring(iii,iii+1);
	  String h = hstr.substring(iii,iii+1);
	  
	  if (! q.equals(h)) {
	    g2.drawLine((int)((cdnaoffset + fp.getHstart() + iii)/newbpp),y1+char_height,pexon.getStart() + (int)((fp.getStart() - pexon.getHstart() + 1 + iii)/bpp),ymid);
	  }
	  
	}
	
	ac =  AlphaComposite.getInstance(type,(float)1.0);
	
	g2.setComposite(ac);
	
	
      }
    


      if (gen_hits == null) {
	return;
      }
    // Draw the links to the genomic hits
      for (int i = 0; i < gen_hits.size(); i++) {
	FeaturePair fp = (FeaturePair)gen_hits.elementAt(i);
	
	int x1 = (int)((cdnaoffset + fp.getHstart())/newbpp);
	int x2 = (int)((cdnaoffset + fp.getHend())/newbpp);
	
	Color c = g1;

	if (i%2 == 0) {
	  c = gr2;
	}
	
	g.setColor(Color.black);
	g.drawRect(x1,y1,(x2-x1),char_height);
	
	g.setColor(c);
	g.fillRect(x1,y1,(x2-x1),char_height);
	
	// Now draw the link to the hit
	
	String id = (String)regionhash.get(fp);

	FeaturePair pregion = (FeaturePair)pixelRegions.get(id);
	
	int x11 = pregion.getHstart() + (int)((fp.getStart() - pregion.getStart() + 1)/bpp);
	int x12 = pregion.getHstart() + (int)((fp.getEnd()   - pregion.getStart() + 1)/bpp);
	
	int y2 =  ylevel - char_height - (1+hitlevel.size())*hit_height - hit_height* (Integer) genhitlevel.get(selectedHit);
	int ymid =      ylevel - hit_height * hitlevel.size() - char_height;

	Polygon p = new Polygon();
	
	p.addPoint(x1,y1+char_height);
	p.addPoint(x11,ymid);
	
	p.addPoint(x12+1,ymid);
	p.addPoint(x2+1,y1+char_height);
	Polygon p2 = new Polygon();
	
	p2.addPoint(x11,ymid);
	p2.addPoint(x11,y2);
	
	p2.addPoint(x12,y2);
	p2.addPoint(x12,ymid);
	
	Graphics2D g2 = (Graphics2D)g;
	
	int type = AlphaComposite.SRC_OVER;
	
	AlphaComposite ac =  AlphaComposite.getInstance(type,(float)0.2);
	g2.setComposite(ac);
	
	g2.fillPolygon(p);
	g2.fillPolygon(p2);
	ac =  AlphaComposite.getInstance(type,(float)1.0);

	String qstr = seq.getSequence().substring(fp.getStart()-1,fp.getEnd());
	String hstr = fp.getAlignString();
	
	qstr = qstr.toUpperCase();
	hstr = hstr.toUpperCase();
	
	//System.out.println("QSTR\t" + qstr);
	//System.out.println("HSTR\t" + hstr);

	if (fp.getHstrand() == -1) {
	  hstr = ResidueProperties.reverseComplement(hstr);
	}
	
	g2.setColor(Color.red);
	for (int iii = 0; iii < qstr.length(); iii++) {
	  
	  String q = qstr.substring(iii,iii+1);
	  String h = hstr.substring(iii,iii+1);
	  
	  if (! q.equals(h)) {
	    g2.drawLine((int)((cdnaoffset + fp.getHstart() + iii)/newbpp),y1+char_height,pregion.getHstart() + (int)((fp.getStart() - pregion.getStart() + 1 + iii)/bpp),ymid);
	  }
	}
	g2.setComposite(ac);
	
      }
      }
    }

  public Dimension getPreferredSize() {

    if (hitlevel    != null &&
	genhitlevel != null) {

      int height = 50 + 3*char_height + (hitlevel.size() + genhitlevel.size()) * hit_height + 100;

      return new Dimension(1400,height);
    } else {
	
      return new Dimension(1400,300);
    }
  }

  public void windowClosing(WindowEvent evt) {
    setVisible(false);
    System.exit(0);
  }

  public void windowOpened     (java.awt.event.WindowEvent e){}
  public void windowClosed     (java.awt.event.WindowEvent e){}
  public void windowIconified  (java.awt.event.WindowEvent e){}
  public void windowDeiconified(java.awt.event.WindowEvent e){}
  public void windowActivated  (java.awt.event.WindowEvent e){}
  public void windowDeactivated(java.awt.event.WindowEvent e){}

  public void mouseMoved  (MouseEvent e) { }
  public void mouseClicked(MouseEvent e) { }
  public void mouseEntered(MouseEvent e) { }
  public void mouseExited (MouseEvent e) { }

  public void mouseDragged(MouseEvent e) {
    int x = e.getX();
    int y = e.getY();


    
    cdnaoffset = cdnaoffset + (int)((x - prevx)*bpp);

    cdnalevel  = cdnalevel  + (y - prevy);

    prevx = x;
    prevy = y;

    repaint();
 }
  
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

    if (exon != null) {
      System.out.println("Found exon " + exon.getId());
      
      int    level = (ylevel - y)/hit_height + 1;
      String name  = (String)levelhit.get(new Integer(level));
      
      if (name != null) {

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
		
		System.out.println("Found hit\t" + fp.getStart() + "\t" + fp.getEnd());
		System.out.println("Align string\t" + fp.getAlignString());

		if (!name.equals(selectedHit)) {
		  cdnaoffset = xoffset;
		}
		selectedHit = name;
		repaint();
	      }
	    }
	  }
	}
      }
    }


    
    int    level = (ylevel - 3*char_height - hit_height*exonhithash.size() - y)/hit_height + 2;

    String name  = (String)levelgen.get(new Integer(level));

    if (name == null) {
      return;
    }

    System.out.println("GenName  = " + name);
    System.out.println("GenLevel = " + level);

    for (int i = 0; i < regions.size(); i++) {
      Region r = (Region)regions.elementAt(i);
      Vector f = r.features;

      for (int j = 0; j < f.size(); j++) {

	if (f.elementAt(j) instanceof FeaturePair) {
	  FeaturePair fp = (FeaturePair)f.elementAt(j);

	  if (fp.getHitId().equals(name)) {

	    int x1 = r.getHstart() + (int)((fp.getStart()- r.getStart()+1)/bpp);
	    int x2 = r.getHstart() + (int)((fp.getEnd()  - r.getStart()+1)/bpp);

	    if (x >= x1 && x <= x2) {
	      System.out.println("Got selected gen hit " + name);
	      selectedGenHit = name;
	      selectedHit = name;
	      repaint();
	    }
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
       String    geneid    = args[5];
       FastaFile fasta     = new FastaFile(args[1],"File");
       FastaFile fasta_msp = new FastaFile(args[3],"File");
       Vector    seqs      = fasta.getSeqs();

       Hashtable msphash   = new Hashtable();

       for (int i = 0; i < fasta_msp.getSeqs().size(); i++) {
	 Sequence seq = (Sequence)fasta_msp.getSeqs().elementAt(i);
	 msphash.put(seq.getName(),seq);
       }

       System.out.println("Got seqs " + seqs.size());

       //MSPFile2 msp    = new MSPFile2(args[2],"File");
       Vector   hits   = null;//msp.getFeatures();

       System.out.println("Got seqs " + hits.size());

          for (int i = 0; i < hits.size(); i++) {
           FeaturePair sf = (FeaturePair)hits.elementAt(i);
           System.out.println("Feat " + sf.getStart() + "\t" + sf.getEnd() + "\t" + sf.getHitId() + "\t" + sf.getHstart() + "\t" + sf.getHend() + "\t" + sf.getScore() + "\t" + sf.getHstrand());
       }

       Sequence seq = (Sequence)seqs.elementAt(0);


       String id = seq.getName();

       String chr = "Un";

       int    chrstart = -1;
       int    chrend = -1;

       if (id.indexOf(".") > 0) {
	 chr = id.substring(0,id.indexOf("."));

	 if (id.indexOf("-") > 0) {
	   chrstart = Integer.parseInt(id.substring(id.indexOf(".")+1,id.indexOf("-")));
	   chrend   = Integer.parseInt(id.substring(id.indexOf("-")+1));
	 }
       }

       GFFFile gfffile = new GFFFile(args[0],"File");
       Vector exons = gfffile.getFeatures();

       //MSPFile2 genmsp  = null;//new MSPFile2(args[4],"File");
       Vector   genhits = null;//genmsp.getFeatures();

       System.out.println("Features " + exons.size());

       GeneDisplay gd = new GeneDisplay(exons);
       gd.setGeneId(geneid);
       gd.setChr(chr);
       gd.setChrStart(chrstart);
       gd.setChrEnd(chrend);

       System.out.println("Chr start/end "  + chr + "\t" + chrstart + "\t" + chrend);
       gd.setGeneSequence(seq,chrstart);
       gd.setHits(hits);

       System.out.println("Hits");

       gd.setGenomicHits(genhits);
       gd.clusterGenomicRegions();

       gd.setGenomicLevels();

       gd.setMspSeqs(msphash);
       System.out.println("chr start end " + chr + " " + chrstart + " " + chrend);
       
       System.out.println("Oane");
       JScrollPane jsp = new JScrollPane();

       System.out.println("Scroll");


       System.out.println("Seq");

       System.out.println("Hits");

       gd.setSize(1024,768);

       jsp.setViewportView(gd);
       jf.addWindowListener(gd);
       jf.getContentPane().add(jsp);
       jf.setSize(700,500);
       jf.pack();
       jf.setVisible(true);

     } catch (IOException e) {
       System.out.println("Exception " + e);
     }
  }
}

