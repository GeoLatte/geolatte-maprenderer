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
