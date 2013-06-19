package pogvue.io;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import pogvue.analysis.*;

public class FileParse {
  private   InputStream       inStream;
  public   BufferedReader    bufReader;
  private   URLConnection     urlconn;
  private   ActionListener    l = null;

  public RandomAccessFile raf;
  private   long size = -1;
  private   long estimatedSize = -1;
  private   long updateSize =1000;
  
  private   int curlen = 0;
  private   long prevchunk = -1;
  
  private   int i = 0;

  private String    inFile;
  private String    type;

  private long pos = 0;

  public FileParse() {}
  
  public FileParse(String fileStr, String type) throws MalformedURLException, IOException {
  	this(fileStr,type, 0,0);
  }
  public FileParse(String fileStr, String type, int col, long coord) throws MalformedURLException, IOException {

    this.inFile = fileStr;			
    this.type   = type;
    
    if (fileStr.indexOf("http://") == 0) {
      URL url = new URL(fileStr);
      urlconn = url.openConnection();
      
      inStream  = urlconn.getInputStream();
      bufReader = new BufferedReader(new InputStreamReader(inStream));

    } else if (type.equals("BSearch")) {
      BSearch bs  = new BSearch(fileStr, col);
        	
    } else if (type.equals("File")) {
      
      //File inFile = new File(fileStr);
      //System.out.println("File length is " + inFile.length());  
      //size = inFile.length();
      //inStream  = new FileInputStream(inFile);

      raf = new RandomAccessFile(fileStr,"r");
      
      FileReader       fReader = new FileReader(raf.getFD());
      bufReader = new LineNumberReader(fReader, 65536);  // default buffersize 8192
      //bufReader = new BufferedReader(new InputStreamReader(inStream));

    } else if (type.equals("URL")) {
      
      URL url = new URL(fileStr);
      urlconn = url.openConnection();
      
      inStream  = urlconn.getInputStream();
      bufReader = new BufferedReader(new InputStreamReader(inStream));
      
    }  else if (type.equals("URLZip")) {
      
      URL url = new URL(fileStr);
      
      inStream  = new GZIPInputStream(url.openStream(),16384);
      InputStreamReader zis = new InputStreamReader(inStream);
      bufReader = new BufferedReader(zis,16384);
      
    } else {
      System.out.println("Unknown FileParse inType " + type);
    }
  }
  
  public long getPos() {
    return pos;
  }
  private int getSize() {
    return (int)size;
  
  }
  public void setEstimatedSize(long size) {
    this.estimatedSize = size;
    //    System.out.println("Setting estimated size to " + size);
  }
  public long getEstimatedSize() {
    return estimatedSize;
  }
  
  public BufferedReader getBufferedReader() {
    return bufReader;
  }
  
  public String nextLine() throws IOException {
    
    String next = bufReader.readLine();

    if (next == null) {
      return null;
    }
    pos+= next.length()+1;
    i++;
  
    if (i == 1 && l != null && size > 0)  {
      ActionEvent e = new ActionEvent(this,0,String.valueOf(size));
      l.actionPerformed(e);
    }
        
    if (i == 1) {
      if (size > 0) {
        updateSize = (long)(size/(100));
      } else if (estimatedSize > 0) {
        updateSize = (long)(estimatedSize/100);
      }
      //System.out.println("Update size " + updateSize);
    }
    
    if (next != null) {
      curlen += next.length();
    
      if (l != null) {
        ActionEvent e = null;
	
        int chunk = (int)(curlen/updateSize);
        if (next != null && chunk != prevchunk) {
	  //System.out.println("Updating " + chunk);
          e = new ActionEvent(this,0,String.valueOf(chunk));
          l.actionPerformed((ActionEvent)e);
        }
        prevchunk = chunk;
   
      }
    }

    return next;
    
  }
  
  public void setActionListener(ActionListener l) {
  
    this.l = l;
  }
}
