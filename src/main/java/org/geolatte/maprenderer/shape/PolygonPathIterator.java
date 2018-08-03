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

import java.awt.geom.AffineTransform;

import org.geolatte.geom.C2D;
import org.geolatte.geom.LinearRing;
import org.geolatte.geom.Polygon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PolygonPathIterator extends GeometryPathIterator {

	private static Logger LOGGER = LoggerFactory.getLogger( PolygonPathIterator.class );

	private final Polygon<C2D> polygon;
	private int coordinateIndex = 0;
	private int ringIndex = -1;
	private LinearRing<C2D> currentRing = null;
	private C2D currentCoordinate;


	public PolygonPathIterator(Polygon geom, AffineTransform transform, AffineTransform worldToImageTransform) {
		super( worldToImageTransform, transform );
		this.polygon = geom;
		this.currentRing = this.polygon.getExteriorRing();
		this.coordinateIndex = 0;
		currentCoordinate = this.currentRing.getPositionN( (this.coordinateIndex) );
	}

	protected void advance() {
		coordinateIndex++;
		if ( beyondCurrentRing() ) {
			nextRing();
		}
		setCurrentCoordinate( currentRing.getPositionN( coordinateIndex ) );
	}

	protected void setCurrentCoordinate(C2D coordinate) {
		this.currentCoordinate = coordinate;
	}

	protected C2D getCurrentCoordinate() {
		return this.currentCoordinate;
	}

	@Override
	protected int determineOperation() {
		if ( coordinateIndex == 0 ) {
			return SEG_MOVETO;
		}
		else if ( coordinateIndex == currentRing.getNumPositions() - 1 ) {
			return SEG_CLOSE;
		}
		else {
			return SEG_LINETO;
		}
	}

	private void nextRing() {
		coordinateIndex = 0;
		ringIndex++;
		if ( ringIndex < this.polygon.getNumInteriorRing() ) {
			currentRing = polygon.getInteriorRingN( ringIndex );
		}
		else {
			setIsDone();
		}
	}

	private boolean beyondCurrentRing() {
		return (coordinateIndex >= currentRing.getNumPositions());
	}


}
