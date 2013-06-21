package pogvue.analysis;

import pogvue.datamodel.*;
import pogvue.gui.AlignViewport;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class AAFrequency {

  // Takes in a vector of sequences and column start and column end
  // and returns a vector of size (end-start+1). Each element of the
  // vector contains a hashtable with the keys being residues and
  // the values being the count of each residue in that column.
  // This class is used extensively in calculating alignment colourschemes
  // that depend on the amount of conservation in each alignment column.

  // This is used in the Renderers
  public static Vector calculate(Vector sequences, int start, int end) {

    Vector result = new Vector();

    for (int i = start; i <= end; i++) {

      Hashtable residueHash = new Hashtable();
      int maxCount          = -1;
      String maxResidue     = "-";
      int nongap            = 0;
      int totCount = 0;

      for (int j = 0; j < sequences.size(); j++) {

        if (sequences.elementAt(j) instanceof Sequence
	  && !(sequences.elementAt(j) instanceof GFF)) {

          Sequence s = (Sequence) sequences.elementAt(j);

          if (s.getName().indexOf("CpG") != 0
              && s.getName().indexOf("Wobble") != 0
              && s.getName().indexOf("Trans") != 0
              && s.getName().indexOf("RevTrans") != 0
              && s.getName().indexOf("Insert") != 0) {
            if (s.getSequence().length() > i) {

              String res = s.getSequence().substring(i, i + 1);

              if (!res.equals("-")) {
                nongap++;
                totCount++;
              }
              if (residueHash.containsKey(res)) {

                int count = (Integer) residueHash.get(res);
                count++;

                if (!res.equals("-") && count >= maxCount) {
                  maxCount = count;
                  maxResidue = res;
                }

                residueHash.put(res, count);
              } else {
                residueHash.put(res, 1);
              }

            } else {
              if (residueHash.containsKey("-")) {
                int count = (Integer) residueHash.get("-");
                count++;
                residueHash.put("-", count);
              } else {
                residueHash.put("-", 1);
              }
            }
          }
        }
      }

      residueHash.put("maxCount", maxCount);
      residueHash.put("totCount", totCount);
      residueHash.put("maxResidue", maxResidue);
      residueHash.put("size", sequences.size());
      residueHash.put("nongap", nongap);
      
      result.addElement(residueHash);
    }

    return result;
  }

  public static Vector calculatePID(Sequence refseq, Vector sequences,
      int window, int start, int end) {

    char bases[] = new char[5];

    bases[0] = 'A';
    bases[1] = 'T';
    bases[2] = 'C';
    bases[3] = 'G';
    bases[4] = 'N';

    Vector result = new Vector();
    boolean init = true;
    Vector prev = null;

    for (int i = start; i <= end; i++) {
      Vector values = new Vector();

      result.addElement(values);

      // If start < window/2 then set value to zero.

      if (i < window / 2 || i >= refseq.getSequence().length() - window / 2) {
        for (Object sequence : sequences) {
          values.addElement(0);
        }
      } else if (init) {
        init = false;

        int winstart = i - window / 2;
        int winend = i + window / 2;

        if (window % 2 != 0) {
          winend++;
        }

        for (Object sequence : sequences) {
          values.addElement(0);
        }

        // Calculate the first window fully

        for (int k = winstart; k <= winend; k++) {
          char refchar = refseq.getSequence().charAt(k);

          for (int j = 0; j < sequences.size(); j++) {

            if (refchar != '-') {

              Sequence s = (Sequence) sequences.elementAt(j);

              if (!(s instanceof GFF) && s.getSequence().length() > k) {

                char res = s.getSequence().charAt(k);

                if (res == refchar) {
                  int val = (Integer) values.elementAt(j);
                  val++;
                  values.setElementAt(val, j);
                }
              }
            }
          }
        }
        prev = values;
      } else {
        int winstart = i - window / 2;
        int winend = i + window / 2;

        if (window % 2 != 0) {
          winend++;
        }
        // We need to take the previous set of values
        // subtract the pid at winstart-1
        // and add the pid at winend;

        char pre_refchar = refseq.getSequence().charAt(winstart - 1);
        char pos_refchar = '-';

        if (refseq.getSequence().length() > winend) {
          pos_refchar = refseq.getSequence().charAt(winend);
        }

        for (int j = 0; j < sequences.size(); j++) {
          // First copy the pid value from i-1

          Sequence s = (Sequence) sequences.elementAt(j);

          if (!(s instanceof GFF) && winend < s.getLength()
              && winstart < s.getLength()) {

            int val = (Integer) prev.elementAt(j);

            char pre_char = s.getSequence().charAt(winstart - 1);
            char pos_char = '-';

            if (s.getSequence().length() > winend) {
              pos_char = s.getSequence().charAt(winend);
            }

            // Now substract 1 if the chars at winstart-1 match

            if (pre_refchar != '-' && pre_char == pre_refchar) {
              val--;
            }

            if (pos_refchar != '-' && pos_char == pos_refchar) {
              val++;
            }
            values.addElement(val);
          } else {
            values.addElement(0);
          }

        }
        prev = values;
      }
    }

    return result;
  }

  public static int[][] calculatePID_test(Sequence refseq, Vector sequences,
					  int window, int start, int end) {
    
    int numseqs = sequences.size();
    int seqlen = refseq.getLength();

    int result[][] = new int[end - start + 1][numseqs];

    boolean init = true;

    int intprev[] = new int[numseqs];

    int lengths[] = new int[numseqs];
    // String seqstr[] = new String[numseqs];
    Sequence seqstr[] = new Sequence[numseqs];
    Sequence seqs[] = new Sequence[numseqs];

    for (int j = 0; j < numseqs; j++) {
      lengths[j] = ((Sequence) sequences.elementAt(j)).getLength();
      seqstr[j]  = (Sequence) sequences.elementAt(j);
      seqs[j]    = (Sequence) sequences.elementAt(j);
    }

    for (int i = start; i <= end; i++) {
      int intvals[] = new int[numseqs];

      result[i - start] = intvals;

      // If start < window/2 then set value to zero.

      if (i < window / 2 || i >= seqlen - window / 2) {

        for (int j = 0; j < intvals.length; j++) {
          intvals[j] = 0;
        }

      } else if (init) {
        init = false;

        int winstart = i - window / 2;
        int winend = i + window / 2;

        if (window % 2 != 0) {
          winend++;
        }

        for (int j = 0; j < intvals.length; j++) {
          intvals[j] = 0;
        }

        // Calculate the first window fully

        for (int k = winstart; k <= winend; k++) {
          char refchar = seqstr[0].getCharAt(k);
			  
          for (int j = 0; j < numseqs; j++) {

            if (refchar != '-') {

              Sequence s = seqs[j];

              //if (!(s instanceof GFF) && lengths[j] > k) {
	      if (lengths[j] > k) {
                char res = seqstr[j].getCharAt(k);

                if (res == refchar) {
                  int val = intvals[j];
                  val++;
                  intvals[j] = val;
                }
              }
            }
          }
        }
        intprev = intvals;
      } else {
        int winstart = i - window / 2;
        int winend = i + window / 2;

        if (window % 2 != 0) {
          winend++;
        }
        // We need to take the previous set of values
        // subtract the pid at winstart-1
        // and add the pid at winend;

        char pre_refchar = seqstr[0].getCharAt(winstart - 1);
        char pre_refchar2 = seqstr[0].getCharAt(winstart - 2);
        char pre_refchar3 = seqstr[0].getCharAt(winstart - 3);
        char pre_refchar4 = seqstr[0].getCharAt(winstart - 4);

        char pos_refchar = '-';
        char pos_refchar2 = '-';
        char pos_refchar3 = '-';
        char pos_refchar4 = '-';

        if (seqlen > winend + 3) {
          pos_refchar = seqstr[0].getCharAt(winend);
          pos_refchar2 = seqstr[0].getCharAt(winend + 1);
          pos_refchar3 = seqstr[0].getCharAt(winend + 2);
          pos_refchar4 = seqstr[0].getCharAt(winend + 3);
        }

        for (int j = 0; j < numseqs; j++) {
          // First copy the pid value from i-1

          Sequence s = seqs[j];

          //if (!(s instanceof GFF) && winend < lengths[j]
	  if (winend < lengths[j]
              && winstart < lengths[j]) {

            int val = intprev[j];

            char pre_char = seqstr[j].getCharAt(winstart - 1);
            char pre_char2 = seqstr[j].getCharAt(winstart - 2);
            char pre_char3 = seqstr[j].getCharAt(winstart - 3);
            char pre_char4 = seqstr[j].getCharAt(winstart - 4);
            char pos_char = '-';
            char pos_char2 = '-';
            char pos_char3 = '-';
            char pos_char4 = '-';

            if (lengths[j] > winend + 3) {
              pos_char = seqstr[j].getCharAt(winend);
              pos_char2 = seqstr[j].getCharAt(winend + 1);
              pos_char3 = seqstr[j].getCharAt(winend + 2);
              pos_char4 = seqstr[j].getCharAt(winend + 3);

            }

            // Now substract 1 if the chars at winstart-1 match

            if (pre_refchar != '-' && pre_char == pre_refchar) {
              val--;
            }
            if (pre_refchar2 != '-' && pre_char2 == pre_refchar2) {
              val--;
            }
            if (pre_refchar3 != '-' && pre_char3 == pre_refchar3) {
              val--;
            }
            if (pre_refchar4 != '-' && pre_char4 == pre_refchar4) {
              val--;
            }

            if (pos_refchar != '-' && pos_char == pos_refchar) {
              val++;
            }
            if (pos_refchar2 != '-' && pos_char2 == pos_refchar2) {
              val++;
            }

            if (pos_refchar3 != '-' && pos_char3 == pos_refchar3) {
              val++;
            }
            if (pos_refchar4 != '-' && pos_char4 == pos_refchar4) {
              val++;
            }

            intvals[j] = val;
          } else {
            intvals[j] = 0;
          }

        }
        intprev = intvals;
      }

      if (i + 1 <= end) {
        result[i + 1 - start] = result[i - start];
      }
      if (i + 2 <= end) {
        result[i + 2 - start] = result[i - start];
      }
      if (i + 3 <= end) {
        result[i + 3 - start] = result[i - start];
      }
      i += 3;
    }

    return result;
  }

  public static Vector calculateFrameBasedPID(Sequence refseq,
      Vector sequences, int window, int start, int end) {

    Vector result = new Vector();
    boolean init = true;

    // System.out.println("Start/end " + start + " " + end);

    int count = 0;

    for (int i = start; i <= end; i++) {
      Vector values = new Vector();

      result.addElement(values);

      // If start < window/2 then set value to zero.

      if (i < window * 3 || i >= refseq.getSequence().length() - window * 3) {
        // System.out.println("Skipping" + i + " " + window*3);

        for (Object sequence : sequences) {
          values.addElement(0);
        }
      } else if (init && count < 3) {
        // System.out.println("Initializing");

        if (init) {
          count++;
        }
        if (count == 3) {
          init = false;
        }
        int winstart = i - window * 3;
        int winend = i + window * 3;

        for (Object sequence : sequences) {
          values.addElement(0);
        }

        for (int k = winstart; k <= winend; k += 3) {
          String refchar = refseq.getSequence().substring(k, k + 1);

          for (int j = 0; j < sequences.size(); j++) {

            if (!refchar.equals("-")) {

              Sequence s = (Sequence) sequences.elementAt(j);

              //if (!(s instanceof GFF) && s.getSequence().length() > k) {
              if (s.getSequence().length() > k) {

                String res = s.getSequence().substring(k, k + 1);

                // System.out.println("chars " + refchar + " " + res);

                if (res.equals(refchar)) {
                  int val = (Integer) values.elementAt(j);
                  val++;
                  // System.out.println("Init val " + i + " " + k + " " + val);
                  values.setElementAt(val, j);
                }
              }
            }
          }
        }
        // prev = values;
      } else {

        int winstart = i - window * 3;
        int winend = i + window * 3;

        // System.out.println("Slicing " + i + " " + winstart + " " + winend);

        // if (window%3 != 0) {
        // winend++;
        // }
        // We need to take the previous set of values
        // subtract the pid at winstart-3
        // and add the pid at winend;

        String pre_refchar = refseq.getSequence().substring(winstart - 3,
            winstart - 2);
        String pos_refchar = "-";

        if (refseq.getSequence().length() > winend) {
          pos_refchar = refseq.getSequence().substring(winend, winend + 1);
        }

        for (int j = 0; j < sequences.size(); j++) {

          // First copy the pid value from i-1

          Sequence s = (Sequence) sequences.elementAt(j);

          // System.out.println("Start end " + s.getName() + " " + winstart +
          // " " + winend + " " + s.getLength());
          //if (!(s instanceof GFF) && winend < s.getLength()
	      if (winend < s.getLength()
              && winstart < s.getLength()) {
            // int val = ((Integer)prev.elementAt(j)).intValue();

            Vector tmpvec = (Vector) (result.elementAt(i - 3 - start));
            int val = (Integer) tmpvec.elementAt(j);

            String pre_char = s.getSequence().substring(winstart - 3,
                winstart - 2);

            String pos_char = "-";

            if (s.getSequence().length() > winend) {
              pos_char = s.getSequence().substring(winend, winend + 1);
            }

            // Now substract 1 if the chars at winstart-4 match

            // System.out.println("I " + i + " winstart " + winstart +
            // " winend " + winend + " pre_refchar " + pre_refchar + " " +
            // pre_char + " pos_refchar " + pos_refchar + " " + pos_char);

            if (!pre_refchar.equals("-") && pre_char.equals(pre_refchar)) {
              val--;
              // System.out.println("Subtracting " + val);
            }

            // Add 1 if the chars at winend match
            if (!pos_refchar.equals("-") && pos_char.equals(pos_refchar)) {
              val++;
              // System.out.println("Adding " + val);
            }

            values.addElement(val);
          } else {
            values.addElement(0);
          }

        }
      }
    }

    return result;
  }

 
 
  public static Vector findKmerPeaks(Sequence seq0, Sequence seq, int start,
      int end, int window, int step, Vector kmers) {

    Vector feat = new Vector();

    Hashtable pos = findKmerCount(seq0, seq, start, end, window, step, kmers);

    Enumeration en = pos.keys();

    Vector posset = new Vector();

    while (en.hasMoreElements()) {
      posset.add(en.nextElement());
    }

    Collections.sort(posset);

    int prev = -1;
    int prevstart = -1;
    int gap = 200;
    int thresh = 30;

    for (int i = 0; i < posset.size(); i++) {
      Integer posInt = (Integer) posset.elementAt(i);
      int count = (Integer) pos.get(posInt);
      int posval = posInt;

      if (count > thresh) {

        if (prev == -1) {
          prevstart = posval;
          prev = posval;

        } else if (posval - prev > gap) {
          // Make new peak

          SequenceFeature sf = new SequenceFeature(null, "Peak", prevstart,
              prev, "Peak");

          feat.addElement(sf);

          prev = posval;
          prevstart = posval;
        } else if (posval - prev <= gap) {
          prev = posval;
        }
      }
    }
    if (prev != -1) {
      SequenceFeature sf = new SequenceFeature(null, "Peak", prevstart, prev,
          "Peak");
      feat.addElement(sf);
    }
    return feat;
  }

 
  public static Hashtable findKmerCount(Sequence seq0, Sequence seq, int start,
      int end, int window, int step, Vector kmers) {

    int tmpstart = start;
    Hashtable vals = new Hashtable();

    while (tmpstart <= end) {

      String tmpstr = seq.getSequence().substring(tmpstart - window / 2,
          tmpstart + window / 2);

      int count = 0;

      for (int ii = 0; ii < kmers.size(); ii++) {
        String kmer = ((Sequence) kmers.elementAt(ii)).getSequence();

        int i = -1;

        while (tmpstr.indexOf(kmer, i) != -1) {
          i = tmpstr.indexOf(kmer, i);
          if (seq0.getName().equals(seq.getName())) {
            count++;
          } else {
            count++;
          }
          i++;
        }
        ii++;
      }
      vals.put(tmpstart, count);
      tmpstart += step;
    }
    return vals;
  }

 
 
  public static Hashtable findConservedKmerCount(Sequence seq0, Sequence seq,
      int start, int end, int window, int step, Vector kmers) {

    int tmpstart = start;
    Hashtable vals = new Hashtable();

    while (tmpstart <= end) {

      String tmpstr = seq.getSequence().substring(tmpstart - window / 2,
          tmpstart + window / 2);

      int count = 0;

      for (int ii = 0; ii < kmers.size(); ii++) {
        String kmer = ((Sequence) kmers.elementAt(ii)).getSequence();

        int i = -1;

        while (tmpstr.indexOf(kmer, i) != -1) {
          i = tmpstr.indexOf(kmer, i);

          if (seq0.getSequence().substring(tmpstart - window / 2 + i,
              tmpstart - window / 2 + i + kmer.length()).equals(kmer)) {
            count++;
          }
          i++;
        }
        ii++;
      }
      vals.put(tmpstart, count);
      tmpstart += step;
    }
    return vals;
  }

  public static Hashtable findBlockStarts(Vector seqs, int start, int end,
      Vector exc) {

    // start and end are in real (not relative coords);

    // The coords in the hashtable that is returned are in relative coords
    // i.e. start from 0

    Hashtable blocks = new Hashtable();

    boolean prev = false;
    int bstart = -1;

    for (int i = start; i <= end; i++) {
      Sequence seq = (Sequence) seqs.elementAt(0);

      char c = seq.getCharAt(i);

      boolean found = true;

      int j = 1;

      while (j < seqs.size() && found) {

        Sequence jseq = (Sequence) seqs.elementAt(j);

        if (!exc.contains(jseq)) {

          char cc = jseq.getCharAt(i);

          if (cc != c) {
            found = false;
          }
        }
        j++;
      }

      if (!prev && found) {
        bstart = i;
      } else if (prev && !found && bstart != -1) {

        int blockstart = bstart - start;
        int blocklen = i - bstart;

        blocks.put(blockstart, blocklen);

        bstart = -1;
      }
      prev = found;
    }

    if (bstart != -1) {

      int blockstart = bstart - start;
      int blocklen = end - bstart;

      blocks.put(blockstart, blocklen);

    }
    return blocks;
  }

}
