package pogvue.io;

import pogvue.datamodel.Sequence;
import pogvue.datamodel.Sequence;
import pogvue.util.Format;

import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

public class MSFfile extends AlignFile {

    public MSFfile(Sequence[] s) {
	super(s);
    }

  public MSFfile(String inStr) {
    super(inStr);
  }

  public MSFfile(String inFile, String type) throws IOException {
    super(inFile,type);
  }

  public void parse() {

    int       i;
    boolean   seqFlag = false;
    String    key;
    Vector    headers = new Vector();
    Hashtable seqhash = new Hashtable();
    String    line;

    try {
    while ((line = nextLine()) != null) {

      StringTokenizer str = new StringTokenizer(line);

      while (str.hasMoreTokens()) {

        String inStr = str.nextToken();

        //If line has header information add to the headers vector
        if (inStr.indexOf("Name:") != -1) {
          key = str.nextToken();
          headers.addElement(key);
        }

        //if line has // set SeqFlag to 1 so we know sequences are coming
        if (inStr.indexOf("//") != -1) {
          seqFlag = true;
        }

        //Process lines as sequence lines if seqFlag is set
        if (( inStr.indexOf("//") == -1) && (seqFlag)) {
          //seqeunce id is the first field
          key = inStr;
          StringBuffer tempseq;

          //Get sequence from hash if it exists
          if (seqhash.containsKey(key)) {
            tempseq = (StringBuffer)seqhash.get(key);
          } else {
	    tempseq = new StringBuffer();
	    seqhash.put(key,tempseq);
	  }

          //loop through the rest of the words
          while (str.hasMoreTokens()) {
            //append the word to the sequence
            tempseq.append(str.nextToken());
          }
        }
      }
    }
    } catch (IOException e) {
      System.out.println("Exception parsing MSFFile " + e);
    }

    this.noSeqs = headers.size();

    //Add sequences to the hash
    for (i = 0; i < headers.size(); i++ ) {

      if ( seqhash.get(headers.elementAt(i)) != null) {
        String head =  headers.elementAt(i).toString();
        String seq  =  seqhash.get(head).toString();

        int start = 1;
        int end = seq.length();

        if (maxLength <  head.length() ) {
          maxLength =  head.length();
        }

        if (head.indexOf("/") > 0 ) {

          StringTokenizer st = new StringTokenizer(head,"/");

          if (st.countTokens() == 2) {

            head = st.nextToken();
            String tmp = st.nextToken();
            st = new StringTokenizer(tmp,"-");
            if (st.countTokens() == 2) {
              start = Integer.valueOf(st.nextToken());
              end = Integer.valueOf(st.nextToken());
            }
          }
        }

        Sequence newSeq = new Sequence(head,seq,start,end);

        seqs.addElement(newSeq);

      } else {
        System.out.println("Can't find sequence for " + headers.elementAt(i));
      }
    }

  }

  private static int checkSum(String seq) {
    //String chars =  "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz.*~&@";
    int check = 0;

    String index =  "--------------------------------------&---*---.-----------------@ABCDEFGHIJKLMNOPQRSTUVWXYZ------ABCDEFGHIJKLMNOPQRSTUVWXYZ----@";
    index += "--------------------------------------------------------------------------------------------------------------------------------";

    for(int i = 0; i < seq.length(); i++) {
      try {
        if (i <seq.length()) {
          int pos = index.indexOf(seq.substring(i,i+1));
          if (!index.substring(pos,pos+1).equals("_")) {
            check += ((i % 57) + 1) * pos;
          }
        }
      } catch (Exception e) {
        System.err.println("Exception " + e);
      }
    }
    return check % 10000;
  }

  private static String print(Sequence[] s) {
    StringBuffer out = new StringBuffer("PileUp\n\n");

    int max = 0;
    int maxid = 0;

    int i = 0;
    String big = "";
    while (i < s.length && s[i] != null) {
      big += s[i].getSequence();
      i++;
    }
    i = 0;
    int bigcheck = checkSum(big);

		out.append("   MSF: ").append(s[0].getSequence().length()).append("   Type: P    Check:  ").append(bigcheck).append("   ..\n\n\n");

    while (i < s.length && s[i] != null) {
      String seq = s[i].getSequence();
      String name =  s[i].getName()+ "/" + s[i].getStart() + "-" + s[i].getEnd();
      int check = checkSum(s[i].getSequence());
			out.append(" Name: ").append(name).append(" oo  Len:  ").append(s[i].getSequence().length()).append("  Check:  ").append(check).append("  Weight:  1.00\n");
      if (seq.length() > max) {
        max = seq.length();
      }
      if (name.length() > maxid) {
        maxid = name.length();
      }
      i++;
    }

    if (maxid < 10) {
      maxid = 10;
    }
    maxid++;
    out.append( "\n\n//\n\n");

    int len = 50;

    int nochunks =  max / len + 1;
    if (max%len == 0) {
      nochunks--;
    }
    for (i = 0; i < nochunks; i++) {
      int j = 0;
      while (j < s.length && s[j] != null) {
        String name =  s[j].getName();
          out.append(new Format("%-" + maxid + "s").form(name + "/" + s[j].getStart() + "-" + s[j].getEnd())).append(" ");
        for (int k = 0; k < 5; k++) {

          int start = i*50 + k*10;
          int end = start + 10;

          if (end < s[j].getSequence().length() && start < s[j].getSequence().length() ) {
            out.append(s[j].getSequence().substring(start,end));
            if (k < 4) {
              out.append(" ");
            } else {
              out.append("\n");
            }
          } else {
            if (start < s[j].getSequence().length()) {
              out.append(s[j].getSequence().substring(start));
              out.append("\n");
            } else {
              if (k == 0) {
                out.append("\n");
              }
            }
          }
        }
        j++;
      }
      out.append("\n");

    }
    return out.toString();
  }
  public String print() {
    return print(getSeqsAsArray());
  } 
}







