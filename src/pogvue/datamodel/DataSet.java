package pogvue.datamodel;

import java.io.*;
import java.util.*;

import pogvue.io.*;
  
public class DataSet {
  
  Vector x;
  Vector y;
  
  double xmin;
  double xmax;
  
  double ymin;
  double ymax;
  
  public DataSet(Vector x, Vector y) {
    this.x = x;
    this.y = y;
  }

  public double getXMin() {
    xmin = 1e100;
    
    for (int i = 0; i < x.size(); i++) {
      double tmp = ((Double)x.elementAt(i)).doubleValue();
      
      if (tmp < xmin) {
        xmin = tmp;
      }
    }
 
    return xmin;
  }
  
  
  public double getXMax() {
    xmax = -1e100;
    
    for (int i = 0; i < x.size(); i++) {
      double tmp = ((Double)x.elementAt(i)).doubleValue();
      
      if (tmp > xmax) {
        xmax = tmp;
      }
    }
 
    return xmax;
  }
  
  
  public double getYMin() {
    ymin = 1e100;
    
    for (int i = 0; i < y.size(); i++) {
      double tmp = ((Double)y.elementAt(i)).doubleValue();
      
      if (tmp < ymin) {
        ymin = tmp;
      }
    }
 
    return ymin;
  }
  
  public double getYMax() {
    ymax = -1e100;
    
    for (int i = 0; i < y.size(); i++) {
      double tmp = ((Double)y.elementAt(i)).doubleValue();
      
      if (tmp > ymax) {
        ymax = tmp;
      }
    }
 
    return ymax;
  }
  
  public static void main(String[] args) {
    try {
      String filename = args[0];
      
      FileParse fp = new FileParse(filename,"File");
      
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
      
    } catch (IOException e) {
      e.printStackTrace();
    }
   }
}
