package pogvue.analysis;

import pogvue.io.*;
import java.util.*;
import java.io.*;

public class Corr {

  public static void main(String[] args) {
     try {
     FileParse fp = new FileParse(args[0],"File");

     Vector col1 = new Vector();
     Vector col2 = new Vector();

     int colnum1 = Integer.parseInt(args[1]);
     int colnum2 = Integer.parseInt(args[2]);

     String line = null;;
     while ( (line = fp.nextLine()) != null) {

          StringTokenizer str = new StringTokenizer(line);

	  int i = 0;

	  while (str.hasMoreTokens()) {

	      String t = str.nextToken();

	      if (i == colnum1) {
	         col1.addElement(new Double(Double.parseDouble(t)));
              } 
	      if (i == colnum2) {
	         col2.addElement(new Double(Double.parseDouble(t)));
              } 
	      i++;
	  }
      }

      int size = col1.size();
      if (col2.size() < col1.size()) {
        size = col2.size();
      }
      double[] dcol1 = new double[size];
      double[] dcol2 = new double[size];

      int i = 0;

      while (i < size) {
         dcol1[i] = ((Double)col1.elementAt(i)).doubleValue();
         dcol2[i] = ((Double)col2.elementAt(i)).doubleValue();
	 i++;
      }

      double corr = Correlation4.get(dcol1,dcol2);

      System.out.println("Correlation " + corr);
      } catch (IOException e) {
        e.printStackTrace();

      }
}
}
