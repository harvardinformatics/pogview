package pogvue.datamodel.comparer;

import java.util.Comparator;
import pogvue.datamodel.*;

public class SeqFeatureCoordComparer implements Comparator {
    public int compare(Object obj1, Object obj2) {

	SequenceFeature sf1 = (SequenceFeature)obj1;
	SequenceFeature sf2 = (SequenceFeature)obj2;

	return sf1.getStart() - sf2.getStart();
    }
}
