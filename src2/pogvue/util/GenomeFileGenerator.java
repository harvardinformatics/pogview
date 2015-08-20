package pogvue.util;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Vector;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Iterator;
import pogvue.analysis.AlignSeq;
import pogvue.datamodel.Sequence;
import pogvue.datamodel.Sequence;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.StringTokenizer;
import pogvue.io.FastaFile;
import pogvue.util.GetOptions;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class GenomeFileGenerator {
  String fastafilename;
  String stub;
  int    minchrlen;

  public GenomeFileGenerator(String fastafilename,String stub,int minchrlen) throws IOException {

     this.fastafilename = fastafilename;
     this.stub          = stub;
     this.minchrlen     = minchrlen;

     this.parse();
  }

  public void parse() throws IOException{


     // Read the fasta file
     FastaFile ff                 = new FastaFile(this.fastafilename,"File");
     HashMap<String, Integer> chr = new HashMap<String, Integer>();

     Vector    seqs = ff.getSeqs();


     // Get the chr lengths over minchrlen
     for (int i = 0; i < seqs.size(); i++) {
         Sequence seq = (Sequence)seqs.elementAt(i);

         int      len = seq.getLength();
         String   id  = seq.getName();

         if (len > this.minchrlen) {
           chr.put(id,len);
           System.out.println("Name " + id + " " + len);
         }
     }


     // Reverse sort the chr
     HashMap<String, Integer> sortedchr = sortByComparator(chr, false);


     Iterator iter = sortedchr.entrySet().iterator();

     // Open the output filesA

     Writer lenwriter  = null;
     Writer bandwriter = null;

     try {
        lenwriter  = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(this.stub + ".chrlen"),       "utf-8"));
        bandwriter = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(this.stub + ".cytoband.txt"), "utf-8"));

        while (iter.hasNext()) {
             Map.Entry pair = (Map.Entry)iter.next();
             lenwriter.write (pair.getKey() + "\t" + pair.getValue() + "\n");
             bandwriter.write(pair.getKey() + "\t0\t" + pair.getValue() + "\tchr\tgneg\n");
        }
     } catch (IOException ex) {
           // report
     } finally {
        try {lenwriter.close();} catch (Exception ex) {/*ignore*/}
        try {bandwriter.close();} catch (Exception ex) {/*ignore*/}
     }
     
  }
  private static HashMap<String, Integer> sortByComparator(HashMap<String, Integer> unsortMap, final boolean order) {

        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Integer>>()
        {
            public int compare(Entry<String, Integer> o1,
                    Entry<String, Integer> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });
        // Maintaining insertion order with the help of LinkedList
        HashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
  }
  public static void help() {

     System.out.println("Usage:  java pogvue.util.GenomeFileGenerator -f <fastafile> -s <outfile stub> -l <minchrlen>");

  }

  public static void main(String[] args) {

       Hashtable opts = GetOptions.get(args);

       if (! opts.containsKey("-f") ||
           ! opts.containsKey("-s") ||
           ! opts.containsKey("-l")) {

         GenomeFileGenerator.help();
         System.exit(0);

      }

            
      String fastafilename = (String)opts.get("-f");
      String stub          = (String)opts.get("-s");
      int    minchrlen     = Integer.parseInt((String)opts.get("-l"));

      try {
        GenomeFileGenerator gfg = new GenomeFileGenerator(fastafilename,stub,minchrlen);
      } catch (IOException ex) {
        System.out.println(ex);
      }

  }
}



