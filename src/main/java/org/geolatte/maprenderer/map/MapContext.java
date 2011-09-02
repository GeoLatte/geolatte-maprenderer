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

package org.geolatte.maprenderer.map;

import org.geolatte.maprenderer.reference.SpatialReference;

public interface MapContext {

    public abstract boolean addLayer(int index, MapLayer layer);

    public abstract void clearLayerList();

    public abstract SpatialReference getSpatialReference();

    public abstract MapLayer getLayer(int index)
            throws IndexOutOfBoundsException;

    public abstract SpatialExtent getLayerBounds();

    public abstract int getLayerCount();

    public abstract MapLayer[] getLayers();

    public abstract String getTitle();

    public abstract int indexOf(MapLayer layer);

    public abstract void moveLayer(int sourcePosition, int destPosition);

    public abstract MapLayer removeLayer(int index);

    public abstract boolean removeLayer(MapLayer layer);

    public abstract void setSpatialReference(SpatialReference crs);

    public abstract void setTitle(String title);

}