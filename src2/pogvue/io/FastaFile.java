package pogvue.io;

import java.io.OutputStreamWriter;
import java.util.Vector;
import pogvue.analysis.AlignSeq;
import pogvue.datamodel.Sequence;
import pogvue.datamodel.Sequence;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.StringTokenizer;

public class FastaFile extends AlignFile {

  public FastaFile(Sequence[] s) {
    super(s);
  }
  public FastaFile(String inStr) {
    super(inStr);
  }
  public FastaFile(BufferedReader reader) throws IOException {
    super(reader);
  }
  public FastaFile(String inFile, String type) throws IOException {
    super(inFile,type,true);
  }
  public FastaFile(String inFile, String type, boolean parse) throws IOException {
    super(inFile,type,parse);
  }
  public void parse() {

    String       id    = "";
    StringBuffer seq   = new StringBuffer();
    int          count = 0;
    boolean      flag  = false;

    int          sstart = 0;
    int          send   = 0;

    String line;

    try {
      while ((line = nextLine()) != null) {
        //System.out.println("Line " + line);
	if (line.length() > 0) {

	  // Do we have an id line?

	  if (line.substring(0,1).equals(">")) {
	    if (count != 0) {
	      if (sstart != 0) {
		seqs.addElement(new Sequence(id,seq.toString().toUpperCase(),sstart,send));
	      } else {
                //System.out.println("ID " + id);
                //System.out.println("Seq " + seq);
		seqs.addElement(new Sequence(id,seq.toString().toUpperCase(),1,seq.length()));
	      }
	    }

	    count++;

	    StringTokenizer str = new StringTokenizer(line," ");

	    id = str.nextToken();
	    id = id.substring(1);

	    if (id.indexOf("/") > 0 ) {

	      StringTokenizer st = new StringTokenizer(id,"/");
	      if (st.countTokens() == 2) {
		id = st.nextToken();
		String tmp = st.nextToken();
		
		st = new StringTokenizer(tmp,"-");

		if (st.countTokens() == 2) {
		  sstart = Integer.valueOf(st.nextToken());
		  send   = Integer.valueOf(st.nextToken());
		}
	      }
	    }

	    seq = new StringBuffer();

	  } else {
	    seq = seq.append(line);
	  }
	}
      }
      if (count > 0) {
	if (sstart != 0) {
	  seqs.addElement(new Sequence(id,seq.toString().toUpperCase(),sstart,send));
	} else {
	  seqs.addElement(new Sequence(id,seq.toString().toUpperCase(),1,seq.length()));
	}
      }

    } catch (IOException e) {
      System.out.println("Exception parsing fastafile");
    }

    System.out.println("SEQS " + seqs.size());
  }

  public static void write(OutputStream os,Sequence seq) throws IOException {
    int i = 0;
    
    Writer writer = new OutputStreamWriter(os);
    

    writer.write(">" + seq.getName() + "\n");
    
    int j = 0;
    
    while (j < seq.getLength()) {
      int end = j+72;
      if (end > seq.getLength()) {
	end = seq.getLength();
      }
      writer.write(seq.getSequence().substring(j,end) + "\n");
      j+= 72;
    }
  }
  private static String print(Sequence[] s) {
    return print(s,72);
  }
  private static String print(Sequence[] s, int len) {
    return print(s,len,true);
  }
  private static String print(Sequence[] s, int len,boolean gaps) {
    StringBuffer out = new StringBuffer();
    int i = 0;
    while (i < s.length && s[i] != null) {
      String seq;
      if (gaps) {
        seq = s[i].getSequence();
      } else {
        seq = AlignSeq.extractGaps(s[i].getSequence(),"-");
        seq = AlignSeq.extractGaps(seq,".");
        seq = AlignSeq.extractGaps(seq," ");
      }

			out.append(">").append(s[i].getName()).append("/").append(s[i].getStart()).append("-").append(s[i].getEnd()).append("\n");
      
      int nochunks = seq.length() / len + 1;
      
      for (int j = 0; j < nochunks; j++) {
        int start = j*len;
        int end = start + len;

        if (end < seq.length()) {
            out.append(seq.substring(start, end)).append("\n");
        } else if (start < seq.length()) {
            out.append(seq.substring(start)).append("\n");
        }
      }
      i++;
    }
    return out.toString();
  }

  public String print() {
    return print(getSeqsAsArray());
  } 
}



