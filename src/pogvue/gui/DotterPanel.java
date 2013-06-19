package pogvue.gui;

import pogvue.datamodel.SequenceFeature;
import pogvue.io.GFFFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


public final class DotterPanel extends JPanel implements MouseListener, KeyListener {

    private final Vector fset;

    private final Vector xfset;
    private final Vector yfset;

    private int        minx = 300000000;
    private int        miny = 300000000;

  
    private int        maxx = -1;
    private int        maxy = -1;
  
  private int origxstart;
  private int origxend;
  private int origystart;
  private int origyend;

    private double      xBasesPerPixel;
    private double      yBasesPerPixel;

    private int xborder;
    private int yborder;

    private int tmpxstartbase = -1;
    private int tmpystartbase = -1;
    private int tmpxendbase = -1;
    private int tmpyendbase = -1;

    private int xstartbase = -1;
    private int ystartbase = -1;

    private int xendbase   = -1;
    private int yendbase   = -1;

  private int xtick = -1;
  private int ytick = -1;

    private final Color repeat = new Color(255,255,205);
    private final Color gene   = new Color(255,205,205);

    private DotterPanel(Vector fset,Vector xfset,Vector yfset) {
	this.fset = fset;

	this.xfset = xfset;
	this.yfset = yfset;

	panelInit();

    }
  private void setXtick(int xtick) {
    this.xtick = xtick;
  }
  private void setYtick(int ytick) {
    this.ytick = ytick;
  }
    private void setXstart(int x) {
        minx = x;
	origxstart = x;
    }
    private void setXend(int x) {
        maxx = x;
	origxend = x;
    }
    private void setYstart(int x) {
        miny = x;
	origystart = x;
    }
    private void setYend(int x) {
        maxy = x;
	origyend = x;
    }
    private void panelInit() {

	for (int i = 0 ; i <fset.size(); i++) {

	    if (fset.elementAt(i) instanceof SequenceFeature) {
	      SequenceFeature fp  = (SequenceFeature)fset.elementAt(i);

		System.out.println("Feature pair " + fp.getStart());

		if (fp.getHitFeature() != null) {
		  if (fp.getStart() < minx) {
		    minx = fp.getStart();
		  }
		  if (fp.getHitFeature().getStart() < miny) {
		    miny = fp.getHitFeature().getStart();
		  }
		  if (fp.getEnd() > maxx) {
		    maxx = fp.getEnd();
		  }
		  if (fp.getHitFeature().getEnd() > maxy) {
		    maxy = fp.getHitFeature().getEnd();
		  }
		}
			
	    }
	}

	addMouseListener(this);
	addKeyListener(this);
    }

    public void paint(Graphics g) {
      requestFocusInWindow();

	int xsize = size().width;
	int ysize = size().height;

	System.out.println("Sizes are " + xsize + " " + ysize);

	xstartbase = minx;
	xendbase   = maxx;
	ystartbase = miny;
	yendbase   = maxy;

	xborder = (int)(xsize*0.2/2);
	yborder = (int)(ysize*0.2/2);

	int xstart = xborder;
	int ystart = yborder;

	int xdrawsize = xsize - 2*xborder;
	int ydrawsize = ysize - 2*yborder;

	xBasesPerPixel = 1.0*(xendbase - xstartbase + 1)/(1.0*xdrawsize);
	yBasesPerPixel = 1.0*(yendbase - ystartbase + 1)/(1.0*ydrawsize);

	g.setColor(Color.white);
	g.fillRect(0,0,xsize,ysize);
	
	g.setColor(Color.black);

	g.drawRect(xstart,ystart,xdrawsize,ydrawsize);

	for (int i = 0; i < yfset.size(); i++) {

	    SequenceFeature sf = (SequenceFeature)yfset.elementAt(i);

	    if (sf.getType().equals("ensembl")) {
	      g.setColor(gene);
	    } else {
	      g.setColor(repeat);
            }

	    int y1 = ystart + (int)((sf.getStart()  - ystartbase + 1)/yBasesPerPixel);
	    int y2 = ystart + (int)((sf.getEnd()    - ystartbase + 1)/yBasesPerPixel);
	    
	    g.fillRect(xstart + 1,y1,xsize - 2*xstart - 1,y2-y1);
	    
	}

	for (int i = 0; i < xfset.size(); i++) {
	    g.setColor(repeat);

	    SequenceFeature sf = (SequenceFeature)xfset.elementAt(i);

	    if (sf.getHitFeature() != null) {
	      System.out.println("Name " + sf.getHitFeature().getId());
	
		String name = sf.getHitFeature().getId();

		if (name.indexOf("Alu") == 0 ||
		    name.indexOf("L1P") == 0 ||
		    name.indexOf("MER") == 0) {
		    g.setColor(Color.lightGray);
		} else {
		    g.setColor(repeat);
		}
	   
	    } else if (sf.getType().equals("ensembl")) {
		g.setColor(gene);
	    }
	    
	    int x1 = xstart + (int)((sf.getStart() - xstartbase+1)/xBasesPerPixel);
	    int x2 = xstart + (int)((sf.getEnd()- xstartbase+1)/xBasesPerPixel);

	    g.fillRect(x1,ystart+1,x2 - x1+1,ysize - 2*ystart - 1);
	    
	}

	for (int i = 0; i < fset.size(); i++) {
	    g.setColor(Color.black);

	    if (fset.elementAt(i) instanceof SequenceFeature) {
	      SequenceFeature fp  = (SequenceFeature)fset.elementAt(i);

	      if (fp.getHitFeature() != null) {
                int ratio = (int)(fp.getScore()/(fp.getEnd()-fp.getStart()+1));
                if (ratio > 60) {
                  ratio = 60;
                }
                //System.out.println("Ratio " + ratio + " " + (double)(ratio/60.0));
                Color mix = mixColor(Color.red,Color.green, ratio/60.0);
                
                g.setColor(mix);
		
		int x1 = xstart + (int)((fp.getStart() - xstartbase + 1)/xBasesPerPixel);
		int y1 = ystart + (int)((fp.getHitFeature().getStart()- ystartbase +1)/yBasesPerPixel);
		
		int x2 = xstart + (int)((fp.getEnd()- xstartbase +1)/xBasesPerPixel);
		int y2 = ystart + (int)((fp.getHitFeature().getEnd()- ystartbase +1)/yBasesPerPixel);
		
		if (fp.getHitFeature().getStrand() == 1) {
		  g.drawLine(x1,y1,x2+1,y2+1);
		} else {
		  g.drawLine(x1,y2,x2+1,y1+1);
		}
	      }
	    }
	}

	
	g.setColor(Color.white);

	g.fillRect(0,0,xsize,yborder-1);
	g.fillRect(0,ysize-yborder+1,xsize,yborder-1);
	g.fillRect(0,yborder-1,xborder,ysize - 2*yborder);
	g.fillRect(xsize-xborder+1,yborder-1,xborder-1,ysize - 2*yborder);

	g.setColor(Color.black);
	g.setFont(new Font("Helvetica",Font.PLAIN,10));

	FontMetrics fm = g.getFontMetrics(g.getFont());

	int i = 0;

	int xpos = xstart-10;
	int ypos = ystart+10;

	
	while (i < 11) {

	  g.drawString(String.valueOf(minx + xtick*i),xstart - 10 + i*xdrawsize/10,ystart-1);

	  g.drawString(String.valueOf(miny + ytick*i),xstart - 10 - fm.stringWidth(String.valueOf(maxy)),ystart + 10 + ydrawsize*i/10);

	  i++;
	}

    }

    public void keyPressed(KeyEvent evt) {
	int key = evt.getKeyChar();

	if (evt.getKeyCode() == KeyEvent.VK_UP) {
	    xstartbase = xstartbase  - (int)((xendbase - xstartbase)*0.5);
	    xendbase   = xendbase    + (int)((xendbase - xstartbase)*0.5);

	    ystartbase = ystartbase  - (int)((yendbase - ystartbase)*0.5);
	    yendbase   = yendbase    + (int)((yendbase - ystartbase)*0.5);


	    if (xstartbase < minx) {xstartbase = minx;}
	    if (xendbase   > maxx) {xendbase   = maxx;}
	    if (ystartbase < miny) {ystartbase = miny;}
	    if (yendbase   < maxy) {yendbase   = maxy;}

	    repaint();
	} else	if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
	    xstartbase = xstartbase  + (int)((xendbase - xstartbase)*0.5);
	    xendbase   = xendbase    - (int)((xendbase - xstartbase)*0.5);

	    ystartbase = ystartbase  + (int)((yendbase - ystartbase)*0.5);
	    yendbase   = yendbase    - (int)((yendbase - ystartbase)*0.5);


	    if (xstartbase < minx) {xstartbase = minx;}
	    if (xendbase   > maxx) {xendbase   = maxx;}
	    if (ystartbase < miny) {ystartbase = miny;}
	    if (yendbase   < maxy) {yendbase   = maxy;}

	    repaint();
	} else if (evt.getKeyChar() == 'r') {

	  minx = origxstart;
	  maxx = origxend;
	  miny = origystart;
	  maxy = origyend;
	  
	  repaint();
	}
    }	    

    public void keyReleased(KeyEvent evt) {}

    public void keyTyped(KeyEvent evt) {
    }
    public void mousePressed(MouseEvent evt) {

	int x = evt.getX();
	int y = evt.getY();
	
	System.out.println("Pressed Coords are " + x + " " + y);

	x = x - xborder;
	y = y - yborder;

	tmpxstartbase = xstartbase + (int)(x*xBasesPerPixel);
	tmpystartbase = ystartbase + (int)(y*yBasesPerPixel);

	System.out.println("Pressed " + tmpxstartbase + " " + tmpystartbase);

    }

    public void mouseReleased(MouseEvent evt) {
	int x = evt.getX();
	int y = evt.getY();
	
	System.out.println("Release Coords are " + x + " " + y);

	x = x - xborder;
	y = y - yborder;

	tmpxendbase = xstartbase + (int)(x*xBasesPerPixel);
	tmpyendbase = ystartbase + (int)(y*yBasesPerPixel);

	System.out.println("Pressed " + tmpxendbase + " " + tmpyendbase);

	minx = tmpxstartbase;
	maxx = tmpxendbase;
	miny = tmpystartbase;
	maxy = tmpyendbase;

	repaint();
    }
    public void mouseExited(MouseEvent evt) {}
    public void mouseEntered(MouseEvent evt) { }
    public void mouseClicked(MouseEvent evt) { }

    public static void main(String[] args) {

	try {
	  GFFFile gff = new GFFFile(args[0],"File");
	  GFFFile gffx = new GFFFile(args[1],"File");
	  GFFFile gffy = new GFFFile(args[2],"File");

	  Vector fset = gff.getFeatures();


	  int minx = 300000000;
	  int maxx = -1;
	  
	  int miny = 300000000;
	  int maxy = -1;
	  
	  Hashtable hash = new Hashtable();

	  for (int i = 0; i < fset.size(); i++) {
	    String name = ((SequenceFeature)fset.elementAt(i)).getId();
	    
	    if (hash.get(name)== null) {
	      Vector tmp = new Vector();
	      hash.put(name,tmp);
	    }

	    SequenceFeature sf = (SequenceFeature)fset.elementAt(i);

	    if (sf.getHitFeature() != null) {
	      if (sf.getStart() < minx) {
		minx = sf.getStart();
	      }
	      if (sf.getEnd() > maxx) {
		maxx = sf.getEnd();
	      }

	      if (sf.getHitFeature().getStart() < miny) {
		miny = sf.getHitFeature().getStart();
	      }
	      if (sf.getHitFeature().getEnd() > maxy) {
		maxy = sf.getHitFeature().getEnd();
	      }
	    }

	    Vector tmp = (Vector)hash.get(name);
	    tmp.addElement(fset.elementAt(i));
	  }

	  System.out.println("Min/max x " + minx + " " + maxx);
	  System.out.println("Min/max y " + miny + " " + maxy);


	  double roughtick = (maxx-minx)/10;
  
	  int   digits  = (int)(Math.log(roughtick)/Math.log(10));
	  int   num     = (int)(roughtick/Math.pow(10,digits));

	  if (Math.abs(10-num) < 3) {
	    num = 10;
	  } else if (Math.abs(5-num) <= 2) {
	    num = 5;
	  } else {
	    num = 2;
	  }

	  int xtick = (int)(num * Math.pow(10,digits));

	  roughtick = (maxy-miny)/10;
  
	  digits  = (int)(Math.log(roughtick)/Math.log(10));
	  num     = (int)(roughtick/Math.pow(10,digits));

	  if (Math.abs(10-num) < 3) {
	    num = 10;
	  } else if (Math.abs(5-num) <= 2) {
	    num = 5;
	  } else {
	    num = 2;
	  }

	  int ytick = (int)(num * Math.pow(10,digits));

	  System.out.println("Ticks are " + xtick + " " + ytick);

	  int xstart = minx  - minx % xtick;
	  int ystart = miny  - miny % ytick;

	  int xend   = maxx  - maxx % xtick + xtick;
	  int yend   = maxy  - maxy % ytick + ytick;
	  

	  System.out.println("Start/end " + xstart + " " + xend + " " + ystart + " " + yend);

			     
	  Vector xfset = gffx.getFeatures();
	  Vector yfset = gffy.getFeatures();

	  JFrame       f  = new JFrame(args[0]);



	  Enumeration en = hash.keys();

	  if (hash.size() > 1) {
	    f.setLayout(new GridLayout(hash.size(), hash.size()/3));
	  } 
	  while (en.hasMoreElements()) {
	    String name = (String)en.nextElement();

	    Vector tmpfset = (Vector)hash.get(name);
	    DotterPanel dp = new DotterPanel(tmpfset,xfset,yfset);
	    dp.setXstart(xstart);
	    dp.setXend(xend);
	    dp.setYstart(ystart);
	    dp.setYend(yend);
	    dp.setXtick(xtick);
	    dp.setYtick(ytick);
	    f.add(dp);
	    dp.requestFocusInWindow(); 
	  }
	  
	  f.setSize(1500,1500);

	  f.show();
	} catch (IOException e) {
	  System.out.println("Exception " + e);
	}
    }
  
  private Color mixColor(Color from, Color to, double amount) {
    float x = (float) amount;
    float y = (float) (1.0 - amount);
    
    return new Color((int) (y * from.getRed() + x * to.getRed()),
		     (int) (y * from.getGreen() + x * to.getGreen()),
		     (int) (y * from.getBlue() + x * to.getBlue()));
  }
}





















