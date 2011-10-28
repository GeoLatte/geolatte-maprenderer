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

package org.geolatte.maprenderer.map;

import com.vividsolutions.jts.geom.Envelope;
import org.geolatte.maprenderer.geotools.GTSpatialReference;

import java.awt.*;
import java.io.Serializable;

//TODO -- replace by corresponding class in Geolatte-geom or Geolatte-common
@Deprecated
public class SpatialExtent implements Serializable {

    private final double minX;

    private final double minY;

    private final double maxX;

    private final double maxY;

    private final GTSpatialReference spatialReference;

    public SpatialExtent(GTSpatialReference reference) {
        this.minX = 0;
        this.minY = 0;
        this.maxX = 0;
        this.maxY = 0;
        this.spatialReference = reference;
    }

    public SpatialExtent(double minX, double minY, Dimension dimension, GTSpatialReference reference) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = this.minX + dimension.getWidth();
        this.maxY = this.minY + dimension.getHeight();
        this.spatialReference = reference;
    }

    public SpatialExtent(double minX, double minY, double maxX, double maxY, GTSpatialReference reference) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.spatialReference = reference;
    }

    public GTSpatialReference getSpatialReference() {
        return this.spatialReference;
    }

    public double getMinX() {
        return this.minX;
    }

    public double getMaxX() {
        return this.maxX;
    }

    public double getMinY() {
        return this.minY;
    }

    public double getMaxY() {
        return this.maxY;
    }

    public double getWidth() {
        return this.getMaxX() - this.getMinX();
    }

    public double getHeight() {
        return this.getMaxY() - this.getMinY();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[")
                .append(getMinX())
                .append(" ")
                .append(getMinY())
                .append(", ")
                .append(getMaxX())
                .append(" ")
                .append(getMaxY())
                .append(", SRID=")
                .append(getSpatialReference().toString())
                .append("]");
        return builder.toString();
    }

    public static SpatialExtent union(SpatialExtent bounds, SpatialExtent otherBounds) {
        if (bounds == null) return otherBounds;
        if (otherBounds == null) return otherBounds;
        if (!bounds.getSpatialReference().equals(otherBounds.getSpatialReference()))
            throw new IllegalArgumentException("Bounds must have equal SpatialReferences.");
        double minX = Math.min(bounds.getMinX(), otherBounds.getMinX());
        double minY = Math.min(bounds.getMinY(), otherBounds.getMinY());
        double maxX = Math.max(bounds.getMaxX(), otherBounds.getMaxX());
        double maxY = Math.max(bounds.getMaxY(), otherBounds.getMaxY());
        return new SpatialExtent(minX, minY, maxX, maxY, bounds.getSpatialReference());
    }

    public static SpatialExtent fromEnvelope(Envelope envelope, GTSpatialReference reference) {
        return new SpatialExtent(envelope.getMinX(), envelope.getMinY(), envelope.getMaxX(), envelope.getMaxY(), reference);
    }


    public Envelope toEnvelope() {
        return new Envelope(getMinX(), getMaxX(), getMinY(), getMaxY());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpatialExtent that = (SpatialExtent) o;

        if (Double.compare(that.maxX, maxX) != 0) return false;
        if (Double.compare(that.maxY, maxY) != 0) return false;
        if (Double.compare(that.minX, minX) != 0) return false;
        if (Double.compare(that.minY, minY) != 0) return false;
        if (spatialReference != null ? !spatialReference.equals(that.spatialReference) : that.spatialReference != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = minX != +0.0d ? Double.doubleToLongBits(minX) : 0L;
        result = (int) (temp ^ (temp >>> 32));
        temp = minY != +0.0d ? Double.doubleToLongBits(minY) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = maxX != +0.0d ? Double.doubleToLongBits(maxX) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = maxY != +0.0d ? Double.doubleToLongBits(maxY) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (spatialReference != null ? spatialReference.hashCode() : 0);
        return result;
    }
}
