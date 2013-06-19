package pogvue.feature;

import pogvue.datamodel.ChrRegion;

import java.awt.*;
import javax.swing.*;

public class RegionFeature implements Feature {
	private ChrRegion region;
	
	public RegionFeature(ChrRegion region) {
		this.region = region;
	}
	
	public void draw(Graphics2D g, int xoffset, int yoffset) {
		// It needs to know things like width, height, color, Font
		
		// Some drawing here
		
	}

	public String toString() {
		return region.toString();
	}
}
