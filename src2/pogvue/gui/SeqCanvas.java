package pogvue.gui;

import pogvue.gui.renderer.TrackRenderer;
import pogvue.gui.schemes.*;

import pogvue.io.*;
import pogvue.gui.hub.*;
import pogvue.analysis.*;
import pogvue.datamodel.*;
import pogvue.gui.event.*;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import java.awt.font.*;
import java.awt.geom.*;

public class SeqCanvas extends ControlledCanvas implements AlignViewportListener {

  public  Graphics2D        gg;
  private Image             img;
  private int               imgWidth;
  private int               imgHeight;
  
  private final AlignViewport     av;
  
  final int pady = 2;
  
  private int oldstartx;
  private int oldstarty;

  private int oldendx;
  private int oldendy;
  
  private boolean paintFlag = false;
  
  private SeqCanvasListener scl;

    private Image[][] images = null;
    
    private Color[] colors;
    private char[] base;


  public SeqCanvas(AlignViewport av,Controller c) {
    this.av         = av;
    
    scl = new SeqCanvasListener(this,av,c);

    addKeyListener(scl);
    addMouseMotionListener(scl);
    addMouseListener(scl);

    setOpaque(false);

    setController(c);

    colors = new Color[4];

    colors[0] = Color.green.darker();
    colors[1] = Color.red.darker();
    colors[2] = Color.blue;
    colors[3] = Color.yellow.darker();

    base = new char[4];

    base[0] = 'A';
    base[1] = 'T';
    base[2] = 'C';
    base[3] = 'G';


  }
  public boolean handleAlignViewportEvent(AlignViewportEvent e) {

    if (e.getAlignViewport() == av) {

  	paintFlag = e.getType() == AlignViewportEvent.COLOURING ||
  	e.getType() == AlignViewportEvent.DELETE ||
  	e.getType() == AlignViewportEvent.ORDER ||
  	e.getType() == AlignViewportEvent.SHOW ||
  	e.getType() == AlignViewportEvent.WRAP;
      
  	if (e.getType() == AlignViewportEvent.RESHAPE ||
  			e.getType() == AlignViewportEvent.COLOURING) {
  		paintFlag = true;
  	}
  	if (scl.shiftPressed == false) {
  		paintComponent(this.getGraphics());
  	}
    }
    return true;
  }
  
    public void createImages(int charwidth, int charheight) {
	images = new Image[4][5];



	int j = 0;
	
	while (j < 5) {


	    int i = 0;
	    
	    while (i < 4) {
		int        imgheight  = (int)((j+1)*charheight/5);
		Image      img        = createImage(charwidth,imgheight);
		System.out.println("Image " + img + " " + charwidth + " " + imgheight);
		Graphics2D gg         = (Graphics2D)img.getGraphics();
		
		FontRenderContext frc = gg.getFontRenderContext();

		// Scale the font to the right width and height

		Font font = new Font("Helvetica", Font.BOLD, charwidth);
		TextLayout tstring = new TextLayout("A", font, frc);

		System.out.println("Height " + tstring.getBounds().getHeight());		
		float theight = (float) tstring.getBounds().getHeight();
		float twidth  = (float) tstring.getBounds().getWidth();

		Font charfont = scaleFont(font, (float) 1, (float)(1+j)*imgheight/5);

		String s = String.valueOf(base[i]);


		TextLayout tstring2 = new TextLayout(s, charfont, frc);

		System.out.println("Height " + tstring2.getBounds().getHeight());		
		gg.setColor(colors[i]);
		tstring2.draw(gg, 0,10);

		//float yoff = (float) tstring.getBounds().getHeight();

		//drawLetter(gg, s, charfont, colors[i],frc,0,charheight);

		images[i][j] = img;

		i++;
	    }
	    j++;
	}
	av.setImages(images);
    }

    public Font scaleFont(Font font, float xval, float yval) {
	AffineTransform ascale = getScaleTransform(xval, yval);

	Font newfont = font.deriveFont(ascale);

	return newfont;
    }

    public AffineTransform getScaleTransform(float xscale, float yscale) {
	AffineTransform ascale = new AffineTransform();

	ascale.scale(xscale, yscale);

	return ascale;
    }


  public void paintComponent(Graphics g) {
  	// What should this be?
  	// TrackFeatureSet
  	
      if (images == null) {
	  
	//createImages(10,av.getCharHeight());
      }
  	Alignment da = av.getAlignment();
    
  	if (img == null ||
  			imgWidth  != size().width  ||
  			imgHeight != size().height ||
  			paintFlag == true) {
  		
  		imgWidth  = (size().width  > 0 ? size().width  : 1);
  		imgHeight = (size().height > 0 ? size().height : 1);
  		
  		img = createImage(imgWidth,imgHeight);
  		gg  = (Graphics2D)img.getGraphics();
  		
  		gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
  		RenderingHints.VALUE_ANTIALIAS_ON);
  		
  		av.setFont(g,av.getFont());
  		av.updateRegion(imgWidth, imgHeight);
  		
  		gg.setFont(av.getFont());
  		
  		paintFlag = false;
  		
  		oldstartx = -1;
  		oldendx   = -1;
  		
  		oldstarty = -1;
  		oldendy   = -1;
  		
  	}
  	
  	if (av.useImage() == false) {
  		oldstartx = -1;
  		oldendx   = -1;
  		
  		oldstarty = -1;
  		oldendy   = -1;
  		
  		gg = (Graphics2D)g;
  	}
    
  	// So I should be able to generalize this.
  	// We need the scrollbars to be in coordinates (bases and sequences)
  	// Then all the calculations just need to be relative to scrollbars.
  	
    int startx = av.getStartRes();
    int starty = av.getStartSeq();
    
    int endx   = av.getEndRes();
    int endy   = av.getEndSeq();
    
    if (startx <= 1 ) {
      av.setStartRes(1);
      startx = 1;
      endx   = av.getEndRes()-av.getStartRes() + 1;
    }

    double charWidth     = av.getCharWidth();
    int    charHeight    = av.getCharHeight();
    
    int offy = av.getStartSeq();

    if (oldendx == -1) {
      
      // Full redraw

    	gg.setColor(Color.white);
      gg.fillRect(0,0,size().width,size().height);
            
      drawPanel(gg,startx,endx,starty,endy+1,startx,starty,0);
        
      oldstartx = startx;
      oldendx   = endx;
      oldstarty = starty;
      oldendy   = endy;
        
    }  else if (oldstartx < startx) {
      // This is dragging horizontal scrollbar to the right
      //
      //      |  old region view  ; new region view
      //
      //      ----------------------------------------------
      //           |    ;              |     ;
      //           |    ;              |     ;
      //           |    ;              |     ;
      //      ----------------------------------------------
      //           ^    ^              ^     ^
      //            delx   <-delx2    
      //         osx   sx             oex   ex
      //
      // Copy the delx2 region -delx to the left and draw the (ex-oex) strip
    	// If sx > oex - draw the whole thing
    	//

      //System.out.println("Char width " + charWidth);
      int delx  = (int)((startx - oldstartx) * charWidth);
      int delx2 = (int)((oldendx - startx +1 )   * charWidth);
      
      gg.copyArea(delx,0,delx2,av.getPixelHeight(starty,endy+1,charHeight),-delx,0);
      
      if (startx > oldendx) {
      	drawPanel(gg,startx,endx,starty,endy+1,startx,starty,0);
      } else {
      	// Over draw one residue to the left to cover droppings from the copy
      	drawPanel(gg,oldendx,endx,starty,endy+1,startx,starty,0);
      }
      
      oldstartx = startx;
      oldendx   = endx;
      
      

    } else if (oldstartx > startx) {

      // This is dragging horizontal scrollbar to the left
      //
      //      |  old region view  ; new region view
      //
      //      ----------------------------------------------
      //           ;    |              ;     |
      //           ;    |              ;     |
      //           ;    |              ;     |
      //      ----------------------------------------------
      //           ^    ^              ^     ^
      //            delx      delx2->  
      //         sx    osx             ex   oex
      
      int delx  = (int)((oldstartx - startx) * charWidth);
      int delx2 = (int)((endx - oldstartx + 1)   * charWidth);
      
      gg.copyArea(0,0,delx2,av.getPixelHeight(starty,endy+1,charHeight),delx,0);
      
      if (oldstartx > endx) {
        drawPanel(gg,startx,endx,starty,endy,startx,starty,0);
      } else {
        drawPanel(gg,startx,oldstartx-1,starty,endy,startx,starty,0);
      }
      
      oldstartx = startx;
      oldendx   = endx;
      
    }  else if (oldstarty < starty) {
      
      // Moving vertical scrollbar down
      //
      //  ---------------------- osy
      //                               dely
      //  ====================== sy
      //
      //                               dely2
      //
      //  ---------------------- oey
      //
      //  ====================== ey

      int dely  = av.getPixelHeight(oldstarty,starty,charHeight);
      int dely2 = av.getPixelHeight(starty,oldendy+1,charHeight);
      
      gg.copyArea(0,dely,(int)((endx-startx)*charWidth),dely2,0,-dely);
      
      if (starty > oldendy) {
        drawPanel(gg,startx,endx,starty,endy,startx,starty,0);
      } else {
        drawPanel(gg,startx,endx,oldendy,endy,startx,starty,0);
      }
      
      oldstarty = starty;
      oldendy   = endy;
      
    } else if (oldstarty > starty) {
      
      // This is moving the scrollbar up
      //
      //  ---------------------- sy
      //                               
      //  ====================== osy
      //
      //                               dely2
      //
      //  ---------------------- ey
      //                               dely
      //  ====================== oey
      
      int dely  = av.getPixelHeight(endy,oldendy,charHeight);
      int dely2 = av.getPixelHeight(oldstarty,endy+1,charHeight);
      
      gg.copyArea(0,0,(int)((endx-startx)*charWidth),dely2,0,dely);
      
      if (oldstarty > endy) {
        drawPanel(gg,startx,endx,starty,endy,startx,starty,0);
      } else {
        drawPanel(gg,startx,endx,starty,oldstarty+1,startx,starty,0);
      }
      
      oldstarty = starty;
      oldendy   = endy;
    }

    if (av.useImage() == true) {
      g.drawImage(img,0,0,this);
    }
  }
  
  public void drawPanel(Graphics2D g,int x1,int x2, int y1, int y2,int startx, int starty,int offset) {
    
    if (x2 >= x1 && (av.getEndRes()-av.getStartRes()) < 2000) {
      //System.out.println("Fetching sequence");
      //av.getSequence(x1,x2);   // this can go down into alignment
    }
    long start = System.currentTimeMillis();

    TrackRenderer.drawTracks(g,x1,x2,y1,y2,startx,starty,offset,0,av);

    long end = System.currentTimeMillis();

    //System.out.println("Time for redraw " + (x2-x1+1) + " " + (end-start+1) + " " + av);
  }


  public boolean handleFontChangeEvent(FontChangeEvent e) {
  	
  	av.setFont(getGraphics(),e.getFont());
  	av.updateRegion(size().width,size().height);
  	
    paintFlag = true;
    
    repaint();
    
    return true;
  }
  
  public boolean isFocusable() {
  	return true;
  }


}
