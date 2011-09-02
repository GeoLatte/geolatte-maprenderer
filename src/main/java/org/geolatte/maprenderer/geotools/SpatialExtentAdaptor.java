/*
 * Copyright (c) 2011. Geovise BVBA, QMINO BVBA
 *
 * This file is part of GeoLatte Mapserver.
 *
 * GeoLatte Mapserver is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GeoLatte Mapserver is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GeoLatte Mapserver.  If not, see <http://www.gnu.org/licenses/>.
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
