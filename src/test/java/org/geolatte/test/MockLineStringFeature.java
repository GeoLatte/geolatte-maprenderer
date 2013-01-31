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

import org.geolatte.geom.*;
import org.geolatte.geom.crs.CrsId;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: May 23, 2010
 */
public class MockLineStringFeature extends AbstractMockFeature {


    public MockLineStringFeature(Geometry geom) {
        super(geom);
    }

    public MockLineStringFeature() {
        super();
    }

    protected Geometry generateGeom() {
        double startx = 10;
        double starty = 10;

        PointSequenceBuilder sequenceBuilder = PointSequenceBuilders.fixedSized(4, DimensionalFlag.d2D, CrsId.UNDEFINED);
        sequenceBuilder.add(startx, starty);
        sequenceBuilder.add(startx + 10.0, starty + 12.0);
        sequenceBuilder.add(startx + 20.0, starty);
        sequenceBuilder.add(startx + 30, starty + 10.0);
        return new LineString(sequenceBuilder.toPointSequence());
    }

    public static MockLineStringFeature createLine(double startX, double startY, double endX, double endY) {
        PointSequenceBuilder sequenceBuilder = PointSequenceBuilders.fixedSized(2, DimensionalFlag.d2D, CrsId.UNDEFINED);
        sequenceBuilder.add(startX, startY);
        sequenceBuilder.add(endX, endY);
        Geometry geom = new LineString(sequenceBuilder.toPointSequence());
        return new MockLineStringFeature(geom);
    }


    @Override
    public String getGeometryName() {
        return "geometry";
    }
}
