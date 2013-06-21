package pogvue.util;

import java.util.Hashtable;


public class GetOptions {

	public static Hashtable get(String [] args) {

		Hashtable hash = new Hashtable();

		
		for (int i = 0; i < args.length; i++) {
			if (args[i].indexOf("-") == 0) {
				if (args.length > i+1 && args[i+1].indexOf("-") != 0) {
					hash.put(args[i],args[i+1]);
					i++;
				} else {
					hash.put(args[i], 1);
				}
			}
		}
		
		return hash;
	}
}
