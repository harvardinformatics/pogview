package pogvue.datamodel;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import pogvue.util.QuickSort;

public class GappedSequence extends Sequence {
  public Vector inserts;
  public Hashtable inserts_by_coord;
  private Hashtable inserts_by_hcoord;

  String queryString = "";
  String hitString = "";
  
  public GappedSequence(String name, String sequence, int start, int end) {
    super(name,sequence,start,end);
  }
  
  public GappedSequence(String name, String sequence, int start, int end,Vector inserts,Hashtable inserts_by_coord, Hashtable inserts_by_hcoord) {
    super(name,sequence,start,end);
    
    this.inserts = inserts;
    this.inserts_by_coord = inserts_by_coord;
    this.inserts_by_hcoord = inserts_by_hcoord;
  
  }
  
  public String getExpandedHitString(String query) {
    getExpandedQueryString(query);
    return hitString;
  }
  
  public String getExpandedQueryString(String query) {
    
    if (queryString.equals("")) {
      
      // Sort the inserts_by_coord
      
      int[]     icoord = new int[inserts_by_coord.size()];
      Integer[] obj    = new Integer[inserts_by_coord.size()];
      
      Enumeration en = inserts_by_coord.keys();
      
      int i = 0;
      
      while (en.hasMoreElements()) {
        Integer tmp = (Integer)en.nextElement();
        icoord[i]   = tmp.intValue();
        obj[i] = tmp;

        i++;
      }
              
      QuickSort.sort(icoord,obj);

      i = 0;
      
      while (i < icoord.length) {
	//System.out.println("Insert " + i + " " + icoord[i]);
        i++;
      }

      
      int coord = 0;
      
      for (i = 0; i < obj.length; i++) {
        int    ic     = icoord[i]-1;
        String insert = (String)inserts_by_coord.get(obj[i]);
	int    len    = insert.length();
	
        // Fetch the intervening pieces
        
	//System.out.println("Insert coord " + coord + " " + ic  + " string lengths are " + query.length() + " " + sequence.length());
      
	if (ic > 0) {
	  //System.out.println("Getting substring from " + coord + " length " + (ic-coord+1) + " " + query.length());
	  queryString += query.substring(coord,ic-1);
	  hitString   += sequence.substring(coord,ic-1);
	  
	  queryString += getPadString(len);
	  hitString   += insert;
        
	  coord = ic;
	}
      }

      if (queryString != null) {
	queryString += query.substring(coord);
	hitString   += sequence.substring(coord);
      } else {
	queryString = query.substring(coord);
	hitString   = sequence.substring(coord);
      }
    }
    return queryString;
  }
  
  public String getPadString(int count) {
    int i = 0;
    StringBuffer b = new StringBuffer();
    
    while (i < count) {
      b.append("-");
      i++;
    }
    return b.toString();
    
    
  }
}
