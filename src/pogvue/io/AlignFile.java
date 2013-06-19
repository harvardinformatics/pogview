package pogvue.io;

import pogvue.datamodel.Sequence;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

public abstract class AlignFile extends FileParse {
  int noSeqs    = 0;
  int maxLength = 0;
  
  Hashtable myHash;
  Vector<Sequence>    seqs;
  private Vector    headers;
  
  private long start;
  private long end;
  
  public AlignFile(String inFile, String type, boolean parse) throws IOException {
    super(inFile,type);
    
    initData();
    
    if (parse) {
      parse();
    }
  }
  
  public AlignFile() {
  }
  public AlignFile(String inStr) {
    initData();
    
    parse();
  }
  
  public AlignFile(Sequence[] s) {
    seqs = new Vector<Sequence>();
    for (Sequence value : s) {
      seqs.addElement(value);
    }
  }
  /**
   * Constructor which parses the data from a file of some specified type.
   * @param inFile Filename to read from.
   * @param type   What type of file to read from (File, URL)
   */
  public AlignFile(String inFile, String type) throws IOException {
    super(inFile,type);
    
    initData();
    
    parse();
    
  }
  
  /**
   * Return the seqs Vector
   */
  public Vector getSeqs() {
    return seqs;
  }
  
  /**
   * Return the Sequences in the seqs Vector as an array of Sequences
   */
  public Sequence [] getSeqsAsArray() {
    Sequence [] s = new Sequence[seqs.size()];
    for (int i=0;i < seqs.size();i++) {
      s[i] = seqs.elementAt(i);
    }
    return s;
  }
  
  
  /**
   * Initialise objects to store sequence data in.
   */
  void initData() {
    seqs    = new Vector<Sequence>();
    headers = new Vector();
    myHash  = new Hashtable();
  }
  
  protected void setSeqs(Sequence [] s) {
    for (Sequence value : s) {
      seqs.addElement(value);
    }
  }
  
  /**
   * This method must be implemented to parse the contents of the file.
   */
  protected abstract void parse();
  
  
  /**
   * Print out in alignment file format the Sequences in the seqs Vector.
   */
  public abstract String print();
}
