/*
 * This file is part of the GeoLatte project.
 *
 *     GeoLatte is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     GeoLatte is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with GeoLatte.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright (C) 2010 - 2011 and Ownership of code is shared by:
 *  Qmino bvba - Esperantolaan 4 - 3001 Heverlee  (http://www.qmino.com)
 *  Geovise bvba - Generaal Eisenhowerlei 9 - 2140 Antwerpen (http://www.geovise.com)
 */

package org.geolatte.maprenderer.shape;

import java.awt.*;
import java.awt.geom.AffineTransform;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.LineString;
import org.geolatte.geom.MultiLineString;
import org.geolatte.geom.MultiPolygon;


public class ShapeAdapter {

	final private AffineTransform worldToImageTransform;

	public ShapeAdapter(AffineTransform worldToImageTransform) {
		this.worldToImageTransform = worldToImageTransform;
	}

	public Shape[] toShape(Geometry<C2D> geometry) {
		if ( geometry == null ) {
			return new Shape[] {};
		}
		if ( geometry instanceof org.geolatte.geom.Polygon ) {
			return new Shape[] {new PolygonWrapper( (org.geolatte.geom.Polygon<C2D>) geometry, worldToImageTransform )};
		}
		else if ( geometry instanceof MultiPolygon ) {
			MultiPolygon<C2D> multiPolygon = (MultiPolygon<C2D>) geometry;
			Shape[] shapes = new Shape[multiPolygon.getNumGeometries()];
			load( shapes, multiPolygon );
			return shapes;
		}
		else if ( geometry instanceof LineString ) {
			return new Shape[] {new LineStringWrapper( (LineString<C2D>) geometry, worldToImageTransform )};
		}
		else if ( geometry instanceof MultiLineString ) {
			MultiLineString<C2D> multiLineString = (MultiLineString<C2D>) geometry;
			Shape[] shapes = new Shape[multiLineString.getNumGeometries()];
			loadShape( shapes, multiLineString );
			return shapes;
		}
		throw new UnsupportedOperationException( "Can't adapt shapes fo type " + geometry.getGeometryType() );
	}

	private void loadShape(Shape[] shapes, MultiLineString<C2D> multiLineString) {
		for ( int i = 0; i < shapes.length; i++ ) {
			LineString<C2D> ls = multiLineString.getGeometryN( i );
			shapes[i] = new LineStringWrapper( ls, worldToImageTransform );
		}
	}

	private void load(Shape[] shapes, MultiPolygon<C2D> multiPolygon) {
		for ( int i = 0; i < shapes.length; i++ ) {
			org.geolatte.geom.Polygon<C2D> pg = multiPolygon.getGeometryN( i );
			shapes[i] = new PolygonWrapper( pg, worldToImageTransform );
		}
	}

}
