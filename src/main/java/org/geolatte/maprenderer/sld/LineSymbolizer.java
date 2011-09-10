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

import net.opengis.se.v_1_1_0.LineSymbolizerType;
import net.opengis.se.v_1_1_0.ParameterValueType;
import net.opengis.se.v_1_1_0.StrokeType;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.shape.ScalableStroke;
import org.geolatte.maprenderer.util.JAXBHelper;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.io.Serializable;

public class LineSymbolizer extends AbstractSymbolizer {


    final private Value<Float> perpendicularOffset;
    final private SvgParameters svgParameters;
    final private String geometryProperty;
    final private StrokeFactory strokeFactory = new StrokeFactory();

    //TODO -- strokeFactory should be injected in constructor.

    public LineSymbolizer(LineSymbolizerType type) {
        super(type);
        perpendicularOffset = readPerpendicularOffset(type);
        geometryProperty = readGeometryProperty(type);
        StrokeType strokeType = type.getStroke();
        verify(strokeType);
        svgParameters = SvgParameters.create(strokeType.getSvgParameter());
    }

    public String getGeometryProperty() {
        return geometryProperty;
    }

    public Value<Float> getPerpendicularOffset() {
        return this.perpendicularOffset;
    }

    @Override
    public void symbolize(MapGraphics graphics, Shape[] shapes) {
        //ScalableStrokes are not thread-safe,so should always be recreated when symbolizing shapes
        ScalableStroke stroke = createStroke();
        graphics.setStroke(stroke);
        Color c = svgParameters.getStrokeColor();
        Color sRGB = new Color(ColorSpace.getInstance(ColorSpace.CS_sRGB), c.getRGBColorComponents(null), svgParameters.getStrokeOpacity());
        graphics.setColor(sRGB);
        for(Shape s : shapes){
            graphics.draw(s);
        }

    }


    private Value<Float> readPerpendicularOffset(LineSymbolizerType type) {
        ParameterValueType pv = type.getPerpendicularOffset();
        Value<Float> defaultOffset = Value.of(0f, UOM.PIXEL);
        if (pv == null){
            return defaultOffset;
        }
        java.util.List<Serializable> content = pv.getContent();
        if (content == null || content.isEmpty()) {
            return defaultOffset;
        }
        String valueStr = JAXBHelper.extractValueToString(content);
        return Value.of(valueStr.toString(), this.getUOM());
    }


    //XPath expressions or more complex expressions are not supported.
    private String readGeometryProperty(LineSymbolizerType type) {
        if (type.getGeometry() == null) return null;
        if (type.getGeometry().getPropertyName() == null) return null;
        java.util.List<Object> list = type.getGeometry().getPropertyName().getContent();
        return JAXBHelper.extractValueToString(list);
    }

    private ScalableStroke createStroke() {
           return strokeFactory.create(svgParameters, perpendicularOffset);
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
