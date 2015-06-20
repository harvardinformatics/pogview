package pogvue.datamodel.expression;

import java.util.Comparator;
import java.util.Vector;

public class ExpressionVectorComparer implements Comparator {
    public int compare(Object obj1, Object obj2) {

      Vector v1 = (Vector)obj1;
      Vector v2 = (Vector)obj2;

      int col = ((ExpressionLevel)v1.elementAt(0)).getSortCol();


      return ((ExpressionLevel)v1.elementAt(col)).getLevel() - ((ExpressionLevel)v2.elementAt(col)).getLevel();
    }
}
