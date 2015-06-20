package pogvue.feature;

public class RegionFeatureType implements FeatureType {
	String name;
	
	public RegionFeatureType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	public String toString() {
		return getName();
	}
}
