package org.geolatte.common;

import java.util.Map;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Geometry;

/**
 * A Simple Feature class
 *
 * TODO -- this needs to be moved to the Geolatte-geom package
 *
 * Created by Karel Maesen, Geovise BVBA on 05/04/2018.
 */
public class Feature {

	private final String Id;
	private final Geometry<C2D> geometry;
	private final Map<String, Object> properties;


	public Feature(String id, Geometry<C2D> geometry, Map<String, Object> properties) {
		Id = id;
		this.geometry = geometry;
		this.properties = properties;
	}

	public String getId() {
		return Id;
	}

	public Geometry<C2D> getGeometry() {
		return geometry;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public Object getProperty(String properyName) {
		return properties.get( properyName );
	}
}
