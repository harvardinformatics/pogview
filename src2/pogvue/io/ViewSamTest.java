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
import htsjdk.samtools.util.AsciiWriter;
import htsjdk.samtools.util.BufferedLineReader;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.samtools.util.Interval;
import htsjdk.samtools.util.IntervalList;
import htsjdk.samtools.util.LineReader;
import htsjdk.samtools.util.SamRecordIntervalIteratorFactory;
import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.SamInputResource;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.variant.utils.SAMSequenceDictionaryExtractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import picard.PicardException;
import pogvue.io.ViewSam.AlignmentStatus;
import pogvue.io.ViewSam.PfStatus;

public class ViewSamTest{
  

    public static void main(String[] args) throws IOException {
    	
    	final File   inputSam         = new File(args[0]);
    	final String chr              = args[1];
    	final int    start            = Integer.parseInt(args[2]);
    	final int    end              = Integer.parseInt(args[3]);
    	
        // Generate the header from the sam file and create an interval list
    	
        final SAMFileHeader         header                = new SAMFileHeader();
        final SAMSequenceDictionary samSequenceDictionary = SAMSequenceDictionaryExtractor.extractDictionary(inputSam);
        
        header.setSequenceDictionary(samSequenceDictionary);
        header.setSortOrder(SAMFileHeader.SortOrder.coordinate);
        
        final IntervalList intervalList      = new IntervalList(header);
                

        // Make the sam reader from sam file
        final CloseableIterator<SAMRecord> samRecordsIterator;
        final SamReader samReader = SamReaderFactory.makeDefault().open(SamInputResource.of(inputSam));

        
        // Make the interval  - this will come from the AlignViewport usually
        final Interval tmpinterval = new Interval(chr,start,end,false, "tmp");
        intervalList.add(tmpinterval);
        
        samRecordsIterator = new SamRecordIntervalIteratorFactory().makeSamRecordIntervalIterator(samReader, intervalList.getIntervals(), samReader.hasIndex());
        
        while (samRecordsIterator.hasNext()) {
            final SAMRecord rec = samRecordsIterator.next();
            System.out.println(rec.getSAMString());
        }


    }
}
