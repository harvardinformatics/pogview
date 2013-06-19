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
import java.awt.image.*;

public class GlassCanvas extends ControlledCanvas implements AlignViewportListener,
                                                             MouseListener {
  
  private final AlignViewport     av;
  BufferedImage buffer;
  int x;
  int y;
  Graphics2D g2;

  public GlassCanvas(AlignViewport av,Controller c) {
    this.av         = av;
    setController(c);

    setOpaque(false);
  }

  public boolean handleAlignViewportEvent(AlignViewportEvent e) {

    if (e.getAlignViewport() == av) {
      repaint();
    }
    return true;
  }

  public Dimension getPreferredSize() {
    if (getSize().width > 0) {
      return getSize();
    } else {
      return new Dimension(1000,700);
    }
  }
  public void paintComponent(Graphics g) {
    if (buffer == null) {
      buffer = createBuffer();
    }
    
    g2 = buffer.createGraphics();
    g2.setComposite(AlphaComposite.Clear);
    g2.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
    g2.setComposite(AlphaComposite.Src);

    setVisible(true);
    setOpaque(false);

    Alignment da = av.getAlignment();

    g2.setColor(Color.black);

    int startres = av.getStartRes();
    int endres   = av.getEndRes();

    int width  = getSize().width;
    int height = getSize().height;

    
    //System.out.println("Drawing range " + startres);
    g2.setColor(Color.red);
    g2.fillRect(x,0,x+2000,50);

    TrackRenderer.drawTracks(g2,startres+1000,startres+2000,0,2,startres,0,1000,0,av);

    g.drawImage(buffer,0,0,null);
  }

  private BufferedImage createBuffer() {
    GraphicsEnvironment   local  = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice        device = local.getDefaultScreenDevice();
    GraphicsConfiguration config = device.getDefaultConfiguration();
        
    Container parent = getParent();
    return config.createCompatibleImage(parent.getWidth(), parent.getHeight(),
					Transparency.TRANSLUCENT);
  }

  public void mouseEntered(MouseEvent evt) { }
  public void mouseExited (MouseEvent evt) { }
  public void mouseClicked(MouseEvent evt) { }
  public void mouseMoved  (MouseEvent evt) { }
  public void mousePressed(MouseEvent evt) { 

    x = evt.getX();
    y = evt.getY();

    repaint();
  }

  public void mouseDragged(MouseEvent evt) { }
  
  public void mouseReleased(MouseEvent evt) { }

}
