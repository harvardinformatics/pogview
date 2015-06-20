package pogvue.feature;

import pogvue.io.*;
import pogvue.datamodel.ChrRegion;
import pogvue.datamodel.comparer.ChrRegionStartComparer;
import java.util.*;
import java.io.*;

public class HttpFeatureFactory implements FeatureFactory {

	String    url1 = "http://www.broad.mit.edu/~mclamp/fetchmamgff.php";
	String    url2 = "http://www.broad.mit.edu/~mclamp/fetchmamgraph.php";
	
	ArrayList regions;
	
	public HttpFeatureFactory() {
		regions = new ArrayList();
	}
	
	public FeatureIterator getFeatureIterator(ChrRegion reg) {
		
		getFeatures(reg);
		Collections.sort(regions,new ChrRegionStartComparer());
		// So now we just need to return an iterator with the features
		return null;
	}
	
	private void getFeatures(ChrRegion reg) {				
		reg.expandRegions(regions);
		
		// Now we have to fetch features on the new ones
		// ChrRegion has a flag saying whether it has features or not
		
		for (int i = 0; i < regions.size();i++) {
			ChrRegion tmp = (ChrRegion)regions.get(i);
			if (!tmp.hasFeatures()) {
				try {
					String regstr = "?query=" + tmp.getChr() + "&start=" + tmp.getStart() + "&end=" + tmp.getEnd();
					
					System.out.println("regstr " + regstr);
					
					GFFFile   gff = new GFFFile  (url1 + regstr,"URL");
					GraphFile grf = new GraphFile(url2 + regstr,"URL");
					
					gff.parse();
					grf.parse();
					
					Vector f1 = gff.getFeatures();
					Vector f2 = grf.getFeatures();
					
					tmp.hasFeatures(true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public void printRegions() {
		for (int i = 0; i < regions.size(); i++) {
			ChrRegion reg = (ChrRegion)regions.get(i);
			System.out.println(reg);
		}
	}
	
	public static void main(String[] args) {
		int count = 100;
		
		HttpFeatureFactory hff = new HttpFeatureFactory();
		
		for (int i = 0; i < count; i++) {
			int start = (int)(100000*Math.random());
			int end   = start + 10000 - 1;
			hff.getFeatures(new ChrRegion("chr1",start,end));
		}
		hff.printRegions();
	}
}		
