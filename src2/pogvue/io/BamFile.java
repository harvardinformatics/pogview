/*
 * The MIT License
 *
 * Copyright (c) 2012 The Broad Institute
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
//package picard.sam;
package pogvue.io;

import htsjdk.samtools.SAMRecord;

import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.samtools.util.Interval;
import htsjdk.samtools.util.IntervalList;
import htsjdk.samtools.util.SamRecordIntervalIteratorFactory;
import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SamInputResource;
import htsjdk.variant.utils.SAMSequenceDictionaryExtractor;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Iterator;

import pogvue.datamodel.GFF;
import pogvue.datamodel.SequenceFeature;

public class BamFile {
  
	public static void main(String[] args) {
		try {
			
			if (args.length != 4) {
				System.out.println("Usage: java pogvue.io.BamFile <bamfile> <chr> <start> <end>");
				System.exit(0);
			}
			
			final String filename         = args[0];
	    	final String chr              = args[1];
	    	final int    start            = Integer.parseInt(args[2]);
	    	final int    end              = Integer.parseInt(args[3]);
	    		
			GFF gff = BamFile.getRegion(filename,chr,start,end);
			
			System.out.println(gff.getName() + " " + gff.getStart() + " " + gff.getEnd() + " " + gff.getSequence());
			Iterator<SequenceFeature> sfiter = gff.getFeatures().iterator();
				
			while(sfiter.hasNext()) {
				SequenceFeature sf = (SequenceFeature)sfiter.next();
				System.out.println(sf.toGFFString());
			}
			System.out.println("Done");
						
		} catch (IOException e) {
			System.out.println("ERROR: " + e);
			System.out.println("Usage: java pogvue.io.BamFile <bamfile> <chr> <start> <end>");
		
		} catch (Exception e) {
			System.out.println("ERROR: " + e);
			System.out.println("Usage: java pogvue.io.BamFile <bamfile> <chr> <start> <end>");
		}
		System.out.println("Done");
	}
	
    public static GFF getRegion(String filename,String chr, int start, int end) throws IOException {
    	
    	final File input = new File(filename);
    	
        // Generate the headers from the sam file
    	
        final SAMFileHeader         header                = new SAMFileHeader();
        final SAMSequenceDictionary samSequenceDictionary = SAMSequenceDictionaryExtractor.extractDictionary(input);
        
        header.setSequenceDictionary(samSequenceDictionary);
        header.setSortOrder(SAMFileHeader.SortOrder.coordinate);
        
        final IntervalList intervalList      = new IntervalList(header);
                
        // Make the sam reader from sam file
        final SamReader samReader = SamReaderFactory.makeDefault().open(SamInputResource.of(input));
        
        // Make the interval
        final Interval tmpinterval = new Interval(chr,start,end,false, "tmp");
        intervalList.add(tmpinterval);
        
        final CloseableIterator<SAMRecord> samRecordsIterator = new SamRecordIntervalIteratorFactory().makeSamRecordIntervalIterator(samReader, intervalList.getIntervals(), samReader.hasIndex());
        Vector<SequenceFeature> feat = new Vector();
        
        while (samRecordsIterator.hasNext()) {
            try {
            final SAMRecord rec = samRecordsIterator.next();
            //System.out.println(rec.getSAMString());

            GFF                  gff    = new GFF(rec.getReadName(),rec.getReadBases().toString(),rec.getStart(),rec.getEnd());
            List<AlignmentBlock> blocks = rec.getAlignmentBlocks();

            for (int i = 0;i < blocks.size(); i++) {
              AlignmentBlock block = (AlignmentBlock)blocks.get(i);

              int refstart = block.getReferenceStart();
              int refend   = refstart + block.getLength() - 1;

              int hitstart = block.getReadStart();
              int hitend   = hitstart + block.getLength() - 1;

              //SequenceFeature sf = new SequenceFeature(null, "BAM", rec.getStart(), rec.getEnd(),"BAM");
              SequenceFeature sf = new SequenceFeature(null, "BAM", refstart,refend,"BAM");
              SequenceFeature sf2 = new SequenceFeature(null, "BAM", hitstart,hitend,"BAM");
              sf2.setId(rec.getReadName()); 
              sf.setId(rec.getReadName());
              sf.setScore(rec.getMappingQuality());
              sf.setHitFeature(sf2);
              boolean negstrand = rec.getReadNegativeStrandFlag();

              if (negstrand) {
            	sf.setStrand(-1);
              } else {
            	sf.setStrand(1);
              }

              sf.setType2("BAM");
              sf.setPhase(".");
              sf.setAlignString(rec.getReadString().substring(hitstart-1,hitend));
              feat.addElement(sf);
           }
          } catch (Exception ex) {
             System.out.println("Exception reading bamfile : " + ex);
          }
        }
        GFF gff = new GFF(chr,"",start,end);
        gff.addFeatures(feat);

        return gff;
    }
}
