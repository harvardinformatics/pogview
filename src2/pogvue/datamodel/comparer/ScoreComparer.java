package pogvue.datamodel.comparer;

import java.util.Comparator;
import pogvue.datamodel.motif.*;

public class ScoreComparer implements Comparator {

    public int compare(Object obj1, Object obj2) {

      return (int)(((TFMatch)obj1).getScore() - ((TFMatch)obj2).getScore());

    }
}
