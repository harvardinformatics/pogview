package pogvue.gui;

import java.util.*;
import java.io.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;

import javax.swing.border.*;
import javax.swing.*;

import pogvue.datamodel.*;
import pogvue.datamodel.motif.*;
import pogvue.gui.event.*;
import pogvue.gui.hub.*;
import pogvue.analysis.*;
import pogvue.io.*;
import pogvue.util.*;
import pogvue.gui.*;

public final class Pogvue {

  public static void main(String[] args) {
    
    Hashtable opts = GetOptions.get(args);
    
    String seqfile   = null;
    String gfffile   = null;

    int    charheight    = 10;

    if (opts.containsKey("-seqfile")) {
      seqfile  = (String)opts.get("-seqfile");
    }
    if (opts.containsKey("-gfffile")) {
      gfffile  = (String)opts.get("-gfffile");
    }

    int width  = 1500;
    int height = 1000;

    Dimension  dim = Toolkit.getDefaultToolkit().getScreenSize();

    try {
      AlignFile ff = new FastaFile(seqfile,"File");
      GFFFile   gf = new GFFFile(gfffile,"File");

      Sequence[] seq = ff.getSeqsAsArray();
      GFF[]      gff  = new GFF[seq.length];

      LinkedHashMap typeorder   = AlignViewport.readGFFConfig("data/gff.conf", "File");
      Vector        tmpgenefeat = GFFFile.extractFeatures(gf.getFeatures(), "exon");
      Vector        tmpfeat     = gf.getFeatures();
	    
      tmpgenefeat = GFFFile.groupFeatures(tmpgenefeat, true);
      
      Vector geneFeat = SequenceFeature.hashFeatures(tmpgenefeat, 0, typeorder, true);
      Vector gffFeat  = SequenceFeature.hashFeatures(tmpfeat, 0, typeorder,false);


      int i = 0;
      while (i < seq.length) {
	Sequence s = seq[i];
	GFF      f = new GFF(s.getName(),s.getSequence(), 1,s.getLength());
	//SequenceFeature sf = new SequenceFeature(s.getSequence(),s.getName(),1,s.getLength(),"SEQ");
	
	//s.addFeature(sf);

	gff[i] = f;
	i++;
      }

      Alignment al = new Alignment(gff);

      al.addSequences(geneFeat);
      al.addSequences(gffFeat);

      System.out.println("Number of sequences " + seq.length);


      double          cw  = (width-150)*1.0/al.getWidth();      

      // Alignment, title, width1, width2, offset, start, end, width
      AlignSplitPanel asp = GenomeInfoFactory.makePanel(al,"Pogvue",cw,1,1,1,1000000,width-150);	    

      asp.getAlignmentPanel2().getAlignViewport().setCharHeight(charheight);
      //AlignmentPanel ap = new AlignmentPanel(al);
      JFrame          jf  = new JFrame("Pogvue");
	  
      jf.getContentPane().add(asp);
      
      jf.setLocation(dim.width  / 2 - width / 2,
		     dim.height / 2 - height / 2);
      
      jf.setSize(width,height);
      jf.setVisible(true);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}


