package pogvue.datamodel.comparer;

import java.util.Comparator;
import pogvue.datamodel.ChrRegion;

public class ChrRegionStartComparer implements Comparator {
	
	public int compare(Object obj1, Object obj2) {
		return (((ChrRegion)obj1).getStart() - ((ChrRegion)obj2).getStart());
	}
}
