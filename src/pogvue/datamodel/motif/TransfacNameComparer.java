package pogvue.datamodel.motif;

import java.util.Comparator;

public class TransfacNameComparer implements Comparator {
    public int compare(Object obj1, Object obj2) {

      TFMatrix tfm1 = (TFMatrix)obj1;
      TFMatrix tfm2 = (TFMatrix)obj2;

      return tfm1.getName().compareTo(tfm2.getName());
    }
}
