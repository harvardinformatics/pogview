package pogvue.io;

import java.io.IOException;
import java.util.*;

import pogvue.datamodel.motif.*;

public class TFMatrixFile extends FileParse {
    Vector  matrices;

    public TFMatrixFile(String name,String type) throws IOException {
	super(name,type);

	matrices = new Vector();

	parse();
    }
    private void parse() throws IOException {

	  String line;
	  int    linenum = 0;

	  String acc  = "";
	  String id   = "";
	  String name = "";
	  String desc = "";
	  String cons = "";
	  int    len  = 0;
	  int    count = 0;

	  double mat[][] = null;

	  while ((line = nextLine()) != null) {
	      StringTokenizer str = new StringTokenizer(line," ");

	      String strtype = str.nextToken();

	      if (strtype.equals("AC")) {
		  acc = str.nextToken();
		  cons = "";
	      } else if (strtype.equals("ID")) {
		  id = str.nextToken();
	      } else if (strtype.equals("NA")) {
		  name = str.nextToken();
	      } else if (strtype.equals("DE")) {
		  desc = str.nextToken();
	      } else if (strtype.equals("P0")) {

		  Vector vals = new Vector();

		  int j = 0;   // Cols go A C G T   we store them ATCG
		  
		  while ((line = nextLine()) != null && line.indexOf("XX") != 0) {

		      StringTokenizer str2 = new StringTokenizer(line," ");

		      str2.nextToken();

		      int k = 0;  // k is a row in the matrix

		      double tmp[] = new double[4];

		      while (k < 5) {
			  String strval = str2.nextToken();

			  if (k < 4) {
			      double val = Double.parseDouble(strval);
			      if (k == 0) { 
			        tmp[k] = val;
			      } else if (k == 1) { 
			        tmp[2] = val;
			      } else if (k == 2) { 
			        tmp[3] = val;
			      } else if (k == 3) { 
			        tmp[1] = val;
			      }
			  } else {
			      cons = cons + strval;
			  }
			  k++;
		      }
		      
		      vals.addElement(tmp);
		      
		      j++;
		      
		  }
		  
		  len = j;

		  mat = new double[vals.size()][4];

		  j = 0;

		  while (j < vals.size()) {
		      int jj = 0 ;
		      double[] tmp = (double[])vals.elementAt(j);

		      while (jj < 4) {
			  mat[j][jj] = tmp[jj];
			  jj++;
		      }

		      j++;
		  }
		  
	      } else if (strtype.equals("//") && mat != null) {

		  TFMatrix tf = new TFMatrix(mat,len,4);

		  tf.setAcc(acc);
		  tf.setId(id);

		  int inf = (int)(tf.getPwm().getInfContent());

		  tf.setName(name + "." + inf);
		  tf.setDesc(desc);
		  tf.setConsensus(cons);
		  count++;
		  matrices.addElement(tf);

	      }
	  }
  }

    public Vector getMatrices() {
	return matrices;
    }
    public static void main(String[] args) {
        try {
	    TFMatrixFile tf = new TFMatrixFile(args[0],"File");
	    
	    Vector m = tf.getMatrices();
	    
	    for (int i = 0 ; i < m.size() ; i++) {
		
		TFMatrix tfm = (TFMatrix)m.elementAt(i);
		
		
		//System.out.println("Matrix " + tfm.getName() + " " + tfm.getConsensus() + " " + tfm.getDesc() + tfm.getPwm().getPwm().length );
		PwmLineFile.print(tfm.getPwm());
	    }
	} catch (IOException e) {
	    System.out.println("Exception " + e);
	}
   }
}




