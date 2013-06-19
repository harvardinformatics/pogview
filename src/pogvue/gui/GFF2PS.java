package pogvue.gui;

import org.jibble.epsgraphics.EpsGraphics2D;
import java.util.*;
import java.io.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;

import javax.swing.*;

import pogvue.datamodel.*;
import pogvue.gui.event.*;
import pogvue.gui.hub.*;
import pogvue.analysis.*;
import pogvue.io.*;
import pogvue.util.*;


public class GFF2PS {
       
    public static void main(String[] args) {
	
	Alignment al  = null;

	    Hashtable opts = GetOptions.get(args);
	    
	    String chr       = null;
	    int    chrstart  = -1;
	    int    chrend    = -1;
	    int    pad       = 0;
	    String gfffile   = null;
	    boolean overwrite = false;

	    if (opts.containsKey("-chr")) {
		chr = (String)opts.get("-chr");
	    }
	    if (opts.containsKey("-start")) {
		chrstart = Integer.parseInt((String)opts.get("-start"));
	    }
	    if (opts.containsKey("-end")) {
		chrend = Integer.parseInt((String)opts.get("-end"));
	    }
	    if (opts.containsKey("-pad")) {
		pad = Integer.parseInt((String)opts.get("-pad"));
	    }
	    if (opts.containsKey("-gfffile")) {
		gfffile =  (String)opts.get("-gfffile");
	    }
	    if (opts.containsKey("-overwrite")) {
		overwrite = true;
	    }
	    
	    if (gfffile == null && (chr == null || chrstart == -1 || chrend == -1)) {
		System.out.println("Command line is: java pogvue.gui.GFF2PS -chr <chr> -start <start> -end <end> or -gfffile <gfffile> -pad <pad>");
		System.exit(0);
	    }

	    if (gfffile != null) {
		try {
		    GFFFile gff = new GFFFile(gfffile,"File");
		    
		    Vector feat = gff.getFeatures();
		    
		    for (int i = 0; i < feat.size(); i++) {
			SequenceFeature sf = (SequenceFeature)feat.elementAt(i);
			
			String file = sf.getHitFeature().getId() + ".ps";
			System.out.println(file);
			
			File f = new File(file);
			
			if (overwrite || !f.exists()) {
			    region2ps(sf.getId(),sf.getStart()-pad,sf.getEnd()+pad,file);
			}
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		}

	    }

	    if (!(chr == null || chrstart == -1 || chrend == -1)) {
		chrstart -= pad;
		chrend   += pad;

		String file = chr + "." + chrstart + "-" + chrend + ".ps";

		region2ps(chr,chrstart,chrend,file);
	    }
	    
	    System.exit(0);
    }

    public static void region2ps(String chr, int chrstart, int chrend, String file) {
      Alignment al  = GenomeInfoFactory.requestRegion(chr,chrstart,chrend,new Vector(),null);
	    
	if (al != null) {
	    al.setChrRegion(new ChrRegion(chr,chrstart,chrend));
	    
	    int width  = al.getWidth()*3 + 150;

	    
	    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	    double    cw = (width-150)*1.0/al.getWidth();
	    
	    AlignSplitPanel asp = GenomeInfoFactory.makePanel(al,"Pogvue",cw,100,chrstart,chrstart,chrend,width-150);	    
	    
	    asp.setDividerLocation(1.0);

	    AlignViewport av = asp.getAlignmentPanel1().getAlignViewport();

	    int height = 1000;

	    SequenceFetchThread sft = new SequenceFetchThread(av.getController(),av);
	    sft.run();
	    
	    
	    Sequence tmp = new Sequence("Logo","",1,1000);
	    
	    Sequence tmp2 = new Sequence("","",1,1);
	    av.getAlignment().addSequence(tmp2);
	    av.getAlignment().addSequence(tmp2);
	    av.getAlignment().addSequence(tmp2);
	    av.getAlignment().addSequence(tmp2);
	    av.getAlignment().addSequence(tmp2);
	    av.getAlignment().addSequence(tmp);
	    
	    
	    SearchWorker sw = new SearchWorker(al.getSequenceAt(0).getSequence(),0);
	    
	    sw.setViewport(av);
	    sw.setThreshold(0.9);
	    
	    sw.run();
	    
	    while (sw.getOutput() == null) {
		
		try {Thread.sleep(100);} catch (InterruptedException e){e.printStackTrace();}
		
	    }
	    Vector out = sw.getOutput();
	    
	    if (out != null && out.size() > 0) {
		
		GFF top = new GFF("Transfac","",1,2);
		GFF dum = new GFF("Transfac","",1,2);
		
		for (int i = 0 ; i < out.size(); i++) {
		    SequenceFeature sf = (SequenceFeature)out.elementAt(i);
		    
		    top.addFeature(sf);
		}
		
		Vector newgff = GFFFile.bumpGFF_nosort(top);
		
		for (int i = 0 ; i < newgff.size(); i++) {
		    GFF tmpgff = (GFF)newgff.elementAt(i);
		    av.getAlignment().addSequence(dum);
		    av.getAlignment().addSequence(dum);
		    av.getAlignment().addSequence(dum);
		    av.getAlignment().addSequence(tmpgff);
		}
		
		
	    }
	    
	    //asp.getAlignmentPanel1().setDividerLocation(0.0D);	    

	    asp.revalidate();
	    asp.repaint();
	    
	    JFrame          jf  = new JFrame(chr + ":" + chrstart + "-" + chrend);		
	    
	    jf.getContentPane().add(asp);
	    
	    jf.setLocation(dim.width  / 2 - width / 2,
			   dim.height / 2 - height / 2);
	    
	    jf.pack();
	    
	    jf.setSize(width,height);
	    

	    try {Thread.sleep(2000);} catch (InterruptedException e){e.printStackTrace();}
	    
	    try {
		
		av.useImage(false);

		
		AlignmentPanel ap1 = asp.getAlignmentPanel1();
		AlignmentPanel ap2 = asp.getAlignmentPanel2();
		
		FileOutputStream outputStream = new FileOutputStream(file);
		
		//EpsGraphics2D g = new EpsGraphics2D("demo",outputStream,0,0,ap1.size().width,ap1.size().height);
		EpsGraphics2D g = new EpsGraphics2D("demo",outputStream,0,0,width,height);
		System.out.println("Painting postscript " + width + " " + height);
		ap1.paint(g);
		System.out.println("Done");
		
		g.flush();
		g.close();
		
		
	    } catch (Exception e2) {
		e2.printStackTrace();
	    }
	    
	    av.useImage(true);
	    //jf.setVisible(true);	      		
	}
    }
    
}



