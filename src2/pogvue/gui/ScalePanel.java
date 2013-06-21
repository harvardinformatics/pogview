package pogvue.gui;

import pogvue.gui.event.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.*;

public final class ScalePanel extends JPanel implements MouseListener {
	
	public static final int BLOCKHEIGHT = 4;
	public static final int HEIGHT      = 30;
	
	int offy;
  int width;

  AlignViewport av;
  Controller    controller;

  boolean paintFlag = true;
  
  public ScalePanel(AlignViewport av,Controller c) {
    this.av         = av;
    this.controller = c;

    componentInit();
  }

  private void componentInit() {
    addMouseListener(this);
    controller.addListener(this);
  }

  public void paintComponent(Graphics g) {
  	double charWidth  = av.getCharWidth();
  	int    charHeight = av.getCharHeight();
  	
  	Font f = av.getFont();
  	
  	g.setFont(f);
  	g.setColor(Color.white);
  	g.fillRect(0,0,getSize().width,getSize().height);
  	
  	int tickSpace;
  	int resWidth   = av.getEndRes() - av.getStartRes() + 1;
  	
  	double rough        = getWidth()/100.0;
  	double roughtick    = resWidth/rough;
  	
  	int digits = (int)(Math.log(roughtick)/Math.log(10));
  	int num    = (int)(roughtick/Math.pow(10,digits));
  	
  	if (Math.abs(10-num) < 3) {
  		num = 10;
  	} else if (Math.abs(5-num) <= 2) {
  		num = 5;
  	} else {
  		num = 2;
  	}
  	
  	int space = (int)(num * Math.pow(10,digits));
  	
  	g.setColor(Color.black);
  	
  	int offset      = av.getOffset();
  	int startx      = av.getStartRes() + offset;
  	int endx        = av.getEndRes()   + offset;
  	
  	//System.out.println("X offset base coord is " + offset);
  	//System.out.println("We are drawging from " + startx + " to " + endx);
  	//System.out.println("The no. of bases in between ticks is " + space);
  	
  	if (space < 10) {
  		space = 10;
  	}
  	
  	int scalestartx = startx - startx%space + space;
  	
  	for (int i = scalestartx; i < endx; i+= space) {
  		NumberFormat nf = NumberFormat.getInstance();
  		String formattedNumber = nf.format(i);
  		
  		g.drawString(formattedNumber,(int)((i-startx-1)*charWidth),15);
  	}
 	
  	// Now minipog
  	
  	if (av.getMinipog() != null) {
  		int startres = av.getMinipog().getAlignViewport().getStartRes();
  		int endres   = av.getMinipog().getAlignViewport().getEndRes();
  		
  		g.setColor(new Color(255,0,0,80));
  		
  		width = (int)((endres-startres+1)*charWidth);
  		
  		if (width < 3) {
  			width = 3;
  		}
  		
  		g.fillRect((int)((startres-av.getStartRes())*charWidth),0,width,40);
  		
  	}
  }
  public boolean handleAlignViewportEvent(AlignViewportEvent e) {
  	paintFlag = true;
  	repaint();
  	return true;
  }
  public Dimension getMinimumSize() {
    return new Dimension(500,HEIGHT);
  }

  public Dimension getPreferredSize() {
    return getMinimumSize();
  }

  public void mouseEntered(MouseEvent evt) { }
  public void mouseExited(MouseEvent evt) { }
  public void mouseClicked(MouseEvent evt) { }
  public void mousePressed(MouseEvent evt) { }
  public void mouseReleased(MouseEvent evt) { }
}
