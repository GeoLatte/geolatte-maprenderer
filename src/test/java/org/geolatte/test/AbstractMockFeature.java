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

import org.geolatte.common.Feature;
import org.geolatte.geom.C2D;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.crs.CoordinateReferenceSystems;
import org.geolatte.geom.crs.ProjectedCoordinateReferenceSystem;
import org.geolatte.geom.crs.SingleCoordinateReferenceSystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: May 23, 2010
 */
public abstract class AbstractMockFeature extends Feature {
    protected static AtomicInteger counter = new AtomicInteger(0);

    public static final SingleCoordinateReferenceSystem<C2D> CRS = CoordinateReferenceSystems.PROJECTED_2D_METER;


    static private String generateId() {
        int i = counter.incrementAndGet();
        return Integer.toString( i );
    }

    public AbstractMockFeature(Geometry geom){
        super(generateId(), geom, new HashMap<String, Object>());

    }

    public AbstractMockFeature(Geometry geom, Map<String, Object> properties){
        super(generateId(), geom, properties);

    }

}
