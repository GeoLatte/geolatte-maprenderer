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

import net.opengis.se.v_1_1_0.SvgParameterType;
import org.geolatte.maprenderer.util.JAXBHelper;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO -- add support for non-literal expressions.

/**
 * Represents the SVG parameters in the SLD Symbolizers.
 *
 * <p>Currently the implementation is limited to SVG parameters whose values are literals. Non-literal expressions are
 * not currently supported.
 * </p>
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 9/2/11
 */
public class SvgParameters  {

    //Defaults.
    public static final Color DEFAULT_STROKE_COLOR = Color.BLACK;

    Map<String, String> parameterMap;
    public static final float DEFAULT_STROKE_WIDTH = 1.0f;
    public static final float DEFAULT_STROKE_OPACITY = 1.0f;


    public static SvgParameters create(List<SvgParameterType> types) {
        SvgParameters result = new SvgParameters(types.size());
        for (SvgParameterType param : types) {
            result.add(param);
        }
        return result;
    }

    public void add(SvgParameterType svgParam) {
        String value = JAXBHelper.extractValueToString(svgParam.getContent());
        parameterMap.put(svgParam.getName().toLowerCase(), value);
    }

    private SvgParameters(int size) {
        super();
        parameterMap = new HashMap<String, String>(size);
    }

    public boolean isEmpty() {
        return parameterMap.isEmpty();
    }

    public Color getStrokeColor() {
        String colorRGB = parameterMap.get("stroke");
        if (colorRGB == null) return DEFAULT_STROKE_COLOR;
        return Color.decode(colorRGB);
    }

    public float getStrokeWidth() {
        String strokeWidth = parameterMap.get("stroke-width");
        if (strokeWidth == null) return DEFAULT_STROKE_WIDTH;
        return Float.parseFloat(strokeWidth);
    }

    public float getStrokeOpacity() {
        String str = parameterMap.get("stroke-opacity");
        if (str == null) return DEFAULT_STROKE_OPACITY;
        return Float.parseFloat(str);
    }
}
