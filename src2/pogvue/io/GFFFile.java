package pogvue.io;

import pogvue.analysis.*;
import pogvue.datamodel.*;
import pogvue.util.QuickSort;

import java.io.IOException;
import java.util.*;
import java.awt.event.*;
import pogvue.datamodel.comparer.*;

public class GFFFile extends AlignFile {
  private Vector<SequenceFeature> feat;
  
  private ActionListener l;
  
  private boolean donePhase = false;
  
  private int col;
  private BSearch bs;
  public int bsend = 0;
  public int bsstart = 0;
  public String bschr = null;

  public GFFFile(String infile,String type,int col, long coord) throws IOException {
    this.col = col;
    bs = new BSearch(infile,col,"\t");
    bs.search_file(coord);
    System.out.println("BS " + bs);

  }
  public GFFFile(String inStr) {
    super(inStr);
  }
  
  public GFFFile(String inFile, String type) throws IOException {
    super(inFile, type, true);
  }
  
  public GFFFile(String inFile, String type, boolean parse) throws IOException {
    super(inFile, type, parse);
  }
  
  public Vector<SequenceFeature> getFeatures() {
    System.out.println("Features " + feat.size());
    return feat;
  }
  
  public Hashtable<String, GFF> getFeatureHash() {
    Hashtable<String, GFF> fhash = new Hashtable<String, GFF>();
    
    for (int i = 0; i < feat.size(); i++) {
      SequenceFeature sf = feat.elementAt(i);
      String type = sf.getType();

      if (fhash.get(type) == null) {
	GFF g = new GFF(type, "", 1, 2);
	fhash.put(type, g);
      }

      GFF g = fhash.get(type);

      g.addFeature(sf);
    }

    // Group the features if they have a hit id

    Enumeration en = fhash.keys();

    // Loop over feature types
    while (en.hasMoreElements()) {
      String type = (String) en.nextElement();

      GFF gfffeat = (GFF) fhash.get(type);

      Vector feat = (Vector) gfffeat.getFeatures();

      SequenceFeature sf = (SequenceFeature) feat.elementAt(0);

      if (sf.getHitFeature() != null && !sf.getType().equals("tfbs")
	  && !sf.getType().equals("scan") && !sf.getType().equals("repeat")) {

	Vector newfeat = new Vector();

	Hashtable hashset = hash_set(feat);

	Enumeration en2 = hashset.keys();

	while (en2.hasMoreElements()) {
	  String hitid = (String) en2.nextElement();

	  Vector f = (Vector) hashset.get(hitid);

	  SequenceFeature sf2 = new SequenceFeature();

	  for (int i = 0; i < f.size(); i++) {
	    sf2.addFeature((SequenceFeature) f.elementAt(i));
	  }
	  newfeat.addElement(sf2);
	}
	GFF g = new GFF(type, "", 1, 2);

	for (int i = 0; i < newfeat.size(); i++) {
	  g.addFeature((SequenceFeature) newfeat.elementAt(i));
	}
	fhash.put(type, g);
      }
    }
    return fhash;
  }

  public static Vector groupFeatures(Vector feat, boolean bytype) {
    Hashtable typehash = new Hashtable();

    if (bytype == true) {

      for (int i = 0; i < feat.size(); i++) {
	SequenceFeature sf = (SequenceFeature) feat.elementAt(i);

	String type = sf.getType();

	if (typehash.containsKey(type)) {
	  Vector f = (Vector) typehash.get(type);
	  f.addElement(sf);
	} else {
	  Vector f = new Vector();
	  f.addElement(sf);
	  typehash.put(type, f);
	}
      }
    } else {
      typehash.put("all", feat);
    }

    Vector out = new Vector();
    Enumeration typen = typehash.keys();

    while (typen.hasMoreElements()) {
      String type = (String) typen.nextElement();

      Vector tmpf = (Vector) typehash.get(type);

      Hashtable idhash = new Hashtable();

      for (int i = 0; i < tmpf.size(); i++) {
	SequenceFeature sf = (SequenceFeature) tmpf.elementAt(i);

	type = sf.getType();

	if (sf.getHitFeature() != null) {

	  String hitid = sf.getHitFeature().getId();

	  if (idhash.containsKey(hitid)) {
	    Vector tmp = (Vector) idhash.get(hitid);
	    tmp.addElement(sf);
	  } else {
	    Vector tmp = new Vector();
	    tmp.addElement(sf);
	    idhash.put(hitid, tmp);
	  }

	}

      }

      Enumeration ids = idhash.keys();

      while (ids.hasMoreElements()) {
	String hitid = (String) ids.nextElement();
	Vector feats = (Vector) idhash.get(hitid);

	SequenceFeature sf = new SequenceFeature();
	sf.addFeatures(feats);
	out.addElement(sf);
      }
    }
    return out;
  }

  public static Vector extractFeatures(Vector feat, String type) {

    Vector out = new Vector();

    for (int i = 0; i < feat.size(); i++) {
      SequenceFeature sf = (SequenceFeature) feat.elementAt(i);

      if (sf.getType().equals(type)) {
	out.addElement(sf);
	feat.removeElement(sf);
	i--;
      }
    }

    return out;
  }

  public void parse() {
    feat = new Vector<SequenceFeature>();
    String line;
	  
    try {
      while ((line = nextLine()) != null) {
	//System.out.println("Line " + line);
	if (line.length() > 0) {
		
	  StringTokenizer str = new StringTokenizer(line, "\t");
		
	  if (str.countTokens() >= 8) {
		  
	    String name = str.nextToken();
	    String type1 = str.nextToken();
	    String type2 = str.nextToken();
	    int start = Integer.parseInt(str.nextToken());
	    int end = Integer.parseInt(str.nextToken());
	    double score = Double.parseDouble(str.nextToken());
	    String strand = str.nextToken();
	    String phase = str.nextToken();
		  
	    int strand_int;

	    if (bsend > 0 && start >  bsend) {
	      return;
	    }
		  
	    if (bschr == null || bschr.equals(name)) {

	    if (bs != null) {
	      start = start - bsstart;
	      end   = end   - bsstart;
	    }
	    
	    if (strand.equals("+") || strand.equals("1")) {
	      strand_int = 1;
	    } else if (strand.equals("-") || strand.equals("-1")) {
	      strand_int = -1;
	    } else {
	      strand_int = 0;
	    }
		  
	    SequenceFeature sf = new SequenceFeature(null, type1, start, end,
						     type2);
		  
	    sf.setId(name);
	    sf.setScore(score);
	    sf.setStrand(strand_int);
	    sf.setType2(type2);
	    sf.setPhase(phase);
		  
	    if (str.hasMoreTokens() && str.countTokens() >= 4) {
		    
	      String hitname = str.nextToken();
		    
	      SequenceFeature hf = new SequenceFeature(null, type1, start, end,
						       type2);
		    
	      hf.setId(hitname);
		    
	      sf.setHitFeature(hf);
		    
	      int hstart = Integer.parseInt(str.nextToken());
	      int hend = Integer.parseInt(str.nextToken());
	      String hstrand = str.nextToken();
		    
	      int hstrand_int;
		    
	      if (hstrand.equals("+") || hstrand.equals("1")) {
		hstrand_int = 1;
	      } else if (hstrand.equals("-") || hstrand.equals("-1")) {
		hstrand_int = -1;
	      } else {
		hstrand_int = 0;
	      }
		    
	      hf.setStart(hstart);
	      hf.setEnd(hend);
	      hf.setStrand(hstrand_int);
		    
	      feat.addElement(sf);
		    
	      if (str.hasMoreTokens() && str.countTokens() >= 4) {
		      
		str.nextToken();
		str.nextToken();
		      
		String s1 = str.nextToken();
		String s2 = str.nextToken();
		      
		GappedSequence gs = GappedFastaFile.get_inserts(s1, s2);

		sf.setSequence(gs);

	      }

	      if (str.hasMoreTokens() && str.countTokens() >= 2) {
		double score2 = Double.parseDouble(str.nextToken());
		int hitlen = Integer.parseInt(str.nextToken());

		sf.setScore2(score2);
		sf.setHitlen(hitlen);
	      }

	    } else if (str.hasMoreTokens()) {

	      String hitname = str.nextToken();

	      SequenceFeature hf = new SequenceFeature(null, type1, start, end,
						       type2);

	      hf.setId(hitname);

	      sf.setHitFeature(hf);

	      int coding_start = -1;
	      int coding_end = -1;

	      while (str.hasMoreTokens()) {
		String tok = str.nextToken();

		if (tok.indexOf("coding_start=") == 0) {
		  coding_start = Integer.parseInt(tok.substring(13));
		}
		if (tok.indexOf("coding_end=") == 0) {
		  coding_end = Integer.parseInt(tok.substring(11));
		}
	      }

	      Exon e = new Exon(null, type1, start, end, type2, phase);

	      e.setStrand(strand_int);
	      e.setCodingStart(coding_start);
	      e.setCodingEnd(coding_end);
	      e.setId(sf.getId());
	      e.setScore(score);
	      e.setType2(type2);
	      e.setHitFeature(hf);
	      //System.out.println("Adding "  + feat.size());
	      feat.addElement(e);
	      
	    } else {
	      feat.addElement(sf);
	    }
	    }
	  }
	  
	}
      } 
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public String nextLine() throws IOException {
    
    if (bs != null) {
      return bs.readLine();
    } else {
      return super.nextLine();
    }
  }
	private static String print(Sequence[] s) {
		return print(s, 72);
	}

	private static String print(Sequence[] s, int len) {
		return print(s, len, true);
	}

	private static String print(Sequence[] s, int len, boolean gaps) {
		StringBuffer out = new StringBuffer();
		int i = 0;
		while (i < s.length && s[i] != null) {
			String seq;
			if (gaps) {
				seq = s[i].getSequence();
			} else {
				seq = AlignSeq.extractGaps(s[i].getSequence(), "-");
				seq = AlignSeq.extractGaps(seq, ".");
				seq = AlignSeq.extractGaps(seq, " ");
			}

			out.append(">").append(s[i].getName()).append("/")
					.append(s[i].getStart()).append("-").append(s[i].getEnd()).append(
							"\n");

			int nochunks = seq.length() / len + 1;

			for (int j = 0; j < nochunks; j++) {
				int start = j * len;
				int end = start + len;

				if (end < seq.length()) {
					out.append(seq.substring(start, end)).append("\n");
				} else if (start < seq.length()) {
					out.append(seq.substring(start)).append("\n");
				}
			}
			i++;
		}
		return out.toString();
	}

	public String print() {
		return print(getSeqsAsArray());
	}

	public static void main(String args[]) {
		try {
			GFFFile gff = new GFFFile(args[0], "File");

			Vector feat = gff.getFeatures();
			Hashtable fhash = new Hashtable();

			for (int i = 0; i < feat.size(); i++) {
				SequenceFeature sf = (SequenceFeature) feat.elementAt(i);

				String type = sf.getType();

				if (fhash.get(type) == null) {
					GFF g = new GFF(type, "", 1, 2);
					fhash.put(type, g);
				}

				GFF g = (GFF) fhash.get(type);

				g.addFeature(sf);
			}

			Enumeration en = fhash.keys();

			while (en.hasMoreElements()) {
				String type = (String) en.nextElement();
				GFF g = (GFF) fhash.get(type);

				System.out.println("GFF " + type + " " + g.getFeatures().size());

			}
		} catch (IOException e) {
			System.out.println("Exception " + e);
		}
	}

	private void setPhase() {
		Vector feat = getFeatures();

		Hashtable fhash = new Hashtable();

		for (int i = 0; i < feat.size(); i++) {
			SequenceFeature sf = (SequenceFeature) feat.elementAt(i);

			if (sf.getHitFeature() != null) {
				String name = sf.getHitFeature().getId();

				if (fhash.get(name) == null) {
					Vector f = new Vector();
					fhash.put(name, f);
				}
				Vector f = (Vector) fhash.get(name);
				f.addElement(sf);
			}
		}

		Enumeration en = fhash.keys();

		while (en.hasMoreElements()) {
			String name = (String) en.nextElement();

			Vector f = (Vector) fhash.get(name);

			SequenceFeature sf[] = new SequenceFeature[f.size()];
			int starts[] = new int[f.size()];

			for (int i = 0; i < f.size(); i++) {
				sf[i] = (SequenceFeature) f.elementAt(i);
				starts[i] = ((SequenceFeature) f.elementAt(i)).getStart();
			}

			QuickSort.sort(starts, sf);

			Vector newvec = new Vector();
			int phase = 0;

			if (sf[0] instanceof Exon) {
				if (sf[0].getStrand() == 1) {
					for (SequenceFeature aSf : sf) {
						// System.out.println("Setting phase to " + phase);
						((Exon) aSf).setPhase(String.valueOf(phase));

						phase = (aSf.getLength() - (3 - phase)) % 3;
					}
				} else {
					for (int i = sf.length - 1; i >= 0; i--) {
						// System.out.println("Setting phase to " + phase);
						((Exon) sf[i]).setPhase(String.valueOf(phase));

						phase = (sf[i].getLength() - (3 - phase)) % 3;
					}
				}
			}
		}
	}

	public Vector getGFFFeatures() {
		Vector feat = getFeatures();
		Hashtable fhash = new Hashtable();
		Vector out = new Vector();

		for (int i = 0; i < feat.size(); i++) {
			SequenceFeature sf = (SequenceFeature) feat.elementAt(i);

			String type = sf.getType();

			if (fhash.get(type) == null) {
				GFF g = new GFF(type, "", 1, 2);
				fhash.put(type, g);
			}

			GFF g = (GFF) fhash.get(type);

			g.addFeature(sf);
		}

		Enumeration en = fhash.keys();

		while (en.hasMoreElements()) {
			String type = (String) en.nextElement();
			GFF g = (GFF) fhash.get(type);
			out.addElement(g);
		}
		return out;
	}

	public Hashtable getGFFHashtable() {
		Vector feat = getFeatures();
		Hashtable fhash = new Hashtable();

		for (int i = 0; i < feat.size(); i++) {
			SequenceFeature sf = (SequenceFeature) feat.elementAt(i);

			String type = sf.getType();

			if (fhash.get(type) == null) {
				GFF g = new GFF(type, "", 1, 2);
				fhash.put(type, g);
			}

			GFF g = (GFF) fhash.get(type);

			g.addFeature(sf);
		}
		return fhash;
	}

	public Vector getTieredGFFFeatures(int tiernum) {
		Vector out = new Vector();

		Vector feat = getFeatures();
		Vector levels = level_features(feat);

		int i = 0;

		while (i < tiernum && i < levels.size()) {
			Vector feat2 = (Vector) levels.elementAt(i);
			Hashtable fh = hash_set(feat2);

			Vector tiers = tier_hash_set(fh);

			for (int ii = 0; ii < tiers.size(); ii++) {
				GFF g = (GFF) tiers.elementAt(ii);

				// Group here if we can

				SequenceFeature sf = (SequenceFeature) g.getFeatures().elementAt(0);

				if (sf.getHitFeature() != null && !sf.getType().equals("tfbs")
						&& !sf.getType().equals("scan") && !sf.getType().equals("repeat")) {
					System.out.println("Grouping " + sf.getType());
					GFFGroup gg = new GFFGroup(g.getFeatures());

					gg.setName(sf.getId());
					out.addElement(gg);
				} else {
					out.addElement(g);
				}
			}
			i++;
		}

		return out;
	}

	private Vector level_features(Vector feat) {
		Hashtable fhash = new Hashtable();

		int toplevel = 0;

		for (int i = 0; i < feat.size(); i++) {
			SequenceFeature sf = (SequenceFeature) feat.elementAt(i);

			String type = sf.getType();

			if (Integer.parseInt(type) > toplevel) {
				toplevel = (Integer.parseInt(type));
			}

			if (fhash.get(type) == null) {
				Vector v = new Vector();
				fhash.put(type, v);
			}

			Vector v = (Vector) fhash.get(type);

			v.addElement(sf);
		}

		int i = 0;

		Vector out = new Vector();

		while (i < toplevel) {
			out.addElement(fhash.get(String.valueOf(i)));
			i++;
		}

		return out;

	}

	public Hashtable hash_set(Vector feat) {
		Hashtable fhash = new Hashtable();

		for (int i = 0; i < feat.size(); i++) {

			SequenceFeature sf = (SequenceFeature) feat.elementAt(i);

			if (fhash.get(sf.getHitFeature().getId()) == null) {
				Vector v = new Vector();
				fhash.put(sf.getHitFeature().getId(), v);
			}

			Vector v = (Vector) fhash.get(sf.getHitFeature().getId());

			v.addElement(sf);
		}

		return fhash;
	}

	private Vector tier_hash_set(Hashtable fhash) {

		// This vector is a vector of sets of features from the same supercontig

		Vector contigs = new Vector();

		Enumeration en = fhash.keys();

		while (en.hasMoreElements()) {
			String name = (String) en.nextElement();

			contigs.addElement(fhash.get(name));
		}

		// First sort the sets of contig features by score

		Collections.sort(contigs, new SeqFeatureVectorScoreComparer());

		// The output is a Vector of GFF

		Vector tiers = new Vector();

		for (int i = 0; i < contigs.size(); i++) {

			int found_tier = 0;

			Vector contig_set = (Vector) contigs.elementAt(i);

			Collections.sort(contig_set, new SeqFeatureCoordComparer());

			System.out.println("Contig set " + contig_set.size());
			System.out
					.println("Name "
							+ ((SequenceFeature) contig_set.elementAt(0)).getHitFeature()
									.getId());
			System.out.println("Score "
					+ ((SequenceFeature) contig_set.elementAt(0)).getScore());
			System.out.println();

			int start = ((SequenceFeature) contig_set.elementAt(0)).getStart();
			int end = ((SequenceFeature) contig_set.elementAt(contig_set.size() - 1))
					.getEnd();

			int foundtier = 0;

			for (int j = 0; j < tiers.size(); j++) {

				GFF g = (GFF) tiers.elementAt(j);

				int overlap = 0;

				Vector tier_feat = g.getFeatures();

				for (int k = 0; k < tier_feat.size(); k++) {

					SequenceFeature f2 = (SequenceFeature) tier_feat.elementAt(k);

					if (!(f2.getStart() > end || f2.getEnd() < start)) {

						overlap = 1;

						k = tier_feat.size();
					}
				}

				if (overlap == 0) {
					// Add to tier
					for (int ii = 0; ii < contig_set.size(); ii++) {
						g.addFeature((SequenceFeature) contig_set.elementAt(ii));
					}

					j = tiers.size();

					found_tier = 1;
				}

			}

			if (found_tier == 0) {
				String type = ((SequenceFeature) contig_set.elementAt(0))
						.getHitFeature().getId();

				GFF g = new GFF(type, "", 1, 2);

				for (int ii = 0; ii < contig_set.size(); ii++) {
					g.addFeature((SequenceFeature) contig_set.elementAt(ii));
				}
				tiers.addElement(g);
			}
		}

		// Now sort the features within the tiers

		for (int i = 0; i > tiers.size(); i++) {
			GFF g = (GFF) tiers.elementAt(i);

			if (g instanceof GFFGroup) {

				Hashtable hash = ((GFFGroup) g).getFeatureHash();

				Enumeration en2 = hash.keys();

				while (en2.hasMoreElements()) {
					Vector v = (Vector) hash.get(en2.nextElement());

					Collections.sort(v, new SeqFeatureCoordComparer());

				}
			} else {
				Vector v = g.getFeatures();
				Collections.sort(v, new SeqFeatureCoordComparer());
			}
		}
		return tiers;
	}

	public static Vector collapseGFF(Vector seqs) {
		Vector out = new Vector();
		Hashtable hash = new Hashtable();

		for (int i = 0; i < seqs.size(); i++) {
			Sequence seq = (Sequence) seqs.elementAt(i);

			if (seq instanceof GFF && ((GFF) seq).getType() != null) {
				GFF gff = (GFF) seq;

				if (!hash.containsKey(gff.getType())) {
					GFF g = new GFF("", "", 1, 2);
					g.setName(gff.getType());
					g.addFeatures(gff.getFeatures());
					hash.put(gff.getType(), g);
					out.addElement(g);
				} else {
					GFF g = (GFF) hash.get(gff.getType());
					g.addFeatures(gff.getFeatures());
				}
			} else {
				out.addElement(seq);
			}
		}
		return out;
	}

	// Old fashioned bumping
	public static Vector bumpGFF(GFF gff) {

		Vector feat = gff.getFeatures();

		int[] tiers = new int[feat.size()];

		// Sort the features by start first

		Collections.sort(feat, new SeqFeatureCoordComparer());

		Vector tiercoords = new Vector();

		int maxtier = 0;

		for (int i = 0; i < feat.size(); i++) {

			SequenceFeature sf = (SequenceFeature) feat.elementAt(i);
			int start = sf.getStart();

			if (sf.getHitFeature() != null) {
				start = start - sf.getHitFeature().getId().length();
			} else if (sf.getId() != null) {
				start = start - sf.getId().length();
			}

			int foundtier = -1;

			for (int j = 0; j < tiercoords.size(); j++) {

				int coord = ((Integer) tiercoords.elementAt(j)).intValue();

				if (start - coord > 0) {
					foundtier = j;
					j = tiercoords.size();
				}
			}

			if (foundtier == -1) {
				foundtier = tiercoords.size();

				tiercoords.addElement(sf.getEnd());
				// System.out.println("Adding new tier " + tiercoords.size());
			} else {
				// System.out.println("Found tier " + foundtier + " " +
				// tiercoords.size());
				tiercoords.setElementAt(sf.getEnd(), foundtier);
			}

			tiers[i] = foundtier;

		}

		// Now make as may GFFs as there are tiers
		Vector out = new Vector();
		int off = 1;
		if (feat.size() > 0) {
			SequenceFeature sf2 = (SequenceFeature) feat.elementAt(0);

			GFF blank = new GFF("", "", 1, 2);

			for (int i = 0; i < tiercoords.size(); i++) {

				GFF tiergff = new GFF("GFF", "", 1, 2);

				out.addElement(tiergff);
				if (sf2.getType().equals("tfbs")) {
					out.addElement(blank);
					off = 2;
				}
			}

		}

		for (int i = 0; i < tiers.length; i++) {
			int tier = tiers[i];
			SequenceFeature sf = (SequenceFeature) feat.elementAt(i);

			GFF tmpgff = (GFF) out.elementAt(tier * off);

			tmpgff.setName(sf.getType());
			tmpgff.addFeature(sf);
		}

		return out;
	}

	public static Vector bumpGFF_nosort(GFF gff) {

		Vector feat = gff.getFeatures();
		Vector[] tiers = new Vector[feat.size()];

		int maxtier = 0;
		String type = "";

		for (int i = 0; i < feat.size(); i++) {

			SequenceFeature sf = (SequenceFeature) feat.elementAt(i);
			type = sf.getType();

			int start = sf.getStart();

			// if (sf.getHitFeature() != null) {
			// start = start - sf.getHitFeature().getId().length();
			// } else if (sf.getId() != null) {
			// start = start - sf.getId().length();
			// }

			int foundtier = -1;

			for (int j = 0; j < maxtier; j++) {

				Vector v = tiers[j];

				boolean overlap = false;

				for (int k = 0; k < v.size(); k++) {
					SequenceFeature tmpsf = (SequenceFeature) v.elementAt(k);

					if (!(tmpsf.getStart() > sf.getEnd() || tmpsf.getEnd() < sf
							.getStart())) {
						// System.out.println("Overlap " + j + " " + k + " " +
						// tmpsf.getStart() + " " + tmpsf.getEnd() + " " + sf.getStart() +
						// " " + sf.getEnd());
						overlap = true;
						k = v.size();
					}
				}

				if (overlap == false) {
					// System.out.println("Found tier " + j);
					foundtier = j;
					tiers[j].addElement(sf);
					j = maxtier;
				}
			}
			if (foundtier == -1) {

				Vector tmpv = new Vector();
				tmpv.addElement(sf);
				tiers[maxtier] = tmpv;
				// System.out.println("New tier " + maxtier);
				maxtier++;
			}
		}

		// Now make as may GFFs as there are tiers
		Vector out = new Vector();

		int off = 1;

		if (feat.size() > 0) {

			SequenceFeature sf2 = (SequenceFeature) feat.elementAt(0);

			GFF blank = new GFF("", "", 1, 2);

			for (int i = 0; i < maxtier; i++) {

				GFF tiergff = new GFF("GFF", "", 1, 2);
				tiergff.setName(type);
				tiergff.addFeatures(tiers[i]);
				out.addElement(tiergff);

			}

		}
		return out;
	}
}
