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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.JTSGeometryOperations;
import org.geolatte.geom.Point;
import org.geolatte.geom.ProjectedGeometryOperations;
import org.geolatte.geom.crs.CoordinateReferenceSystem;

import static org.geolatte.geom.builder.DSL.c;
import static org.geolatte.geom.builder.DSL.polygon;
import static org.geolatte.geom.builder.DSL.ring;
import static org.geolatte.maprenderer.shape.EnvelopeHelper.height;
import static org.geolatte.maprenderer.shape.EnvelopeHelper.width;


public abstract class GeometryWrapper {

	public abstract Geometry<C2D> getGeometry();

	private final ProjectedGeometryOperations ops = new JTSGeometryOperations();

	protected ProjectedGeometryOperations getOperations() {
		return ops;
	}

	public Rectangle getBounds() {
		Envelope<C2D> env = getGeometry().getEnvelope();
		int minX = (int) Math.floor( env.lowerLeft().getX() );
		int minY = (int) Math.floor( env.lowerLeft().getY() );
		int width = (int) Math.ceil( width( env ) );
		int height = (int) Math.ceil( height( env ) );
		return new Rectangle( minX, minY, width, height );
	}


	public Rectangle2D getBounds2D() {
		Envelope<C2D> env = getGeometry().getEnvelope();
		C2D ll = env.lowerLeft();
		return new Rectangle2D.Double( ll.getX(), ll.getY(), width( env ), height( env ) );
	}

	public boolean contains(double x, double y) {
		Geometry<C2D> geom = getGeometry();
		return getOperations().contains( geom, new Point<C2D>( new C2D( x, y ), geom.getCoordinateReferenceSystem() ) );
	}

	public boolean contains(Point2D p) {
		return contains( p.getX(), p.getY() );
	}

	public boolean intersects(double x, double y, double w, double h) {
		Geometry<C2D> geom = getGeometry();
		return getOperations().intersects( geom, toPolygon( x, y, w, h, geom.getCoordinateReferenceSystem() ) );
	}

	public boolean intersects(Rectangle2D r) {
		return intersects( r.getX(), r.getY(), r.getWidth(), r.getHeight() );
	}

	public boolean contains(double x, double y, double w, double h) {
		org.geolatte.geom.Polygon<C2D> polygon = toPolygon( x, y, w, h, getGeometry().getCoordinateReferenceSystem() );
		return getOperations().contains( getGeometry(), polygon );
	}

	public boolean contains(Rectangle2D r) {
		return contains( r.getX(), r.getY(), r.getWidth(), r.getHeight() );
	}

	private org.geolatte.geom.Polygon<C2D> toPolygon(
			double x,
			double y,
			double w,
			double h,
			CoordinateReferenceSystem<C2D> crs) {
		return polygon( crs, ring( c( x, y ),
								   c( x + w, y ),
								   c( x + w, y + h ),
								   c( x, y + h ),
								   c( x, y ) ) );
	}
}
