package pogvue.io;

import pogvue.datamodel.*;
import pogvue.datamodel.motif.*;
import java.util.*;
import java.io.*;


public class Ormat2PwmLine {

  public static void main(String[] args) {
    try {
      
      OrmatFile of = new OrmatFile(args[0],"File");
      
      of.parse();
    
      Vector mats = of.getTFMatrices();

      for (int i = 0;i < mats.size(); i++) {
	TFMatrix tfm = (TFMatrix)mats.elementAt(i);

	Pwm.printPwmLine2(tfm.getPwm().getPwm(),tfm.getName(),1,1);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
