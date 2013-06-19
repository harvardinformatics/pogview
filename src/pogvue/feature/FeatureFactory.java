package pogvue.feature;

import pogvue.datamodel.*;

public interface FeatureFactory {
	
	public FeatureIterator getFeatureIterator(ChrRegion region);

}
