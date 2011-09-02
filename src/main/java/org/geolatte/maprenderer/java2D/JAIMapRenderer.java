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

import org.geolatte.core.Feature;
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
            painter.paint(reader);
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
