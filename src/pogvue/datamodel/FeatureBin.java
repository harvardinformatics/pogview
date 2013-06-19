package pogvue.datamodel;

import java.util.*;


public class FeatureBin {

    private int start = -1;
    private int end   = -1;
    private String chr;
    private Vector feat;

    private int size;

    public FeatureBin(String chr,int start, int end) {
	this.start = start;
	this.end   = end;
	this.chr   = chr;

	feat = new Vector();
    }

    public void addFeature(SequenceFeature f) {
	if (!feat.contains(f)) {
	    feat.add(f);
	}
    }

    public double getDensity() {
	return (end-start+1)/(1.0*feat.size());
    }
    private Vector getFeatures() {
	return feat;
    }
    public int getSize() {
	if (feat.size() > 0) {
	    return feat.size();
	} else {
	    return size;
	}
    }
    public void setSize(int size) {
	this.size = size;
    }
}
