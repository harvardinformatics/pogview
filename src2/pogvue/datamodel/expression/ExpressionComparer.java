package pogvue.datamodel.expression;

import java.util.Comparator;

public class ExpressionComparer implements Comparator {
    public int compare(Object obj1, Object obj2) {

	ExpressionLevel exp1 = (ExpressionLevel)obj1;
	ExpressionLevel exp2 = (ExpressionLevel)obj2;

	return exp1.getLevel() - exp2.getLevel();
    }
}
