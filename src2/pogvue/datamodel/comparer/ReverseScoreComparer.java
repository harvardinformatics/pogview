package pogvue.datamodel.comparer;

import java.util.Comparator;
import pogvue.datamodel.motif.*;

public class ReverseScoreComparer implements Comparator {

    public int compare(Object obj1, Object obj2) {

      return (int)(((TFMatch)obj2).getScore() - ((TFMatch)obj1).getScore());

    }
}
