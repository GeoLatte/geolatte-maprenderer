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

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import org.geolatte.maprenderer.map.SpatialExtent;
import org.geolatte.maprenderer.reference.Projector;
import org.geolatte.maprenderer.reference.SpatialReferenceCreationException;
import org.geolatte.maprenderer.reference.SpatialReferenceFactory;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.NamedIdentifier;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;

import java.util.Set;

public class GTSpatialReferenceFactory implements SpatialReferenceFactory {


    public Projector createProjector(GTSpatialReference source,
                                     GTSpatialReference target) {
        CoordinateReferenceSystem sourceCRS = findCRS(source);
        CoordinateReferenceSystem targetCRS = findCRS(target);
        return new CRSProjector(sourceCRS, targetCRS);
    }

    public CoordinateReferenceSystem findCRS(GTSpatialReference source) {
        return source.getCoordinateReferenceSystem();
    }


    @SuppressWarnings("unchecked")
    public static String extractSRID(CoordinateReferenceSystem crs) {
        Set identifiers = crs.getIdentifiers();
        for (Object o : identifiers) {
            NamedIdentifier namedIdentifier = (NamedIdentifier) o;
            if (namedIdentifier.getCodeSpace().equals("EPSG")) {
                return namedIdentifier.getCode();
            }
        }
        return "0";
    }

    private static class CRSProjector implements Projector {

        private final MathTransform transform;
        private final CoordinateReferenceSystem sourceCRS;
        private final CoordinateReferenceSystem targetCRS;
        private final boolean inLongLat;
        private final String sourceSRID;
        private final String targetSRID;
        private final GTSpatialReference sourceSpatialReference;
        private final GTSpatialReference targetSpatialReference;

        public CRSProjector(String source, String target, boolean inLongLat) {
            try {
                this.inLongLat = inLongLat;
                CRSAuthorityFactory factory = CRS.getAuthorityFactory(inLongLat);
                GTSpatialReferenceFactory referenceFactory = new GTSpatialReferenceFactory();
                this.sourceSpatialReference = new GTSpatialReference(source, inLongLat);
                this.targetSpatialReference = new GTSpatialReference(target, inLongLat);
                this.sourceCRS = factory.createCoordinateReferenceSystem("EPSG:" + source);
                this.targetCRS = factory.createCoordinateReferenceSystem("EPSG:" + target);
                this.transform = CRS.findMathTransform(sourceCRS, targetCRS);
                this.targetSRID = target;
                this.sourceSRID = source;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


        public CRSProjector(CoordinateReferenceSystem sourceCRS, CoordinateReferenceSystem targetCRS) {
            try {
                this.inLongLat = true; //is this correct???
                this.sourceCRS = sourceCRS;
                this.targetCRS = targetCRS;
                this.transform = CRS.findMathTransform(sourceCRS, targetCRS);
                this.targetSRID = extractSRID(targetCRS);
                this.sourceSRID = extractSRID(sourceCRS);
                GTSpatialReferenceFactory referenceFactory = new GTSpatialReferenceFactory();
                this.sourceSpatialReference = new GTSpatialReference(sourceSRID, inLongLat);
                this.targetSpatialReference = new GTSpatialReference(targetSRID, inLongLat);
            } catch (FactoryException e) {
                throw new RuntimeException(e);
            } catch (SpatialReferenceCreationException e) {
                throw new RuntimeException(e);
            }

        }

        public Geometry project(Geometry geom) {
            if (geom == null) return null;
            try {
                return JTS.transform(geom, this.transform);
            } catch (MismatchedDimensionException e) {
                throw new RuntimeException(e);
            } catch (TransformException e) {
                throw new RuntimeException(e);
            }
        }

        public SpatialExtent project(SpatialExtent extent) {
            return transform(extent, this.transform, this.targetSpatialReference);
        }

        public Projector inverse() {
            return new CRSProjector(this.targetSRID, this.sourceSRID, this.inLongLat);
        }

        public SpatialExtent inverseProject(SpatialExtent extent) {
            try {
                return transform(extent, this.transform.inverse(), this.sourceSpatialReference);
            } catch (NoninvertibleTransformException e) {
                throw new RuntimeException(e);
            }
        }

        private SpatialExtent transform(SpatialExtent extent, MathTransform transform, GTSpatialReference targetReference) {
            Envelope env = extent.toEnvelope();
            try {
                Envelope transformed = JTS.transform(env, transform);
                return SpatialExtent.fromEnvelope(transformed, targetReference);
            } catch (TransformException e) {
                throw new RuntimeException(e);
            }
        }

        private double[] getExtentOrdinates(double[] pts) {
            double minX = Double.MAX_VALUE;
            double minY = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;
            double maxY = Double.MIN_VALUE;
            for (int i = 0; i < pts.length; i++) {
                if (isX(i)) {
                    minX = Math.min(minX, pts[i]);
                    maxX = Math.max(maxX, pts[i]);
                } else {
                    minY = Math.min(minY, pts[i]);
                    maxY = Math.max(maxY, pts[i]);
                }
            }
            return new double[]{minX, minY, maxX, maxY};
        }

        private boolean isX(int i) {
            return i % 2 == 0;
        }


    }


}