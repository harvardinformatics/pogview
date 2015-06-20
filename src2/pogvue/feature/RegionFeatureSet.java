package pogvue.feature;

import java.awt.Graphics2D;
import java.util.*;


public class RegionFeatureSet implements FeatureSet, Feature {
	ArrayList feat;
	
	public RegionFeatureSet() {
		feat = new ArrayList();
	}
	public void addFeature(Feature f) {
		feat.add(f);
	}
	
	public void draw(Graphics2D g, int xoffset, int yoffset) {
		// How should the featureset handle this
		// Pass in ViewProperties and ask it for various things?
		
		//  We need FeatureType interface - then we can pass in the FeatureType
		//  double width  = av.getCharWidth();
		//  int    height = av.getCharHeight(this);
		//  Color  color  = av.getColor(this);
	}

	public String toString() {
		return "";
	}

	public int size() {
		return feat.size();
	}
}
