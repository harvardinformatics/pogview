package pogvue.io;

import pogvue.analysis.AlignSeq;
import pogvue.datamodel.*;
import pogvue.util.QuickSort;
import pogvue.datamodel.comparer.*;

import java.io.IOException;
import java.util.*;
import java.awt.event.*;

public class GraphFile extends AlignFile {
	private Vector feat;
	private String pitype;

	private ActionListener l;

	public GraphFile(String inStr) {
		super(inStr);
	}

	public GraphFile(String inFile, String type) throws IOException {
		super(inFile, type, true);
	}

	public GraphFile(String inFile, String type, boolean parse)
			throws IOException {
		super(inFile, type, parse);

	}

	public Vector<SequenceFeature> getFeatures() {
		return feat;
	}

	public Hashtable getFeatureHash() {

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

    public void parse() {
	feat = new Vector();
	
	Hashtable scores = new Hashtable();
	Hashtable pis = new Hashtable();
	
	String line;
	
	int mincoord = 900000000;
	int maxcoord = -1;
	
	try {
	    while ((line = nextLine()) != null) {
		
		if (line.length() > 0) {
		    
		    StringTokenizer str = new StringTokenizer(line, "\t");
		    
		    if (str.countTokens() >= 6) {
			
			String name = str.nextToken();
			String type1 = str.nextToken();
			String type2 = str.nextToken();
			int start = Integer.parseInt(str.nextToken());
			int end = Integer.parseInt(str.nextToken());
			double score = Double.parseDouble(str.nextToken());
			
			if (scores.get(type1) == null) {
			    Hashtable tmp = new Hashtable();
			    scores.put(type1, tmp);
			}
			
			Hashtable tmp = (Hashtable) scores.get(type1);
			
			tmp.put(start, (double) score);

			if (start < mincoord) {
			    mincoord = start;
			}
			if (start > maxcoord) {
			    maxcoord = start;
			}
			
			if (type1.equals("30mamm.pis")) {
			    
			    pitype = type1;
			    
			    str.nextToken(); // strand
			    str.nextToken(); // phase
			    // str.nextToken(); // hitname
			    
			    double a = Double.parseDouble(str.nextToken());
			    double c = Double.parseDouble(str.nextToken());
			    double g = Double.parseDouble(str.nextToken());
			    double t = Double.parseDouble(str.nextToken());
			    
			    Vector pi = new Vector();
			    pi.addElement(a);
			    pi.addElement(c);
			    pi.addElement(g);
			    pi.addElement(t);
			    
			    pis.put(start, pi);
			}
			
		    }
		}
	    }
	} catch (IOException e) {
	    System.out.println("ERROR: parsing GraphFile " + e);
	}
	
	if (pis.size() > 0) {
		    
	    SequenceFeature sf = new SequenceFeature();
	    
	    sf.setStart(mincoord);
	    sf.setEnd(maxcoord);
	    sf.setScores((Hashtable) scores.get(pitype));
	    sf.setScoreVector(pis);
	    sf.setType(pitype);
	    
	    feat.add(sf);
	}
	
	Enumeration en = scores.keys();
	
	while (en.hasMoreElements()) {
	    String type = (String) en.nextElement();
	    
	    if (!type.equals(pitype)) {
		GFF gff = new GFF(type, "", 1, 2);
		seqs.addElement(gff);
		
		SequenceFeature sf = new SequenceFeature();
		//System.out.println("Scores " + scores.get(type));
		sf.setStart(mincoord);
		sf.setEnd(maxcoord);
		
		sf.setScores((Hashtable) scores.get(type));
		sf.setType(type);
		
		feat.add(sf);
	    }
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
						// ((Exon) aSf).setPhase(phase);

						phase = (aSf.getLength() - (3 - phase)) % 3;
					}
				} else {
					for (int i = sf.length - 1; i >= 0; i--) {
						// System.out.println("Setting phase to " + phase);
						// ((Exon)sf[i]).setPhase(phase);

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
				SequenceFeature sf = (SequenceFeature) (g.getFeatures().elementAt(0));

				if (sf.getHitFeature() != null) {
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

	private Hashtable hash_set(Vector feat) {
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
}
