package pogvue.datamodel.motif;

import java.util.Comparator;

public class PwmClusterComparer implements Comparator {
    public int compare(Object obj1, Object obj2) {

      PwmCluster pwm1 = (PwmCluster)obj1;
      PwmCluster pwm2 = (PwmCluster)obj2;

      return pwm2.size() - pwm1.size();
    }
}
