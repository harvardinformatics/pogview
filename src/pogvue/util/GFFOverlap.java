package pogvue.util;

import pogvue.datamodel.SequenceFeature;
import pogvue.io.GFFFile;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class GFFOverlap {

  public static void main(String args[]) {
	try {
    GFFFile gfffile1 = new GFFFile(args[0],"File");
    GFFFile gfffile2 = new GFFFile(args[1],"File");

    Vector  feat1    = gfffile1.getFeatures();
    Vector  feat2    = gfffile2.getFeatures();

    Hashtable hash1 = new Hashtable();
    Hashtable hash2 = new Hashtable();


    for (int i = 0; i < feat1.size(); i++) {
      SequenceFeature sf = (SequenceFeature)feat1.elementAt(i);

      if (hash1.containsKey(sf.getId())) {
         Vector tmp = (Vector)hash1.get(sf.getId());

         tmp.addElement(sf);
      } else {
         Vector tmp = new Vector();
         tmp.addElement(sf);
         hash1.put(sf.getId(),tmp);
      }
    }

    for (int i = 0; i < feat2.size(); i++) {
      SequenceFeature sf = (SequenceFeature)feat2.elementAt(i);

      if (hash2.containsKey(sf.getId())) {
         Vector tmp = (Vector)hash2.get(sf.getId());

         tmp.addElement(sf);
      } else {
         Vector tmp = new Vector();
         tmp.addElement(sf);
         hash2.put(sf.getId(),tmp);
      }
    }

    Enumeration en = hash1.keys();

    while (en.hasMoreElements()) {
      String chr = (String)en.nextElement();

      Vector f1 = (Vector)hash1.get(chr);
      Vector f2 = (Vector)hash2.get(chr);

      if (f2 != null) {
      for (int i = 0;i < f1.size(); i++) {
         SequenceFeature found2 = null;
         int found = 0;
         SequenceFeature sf1 = (SequenceFeature)f1.elementAt(i);

//	  System.out.println("Searching for " + sf1.getStart() + "\t" + sf1.getEnd());
          for (int j = 0; j < f2.size(); j++) {
             SequenceFeature sf2 = (SequenceFeature)f2.elementAt(j);	

//	  System.out.println("\t-\tTesting with " + sf2.getStart() + "\t" + sf2.getEnd());
              if (!(sf1.getStart() > sf2.getEnd() ||
                    sf1.getEnd() < sf2.getStart())) {
                  found = 1;
                  found2 = sf2;
                  j = f2.size();
              }
          }
  
          if (found == 0) {
              System.out.println(sf1.getId() + "\tno_overlap\tno_overlap\t" + sf1.getStart() + "\t" + sf1.getEnd() + "\t" + sf1.getScore() + "\t" + sf1.getStrand() + "\t.");	
          } else {
              System.out.println(sf1.getId() + "\toverlap\t" + found2.getType() + "_" + found2.getType2() + "\t" + sf1.getStart() + "\t" + sf1.getEnd() + "\t" + sf1.getScore() + "\t" + sf1.getStrand() + "\t.");	

          }
    }
    }
  }
  } catch (IOException e) {
      System.out.println("Exception : " + e);
  }
}
}
