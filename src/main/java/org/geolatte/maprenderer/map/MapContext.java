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