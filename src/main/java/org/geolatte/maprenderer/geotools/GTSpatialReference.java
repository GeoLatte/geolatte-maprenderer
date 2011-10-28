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

package org.geolatte.maprenderer.geotools;

import org.geolatte.maprenderer.reference.SpatialReferenceCreationException;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


public class GTSpatialReference {

    private final CoordinateReferenceSystem crs;
    private final int epsgId;


    public GTSpatialReference(String epsgCode, boolean inLongLat) throws SpatialReferenceCreationException {
        CRSAuthorityFactory factory = CRS.getAuthorityFactory(inLongLat);
        try {
            epsgId = Integer.parseInt(epsgCode);
            crs = factory.createCoordinateReferenceSystem("EPSG:" + epsgCode);
        } catch (NumberFormatException e) {
            throw new SpatialReferenceCreationException(e);
        } catch (FactoryException e) {
            throw new SpatialReferenceCreationException(e);
        }
    }

    public int getEPSGCode() {
        return epsgId;
    }

    protected CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }

    @Override
    public String toString() {
        return "EPSG: " + this.epsgId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GTSpatialReference that = (GTSpatialReference) o;

        if (epsgId != that.epsgId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return epsgId;
    }
}
