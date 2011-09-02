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

import org.geolatte.core.Feature;

public interface MapLayer {

    public abstract String getTitle();

    public abstract boolean isVisible();

    public abstract void setPainter(Painter painter);

    public abstract void setTitle(String title);

    public abstract void setVisible(boolean visible);

    public abstract MapLayerSourceFactory getSourceFactory();

    public abstract Painter getPainter();

    public abstract SpatialExtent getBounds();

    public abstract Labeler getLabeler();

    public void setSourceFactory(MapLayerSourceFactory factory);

    public Iterable<Feature> getFeatures(SpatialExtent extent);


}