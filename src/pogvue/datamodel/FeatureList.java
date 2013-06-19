package pogvue.datamodel;

import java.util.*;

public class FeatureList {
	private Vector    feat;
	private Hashtable types;
	private Vector    typevect;
	
	public FeatureList() {
		feat     = new Vector();
		types    = new Hashtable();
		typevect = new Vector();
	}
	
	public FeatureList(Vector feat) {
		this.feat = feat;
		setTypes();
	}
	
	public void addFeature(SequenceFeature f) {
		feat.addElement(f);
		addType(f);
	}
	
	private void setTypes() {
		types    = new Hashtable();
		typevect = new Vector();
		
		for (int i = 0; i < feat.size(); i++) {
			SequenceFeature sf = (SequenceFeature)feat.elementAt(i);
			addType(sf);
		}		
	}
	
	private void addType(SequenceFeature sf) {
		
		if (types == null) {
			types    = new Hashtable();
			typevect = new Vector();
		}
		
		if (!types.containsKey(sf.getType())) {
			Vector v = new Vector();
			types.put(sf.getType(),v);
			typevect.addElement(sf.getType());
		}
		
		Vector v = (Vector)types.get(sf.getType());
		v.addElement(sf);
		
	}
	
	public Vector getTypes() {
		return typevect;
	}
	public Hashtable getTypeHash() {
		return types;
	}
}
