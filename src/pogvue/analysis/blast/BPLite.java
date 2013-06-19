package pogvue.analysis.blast;

//
//  BPLite.java
//
//
//  Created by Michele Clamp on Thu Jan 23 2003.
//  Copyright (c) 2003 Ensembl. All rights reserved.
//  Almost a direct copy of Ian Korf's perl BPlite parser
//  combined with the gapped alignment parsing of the ensembl pipeline.


import com.stevesoft.pat.Regex;
import pogvue.datamodel.FeaturePair;

import java.io.*;
import java.util.Vector;

public final class BPLite {

    private static final Regex reg_query             = new Regex("^Query=\\s+(\\S+)");
    private static final Regex reg_null              = new Regex("\\S");
    private static final Regex reg_replace_space     = new Regex("(\\s+)","");
    private static final Regex reg_fix_header        = new Regex("^(>)","");
    private static final Regex reg_parse_header      = new Regex("\\((\\d+)\\s+\\S+\\)\\s*$");
    private static final Regex reg_database          = new Regex("^Database:\\s+(.+)");
    static final Regex reg_pattern           = new Regex("^\\s*pattern\\s+(\\S+).*position\\s+(\\d+)\\D");
    private static final Regex reg_end_dat           = new Regex("^Parameters|^\\s+Database:");
    private static final Regex reg_end_dat2          = new Regex("^Parameters|^\\s+Database:|^\\s+Posted date:");
    private static final Regex reg_header            = new Regex("^>");
    private static final Regex reg_word              = new Regex("(\\S+)");
    private static final Regex reg_strand            = new Regex("Strand HSP");
    private static final Regex reg_score             = new Regex("^\\s{0,2}Score");
    private static final Regex reg_parameters        = new Regex("^Parameters|^Database:|Posted date:");
    static final Regex reg_posted            = new Regex("Posted date");
    private static final Regex reg_length            = new Regex("Length = ([\\d,]+)$","");
    private static final Regex reg_replace_end_space = new Regex("\\s+$","");
    private static final Regex reg_hitid             = new Regex("^>(\\S+)");
    
    private String         last_line  = "";
    private String         query_line = "";
    private String         name       = "";
    private String         program    = "";
    
    private boolean        report_done = false;
    
    private int length;
    
    private Hit current_hit = null;

    private BufferedReader reader;
    
    private BPLite(BufferedReader reader) throws IOException {
        this.reader = reader;

        parseHeader();
    }

    private void parseHeader() throws IOException {
        report_done = false;

        boolean header_flag = false;
        
        String line;
        String query_string;
        String database = "";

        boolean found = false;
        
        while ((line = reader.readLine()) != null) {
            if (!found) {
                if (reg_word.search(line)) {
                    program = reg_word.stringMatched(1);
                }
                found = true;
            }
           // System.out.println("Read line " + line);
            
            if (reg_query.search(line)) {
                //System.out.println("Found query");
                
                header_flag = true;
                query_string = reg_query.stringMatched(1);

                setName(query_string);
                
                //System.out.println("Query string " + query_string);
                
                while ((line = reader.readLine()) != null) {
                    if (!reg_null.search(line)) {
                        //System.out.println("Found null");
                        break;
                    }

                    query_string = query_string + line;
                }

                reg_replace_space.replaceFirst(query_string);
                reg_fix_header   .replaceFirst(query_string);

                //System.out.println("Query string now " + query_string);
                
                if (reg_parse_header.search(query_string)) {

                    length = Integer.parseInt(reg_parse_header.stringMatched(1));
                    //System.out.println("Found length " + length);
                    query_line = query_string;
                }
            } else if (reg_database.search(line)) {
                
                header_flag = true;

                database = reg_database.stringMatched(1);
                //System.out.println("found database " + database);
                
            } else if (reg_header.search(line)) {
                last_line = line;
                //System.out.println("Found header ");
                return;
            } else if (reg_end_dat.search(line)) {
                header_flag = true;
                //System.out.println("Found end dat");
                last_line = line;
                return;
            }
        }
    }


    private HSP next_feature() throws IOException {
        Hit hit = current_hit;

        if (hit == null) {
            current_hit = nextHit();
            hit = current_hit;

            if (hit == null) {
                return null;
            }
        }
        HSP hsp = hit.nextHSP();

        if (hsp == null) {
            current_hit = null;
            return next_feature();
        }
        return hsp;
        
    }

    private int fast_forward() throws IOException {
        
        if (report_done) {
            return 0;
        }
        if (reg_end_dat2.search(last_line)) {
            return 0;
        }
        if (reg_header.search(last_line)) {
            return 1;
        }
        String line;

        while ((line = reader.readLine()) != null) {
            if (reg_end_dat2.search(line)) {
                last_line = line;
                return 1;
            }
        }
        return 0;
    }

    private Hit nextHit() throws IOException {

        if (fast_forward() == 0) {
            return null;
        }

        //System.out.println("\nLast line " + last_line);
        String def = last_line;
        String database;
        String line;

        
        //System.out.println("DEF " + def);
        //System.out.println("Line " + line);
        
        while ((line = reader.readLine()) != null) {
            //System.out.println("Reading " + line);
            if (!reg_word.search(line)) {
                //System.out.println("not Found word");
                } else if (reg_strand.search(line)) {
                } else if (reg_score.search(line)) {
                //System.out.println("Found score");
                last_line = line;
                break;
            } else if (reg_parameters.search(def)) {
                last_line = line;
                break;
            } else {
                def = def + line;
            }
        }
        //System.out.println("Def " + def);        
        
        if (!reg_header.search(def)) {
            //System.out.println("No header");
            return null;
        } else {
            reg_replace_space.replaceAll    (def);
            reg_replace_end_space.replaceAll(def);
            reg_length.replaceFirst         (def);


            length = Integer.parseInt(reg_length.stringMatched(1));
            String hitid = "";
            
            if (reg_hitid.search(def)) {
                hitid = reg_hitid.stringMatched(1);
            }
            //System.out.println("header " + reg_header.search(def));
            reg_fix_header.replaceFirst(def);

            Hit hit = new Hit(reader);

            //System.out.println("Hit " + length + " | " + last_line + " | " + def);

            hit.setHitName(hitid);
            hit.setName(getName());
            hit.setLength(length);
            hit.setLastLine(last_line);
            hit.setParent(this);

            return hit;
        }
     
    }
    public String getLastLine() {
        return last_line;
    }
    public void setLastLine(String line) {
        this.last_line = line;
    }
    

    public String getProgram() {
        return program;
    }
    public void setProgram(String program) {
        this.program = program;
    }

    public BufferedReader getBufferedReader() {
        return reader;
    }
    public void setBufferedReader(BufferedReader reader) {
        this.reader = reader;
    }

    private String getName() {
        return name;
    }
    private void setName(String name) {
        this.name = name;
    }
    public static void main(String[] args) {
        try {
            File file = new File(args[0]);
            DataInputStream in    = new DataInputStream(new FileInputStream(file));
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            BPLite bp = new BPLite(reader);

            Hit hit;

            while ((hit = bp.nextHit()) != null) {
                HSP hsp;

                System.out.println("\nHit " + hit);
                
                while ((hsp = hit.nextHSP()) != null) {
                    System.out.println("\nHSP " + hsp);

                    if (args.length == 2) {
                        Vector f = hsp.getUngappedFeatures();

                        System.out.println("\nUngapped " + f.size() + "\n");

                        for (int i = 0; i < f.size(); i++) {
                            FeaturePair fp = (FeaturePair)f.elementAt(i);

                            System.out.println("FP " + fp.toGFFString());
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("IOException " + e);
        }
    }
}







