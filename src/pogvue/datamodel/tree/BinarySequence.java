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
package pogvue.datamodel.tree;

import pogvue.gui.schemes.ResidueProperties;
import pogvue.datamodel.*;

public class BinarySequence extends Sequence {
  public static void printMemory(Runtime rt) {
    System.out.println("Free memory = " + rt.freeMemory());
  }
  private int[] binary;

  private double[] dbinary;

  public BinarySequence(Sequence s) {
    super(s.getName(),s.getSequence(),s.getStart(),s.getEnd());
  }

  public BinarySequence(String name, String sequence, int start, int end) {
    super(name,sequence,start,end);
  }

  public void blosumEncode() {

    // Set all matrix to 0
    dbinary = new double[getSequence().length() * 21];
    int nores = 21;
    //for (int i = 0; i < dbinary.length; i++) {
    //  dbinary[i] = 0.0;
    //}

    for (int i=0; i < getSequence().length(); i++ ) {
      int aanum;
      try {
        aanum = (Integer) ResidueProperties.getAAHash().get(getSequence().substring(i, i + 1));
      } catch (NullPointerException e) {
        aanum = 20;
      }
      if (aanum > 20) {
        aanum = 20;
      }

      // Do the blosum thing
      for (int j = 0;j < 20;j++) {
        dbinary[i * nores + j] = ResidueProperties.getBLOSUM62()[aanum][j];
      }

    }
  }

  public void encode() {
    // Set all matrix to 0
    dbinary = new double[getSequence().length() * 21];
    int nores = 21;
    for (int i = 0; i < dbinary.length; i++) {
      dbinary[i] = 0.0;
    }

    for (int i=0; i < getSequence().length(); i++ ) {
      int aanum;
      try {
        aanum = (Integer) ResidueProperties.getAAHash().get(getSequence().substring(i, i + 1));
      } catch (NullPointerException e) {
        aanum = 20;
      }
      if (aanum > 20) {
        aanum = 20;
      }

      dbinary[i* nores + aanum] = 1.0;

    }
  }

  public double[] getDBinary() {
    return dbinary;
  }

  public String toBinaryString() {
    String out = "";
    for (int i=0; i < binary.length;i++) {
      out += (new Integer(binary[i])).toString();
      if (i < binary.length-1) {
        out += " ";
      }
    }
    return out;
  }
}
