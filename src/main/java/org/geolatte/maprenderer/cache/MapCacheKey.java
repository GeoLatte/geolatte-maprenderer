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

package org.geolatte.maprenderer.cache;

import org.geolatte.maprenderer.map.SpatialExtent;

/**
 * Created by IntelliJ IDEA.
 * User: maesenka
 * Date: Feb 15, 2010
 * Time: 5:47:54 PM
 * To change this template use File | Settings | File Templates.
 */
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
