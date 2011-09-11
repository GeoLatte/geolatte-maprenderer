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
import net.opengis.se.v_1_1_0.GeometryType;
import net.opengis.se.v_1_1_0.LineSymbolizerType;
import net.opengis.se.v_1_1_0.ParameterValueType;
import net.opengis.se.v_1_1_0.StrokeType;
import org.geolatte.maprenderer.map.MapGraphics;

import java.awt.*;

public class LineSymbolizer extends AbstractSymbolizer {


    final private String geometryProperty;
    final private Value<Float> perpendicularOffset;
    final private SvgParameters svgParameters;

    public LineSymbolizer(LineSymbolizerType type) {
        super(type);
        perpendicularOffset = readPerpendicularOffset(type);
        StrokeType strokeType = type.getStroke();
        verify(strokeType);
        svgParameters = SvgParameters.create(strokeType.getSvgParameter());
        GeometryType geometryType = type.getGeometry();
        geometryProperty = readGeometry(geometryType);
    }

    public String getGeometryProperty() {
        return geometryProperty;
    }

    public Value<Float> getPerpendicularOffset() {
        return this.perpendicularOffset;
    }

    @Override
    public void symbolize(MapGraphics graphics, Geometry geometry) {
        Shape[] shapes = toShapes(graphics, geometry);
        Stroke stroke = createStroke();
        graphics.setStroke(stroke);
        Paint paint = createPaint();
        graphics.setPaint(paint);
        for(Shape s : shapes){
            graphics.draw(s);
        }

    }


    private Value<Float> readPerpendicularOffset(LineSymbolizerType type) {
        ParameterValueType pv = type.getPerpendicularOffset();
        return readPerpendicularOffset(pv);
    }

    private Paint createPaint() {
        Color c = svgParameters.getStrokeColor();
        float opacity = svgParameters.getStrokeOpacity();
        return getPaintFactory().create(c, opacity);
    }

    private Stroke createStroke() {
           return getStrokeFactory().create(svgParameters, perpendicularOffset);
    }

    /**
     * Verifies that the stroketype is supported.
     * @param stroke
     */
    private void verify(StrokeType stroke) {
        if (stroke == null)
            throw new IllegalArgumentException("No stroke type specified.");
        if (stroke.getGraphicFill() != null
                || stroke.getGraphicStroke() != null) {
            throw new UnsupportedOperationException("Can create only solid-color strokes.");
        }
    }
}
