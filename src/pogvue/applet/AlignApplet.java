package pogvue.applet;

import pogvue.datamodel.Alignment;
import pogvue.datamodel.Alignment;
import pogvue.datamodel.Sequence;
import pogvue.gui.*;
import pogvue.io.*;
import pogvue.gui.hub.*;
import pogvue.datamodel.*;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.io.IOException;
import java.util.*;

public class AlignApplet extends Applet {
    private String gfffile;
    private String seqfile;
    private String conffile;
    private String grffile;

    private String refseqfile;
    
    private String inputtype;
       
    AlignmentPanel ap;
    
    private double fontsize;
    String format;
    
    AlignViewport av;
    Controller    controller;

    private int offset;

    public void init() {
	seqfile    = getParameter("seqfile");
	refseqfile = getParameter("refseqfile");
	gfffile    = getParameter("gfffile");
	grffile    = getParameter("graphfile");
	conffile   = getParameter("gffconffile");
	inputtype  = getParameter("inputtype");
	offset     = Integer.parseInt(getParameter("offset"));
	fontsize   = Double.parseDouble(getParameter("fontsize"));

	makeFrame();
    }
    
    void makeFrame() {
        System.out.println("Using pog params for seqfile: " + seqfile + "\ngff: " + gfffile + "\ninputtype: " + inputtype + "\nfont: " + fontsize);

	try {
	    Sequence[] s = null;
	    Alignment al = null;
    
	    GappedFastaFile fastaFile = new GappedFastaFile(seqfile,inputtype);  
            s = fastaFile.getSeqsAsArray();

	    if (s != null) {
		al = new Alignment(s);
	    
		if (gfffile != null) {
		    GFFFile    gff   = new GFFFile(gfffile,inputtype);
		    Vector     feat  = gff.getGFFFeatures();
		    
		    for (int i = 0; i < feat.size(); i++) {
			al.addSequence((Sequence)feat.elementAt(i));
		    }
		}
		if (grffile != null) {
		    GraphFile grf = new GraphFile(grffile,"URL");
		    Hashtable   fhash  = grf.getFeatureHash();
		    Enumeration en     = fhash.keys();
		    
		    while (en.hasMoreElements()) {
			String type = (String)en.nextElement();
			GFF g = (GFF)fhash.get(type);
			if (type.equals("12merpvalue") || type.equals("omega") || type.equals("pi")) {
			    al.addSequence(new GFF("","",1,2));
			    al.addSequence(new GFF("","",1,2));
			}
			al.addSequence(g);
		    }
		}
            

	    }

	    if (al != null) {
		setLayout(new BorderLayout());
		JPanel jp = GenomeInfoFactory.makePanel(al,"Pogvue",.1,10,0,0,1000,650);
		add("Center",jp);
	    }
	    
	} catch (IOException e) {
	    System.out.println("Exception " + e);
	} catch (Exception ex) {
	    System.out.println("Exception " + ex);
	}
    }
}
