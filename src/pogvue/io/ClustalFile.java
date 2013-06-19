package pogvue.io;

import pogvue.datamodel.Sequence;
import pogvue.datamodel.Sequence;
import pogvue.util.Format;

import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

public class ClustalFile extends AlignFile {

  private Vector ids;

    public ClustalFile(Sequence[] s) {
	super(s);
    }

  public ClustalFile(String inStr) {
    super(inStr);
  }

  public void initData() {
    super.initData();
    ids = new Vector();
  }

  public ClustalFile(String inFile, String type) throws IOException {
    super(inFile,type);
  }

  public void parse() {
    int     i;
    boolean flag = false;

    Vector    headers = new Vector();
    Hashtable seqhash = new Hashtable();

    String line;
    
    try {
      while ((line = nextLine()) != null) {
	if (line.indexOf(" ") != 0) {
	  StringTokenizer str = new StringTokenizer(line," ");
	  String id;
	  
	  if (str.hasMoreTokens()) {
	    id = str.nextToken();
	    if (id.equals("CLUSTAL")) {
	      flag = true;
	    } else {
	      if (flag) {
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
      }
    } catch (IOException e) {
      System.out.println("Exception parsing clustal file " + e);
    }

    if (flag) {
      this.noSeqs = headers.size();

      //Add sequences to the hash
      for (i = 0; i < headers.size(); i++ ) {
        int start;
        int end;

        if ( seqhash.get(headers.elementAt(i)) != null) {
          if (maxLength <  seqhash.get(headers.elementAt(i)).toString().length() ) {
            maxLength =  seqhash.get(headers.elementAt(i)).toString().length();
          }
          String head =  headers.elementAt(i).toString();
          start = 1;
          end   =  seqhash.get(headers.elementAt(i)).toString().length();

          if (head.indexOf("/") > 0 ) {
            StringTokenizer st = new StringTokenizer(head,"/");
            if (st.countTokens() == 2) {

              ids.addElement(st.nextToken());

              String tmp = st.nextToken();
              st = new StringTokenizer(tmp,"-");
              if (st.countTokens() == 2) {
                start = Integer.valueOf(st.nextToken());
                end = Integer.valueOf(st.nextToken());
              }
            } else {
              ids.addElement(headers.elementAt(i));
            }
          }  else {
            ids.addElement(headers.elementAt(i));

          }
          Sequence newSeq = new Sequence(ids.elementAt(i).toString(),
                                         seqhash.get(headers.elementAt(i).toString()).toString(),start,end);

          seqs.addElement(newSeq);

        } else {
          System.out.println("Can't find sequence for " + headers.elementAt(i));
        }
      }
    }

  }

  public String print() {
    return print(getSeqsAsArray());
  } 
  public static String print(Sequence[] s) {
    StringBuffer out = new StringBuffer("CLUSTAL\n\n");

    int max = 0;
    int maxid = 0;

    int i = 0;

    while (i < s.length && s[i] != null) {
      String tmp = s[i].getName() + "/" + s[i].getStart() + "-" + s[i].getEnd();

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
    maxid++;
    int len = 60;
    int nochunks =  max / len + 1;

    for (i = 0; i < nochunks; i++) {
      int j = 0;
      while ( j < s.length && s[j] != null) {
          out.append(new Format("%-" + maxid + "s").form(s[j].getName() + "/" + s[j].getStart() + "-" + s[j].getEnd())).append(" ");
        int start = i*len;
        int end = start + len;

        if (end < s[j].getSequence().length() && start < s[j].getSequence().length() ) {
            out.append(s[j].getSequence().substring(start, end)).append("\n");
        } else {
          if (start < s[j].getSequence().length()) {
              out.append(s[j].getSequence().substring(start)).append("\n");
          }
        }
        j++;
      }
      out.append("\n");

    }
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

}
