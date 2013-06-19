package pogvue.datamodel.comparer;

import java.util.Comparator;
import java.util.Vector;
import pogvue.datamodel.*;

public class SeqFeatureVectorScoreComparer implements Comparator {

    public int compare(Object obj1, Object obj2) {

	Vector v1 = (Vector)obj1;
	Vector v2 = (Vector)obj2;

	double topscore1 = 0;
	double topscore2 = 0;

	for (int i = 0;i < v1.size(); i++) {
	    if (((SequenceFeature)v1.elementAt(i)).getScore() > topscore1) {
		topscore1 = ((SequenceFeature)v1.elementAt(i)).getScore();
	    }
	}
	for (int i = 0;i < v2.size(); i++) {
	    if (((SequenceFeature)v2.elementAt(i)).getScore() > topscore2) {
		topscore2 = ((SequenceFeature)v2.elementAt(i)).getScore();
	    }
	}

	return (int)(topscore1 - topscore2);
    }
}
