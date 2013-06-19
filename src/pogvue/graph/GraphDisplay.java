package pogvue.graph;

import java.awt.*;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.*;

import pogvue.datamodel.*;
import pogvue.io.FileParse;

public class GraphDisplay {

  public DataSetProperties      dsp;
  public GraphDisplayProperties gdp;
  
  public GraphDisplay(DataSetProperties dsp) {
    this.dsp = dsp;
  }
  
  public void draw(Graphics g, int width, int height){
    gdp = new GraphDisplayProperties(dsp,width,height);
    
    //**************************************************
    // Draw axes, tics, numbers, label
    
    g.setColor(Color.black);
    
    gdp.drawXAxis(g);
    gdp.drawYAxis(g);
    
    
    // Now the ticks - how to calculate?
    
    // Draw numbers under the ticks
    
    // Draw the label - centered
    
    
    //**************************************************
    // Draw data 
    
    // Draw title
    
  }
  
  public static void main(String[] args) {
    try {
      String filename = args[0];
      
      FileParse fp = new FileParse("data\\pfam_paralog.cds.bin","File");
      
      Vector x = new Vector();
      Vector y = new Vector();
      
      String line;
      
      while ((line = fp.nextLine()) != null) {
        StringTokenizer str = new StringTokenizer(line,"\t");
        
        if (str.countTokens() >= 2) {
          double tmpx = Double.parseDouble(str.nextToken());
          double tmpy = Double.parseDouble(str.nextToken());
          
          //System.out.println("X " + tmpx + " Y " + tmpy);
          x.addElement(tmpx);
          y.addElement(tmpy);
        }
      }
      
      DataSet set = new DataSet(x,y);
      
      System.out.println("RangeX " + set.getXMin() + " " + set.getXMax());
      System.out.println("RangeY " + set.getYMin() + " " + set.getYMax());

      DataSetProperties dsp  = new DataSetProperties(set);
      GraphDisplay gdp       = new GraphDisplay(dsp);

      int width  = 800;
      int height = 600;
      
      JFrame jf = new JFrame();
      JPanel jp = new JPanel();
      
      jp.setPreferredSize(new Dimension(width,height));
      jf.getContentPane().setLayout(new BorderLayout());
      jf.getContentPane().add("Center",jp);
      jf.pack();
      jf.setVisible(true);
    
      gdp.draw(jp.getGraphics(),800,600);
      
    } catch (IOException e) {
      e.printStackTrace();
    }
   }
  
  
}
