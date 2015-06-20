package pogvue.datamodel.motif;

import java.util.Comparator;

public class TransfacInfComparer implements Comparator {
    public int compare(Object obj1, Object obj2) {

      TFMatrix tfm1 = (TFMatrix)obj1;
      TFMatrix tfm2 = (TFMatrix)obj2;

      return (int)(tfm1.getInformationContent() - tfm2.getInformationContent()+0.5);
    }
}
