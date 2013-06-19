package pogvue.graph;

import java.awt.*;
import javax.swing.*;

public class GraphDisplayProperties {

  public int xmargin;
  public int ymargin;
  
  public String title;
  public String xlabel;
  public String ylabel;
  
  public int xstart;
  public int xend;
  
  public int ystart;
  public int yend;
  
  public DataSetProperties dsp;
  
  public GraphDisplayProperties(DataSetProperties dsp, int width, int height) {
    this.dsp = dsp;
    
    xmargin = (int)(0.1 * width);
    ymargin = (int)(0.1 * height);
    
    xstart  = xmargin;
    ystart  = height - ymargin;
    
    xend    = width - xmargin;
    yend    = ymargin;
    
  }
  
  public void drawXAxis(Graphics g) {
    g.drawLine(xstart,ystart,xend,ystart);
  }
  
  public void drawYAxis(Graphics g) {
    g.drawLine(xstart,ystart,xstart,yend);
  }
  
  
}
