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

package org.geolatte.maprenderer.map;

import com.vividsolutions.jts.geom.Envelope;
import org.geolatte.maprenderer.reference.SpatialReference;

import java.awt.*;
import java.io.Serializable;

public class SpatialExtent implements Serializable {

    private final double minX;

    private final double minY;

    private final double maxX;

    private final double maxY;

    private final SpatialReference spatialReference;

    public SpatialExtent(SpatialReference reference) {
        this.minX = 0;
        this.minY = 0;
        this.maxX = 0;
        this.maxY = 0;
        this.spatialReference = reference;
    }

    public SpatialExtent(double minX, double minY, Dimension dimension, SpatialReference reference) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = this.minX + dimension.getWidth();
        this.maxY = this.minY + dimension.getHeight();
        this.spatialReference = reference;
    }

    public SpatialExtent(double minX, double minY, double maxX, double maxY, SpatialReference reference) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.spatialReference = reference;
    }

    public SpatialReference getSpatialReference() {
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

    public static SpatialExtent fromEnvelope(Envelope envelope, SpatialReference reference) {
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
