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


import net.opengis.se.v_1_1_0.*;
import org.geolatte.geom.C2D;
import org.geolatte.geom.Geometry;
import org.geolatte.maprenderer.map.MapGraphics;

import java.awt.*;
import java.awt.geom.Point2D;
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
    private Point2D displacement;
    private boolean hasStroke = false;
    private boolean hasFill = false;

    public PolygonSymbolizer(PolygonSymbolizerType type) {
        super(type);
        GeometryType geometryType = type.getGeometry();
        geometryProperty = readGeometry(geometryType);
        List<SvgParameterType> params = collectSvgParameters(type);
        svgParameters = SvgParameters.from(params);
        ParameterValueType offset = type.getPerpendicularOffset();
        perpendicularOffset = readPerpendicularOffset(offset);
        this.displacement = readDisplacement(type.getDisplacement());

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

    private Point2D readDisplacement(DisplacementType displacementType) {
        return SLD.instance().readDisplacement(displacementType);
    }

    @Override
    public void symbolize(MapGraphics graphics, Geometry<C2D> geometry) {
        //TODO -- take into account offset, displacement
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

    public Point2D getDisplacement() {
        return displacement;
    }
}
