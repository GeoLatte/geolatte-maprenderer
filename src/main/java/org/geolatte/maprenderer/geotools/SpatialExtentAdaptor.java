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

import com.vividsolutions.jts.geom.*;
import org.geolatte.maprenderer.map.SpatialExtent;
import org.geolatte.maprenderer.reference.SpatialReference;
import org.geolatte.maprenderer.reference.SpatialReferenceException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.geom.Rectangle2D;

/**
 * Created by IntelliJ IDEA.
 * User: maesenka
 * Date: Dec 21, 2009
 * Time: 3:54:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class SpatialExtentAdaptor {

    static private GeometryFactory geometryFactory = new GeometryFactory();

    public static ReferencedEnvelope toReferencedEnvelope(SpatialExtent extent) throws SpatialReferenceException {
        SpatialReference sr = extent.getSpatialReference();
        int code = sr.getEPSGCode();
        try {
            CRSAuthorityFactory factory = CRS.getAuthorityFactory(true);
            CoordinateReferenceSystem crs = factory.createCoordinateReferenceSystem("EPSG:" + code);
            Rectangle2D rect = new Rectangle2D.Double(extent.getMinX(), extent.getMinY(), extent.getWidth(), extent.getHeight());
            return new ReferencedEnvelope(rect, crs);
        } catch (FactoryException e) {
            throw new SpatialReferenceException(e);
        }
    }

    public static Geometry toPolygon(SpatialExtent extent) {
        int srid = extent.getSpatialReference().getEPSGCode();
        Coordinate[] coordinates = new Coordinate[5];
        coordinates[0] = new Coordinate(extent.getMinX(), extent.getMinY());
        coordinates[1] = new Coordinate(extent.getMinX(), extent.getMaxY());
        coordinates[2] = new Coordinate(extent.getMaxX(), extent.getMaxY());
        coordinates[3] = new Coordinate(extent.getMaxX(), extent.getMinY());
        coordinates[4] = new Coordinate(extent.getMinX(), extent.getMinY());
        LinearRing shell = geometryFactory.createLinearRing(coordinates);
        Polygon polygon = geometryFactory.createPolygon(shell, new LinearRing[]{});
        polygon.setSRID(srid);
        return polygon;
    }
}
