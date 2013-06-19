package pogvue.graph;

import java.awt.*;
import javax.swing.*;

import pogvue.datamodel.*;

public class DataSetProperties {
  
  Font  f;
  Color c;
  
  int   lineWidth;
  int   pointSize;
  
  boolean drawLine;
  boolean drawPoint;
  
  int    pointType;
  
  DataSet set;
  
  public DataSetProperties(DataSet set) {
    this(set,Color.black);
  }
  public DataSetProperties(DataSet set,Color c) {
    this(set,c,true,1,false,1,1,new Font("Helvetica",Font.PLAIN,10));
  }
  
  public DataSetProperties(DataSet set, 
      Color c, 
      boolean drawLine, 
      int     lineWidth, 
      boolean drawPoint, 
      int     pointSize,
      int     pointType,
      Font    f) {
    this.c = c;
    this.drawLine = drawLine;
    this.lineWidth = lineWidth;
    this.drawPoint = drawPoint;
    this.pointSize = pointSize;
    this.pointType = pointType;
    this.f         = f;
    
  }
  
}
