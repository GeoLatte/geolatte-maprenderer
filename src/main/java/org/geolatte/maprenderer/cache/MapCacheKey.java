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

package org.geolatte.maprenderer.cache;

import org.geolatte.maprenderer.map.SpatialExtent;


public class MapCacheKey {

    private final String layerName;
    private final String format;
    private final SpatialExtent extent;
    private final int width;
    private final int height;

    public MapCacheKey(String layerName, String format, SpatialExtent extent, int width, int height) {
        this.layerName = layerName;
        this.format = format;
        this.extent = extent;
        this.width = width;
        this.height = height;
    }

    public String getLayerName() {
        return layerName;
    }

    public String getFormat() {
        return format;
    }

    public SpatialExtent getExtent() {
        return extent;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapCacheKey that = (MapCacheKey) o;

        if (height != that.height) return false;
        if (width != that.width) return false;
        if (extent != null ? !extent.equals(that.extent) : that.extent != null) return false;
        if (format != null ? !format.equals(that.format) : that.format != null) return false;
        if (layerName != null ? !layerName.equals(that.layerName) : that.layerName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = layerName != null ? layerName.hashCode() : 0;
        result = 31 * result + (format != null ? format.hashCode() : 0);
        result = 31 * result + (extent != null ? extent.hashCode() : 0);
        result = 31 * result + width;
        result = 31 * result + height;
        return result;
    }
}
