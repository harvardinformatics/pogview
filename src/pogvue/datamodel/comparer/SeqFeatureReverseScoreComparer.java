package pogvue.datamodel.comparer;

import java.util.Comparator;
import pogvue.datamodel.*;

public class SeqFeatureReverseScoreComparer implements Comparator {

    public int compare(Object obj1, Object obj2) {
      SequenceFeature sf1 = (SequenceFeature)obj1;
      SequenceFeature sf2 = (SequenceFeature)obj2;
      
      int score1 = (int)(100*sf1.getScore());
      int score2 = (int)(100*sf2.getScore());

      return score2 - score1;

    }
}
