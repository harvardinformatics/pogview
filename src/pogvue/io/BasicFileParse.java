package pogvue.io;

import java.io.*;

public class BasicFileParse {
  public    BufferedReader    bufReader;
  public    FileReader        fileReader;
  public    RandomAccessFile  raf;
  public    String            inFile;

  private   long  pos = 0;

  public BasicFileParse(String inFile) throws IOException {

    this.inFile = inFile;			
    
    raf = new RandomAccessFile(inFile,"r");
      
    fileReader = new FileReader(raf.getFD());
    bufReader  = new LineNumberReader(fileReader, 65536);  // default buffersize 8192
  }
  
  public String nextLine() throws IOException {
    
    String next = bufReader.readLine();

    if (next == null) {
      return null;
    }

    pos += next.length()+1;

    return next;
    
  }
  public static void main(String[] args) {
    try {
      BasicFileParse bfp = new BasicFileParse(args[0]);
      String         line;

      while ((line = bfp.nextLine()) != null) {
	System.out.println("Line " + line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
