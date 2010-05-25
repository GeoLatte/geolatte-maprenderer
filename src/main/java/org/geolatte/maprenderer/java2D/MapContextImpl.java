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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geolatte.maprenderer.map.MapContext;
import org.geolatte.maprenderer.map.MapLayer;
import org.geolatte.maprenderer.map.SpatialExtent;
import org.geolatte.maprenderer.reference.SpatialReference;

import java.util.ArrayList;
import java.util.List;

public class MapContextImpl implements MapContext {

    private Log logger = LogFactory.getLog(MapContextImpl.class);
    private String title = "";
    private SpatialReference spatialReference = null;
    private List<MapLayer> mapLayers = new ArrayList<MapLayer>();

    public MapContextImpl(SpatialReference spatialReference) {
        this.spatialReference = spatialReference;
    }

    public boolean addLayer(int index, MapLayer layer) {
        mapLayers.add(index, layer);
        return true;
    }

    public boolean addLayer(MapLayer layer) {
        return mapLayers.add(layer);
    }


    public void clearLayerList() {
        mapLayers.clear();

    }

    public SpatialReference getSpatialReference() {
        return this.spatialReference;
    }

    public MapLayer getLayer(int index) throws IndexOutOfBoundsException {
        return mapLayers.get(index);
    }

    public SpatialExtent getLayerBounds() {
        SpatialExtent bounds = new SpatialExtent(this.spatialReference);
        for (MapLayer layer : getLayers()) {
            SpatialExtent lbnds = layer.getBounds();
            //TODO -- this assumes that all layers have the same spatial reference;
            bounds = SpatialExtent.union(bounds, lbnds);
        }
        return bounds;
    }

    public int getLayerCount() {
        return mapLayers.size();
    }

    public MapLayer[] getLayers() {
        return mapLayers.toArray(new MapLayer[mapLayers.size()]);
    }

    public String getTitle() {
        return this.title;
    }

    public int indexOf(MapLayer layer) {
        return mapLayers.indexOf(layer);
    }

    public void moveLayer(int sourcePosition, int destPosition) {
        throw new UnsupportedOperationException();

    }

    public MapLayer removeLayer(int index) {
        return mapLayers.remove(index);
    }

    public boolean removeLayer(MapLayer layer) {
        return mapLayers.remove(layer);
    }

    public void setSpatialReference(SpatialReference crs) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void removeLayers(MapLayer[] layers) {
        throw new UnsupportedOperationException();

    }

    public void SpatialReference(SpatialReference spatialReference) {
        this.spatialReference = spatialReference;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
