package pogvue.io;

import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class MatrixFile extends FileParse {

private double[][] matrix;
      private int    size;
    private Hashtable names;
    private double    min = 100000.0;
    private double    max = -100000.0;

    public MatrixFile(String name,String type) throws IOException {
	super(name,type);
	parse();
    }
  private void parse()  {
      try {

	  String line;
	  int    linenum = 0;

	  while ((line = nextLine()) != null) {
	      StringTokenizer str = new StringTokenizer(line,"\t");

	      if (linenum == 0) {

		  size = str.countTokens();

		  matrix = new double[size][size];
		  names  = new Hashtable();
		  
		  int i = 0;

		  while (str.hasMoreTokens()) {
		      String tmp = str.nextToken();
		      
		      names.put(tmp.substring(0,3), i);
		      i++;
		  }

	      } else {

		  String name = str.nextToken();

		  name = name.substring(0,3);

		  int pos = (Integer) names.get(name);

		  int i = 0;

		  while (str.hasMoreTokens()) {
		      
		      String tmp = str.nextToken();
		      if (!tmp.equals("nan")) {
			  double value = Double.parseDouble(tmp);
			  
			  if (value > max) {
			      max = value;
			  }
			  if (value < min) {
			      min = value;
			  }

			  matrix[pos][i] = value;
			  
		      } else {
			  matrix[pos][i] = 0;
		      }

		      i++;
		  }
	      }
	      linenum++;

	  }
      } catch (Exception e) {
	  System.out.println("Got exception reading matrix " + e);
	  e.printStackTrace();
      }
  }

}




