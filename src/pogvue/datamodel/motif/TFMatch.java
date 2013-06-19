package pogvue.datamodel.motif;


import java.util.*;

import pogvue.datamodel.*;
import pogvue.analysis.*;


public class TFMatch {

  private TFMatrix mat;

  private String   str1;
  private String   str2;
  private double   score;
  private int      strand;

  public TFMatch(TFMatrix mat,String str1, String str2, double score, int strand) {

    this.mat    = mat;
    this.str1   = str1;
    this.str2   = str2;
    this.strand = strand;
    this.score  = score;
  }

  public double getScore() {
	  return score;
  }

  public void print() {
    String name = mat.getName();
    Pwm    pwm  = mat.getPwm();

    System.out.println(name + "\t" + score + "\t" + strand);

    System.out.println("\n\t" + str2);

    int k = 0;

    System.out.print("\t");
    
    while (k < str1.length()) {

      if (str2.substring(k,k+1).equals(str1.substring(k,k+1))) {
	System.out.print("|");
      } else {
	System.out.print(" ");
      }
      k++;
    }
    System.out.println("\n\t" + str1 + "\n");
  }

}
 


