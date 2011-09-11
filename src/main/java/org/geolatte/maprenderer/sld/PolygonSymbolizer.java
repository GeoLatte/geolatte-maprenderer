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
import net.opengis.se.v_1_1_0.*;
import org.geolatte.maprenderer.map.MapGraphics;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 9/11/11
 */
public class PolygonSymbolizer extends AbstractSymbolizer {


    final private String geometryProperty;
    final private SvgParameters svgParameters;
    final private Value<Float> perpendicularOffset;
    private float displacementX = 0f;
    private float displacementY = 0f;
    private boolean hasStroke = false;
    private boolean hasFill = false;

    public PolygonSymbolizer(PolygonSymbolizerType type) {
        super(type);
        GeometryType geometryType = type.getGeometry();
        geometryProperty = readGeometry(geometryType);
        List<SvgParameterType> params = collectSvgParameters(type);
        svgParameters = SvgParameters.create(params);
        ParameterValueType offset = type.getPerpendicularOffset();
        perpendicularOffset = readPerpendicularOffset(offset);
        readDisplacement(type.getDisplacement());

    }

    private List<SvgParameterType> collectSvgParameters(PolygonSymbolizerType type) {
        List<SvgParameterType> params = new ArrayList<SvgParameterType>();
        if (type.getStroke() != null){
            params.addAll(type.getStroke().getSvgParameter());
            hasStroke = true;
        }
        if (type.getFill() != null){
            params.addAll(type.getFill().getSvgParameter());
            hasFill = true;
        }
        return params;
    }

    private void readDisplacement(DisplacementType displacementType) {
        if (displacementType == null) return;
        String dXStr = extractParameterValue(displacementType.getDisplacementX());
        String dYStr = extractParameterValue(displacementType.getDisplacementY());
        if (dXStr == null || dYStr == null) return;
        displacementX = Float.parseFloat(dXStr);
        displacementY = Float.parseFloat(dYStr);
    }

    @Override
    public void symbolize(MapGraphics graphics, Geometry geometry) {
        Shape[] shapes = toShapes(graphics, geometry);
        fill(graphics, shapes);
        stroke(graphics, shapes);
    }

    private void stroke(MapGraphics graphics, Shape[] shapes) {
        if (!hasStroke) return;
        Stroke stroke = getStrokeFactory().create(svgParameters, perpendicularOffset);
        Paint strokeColor = getPaintFactory().create(svgParameters.getStrokeColor(), svgParameters.getStrokeOpacity());
        graphics.setPaint(strokeColor);
        graphics.setStroke(stroke);
        for (Shape shape : shapes) {
            graphics.draw(shape);
        }
    }

    private void fill(MapGraphics graphics, Shape[] shapes) {
        if (!hasFill) return;
        Paint fill = getPaintFactory().create(svgParameters.getFillColor(), svgParameters.getFillOpacity());
        graphics.setPaint(fill);
        for (Shape shape : shapes) {
            graphics.fill(shape);
        }
    }

    public String getGeometryProperty() {
        return geometryProperty;
    }

    public Value<Float> getPerpendicularOffset() {
        return perpendicularOffset;
    }

    public float getDisplacementX() {
        return displacementX;
    }

    public float getDisplacementY() {
        return displacementY;
    }
}
