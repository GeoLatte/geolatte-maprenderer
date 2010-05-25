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

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import org.geolatte.maprenderer.map.SpatialExtent;
import org.geolatte.maprenderer.reference.Projector;
import org.geolatte.maprenderer.reference.SpatialReference;
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


    public Projector createProjector(SpatialReference source,
                                     SpatialReference target) {
        CoordinateReferenceSystem sourceCRS = findCRS(source);
        CoordinateReferenceSystem targetCRS = findCRS(target);
        //TODO :: cache CRSProjector objects....
        return new CRSProjector(sourceCRS, targetCRS);
    }

    public SpatialReference createSpatialReference(String epsgCode, boolean inLatLong) throws SpatialReferenceCreationException {
        return new GTSpatialReference(epsgCode, inLatLong);
    }

    public CoordinateReferenceSystem findCRS(SpatialReference source) {
        if (source instanceof GTSpatialReference) {
            return ((GTSpatialReference) source).getCoordinateReferenceSystem();
        } else {
            throw new UnsupportedOperationException("This operation works only with GTSpatialReference");
        }
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
        private final SpatialReference sourceSpatialReference;
        private final SpatialReference targetSpatialReference;

        public CRSProjector(String source, String target, boolean inLongLat) {
            try {
                this.inLongLat = inLongLat;
                CRSAuthorityFactory factory = CRS.getAuthorityFactory(inLongLat);
                GTSpatialReferenceFactory referenceFactory = new GTSpatialReferenceFactory();
                this.sourceSpatialReference = referenceFactory.createSpatialReference(source, inLongLat);
                this.targetSpatialReference = referenceFactory.createSpatialReference(target, inLongLat);
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
                this.sourceSpatialReference = referenceFactory.createSpatialReference(sourceSRID, inLongLat);
                this.targetSpatialReference = referenceFactory.createSpatialReference(targetSRID, inLongLat);
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

        private SpatialExtent transform(SpatialExtent extent, MathTransform transform, SpatialReference targetReference) {
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