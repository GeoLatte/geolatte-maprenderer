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
