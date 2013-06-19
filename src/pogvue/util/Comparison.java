package pogvue.util;

import pogvue.datamodel.Sequence;
import pogvue.datamodel.Sequence;

public class Comparison {

  public static float compare(Sequence ii, Sequence jj) {
    return Comparison.compare(ii,jj,0,ii.getLength());
  }
  public static float compare(Sequence ii, Sequence jj, int start, int end) {
    String si   = ii.getSequence();
    String sj   = jj.getSequence();

    int ilen = end-start+1;
    int jlen = end-start+1;

    if ( si.substring(start + ilen).equals("-") ||
         si.substring(start + ilen).equals(".") ||
         si.substring(start + ilen).equals(" ")) {

      ilen--;

      while (si.substring(start + ilen,start + ilen+1).equals("-")  ||
             si.substring(start + ilen,start + ilen+1).equals(".")  ||
             si.substring(start + ilen,start + ilen+1).equals(" ")) {
        ilen--;
      }
    }

    if ( sj.substring(start + jlen).equals("-")  ||
         sj.substring(start + jlen).equals(".")  ||
         sj.substring(start + jlen).equals(" ")) {
      jlen--;

      while (sj.substring(start + jlen,start + jlen+1).equals("-")  ||
             sj.substring(start + jlen,start + jlen+1).equals(".")  ||
             sj.substring(start + jlen,start + jlen+1).equals(" ")) {
        jlen--;
      }
    }

    int   count = 0;
    int   match = 0;
    float pid;

    int len = ilen;
    if (ilen > jlen) {
       len = jlen;
    }

    if (ilen > jlen) {

      for (int j = 0; j < jlen; j++) {
        String s1 = si.substring(start + j, start + j + 1);
        String s2 = sj.substring(start + j, start + j + 1);
        if (!(s1.equals("-") || s2.equals("-"))) {
        if (si.substring(start + j,start + j+1).equals(sj.substring(start + j,start + j+1))) {
          match++;
        }
        count++;
        }
      }
      pid = (float)match/(float)ilen * 100;
    } else {
      for (int j = 0; j < jlen; j++) {
        String s1 = si.substring(start + j, start + j + 1);
        String s2 = sj.substring(start + j, start + j + 1);
        if (!(s1.equals("-") || s2.equals("-"))) {
        if (si.substring(start + j,start + j+1).equals(sj.substring(start + j,start + j+1))) {
          match++;
        }
        count++;
        }
      }
      pid = (float)match/(float)jlen * 100;
    }

    return pid;
  }

  /**
	 * @param s1
	 * @param s2		*/
  public static float PID(Sequence s1 , Sequence s2) {
    int res = 0;
    int len;

    if (s1.getSequence().length() > s2.getSequence().length()) {
      len = s1.getSequence().length();
    } else {
      len = s2.getSequence().length();
    }

    int bad = 0;

    for (int i = 0; i < len; i++) {
      String str1;
      String str2;

      if (i < s1.getSequence().length()) {
        str1 = s1.getSequence().substring(i,i+1);
      } else {
        str1 = ".";
      }

      if (i < s2.getSequence().length()) {
        str2 = s2.getSequence().substring(i,i+1);
      } else {
        str2 = ".";
      }

      if (!(str1.equals(".") ||
            str1.equals("-") ||
            str1.equals(" "))   &&
          !(str2.equals(".") ||
            str2.equals("-") ||
            str2.equals(" "))) {

        if (!str1.equals(str2)) {
          bad++;
        }
      }
    }

    return (float)100*(len-bad)/len;
  }
}
