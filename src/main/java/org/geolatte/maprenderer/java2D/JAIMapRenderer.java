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

import org.geolatte.core.reflection.Feature;
import org.geolatte.maprenderer.geotools.GTSpatialReferenceFactory;
import org.geolatte.maprenderer.map.*;
import org.geolatte.maprenderer.reference.Projector;
import org.geolatte.maprenderer.reference.SpatialReference;
import org.geolatte.maprenderer.reference.SpatialReferenceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.RenderedImage;

public class JAIMapRenderer implements MapRenderer {

    private static final long serialVersionUID = 1L;
    private final static Logger LOGGER = LoggerFactory.getLogger(JAIMapRenderer.class);
    private MapContext mapContext;
    private final SpatialReferenceFactory spatialReferenceFactory = new GTSpatialReferenceFactory();

    public JAIMapRenderer(MapContext mapContext) {
        this.mapContext = mapContext;
    }

    public MapContext getMapContext() {
        return this.mapContext;
    }

    public RenderedImage render(Dimension imgDimension, SpatialExtent extent) {
        long t0 = System.currentTimeMillis();
        SpatialExtent mapExtent = transformToTargetCRS(extent);
        MapGraphics graphics = new JAIMapGraphics(imgDimension, this.mapContext.getSpatialReference());
        graphics.setToExtent(mapExtent);
        long t1 = System.currentTimeMillis();
        LOGGER.debug(String.format("Preparing graphics in %d ms.", t1 - t0));
        SpatialReference spatialReference = this.mapContext.getSpatialReference();
        for (MapLayer layer : this.mapContext.getLayers()) {
            Painter painter = layer.getPainter();
            if (painter == null) continue;
            Iterable<Feature> reader = layer.getFeatures(extent);
            painter.paint(graphics, reader);
        }
        long t2 = System.currentTimeMillis();
        RenderedImage image = graphics.createRendering();
        long t3 = System.currentTimeMillis();
        LOGGER.debug(String.format("Creating rendering in %d ms.", t3 - t2));
        long t4 = System.currentTimeMillis();
        LOGGER.debug(String.format("Rendering extent %s in %d ms.", mapExtent.toString(), t4 - t0));
        graphics.dispose();
        return image;
    }


    private SpatialExtent transformToTargetCRS(SpatialExtent extent) {
        SpatialReference target = this.mapContext.getSpatialReference();
        SpatialReference source = extent.getSpatialReference();
        if (source.equals(target)) {
            return extent;
        }
        Projector projector = spatialReferenceFactory.createProjector(source, target);
        return projector.project(extent);
    }

    public void setMapContext(MapContext context) {
        this.mapContext = context;
    }

}
