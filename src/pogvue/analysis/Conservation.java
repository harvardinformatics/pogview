/* Jalview - a java multiple alignment editor
 * Copyright (C) 1998  Michele Clamp
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package pogvue.analysis;

import pogvue.datamodel.Sequence;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public final class Conservation {
  private final Vector sequences;
  private final int    start;
  private final int    end;

  private final Vector total = new Vector();

  final String consString = "";

  private final Hashtable        propHash;
  private final int              threshold;
  private final Hashtable[]      freqs;

  private String name = "";

  public Conservation(String name,Hashtable[] freqs,Hashtable propHash, int threshold, Vector sequences, int start, int end) {
    this.name      = name;
    this.freqs     = freqs;
    this.propHash  = propHash;
    this.threshold = threshold;
    this.sequences = sequences;
    this.start     = start;
    this.end       = end;
  }


  public void  calculate() {

    for (int i = start;i <= end; i++) {
      Hashtable resultHash;
      Hashtable residueHash;

      resultHash  = new Hashtable();
      residueHash = new Hashtable();

      for (int j=0; j < sequences.size(); j++) {

        if (sequences.elementAt(j) instanceof Sequence) {
          Sequence s = (Sequence)sequences.elementAt(j);

          if (s.getSequence().length() > i) {
            String res = s.getSequence().substring(i,i+1);

            if (residueHash.containsKey(res)) {
              int count = (Integer) residueHash.get(res);
              count++;
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

      //What is the count threshold to count the residues in residueHash()
      int thresh = threshold*(sequences.size())/100;

      //loop over all the found residues
      Enumeration e = residueHash.keys();

      while (e.hasMoreElements()) {

        String res = (String)e.nextElement();
        if ((Integer) residueHash.get(res) > thresh) {

          //Now loop over the properties
          Enumeration e2 = propHash.keys();

          while (e2.hasMoreElements()) {
            String    type = (String)e2.nextElement();
            Hashtable ht   = (Hashtable)propHash.get(type);

            //Have we ticked this before?
            if (! resultHash.containsKey(type)) {
              if (ht.containsKey(res)) {
                resultHash.put(type,ht.get(res));
              } else {
                resultHash.put(type,ht.get("-"));
              }
            } else if (!resultHash.get(type).equals(ht.get(res))) {
              resultHash.put(type, -1);
            }
          }
        }
      }
      total.addElement(resultHash);
    }
  }

  private int countGaps(int j) {
    int count = 0;

    for (int i = 0; i < sequences.size();i++) {
      String tmp = ((Sequence)sequences.elementAt(i)).getSequence().substring(j,j+1);
      if (tmp.equals(" ") || tmp.equals(".") || tmp.equals("-")) {
        count++;
      }
    }
    return count;
  }

  public  void  verdict(boolean consflag, float percentageGaps) {
    String consString = "";

    for (int i=start; i <= end; i++) {
      int totGaps = countGaps(i);
      float pgaps = (float)totGaps*100/sequences.size();

      if (percentageGaps > pgaps) {
        Hashtable resultHash = (Hashtable)total.elementAt(i);

        //Now find the verdict
        int         count = 0;
        Enumeration e3    = resultHash.keys();

        while (e3.hasMoreElements()) {
          String type    = (String)e3.nextElement();
          Integer result = (Integer)resultHash.get(type);

          //Do we want to count +ve conservation or +ve and -ve cons.?

          if (consflag) {
            if (result == 1) {
              count++;
            }
          } else {
            if (result != -1) {
              count++;
            }
          }
        }

        if (count < 10) {
          consString = consString + String.valueOf(count);
        } else {
          consString = consString + "*";
        }
      } else {
        consString = consString + "-";
      }
    }
	}
}
