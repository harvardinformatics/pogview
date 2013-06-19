package pogvue.io;

import java.io.*;
import java.util.*;
import java.net.*;

import pogvue.io.*;
import pogvue.datamodel.*;


public class CytoBandFile extends FileParse {
  Hashtable bandhash  = new Hashtable();
  
  public CytoBandFile(String file, String type) throws IOException {
      super(file,type);
      parse();
      
  }
  public void parse() {

    String line;

    try {

      while ((line = nextLine()) != null) {    
	StringTokenizer str = new StringTokenizer(line,"\t" );
	
	String chr   = str.nextToken();
	int    start = Integer.parseInt(str.nextToken());
	int    end   = Integer.parseInt(str.nextToken());
	String name  = str.nextToken();
	String stain = str.nextToken();
	
	CytoBand band = new CytoBand(chr,start,end,name,stain);
	
	if (bandhash.containsKey(chr)) {
	  ((Vector)bandhash.get(chr)).addElement(band);
	} else {
	  Vector bandvect = new Vector();
	  
	  bandvect.addElement(band);
	  bandhash.put(chr,bandvect);
	}
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public Hashtable getBands() {
    return bandhash;
  }
}

