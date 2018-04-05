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

package org.geolatte.test;


import org.geolatte.geom.C2D;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.LinearRing;
import org.geolatte.geom.Polygon;
import org.geolatte.geom.PositionSequenceBuilder;
import org.geolatte.geom.PositionSequenceBuilders;

import static org.geolatte.geom.builder.DSL.c;
import static org.geolatte.geom.builder.DSL.polygon;
import static org.geolatte.geom.builder.DSL.ring;

/**
 * @author Karel Maesen, Geovise BVBA
 * creation-date: May 23, 2010
 */
public class MockPolygonFeature extends AbstractMockFeature {


	public MockPolygonFeature() {
		super(
				generateGeom()
		);
	}

	public MockPolygonFeature(Geometry<C2D> geom) {
		super( geom );
	}

	private static Geometry<C2D> generateGeom() {
		double startx = Math.random() * 90;
		double starty = Math.random() * 90;
		double width = Math.random() * 90;
		double height = Math.random() * 90;

		return polygon( CRS, ring(
				c( startx, starty ),
				c( startx, starty + height ),
				c( startx + width, starty + height ),
				c( startx + width, starty ),
				c( startx, starty )
		) );
	}

	public static MockPolygonFeature createRect(double minX, double minY, double maxX, double maxY) {
		PositionSequenceBuilder sequenceBuilder = PositionSequenceBuilders.fixedSized( 5, C2D.class );
		sequenceBuilder.add( minX, minY );
		sequenceBuilder.add( minX, maxY );
		sequenceBuilder.add( maxX, maxY );
		sequenceBuilder.add( maxX, minY );
		sequenceBuilder.add( minX, minY );
		LinearRing shell = new LinearRing( sequenceBuilder.toPositionSequence(), CRS );
		return new MockPolygonFeature( new Polygon( new LinearRing[] {shell} ) );
	}


}