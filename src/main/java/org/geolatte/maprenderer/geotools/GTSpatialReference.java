/*
 * This file is part of the GeoLatte project. This code is licenced under
 * the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the
 * License.
 *
 * Copyright (C) 2010 - 2010 and Ownership of code is shared by:
 * Qmino bvba - Romeinsestraat 18 - 3001 Heverlee (http://www.Qmino.com)
 * Geovise bvba - Generaal Eisenhowerlei 9 - 2140 Antwerpen (http://www.geovise.com)
 */

package org.geolatte.maprenderer.geotools;

import org.geolatte.maprenderer.reference.SpatialReference;
import org.geolatte.maprenderer.reference.SpatialReferenceCreationException;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Created by IntelliJ IDEA.
 * User: maesenka
 * Date: Dec 21, 2009
 * Time: 3:04:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class GTSpatialReference implements SpatialReference {

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
