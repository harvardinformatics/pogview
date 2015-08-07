package pogvue.gui.hub;

import pogvue.io.AlignFile;

import pogvue.io.BamFile;
import pogvue.io.FastaFile;
import pogvue.io.FileParse;
import pogvue.datamodel.GFF;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.MalformedURLException;
import pogvue.io.*;
import pogvue.gui.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;

import pogvue.gui.menus.*;
import pogvue.gui.renderer.GraphRenderer;

import pogvue.datamodel.*;
import java.io.InputStreamReader;

/**
 * Created by IntelliJ IDEA. User: mclamp Date: Aug 11, 2007 Time: 4:55:46 PM To
 * change this template use File | Settings | File Templates.
 */
public class GenomeInfoFactory {
  // public static final String urlstub =
  // "http://www.broad.mit.edu/~mclamp/alpheus/";
  // public static final String fastaurlstub =
  // "http://www.broad.mit.edu/~mclamp/";
  // public static final String urlstub = "http://srv/~mclamp/";
  public static final String urlstub        = "http://localhost:8080/~mclamp/";
  public static final String fastaurlstub   = AlignViewport.getFastaURL();

  public static final String geneidstub     = "getGenesById.php?";
  public static final String chromosomestub = "queryChromosomes.php";
  public static final String regionstub     = "fetchmam.php?";
  public static final String graphstub      = "fetchmamgraph.php?";
  public static final String humanstub      = "fetchhuman.php?";
  public static final String featurestub    = "fetchmamgff.php?";
  public static final String genestub       = "fetchgene.php?";
  public static final String repeatstub     = "fetchrepeat.php?";
  public static final String mrnastub       = "fetch_mrna.php?";
  public static final String geneinfostub   = "fetch_geneinfo.php?";

  // This is used in the tabbed fetcher but I'm not sure it actually does anything
  public static BufferedReader getGenesById(String idstr) throws IOException {
    String query = urlstub + geneidstub + "search_str=" + idstr;

    FileParse fp = new FileParse(query, "URL");

    return fp.getBufferedReader();
  }

  // Used a lot  - threeof  these should at least use ChrRegion which is passed into URLRegion to be converted 
  public static GappedFastaFile getRegion(String chr, int start, int end)
      throws IOException {
    String regionStr = "query=" + chr + "&start=" + start + "&end=" + end
        + "&z=2";

    return getRegion(regionStr);
  }

  public static FastaFile getUngappedRegion(String chr, int start, int end)
    throws IOException {
    String regionStr = "query=" + chr + "&start=" + start + "&end=" + end
        + "&z=2";
     try {
      //chr = "strCam." + chr;
      chr = "galGal." + chr;
      System.out.println("REGIOASDFASDF " + chr + " " + start + " " + end);
      Process p;
      p = Runtime.getRuntime().exec("./perl/get_maf.pl " + chr + " " + start + " " + end);
      p.waitFor();

      BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));


      //String line = reader.readLine();
      //while (line != null) {
      //   System.out.println("LIJE " + line);
      //   line = reader.readLine();
     // }
       FastaFile fp = new FastaFile(reader);
       return fp;
    } catch (InterruptedException ex) {
       System.out.println("Interrupted " + ex);
    } 
    //return getUngappedRegion(regionStr);
    return null;
  }

  //Used a lot 
  public static GappedFastaFile getRegion(String regionStr) throws IOException {
    return getRegion(regionStr, false);
  }

  // 
  public static GappedFastaFile getRegion(String regionStr, boolean human)
      throws IOException {
    String query = AlignViewport.getFastaURL() + regionStr;

    if (human) {
      query = query + "&org=human";
    }

    GappedFastaFile fp = new GappedFastaFile(query, "URL", false);

    return fp;
  }
  public static FastaFile getHumanRegion(String regionStr) throws IOException{
    String query    = fastaurlstub + regionStr;
    System.out.println("Query " + query);
    FastaFile fp = new FastaFile(query,"URL",false);
    
    return fp;
  }
  public static FastaFile getUngappedRegion(String regionStr) throws IOException{

    String query    = fastaurlstub + regionStr;
    System.out.println("Query " + query);

    return null;
  }


  // Three times used - Needs ChrRegion - Also ActionListener - is this the right place
  public static BlatFile getBlatFile(String regstr, ActionListener l)
      throws IOException {

    // System.out.println("Query is " + AlignViewport.getBLTURL() + regstr);

    String blt_url = AlignViewport.getBLTURL() + regstr;

    BlatFile blt = new BlatFile(blt_url, "URL");
    blt.setEstimatedSize(20000);
    blt.setActionListener(l);
    return blt;
  }

  // File access
  public static GappedFastaFile getGappedFastaFile(String fileStr)
      throws IOException {
    GappedFastaFile fp = new GappedFastaFile(fileStr, "File", false);

    return fp;
  }

  // File access
  public static FastaFile getFastaFile(String fileStr) throws IOException {
    FastaFile fp = new FastaFile(fileStr, "File", false);

    return fp;
  }

  // Used in here and GetFeatureThread
  public static GraphFile getRegionGraph(String regionStr, ActionListener l)
      throws IOException {
    String query = AlignViewport.getGRFURL() + regionStr;

    System.out.println("Query string " + query);
    GraphFile grf = new GraphFile(query, "URL", false);
    grf.setEstimatedSize(200000);
    grf.setActionListener(l);
    return grf;

  }

  // Used in this and GetFeatureThread
  public static GFFFile getRegionFeatures(String regionStr, ActionListener l)
      throws IOException {
    String query = AlignViewport.getGFFURL() + regionStr;

    System.out.println("Query pog string " + query);

    GFFFile gff = new GFFFile(query, "URL", false);
    gff.setEstimatedSize(20000);
    gff.setActionListener(l);
    return gff;

  }

  // BSearch for local feature files
  public static GFFFile getLocalRegionFeatures(String str, int start, int end,
      ActionListener l) {
     GFFFile gff = null;

    try {
    Vector localfiles = new Vector();
    //String file = "/Users/mclamp/cvs/pogdev/data/hg18.refFlat.coding.gff";
    //String file = "/Users/mclamp/cvs/pogdev/data/hg18.elements_filt.gff";
    String file = "./data/all.gff";

    gff = new GFFFile(file, "BSearch", 3, start);
    gff.bschr = str;
    gff.bsend = end;
    gff.bsstart = start;
    gff.setEstimatedSize(20000);
    gff.setActionListener(l);
    return gff;
   } catch (IOException e) {
            e.printStackTrace();
        
      String regionStr = "query=" + str + "&start=" + start + "&end=" + end;
        try {
        gff = getRegionFeatures(regionStr,l);
        } catch (IOException e2) {
            e2.printStackTrace();
        }
   }
   return gff;
  }

  // Used in local requestLocalRegion
  public static Vector getLocalBlatFile(String str, int start, int end,
      ActionListener l) throws IOException {

    Vector localfiles = new Vector();
    Vector out = new Vector();

    // Now we need to get these from somewhere

    for (int i = 0; i < localfiles.size(); i++) {
      String tmpfile = (String) localfiles.elementAt(i);
      // The column is different here
      // BlatFile blt = new BlatFile(tmpfile,"BSearch",3,start);
      // blt.setEstimatedSize(20000);
      // blt.setActionListener(l);

    }
    return out;

  }

  // Used in GetFeatureThread
  public static GFFFile getRegionGenes(String regionStr, ActionListener l)
      throws IOException {
    String query = AlignViewport.getGFFURL() + regionStr;

    GFFFile gff = new GFFFile(query, "URL", false);
    gff.setEstimatedSize(2000);
    gff.setActionListener(l);
    return gff;
  }

  // USed in getfeaturethread
  public static GFFFile getRegionRepeats(String regionStr) throws IOException {
    String query = AlignViewport.getGFFURL() + regionStr;

    GFFFile gff = new GFFFile(query, "URL", false);

    return gff;
  }


  // Used in this and Karyotype Panel - not sure this actually does much though
  public static GeneInfoFile getGeneInfo(String chr, int start, int end) {
    String regionStr = "query=" + chr + "&start=" + start + "&end=" + end
        + "&types=name";

    String query = AlignViewport.getInfoURL() + regionStr;
    try {

      GeneInfoFile gif = new GeneInfoFile(query, "URL", false);

      return gif;
    } catch (IOException e) {
      System.out.println("ERROR reading GeneInfoFile " + query);
      e.printStackTrace();
    }
    return null;
  }

  // So this is a train wreck - where do I start?
  public static AlignSplitPanel makePanel(Alignment al, String title,
      double width1, double width2, int offset, int start, int end, int width) {

    AlignSplitPanel asp = new AlignSplitPanel(al, title, width1, width2);

    AlignmentPanel ap1 = asp.getAlignmentPanel1();
    AlignmentPanel ap2 = asp.getAlignmentPanel2();

    AlignViewport av1 = ap1.getAlignViewport();
    AlignViewport av2 = ap2.getAlignViewport();

    av1.setOffset(offset);
    av2.setOffset(offset);

    int startres = (int) ((end - start) / 2 - (width) / av1.getCharWidth());

    av1.setStartRes(startres);
    startres = (int) ((end - start) / 2 - (width) / av2.getCharWidth());

    av2.setStartRes(startres);

    return asp;

  }
  // Ditto
  public static JPanel makeSinglePanel(Alignment al, String title, double width1) {
    JPanel jp = new JPanel(new BorderLayout());

    AlignViewport av = new AlignViewport(al);
    Controller controller = new Controller();

    av.setController(controller);

    AlignmentPanel ap = new AlignmentPanel(av, controller);

    av.setRenderer(new GraphRenderer());
    av.setFont(new Font("Helvetica", Font.PLAIN, 0));
    av.setCharWidth(width1, "AlignmentPanel");
    // av.setPIDBaseline(70);
    av.setCharHeight(10);

    // MenuManager m = new MenuManager(jp, av, controller);

    ap.setMinimumSize(new Dimension(0, 0));

    // This is needed when the panel is brought up from a Choosepanel button.
    ap.repaint();

    jp.add("Center", ap);
    return jp;
  }

 

  // Used in RegionFetchThread and AlignmentPanel and GFF2PS
  public static Alignment requestRegion(String chr, int start, int end,
      Vector feat, ActionListener l) {

    // Hash the input features by type - farm out

    LinkedHashMap typeorder = AlignViewport.readGFFConfig(
        "data/gff.conf", "File");

    Vector inputFeat = new Vector();


    int regionlen = end - start + 1;

    // This is the generic regions string for querying the website

    String regstr = "query=" + chr + "&start=" + start + "&end=" + end + "&z=2";

    System.out.println("Getting dummy alignment for " + chr + " " + start + " "
        + end);

    //Alignment al = Alignment.getDummyAlignment("Human", chr, start, end);
    //Alignment al = Alignment.getDummyAlignment("hg19", chr, start, end);
    //Alignment al = Alignment.getDummyAlignment("strCam", chr, start, end);
    Alignment al = Alignment.getDummyAlignment("galGal", chr, start, end);
    if (feat != null) {
      inputFeat = SequenceFeature.hashFeatures(feat, start-1, typeorder, true);
     
      for (int i = 0; i < inputFeat.size(); i++) { 
         
        GFF tmpgff = (GFF)inputFeat.elementAt(i);
        System.out.println("Grouping " + tmpgff.getFeatures().size());
        Vector tmpfeat = GFFFile.groupFeatures(tmpgff.getFeatures(),true);
        System.out.println("Grouped " + tmpfeat.size());
        tmpfeat = SequenceFeature.hashFeatures(tmpfeat, 0, typeorder, true);
        al.addSequences(tmpfeat);
       }
       //al.addSequences(inputFeat);
    }


    // Now add in the features

    System.out.println("Feat " + inputFeat.size()); 
    //Vector tmpgenefeat = GFFFile.extractFeatures(input.getFeatures(), "gene");
    //inputFeat = GFFFile.groupFeatures(feat,true);
    //al.addSequences(inputFeat);

    try {

      // Hashtable genes = GenomeInfoFactory.getGenes(chr,start,end);

      // Now the GFF features

      // System.out.println("Getting gff");

      //GFFFile gff = GenomeInfoFactory.getLocalRegionFeatures(regstr, l);
      //GFFFile gff = GenomeInfoFactory.getLocalRegionFeatures(chr,start,end, l);
      //GraphFile grf = GenomeInfoFactory.getRegionGraph(regstr,l);
      //BlatFile blt = GenomeInfoFactory.getBlatFile(regstr, l);

      
      GFF bamgff = BamFile.getRegion("/Users/mclamp/S11L1.bam", chr, start, end);
      Vector<SequenceFeature> bamfeat = bamgff.getFeatures();
      System.out.println("HASDFHASHDFHASHDFAHSDF " + bamfeat.size());
      Iterator iter = bamfeat.iterator();
      while (iter.hasNext()) {
    	  SequenceFeature sf = (SequenceFeature)iter.next();
    	  sf.setStart(sf.getStart()-start+1);
    	  sf.setEnd(sf.getEnd()-start+1);
          sf.setType("BAM2");
      }
      System.out.println("Ungrouped BAM " + bamfeat.size());
      bamfeat = GFFFile.groupFeatures(bamfeat,true);
      System.out.println("Grouped BAM " + bamfeat.size());
      bamfeat = SequenceFeature.hashFeatures(bamfeat, 0, typeorder, true);
      al.addSequences(bamfeat);

      GFF bamgff = BamFile.getRegion("/Users/mclamp/S1L1.bam", chr, start, end);
      Vector<SequenceFeature> bamfeat = bamgff.getFeatures();
      System.out.println("HASDFHASHDFHASHDFAHSDF " + bamfeat.size());
      Iterator iter = bamfeat.iterator();
      while (iter.hasNext()) {
    	  SequenceFeature sf = (SequenceFeature)iter.next();
    	  sf.setStart(sf.getStart()-start+1);
    	  sf.setEnd(sf.getEnd()-start+1);
      }
      System.out.println("Ungrouped BAM " + bamfeat.size());
      bamfeat = GFFFile.groupFeatures(bamfeat,true);
      System.out.println("Grouped BAM " + bamfeat.size());
      bamfeat = SequenceFeature.hashFeatures(bamfeat, 0, typeorder, true);
      al.addSequences(bamfeat);
      System.out.println("HASDFHASHDFHASHDFAHSDF " + bamfeat.size());
      
      //gff.parse();

      //grf.parse();

      // System.out.println("Parsing blat");
      //blt.parse();

      // Group into features and subfeatures by hitname
      //Vector tmpgenefeat = GFFFile.extractFeatures(gff.getFeatures(), "gene");
      //Vector tmpfeat = GFFFile.groupFeatures(gff.getFeatures(),true);
      //Vector tmpfeat = gff.getFeatures();
      //System.out.println("Tmp feat " + tmpgenefeat.size());

      // find the 'proper' gene name for each gene

      //GenomeInfoFactory.assignNames(genes,tmpgenefeat);
      //tmpgenefeat = GFFFile.groupFeatures(tmpgenefeat, true);

      // System.out.println("Tmp feat" + tmpgenefeat.size());
      // System.out.println("Hashing gff");

      //Vector featfeat = new Vector();
      
      //if (feat != null) {
        //featfeat = SequenceFeature.hashFeatures(feat, 0, typeorder, false);
      //}
      
      //al.addSequences(featfeat);
      
      //Vector geneFeat = SequenceFeature.hashFeatures(tmpgenefeat, 0, typeorder, true);
      //Vector gffFeat = SequenceFeature.hashFeatures(tmpfeat, 0, typeorder,false);

      //for (int i = 0; i < gffFeat.size(); i++) {
       // GFF gff2 = (GFF) gffFeat.elementAt(i);
      //}
      //System.out.println("Hashing grf");

      //Vector grfFeat = null;

      //grfFeat = SequenceFeature.hashFeatures(grf.getFeatures(), 0, typeorder,false);

      // System.out.println("Hashing blat " + blt.getFeatures().size());

      //Vector bltFeat = SequenceFeature.hashFeatures(blt.getFeatures(), 0,typeorder, true);

      // System.out.println("Adding genes " + geneFeat.size());
      //al.addSequences(geneFeat, 1);

      // System.out.println("Adding gff " + gffFeat.size());

      //al.addSequences(gffFeat);
      //al.addSequences(bamfeat);
      
      //al.addSequences(grfFeat, 5);

      // System.out.println("Adding blat " + bltFeat.size());
      //al.addSequences(bltFeat);

    } catch (IOException e) {
      e.printStackTrace();
    }
    return al;
  }


}
