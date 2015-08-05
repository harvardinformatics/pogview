package pogvue.gui;

import pogvue.datamodel.Chromosome;
import pogvue.io.*;
import pogvue.gui.hub.*;
import pogvue.datamodel.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.*;

import java.awt.event.*;

public final class KaryotypePanel extends JPanel implements MouseListener, ActionListener {

  public  JScrollPane jsp;
  private final Vector chromosomes;
  
  private int          maxlen = -1;
  private final int    rows   = 2;   // This can't be changed if mouseclick is to work
  private int          maxcount;
  
  private Vector       maxcounts;
  private Vector       colors;
  private Vector       types;

  boolean linked_maxcount = true;

  String chrname;
  int chrstart;
  int chrend;
  
  GetFeatureThread r;
  
  int chr_per_row;
  int height;
  int width;
  int bases_per_pixel;
  
  int yoffset;
  
  public int prefWidth = 500;
  public int prefHeight = 300;
  
  FeatureSelection fsel;
  
  public KaryotypePanel(Vector chromosomes) {
    this.chromosomes = chromosomes;
    
    setMaxlen();
    setMaxCount();

    addMouseListener(this);
  }
  public void setScrollPane(JScrollPane jsp) {
      this.jsp = jsp;
  
  }
  public void setFeatureSelection(FeatureSelection fsel) {
	this.fsel = fsel;
    }
  private void setMaxlen() {
    
    maxlen = -1;
    
    for (int i = 0; i< chromosomes.size(); i++) {
      int len = ((Chromosome)chromosomes.elementAt(i)).getLength();
      
      if (len > maxlen) {
	maxlen = len;
      }
    }
    
  }

  public void setLinkedMaxcount(boolean tmp) {
    linked_maxcount = tmp;
  }

  private void setMaxCount() {
    
  
    maxcounts = new Vector();
    types     = new Vector();


    //types.addElement("gene");
    //types.addElement("peak2");
      
    for (int i = 0; i< chromosomes.size(); i++) {
      Chromosome chr = (Chromosome)chromosomes.elementAt(i);

      int binsize = chr.getBinSize();
      int maxbin  = chr.getLength()/binsize + 1;

      Hashtable   fh = chr.featureDensity(chr.getBinSize());
      Enumeration en = fh.keys();

      while (en.hasMoreElements()) {
	maxcounts.addElement(0);
	en.nextElement();
        
      }

      en = fh.keys();


      // Fill up the types vector for drawing the legend
      while (en.hasMoreElements()) {
	String type = (String)en.nextElement();
	
	boolean found = false;

	for (int ii = 0; ii < types.size(); ii++) { 
	  String tmp = (String)types.elementAt(ii);

	  if (tmp.equals(type)) {
	    found = true;
	  }
	}
	if (!found) {
	  System.out.println("Adding to types " + type);
	  types.addElement(type);
	}
      }

      en = fh.keys();
      int k = 0;
      
      while (en.hasMoreElements()) {
	String type = (String)en.nextElement();
	Vector fd = (Vector)fh.get(type);

        int typenum = k;

        for (int ii = 0; ii < types.size(); ii++) {
            String tmp = (String)types.elementAt(ii);
            if (tmp.equals(type)) {
                typenum = ii;
            }
        }

	maxcount  = -1;

	int tmpcount = 0;
	
	if (maxcounts.size() > typenum) {
	  tmpcount = (Integer) maxcounts.elementAt(typenum);
	}
	
	for (int j = 0; j < maxbin; j++) {
	  //int num = (Integer) fd.elementAt(j);
	  FeatureBin fbin = (FeatureBin)fd.elementAt(j);
	  int num = fbin.getSize();
	  
	  if (num > maxcount) {
	    maxcount = num;
	  }
	  if (num > tmpcount) {
	    tmpcount = num;
	  }
	}
	//System.out.println("Setting count for " + typenum + " " + tmpcount + " " + maxcounts.size());
	  
	maxcounts.setElementAt(tmpcount,typenum);
	  
	k++;
      }
    }
    System.out.println("Max count " + maxcount);

  }
  
    public void paint(Graphics g) {

	int nchr        = chromosomes.size();
	chr_per_row = nchr/rows;

	// Increase the chr_per_row to include stragglers

	while (chr_per_row*rows  < nchr) {
	  chr_per_row++;
	  chr_per_row++;
	}

	height          = getSize().height / rows;
	width           = (int)(getSize().width/chr_per_row);
	bases_per_pixel = (int)(maxlen/(height-60));//(int)(maxlen/(0.8 * height));

	g.setColor(Color.white);
	g.fillRect(0,0,getSize().width,getSize().height);


	for (int i = 0; i < chromosomes.size(); i++) {

	    Chromosome chr = (Chromosome)chromosomes.elementAt(i);
	    
	    Hashtable  fh  = chr.featureDensity(chr.getBinSize());

	    int n = fh.size();

	    if (colors == null) {
	      make_colors(n);
	    }
	}
	
	for (int i = 0; i < chromosomes.size(); i++) {

	    Chromosome chr = (Chromosome)chromosomes.elementAt(i);

	    int xstart = (i%chr_per_row) * width;
	    int ystart = i/chr_per_row * height;

	    render_chromosome(g,chr,xstart,ystart,width,height,bases_per_pixel,10);

	}
	
	// Now draw the legend

	int xcoord = size().width - 20;
	int ycoord = size().height - 5;

	for (int i = 0; i < types.size(); i++) {

	  String type = (String)types.elementAt(i);
	  Color  col  = Color.gray;

	  if (colors.size() > i) {
	    col = (Color)colors.elementAt(i);
	  }
	  FontMetrics metrics = getFontMetrics(g.getFont());
	  int strwidth        = metrics.stringWidth(type);

	  xcoord -= 12;
	  xcoord -= strwidth;

	  g.setColor(col);
	  g.fillRect(xcoord,ycoord - 10,10,10);

	  g.setColor(Color.black);
	  g.drawString(type,xcoord+12,ycoord);

	  xcoord -= 15;
	}
    }

    private void render_chromosome(Graphics g, Chromosome chr, int xstart, int ystart, int width, int height, int bases_per_pixel, int percent_width) {

      g.setColor(Color.black);

      yoffset  = ystart + 30;//(int)(0.1 * height);
      
      int xpos     = xstart + 3*width/4;
      
      int chrwidth = percent_width * width/100;

      int chrheight = chr.getLength()/bases_per_pixel;
 

      Vector bands = chr.getBands();
      int    tmp   = yoffset;

      if (bands != null) {
	for (int i = 0; i < bands.size(); i++) {
	  CytoBand band = (CytoBand)bands.elementAt(i);

	  int start = band.getStart()/bases_per_pixel;

	  int end   = band.getEnd()/bases_per_pixel;

	  g.setColor(band.getColor());

	  if (band.getStain().equals("acen")) {
	    g.fillRect(xpos - chrwidth/2+2,yoffset + start,chrwidth,end-start+1);      
	  } else {
	    g.fillRect(xpos - chrwidth/2+2,yoffset + start,chrwidth,end-start+1);      
	  }
	}
      } else {
	g.setColor(Color.black);
	g.fillRect(xpos - chrwidth/2,yoffset,chrwidth,chrheight);      
      }
      // Draw the name of the chromosome
      g.setColor(Color.black);
      g.drawString(chr.getName(),xpos - 15,(int)(ystart + 20));//0.07*height));

      // Now the feature density

      int maxbin      = chr.getLength()/chr.getBinSize() + 1;

      Hashtable fh    = chr.featureDensity(chr.getBinSize());

      xpos = xstart + 3*width/4 - chrwidth/2;

      if (types.size() == 0) {
           return;
      }
      int fwidth = (int)(0.7*getWidth()/(chr_per_row*types.size()));

      
      
      for (int j = 0 ; j < types.size(); j++) {
	String type  = (String)types.elementAt(j);
	Vector fd    = (Vector)fh.get(type);

	//System.out.println("Type " + j + " " + type);

	if (colors.size() < j) {
	  while (colors.size()-1< j) {
	    Color tmpc = new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
	    colors.addElement(tmpc);
	  }
	}
	if (fd != null) {
	  int tmpmaxcount;
	  if (linked_maxcount) {
	    tmpmaxcount = maxcount;
	  } else {
	    tmpmaxcount = (Integer) maxcounts.elementAt(j);
	  }

	  g.setColor(Color.gray);
	  g.drawLine(xpos,yoffset,xpos,yoffset+chrheight);
	  if (colors.size() > j) {
	    g.setColor((Color)colors.elementAt(j));
	  }

	  Vector selbin = getSelectedBins(chr);

	  for (int i = 0; i < maxbin; i ++) {
	      FeatureBin fbin = (FeatureBin)fd.elementAt(i);
	      int num = fbin.getSize();

	      //	    int num = (Integer) fd.elementAt(i);
	    
	    int denswidth = 0;
	    
	    
	    if (num > 0) {
 	      denswidth = num*fwidth/tmpmaxcount +1;
	    }
	    
	    int ypos = yoffset + ( i * chr.getBinSize())/bases_per_pixel;
	    
	    int yheight = chr.getBinSize()/bases_per_pixel+1;
	    if (yheight < 1) {
	      yheight = 1;
	    }
	    
	    if (selbin.contains(fbin)) {
		g.setColor(Color.red);
	    }
	    g.fillRect(xpos - denswidth-1,ypos,denswidth+1,yheight);
	   
	    if (colors.size() > j) {
	      g.setColor((Color)colors.elementAt(j));
	    }
	  }
	}
	xpos -= (fwidth + 2);
      }

      
    }
      
    public Vector getSelectedBins(Chromosome chr) {
	Vector selbin = new Vector();

	if (fsel != null) {
	  for (int i = 0 ; i < fsel.getFeatures().size(); i++) {
	    
	    SequenceFeature sf = (SequenceFeature)fsel.getFeatures().elementAt(i);
	    
	    int bin     = (int)sf.getStart()/chr.getBinSize();
	    String type = sf.getType();
	    
	    if (chr.getName().equals(sf.getId())) {
	      
	      Hashtable hash = chr.featureDensity(chr.getBinSize());
	      Vector    fdens = (Vector)hash.get(type);
	      
	      selbin.addElement(fdens.elementAt(bin));
	    }
	  }
	}
	return selbin;
    }
    public static void main(String[] args) {


      try {

	
	FileParse file = new FileParse(args[0],"File");
	CytoBandFile cbf = new CytoBandFile(args[1],"File");

	Hashtable bands = cbf.getBands();


	Vector chromosomes = new Vector();
	
	String line;
	
        boolean dens = false;

	if (args.length > 3) {
	  if (args[3].equals("-density")) {
	    dens = true;
	  }
	}
	  while ((line = file.nextLine()) != null) {
	    StringTokenizer str = new StringTokenizer(line,"\t" );
	    
	    String name = str.nextToken();
	    int    len  = Integer.parseInt(str.nextToken());
	    
	    if (name.indexOf("_random") == -1 &&
		name.indexOf("_NT_") == -1) {

	      System.out.println("Length of " + name + " is " + len);

	      Chromosome chr = new Chromosome(len,(Vector)bands.get(name),name);
	      
	      chromosomes.addElement(chr);
	    }
	  }


	  GFFFile gfffile = new GFFFile(args[2],"File");

	  
	  Vector  feat    = gfffile.getFeatures();

	  System.out.println("Features " +  feat.size());

	  for (int i = 0 ; i < chromosomes.size(); i++) {
	    Chromosome chr = (Chromosome)chromosomes.elementAt(i);

	    if (feat.size() > 0) {
		chr.setDensity(dens); 
		chr.addFeatures(feat,true);
		chr.featureDensity(100000);
	    } else {
		chromosomes.removeElement(chr);
	    }
	  }


	  JFrame f          = new JFrame();
	  KaryotypePanel kp = new KaryotypePanel(chromosomes);
	  
	  Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
		    
	  f.setLocation(sd.width / 2 - 700 / 2,
			sd.height / 2 - 500 / 2);
		    
	  f.setTitle("Karyotype panel - " + args[0] + " features " + args[1]);
	  f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

	  f.getContentPane().add(kp);
	  f.setSize(700,500);
	  
	  f.setVisible(true);
          
      } catch (IOException e) {
	System.out.println("Exception reading file " + e);
      }


      
    }

  
  private void  make_colors(int n) {

    int i;
    colors = new Vector();

    colors.addElement(Color.darkGray);
    colors.addElement(Color.gray);
    //colors.addElement(new Color(50,250,50));

    i = 2;
    while (i < n) {

      colors.addElement(new Color((int)(Math.random()*255),
				  (int)(Math.random()*255),
				  (int)(Math.random()*255)));
      i++;

    }
  }

    public Chromosome getChrFromCoords(int x, int y) {

	// First find row

	int tmprow = 0;

	if (y > (int)(size().height/2)) {
	    tmprow = 1;
	}

	// Now find the chromosome in the row

	int tmpcol = (int)(x/width);

	System.out.println("Row/col " + tmprow + " " + tmpcol);

	int chrnum = tmprow * chr_per_row + tmpcol;

	return (Chromosome)chromosomes.elementAt(chrnum);

    }
    public void mouseClicked (MouseEvent e) {
	int x = e.getX();
	int y = e.getY();

        int prevHeight = getHeight();
        
        System.out.println("HEights " + prefHeight + " " + prevHeight + " " + jsp.getViewport().getViewPosition().y);
        prefHeight = getHeight();
        
	System.out.println("X / Y " + x + " " + y);

	if (e.isControlDown() == true) {
	    if (SwingUtilities.isRightMouseButton(e)) {
	
                prefHeight = (int)(prefHeight*0.75);

		int binsize = ((Chromosome)chromosomes.elementAt(0)).getBinSize();

		if (binsize < bases_per_pixel) {
		    for (int i = 0 ; i < chromosomes.size(); i++) {
			Chromosome chr = (Chromosome)chromosomes.elementAt(i);
			int bs = chr.getBinSize();
			bs = (int)(bases_per_pixel);
			chr.featureDensity(bs);
		    }
		}
		setMaxCount();

	    } else {
		prefHeight = (int)(prefHeight*1.25);

		int binsize = ((Chromosome)chromosomes.elementAt(0)).getBinSize();

		if (binsize > bases_per_pixel) {
		    for (int i = 0 ; i < chromosomes.size(); i++) {
			Chromosome chr = (Chromosome)chromosomes.elementAt(i);
			int bs = chr.getBinSize();
			bs = (int)(bases_per_pixel);
			chr.featureDensity(bs);
		    }
		}
		setMaxCount();
            }
            
            // Set the viewport coords to keep the mouse position at the same point.
        
            setVisible(false);
            if (jsp != null) {
                int viewx = jsp.getViewport().getViewPosition().x;
                int viewy = jsp.getViewport().getViewPosition().y;
                
                int offset = y*(prefHeight-prevHeight)/prevHeight;
                
                System.out.println("Offset " + offset);
                
                
                jsp.getViewport().setViewPosition(new Point(viewx,viewy + offset));
                System.out.println("HEights " + prefHeight + " " + prevHeight + " " + viewy + " " + jsp.getViewport().getViewPosition().y);

            }
            
	    // These are needed to get the damn thing to redraw at the different size
	    //getTopLevelAncestor().invalidate();
            //   invalidate();

	    getTopLevelAncestor().validate();
	    validate();

	    repaint();
            setVisible(true);
	} else {
	    Chromosome tmpchr = getChrFromCoords(x,y);

	    System.out.println("Chr " + tmpchr.getName());

	    int tmpbase;

	    if (y > size().height/2) {
		y = y - size().height/2;
	    }

	    y -= 30;// (yoffset)(int)(0.1*height);

	System.out.println("New coord " + y + " " + bases_per_pixel + " " + size().height/2);
	
        tmpbase = y * bases_per_pixel;

	int len = tmpchr.getLength();

        
        if (SwingUtilities.isRightMouseButton(e)) {
            if (jsp != null) {
                        
               System.out.println("Chr " + tmpchr.getName() + " base " + tmpbase);       
               GeneInfoFile gif = GenomeInfoFactory.getGeneInfo(tmpchr.getName(),tmpbase - 1000000, tmpbase + 1000000);
               gif.parse();
               Hashtable h = gif.getGeneHash();
               
               Enumeration en = h.keys();



               while (en.hasMoreElements()) {
                   String key = (String)en.nextElement();

		   Hashtable tmphash = (Hashtable)h.get(key);

		   String name  = key;
		   String pfam  = "";

		   if (tmphash.containsKey("name")) {
		     Vector tmp = (Vector)tmphash.get("name");

		     for (int i = 0; i < tmp.size(); i++) {
		       String tmpname = (String)tmp.elementAt(i);
		       if (name.indexOf(tmpname) < 0) {
			 name = name + " " + tmpname;
		       }
		     }
		   }
		   if (tmphash.containsKey("pfam")) {
		     Vector tmp = (Vector)tmphash.get("pfam");

		     for (int i = 0; i < tmp.size(); i++) {
		       String tmppfam = (String)tmp.elementAt(i);
		       pfam = pfam + " " + tmppfam;

		     }
		   }
		   System.out.println(name + "\t" + pfam);
               }
            
            }
        } else {
        if (tmpbase > len) {
	    tmpbase = len;
	}
	if (tmpbase < 1) {
	    tmpbase = 1;
	}

	System.out.println("Base is " + tmpbase);

	chrname  = tmpchr.getName();
	chrstart = (int)(tmpbase-2*bases_per_pixel);
	chrend   = (int)(tmpbase+2*bases_per_pixel);

        if (chrstart < 1 ) {chrstart = 1;}
        if (chrend   > len){chrend   = len;}

        fsel.removeAllFeatures();

	Vector sel = new Vector();
        Vector lowsel = new Vector();
        
        for (int i =0; i < tmpchr.getFeatures().size(); i++) {
          SequenceFeature sf = (SequenceFeature)tmpchr.getFeatures().elementAt(i);
          
          if (!((sf.getStart()-2*bases_per_pixel) > tmpbase ||
                (sf.getEnd()+2*bases_per_pixel)   < tmpbase)) {
	       if (sf.getType().equals("gene")) {
                  sel.addElement(sf);
              } else {
                  lowsel.addElement(sf);
              }
          }
        }

        fsel.addFeatures(sel);
        fsel.addFeatures(lowsel);
	//r = new GetFeatureThread(chrname,String.valueOf(chrstart),String.valueOf(chrend),this,true,false,false);
	//r.run();
	}
        }
    }

    public void actionPerformed(ActionEvent e) {
	if (e.getActionCommand().equals("Done")) {
	    Vector feat = r.getOutput();

	    Sequence[] s = new Sequence[feat.size()+2];
            
	    for (int i = 0; i < feat.size(); i++) {
	
	      s[i] = (Sequence)feat.elementAt(i);
	    }
            
	    Alignment  al = new Alignment(s);
	    
	    JPanel jp = GenomeInfoFactory.makePanel(al,chrname + "." + chrstart + "-" + chrend,.009, 10,chrstart,chrstart,chrend,850);
	    JFrame jf = new JFrame(chrname + "." + chrstart + "-" + chrend);

	    jf.getContentPane().add(jp);

	    Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
	    
	    jf.setLocation(sd.width / 2 - 1000 / 2,
			  sd.height / 2 - 700 / 2);
		    
	    jf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

	    jf.setSize(1000,700);
	    jf.setVisible(true);
	} else {
	    //System.out.println("Command " + e.getActionCommand());
	}
    }
    public Dimension getPreferredSize() {
	return new Dimension(prefWidth,prefHeight);
    }
    public void mousePressed (MouseEvent e) {
          if (SwingUtilities.isMiddleMouseButton(e)) {

	    Container parent = getParent();
	    Container prevparent = this;

	    while (! (parent instanceof JFrame)) {
	      prevparent = parent;
	      parent = parent.getParent();
	      System.out.println("Parent "  + " " + parent.getLocation().getY() + " " + parent);
	    }
	    JFrame testframe = new JFrame();
	    testframe.setUndecorated(true);
	    testframe.setSize(200,200);
	    testframe.setLocation((int)(parent.getLocation().getX() + e.getX() + 10),(int)(parent.getLocation().getY() + e.getY() + prevparent.getLocation().getY() + 5));
	    ((JPanel)(testframe.getContentPane())).setOpaque(true);
	    //((JPanel)(testframe.getContentPane())).setBackground(new Color(255,255,255,0));
	    testframe.setVisible(true);
	  }
    }
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered (MouseEvent e) {}
    public void mouseExited  (MouseEvent e) {}
}
