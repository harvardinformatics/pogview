package pogvue.datamodel.expression;

import java.util.Comparator;
import java.util.Vector;

public class ExpressionLevelComparer implements Comparator {
    public int compare(Object obj1, Object obj2) {

      ExpressionLevel l1 = (ExpressionLevel)obj1;
      ExpressionLevel l2 = (ExpressionLevel)obj2;

      return l1.getLevel() - l2.getLevel();
    }
}
