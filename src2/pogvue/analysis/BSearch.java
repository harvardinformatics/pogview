package pogvue.analysis;

import java.io.*;
import java.util.*;
import pogvue.io.*;

public class BSearch {
  RandomAccessFile file;
  String           filename;
  long             coord;
  int              col;
  String           chr;
  long             endcoord = -1;
  long             prevstart;
  long             prevend;

  String           indexDelim;

  public BSearch(String filename, int col,String d) throws IOException {
    this.col        = col;
    this.filename   = filename;
    this.indexDelim = d;
    open_file();
		
  }
  public BSearch(String filename, int col) throws IOException {
    this(filename,col,"\t");
		
  }
	
  public RandomAccessFile open_file() throws IOException {
    file = new RandomAccessFile(filename,"r");

    long endpos  = file.length();
		
    file.seek(endpos-1);
    backup_line(file);
		
    Vector out = read_tokens(file,indexDelim);

    if (out == null) {
      //System.out.println("ERROR:  No line found at end of file");
    }
    endcoord = Integer.parseInt((String)out.elementAt(col));
		
    return file;
  }
  public RandomAccessFile search_file(long coord) throws IOException {

    if (endcoord != -1 && coord > endcoord) {
      //System.out.println("Coord exceeds maximum coord " + coord + " " + endcoord);
      return null;
    }
		
    long position = file.length()/2;
    
    file.seek(position);
		
    backup_line  (file);
    find_position(file,(long)0,file.length(),(long)coord,col);

    return file;
    //System.out.println("Out" + out);
		
  }
	
  public void find_position(RandomAccessFile file,long start, long end, long coord, int field) throws IOException {
    long halfpos = (start+end)/2;
	
    file.seek(halfpos);
    backup_line(file);
		
    halfpos = file.getFilePointer();
		
    if (halfpos == 0) {
      file.seek(0);
      return;
    }

    Vector tokens1 = read_tokens(file,indexDelim);
    Vector tokens2 = read_tokens(file,indexDelim);
		 
    long tmpcoord1 =  Long.parseLong((String)tokens1.elementAt(field));
    long tmpcoord2 =  Long.parseLong((String)tokens2.elementAt(field));
		
    //System.out.println("Coord at " + tmpcoord1 + " " + tmpcoord2 + " " + coord);
		
    if (coord > tmpcoord2 && halfpos == file.length()) {
      file.seek(halfpos);

      return;
    }
		
    //System.out.println("Pog");
    //                   #-------------------@--------------------#
    //                  start            halfpos                end

    //  tmpcoord1 = coord at halfpos
    //  tmpcoord2 = coord at halfpos + 1 line

    if (tmpcoord1 > coord) {

      //                   #-------------------@--------------------#
      //                  start            halfpos                end
      //                           ^coord
      //System.out.println("Looing for " + coord + " between " + start + " " + halfpos + " " + prevstart + " " + prevend);

      //System.out.println("Sog");
      if (!(prevstart == start && prevend == halfpos)) {
	prevstart = start;
	prevend   = halfpos;

				
	//System.out.println("Sog1");
	find_position(file,start,halfpos,coord,field);

      } else {
	file.seek(start);
	backup_line(file);
	//System.out.println("Sog2");
	return;
      }
    } else if (tmpcoord1 < coord) {
      //                   #-------------------@--------------------#
      //                  start            halfpos                end
      //                                                ^coord
      
      //System.out.println("Looing for " + coord + " between " + halfpos + " " + end + " " + prevstart + " " + prevend);
      //System.out.println("Log");
      if (!(prevstart == halfpos && prevend == end)) {
	prevstart = halfpos;
	prevend   = end;
	//System.out.println("Log1");
	find_position(file,halfpos,end,coord,field);
      } else {
	file.seek(start);
	backup_line(file);
	//System.out.println("Log2");
	return;
      }
			
    }

  }
  public static String readLine(RandomAccessFile file) throws IOException{
    return file.readLine();
  }
  public String readLine() {
    try {
      return file.readLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  public static Vector read_tokens(RandomAccessFile file) throws IOException {
    return read_tokens(file,"\t");
  }
  public static Vector read_tokens(RandomAccessFile file,String sep) throws IOException {
    String line = file.readLine();
    Vector out = new Vector();
		
    if (line == null) {
      return null;
    }
		
    StringTokenizer str = new StringTokenizer(line,sep);
		
    while (str.hasMoreTokens()) {
      out.addElement((String)str.nextToken());
    }
    return out;
  }
  public static void backup_line(RandomAccessFile file) throws IOException {
    long position = file.getFilePointer();
    //System.out.println("pos1 " + position);
    position--;
		
    file.seek(position);
    
    char b = 'N';
		
    while (position > 0  && b != '\n') {
      b = (char)file.readByte();
      position--;
      file.seek(position);
    }
    if (position > 0) {
      position+=2;
    }
    //System.out.println("pos2 " + position);
    file.seek(position);
  }

  public static void main(String[] args) {
    try {

      String chr = args[1];
      long start = Long.parseLong(args[2]);
      long end   = Long.parseLong(args[3]);
      int col = Integer.parseInt(args[4]);
			
      GFFFile gff = new GFFFile(args[0],"BSearch",col,start);
			
      long coord = 0;
			
      while (coord <= end) {
	String line = gff.nextLine();
	System.out.println("Line " + line);
	StringTokenizer str = new StringTokenizer(line,"\t");
	String tmpchr = str.nextToken();


	  str.nextToken();
	  str.nextToken();
	  long linestart = Long.parseLong((String)str.nextToken());
	  long lineend   = Long.parseLong((String)str.nextToken());

	if (tmpchr.equals(chr)) {				
	  System.out.println("Line " + line);
	}
	coord = linestart;
      }
			
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
