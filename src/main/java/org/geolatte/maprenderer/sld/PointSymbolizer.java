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

package org.geolatte.maprenderer.sld;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import net.opengis.se.v_1_1_0.GraphicType;
import net.opengis.se.v_1_1_0.PointSymbolizerType;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.sld.graphics.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;

/**
 * A Symbolizer for point features.
 *
 * <p>See SE §11.3 </p>
 * <p>Note: the SE spec states on page 23 that Graphic elements can occur multiple times, but the XSD schema
 * specifies that it can occur at most one time. Here the XSD schema is followed.</p>
 */
public class PointSymbolizer extends AbstractSymbolizer {


    final private String geometryProperty;
    final private Graphic graphic;

    //TODO -- do this by injection
    //TODO -- configure or search for graphics packages.
    final private ExternalGraphicsRepository graphicsRepository = new ExternalGraphicsRepository(
            new String[]{"graphics"}
    );

    public PointSymbolizer(PointSymbolizerType type) {
        super(type);
        this.geometryProperty = readGeometry(type.getGeometry());
        this.graphic = readGraphic(type.getGraphic());
    }

    @Override
    public void symbolize(MapGraphics graphics, Geometry geometry) {
        Point point = getPoint(geometry);
        //TODO -- refinement: prefer the rendering of SVG images, then images, then marks
        for (MarkOrExternalGraphicHolder holder: graphic.getSources()){
            if (symbolize(graphics, point, holder)) return;
        }
    }

    private boolean symbolize(MapGraphics graphics, Point point, MarkOrExternalGraphicHolder holder) {
        boolean success;
        if (holder.isExternalGraphic()) {
            success = symbolize(graphics, point, holder.getExternalGrapic());
        } else {
            success = symbolize(graphics, point, holder.getMark());
        }
        return success;
    }

    private boolean symbolize(MapGraphics graphics, Point point, Mark mark) {
        //TODO implement!
        throw new UnsupportedOperationException("Mark rendering to be implemented.");
    }

    private boolean symbolize(MapGraphics graphics, Point point, ExternalGraphic externalGraphic) {
        BufferedImage image = null;
        try {
            image = getImageFromExternalGraphic(externalGraphic, graphic.getSize(), graphic.getRotation(), graphic.isSizeSet());
        } catch (GraphicDrawException e) {
            //TODO -- add logger for errors, and remember that this externalGraphic doesn't work !!
            return false;
        }
        return drawImage(graphics, point, image);
    }

    private boolean drawImage(MapGraphics graphics, Point point, BufferedImage image) {
        //remember transform
        AffineTransform currentTransform = graphics.getTransform();
        try {
            graphics.setTransform(new AffineTransform());
            AffineTransform pointTransform = getPointTransform(currentTransform, image, graphic);
            Point2D dstPnt = applyTransform(point, pointTransform);
            applyOpacity(graphics);
            graphics.drawImage(image, (int)dstPnt.getX(), (int)dstPnt.getY(), (ImageObserver)null);
        } finally {
            // restore transform
            graphics.setTransform(currentTransform);
        }
        return true;
    }

    private void applyOpacity(MapGraphics graphics) {
        if (graphic.getOpacity() < 1.0f) {
            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, graphic.getOpacity()));
        }
    }

    private BufferedImage getImageFromExternalGraphic(ExternalGraphic externalGraphic, float size, float rotation, boolean sizeSet) throws GraphicDrawException {
        try {
            return graphicsRepository.get(externalGraphic.getUrl(), size, rotation, sizeSet);
        } catch (IOException e) {
            throw new GraphicDrawException(e);
        }
    }

    /**
     * Determines the transform of the anchorpoint from its geographic location to a pixel in the graphics' device
     * space.
     *
     * <p>Not that SE specifies the anchorpoint in a coordinate system with origin in the lower-left
     * corner of the image, while java.awt.Graphics uses the top-left as origin</p>
     */
    private AffineTransform getPointTransform(AffineTransform currentTransform, BufferedImage img, Graphic graphic) {
        AffineTransform transform = new AffineTransform();
        applyAnchorPointTranslation(img, graphic, transform);
        applyDisplacement(graphic, transform);
        transform.concatenate(currentTransform);
        return transform;
    }

    private void applyAnchorPointTranslation(BufferedImage img, Graphic graphic, AffineTransform transform) {
        Point2D anchorPoint = graphic.getAnchorPoint();
        transform.setToTranslation(
                -anchorPoint.getX() * img.getWidth(),
                -(1 - anchorPoint.getY()) * img.getHeight());
    }

    private void applyDisplacement(Graphic graphic, AffineTransform transform) {
        Point2D displacement = graphic.getDisplacement();
        transform.setToTranslation(transform.getTranslateX() + displacement.getX(),
                transform.getTranslateY() - displacement.getY());
    }

    private Point2D applyTransform(Point point, AffineTransform transform)  {
        return transform.transform(new Point2D.Double(point.getX(), point.getY()), null);
    }

    private Point getPoint(Geometry geometry) {
        if (geometry instanceof Point) {
            return ((Point) geometry);
        }
        return geometry.getCentroid();
    }

    public String getGeometryProperty() {
        return geometryProperty;
    }

    private Graphic readGraphic(GraphicType graphicType) {
        return new Graphic(graphicType);
    }


    public Graphic getGraphic() {
        return this.graphic;
    }
}
