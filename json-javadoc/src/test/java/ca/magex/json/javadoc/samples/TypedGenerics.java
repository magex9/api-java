package ca.magex.json.javadoc.samples;

import java.util.List;

public interface TypedGenerics<T extends BasicEntity> extends Comparable<T> {

	public List<T> find();
	
}
