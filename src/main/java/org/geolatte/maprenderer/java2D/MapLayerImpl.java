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

package org.geolatte.maprenderer.java2D;

import org.geolatte.core.Feature;
import org.geolatte.maprenderer.map.*;

/**
 * @author Karel Maesen
 */
public class MapLayerImpl implements MapLayer {

    private String title = "";
    private boolean visible = true;
    private MapLayerSourceFactory source;
    private Painter painter;

    public MapLayerImpl() {
    }

    public String getTitle() {
        return title;
    }

    public boolean isVisible() {
        return visible;
    }


    public void setPainter(Painter painter) {
        this.painter = painter;

    }

    public void setTitle(String title) {
        this.title = title;

    }

    public void setVisible(boolean visible) {
        this.visible = visible;

    }

    public SpatialExtent getBounds() {
        return null;
    }


    public MapLayerSourceFactory getSourceFactory() {
        return this.source;
    }


    public Painter getPainter() {
        return this.painter;
    }


    public void setSourceFactory(MapLayerSourceFactory sourceFactory) {
        this.source = sourceFactory;
    }

    public Iterable<Feature> getFeatures(SpatialExtent extent) {
        return source.getSource(extent);
    }

    public Labeler getLabeler() {
        throw new UnsupportedOperationException("Not implemented.");
    }


}
