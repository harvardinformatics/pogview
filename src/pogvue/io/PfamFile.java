package pogvue.io;

import pogvue.datamodel.*;
import pogvue.util.*;

import java.io.*;
import java.util.*;

public class PfamFile extends AlignFile {

  Vector ids;

    public PfamFile(Sequence[] s) {
	super(s);
    }

  public PfamFile(String inStr) {
    super(inStr);
  }

  public void initData() {
    super.initData();
    ids = new Vector();
  }

  public PfamFile(String inFile, String type) throws IOException {
    super(inFile,type);
  }

  public void parse() {
    int i = 0;  
    String line;


    Hashtable seqhash = new Hashtable();
    Vector    headers = new Vector();

    try {
      while ((line = nextLine()) != null) {
      if (line.indexOf(" ") != 0) {
        if (line.indexOf("#") != 0) {

          StringTokenizer str = new StringTokenizer(line," ");
          String id = "";

          if (str.hasMoreTokens()) {
            id = str.nextToken();

            StringBuffer tempseq;

            if (seqhash.containsKey(id)) {
              tempseq = (StringBuffer)seqhash.get(id);
            } else {
	      tempseq = new StringBuffer();
	      seqhash.put(id,tempseq);
	    }

            if (!(headers.contains(id))) {
              headers.addElement(id);
            }

            tempseq.append(str.nextToken());

          }
        }
      }
      }
    } catch (IOException e) {
      System.out.println("IOException parsing pfam file " + e);
    }

    this.noSeqs = headers.size();


    for (i = 0; i < headers.size(); i++ ) {

      if ( seqhash.get(headers.elementAt(i)) != null) {
        if (maxLength <  seqhash.get(headers.elementAt(i)).toString().length() ) {
          maxLength =  myHash.get(headers.elementAt(i)).toString().length();
        }
        String head =  headers.elementAt(i).toString();
        int start = 1;
        int end =  myHash.get(headers.elementAt(i)).toString().length();

        if (head.indexOf("/") > 0 ) {
          StringTokenizer st = new StringTokenizer(head,"/");
          if (st.countTokens() == 2) {
            ids.addElement(st.nextToken());
            String tmp = st.nextToken();
            st = new StringTokenizer(tmp,"-");
            if (st.countTokens() == 2) {
              start = Integer.valueOf(st.nextToken()).intValue();
              end = Integer.valueOf(st.nextToken()).intValue();
            } else {
              start = -1;
              end = -1;
            }
          } else {
            ids.addElement(headers.elementAt(i));
          }
        } else {
          ids.addElement(headers.elementAt(i));
        }

        if (start != -1 && end != -1) {
          Sequence newSeq = new Sequence(ids.elementAt(i).toString(),
                                         seqhash.get(headers.elementAt(i).toString()).toString(),start,end);
          seqs.addElement(newSeq);
        } else {
          Sequence newSeq = new Sequence(ids.elementAt(i).toString(),
                                         seqhash.get(headers.elementAt(i).toString()).toString(),1,
                                         seqhash.get(headers.elementAt(i).toString()).toString().length());
          seqs.addElement(newSeq);
        }

      } else {
        System.out.println("Can't find sequence for " + headers.elementAt(i));
      }
    }

  }

  public static String print(Sequence[] s) {
    StringBuffer out = new StringBuffer("");

    int max = 0;
    int maxid = 0;

    int i = 0;

    while (i < s.length && s[i] != null) {
      String tmp = s[i].getName() + "/" + s[i].getStart()+ "-" + s[i].getEnd();

      if (s[i].getSequence().length() > max) {
        max = s[i].getSequence().length();
      }
      if (tmp.length() > maxid) {
        maxid = tmp.length();
      }
      i++;
    }

    if (maxid < 15) {
      maxid = 15;
    }

    int j = 0;
    while ( j < s.length && s[j] != null) {
      out.append( new Format("%-" + maxid + "s").form(s[j].getName() + "/" + s[j].getStart() + "-" + s[j].getEnd() ) + " ");

      out.append(s[j].getSequence() + "\n");
      j++;
    }
    out.append("\n");

    return out.toString();
  }


  public static void main(String[] args) {
    String inStr = "CLUSTAL\n\nt1  GTGASAAATGGNNTGATTCTGTACCTTGTGGAGACTGGCGTGATGTGCAG\nt2  AAATGATTCTGTACCTTGTGGATGGACTGGCGTGATGTGCAGCAACTATT\n\nt1  CAACTATTCGANNGTGATCCAGTGGTTTTGTCGTTGAATCTGTCTTCGAT\nt2  CGAGTGATCCAGAGGTTTTGTCCTTGAATCTGTCTTCGATGGTTCTCTCG\n\nt1  GGTTCTCGGGTAAGCTATCACCAAGCATAGGTGGATTGGTTCATCTGAAG\nt2  GGTAAGATCCACCAAGCATATGCTAGCT\n ";
    ClustalFile msf = new ClustalFile(inStr);
    Sequence[] s = new Sequence[msf.seqs.size()+1];

    for (int i=0;i < msf.seqs.size();i++) {
      s[i] = (Sequence)msf.seqs.elementAt(i);
    }
    String outStr = msf.print(s);
    System.out.println(outStr);
  }
  public String print() {
    return print(getSeqsAsArray());
  }
}
