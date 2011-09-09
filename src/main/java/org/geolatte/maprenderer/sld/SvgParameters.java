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
    public static final int DEFAULT_STROKE_LINEJOIN = BasicStroke.JOIN_MITER;
    public static final int DEFAULT_STROKE_LINECAP = BasicStroke.CAP_SQUARE;
    public static final float DEFAULT_STROKE_DASHOFFSET = 0.0f;


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

    /**
     * Returns the line join style.
     *
     * @return the line join style as one of the static int members of <code>BasicStroke</code>
     */
    public int getStrokeLinejoin() {
        String str = parameterMap.get("stroke-linejoin");
        if (str == null) return DEFAULT_STROKE_LINEJOIN;
        if (str.equalsIgnoreCase("mitre")) return BasicStroke.JOIN_MITER;
        if (str.equalsIgnoreCase("round")) return BasicStroke.JOIN_ROUND;
        if (str.equalsIgnoreCase("bevel")) return BasicStroke.JOIN_BEVEL;

        throw new IllegalArgumentException(String.format("Can't map %s to a line join.", str));
    }

    /**
     * Returns the line cap style.
     *
     * @return the line cap style as one of the static int members of <code>BasicStroke</code>
     */
    public int getStrokeLinecap() {
        String str = parameterMap.get("stroke-linecap");
        if (str == null) return DEFAULT_STROKE_LINECAP;
        if (str.equalsIgnoreCase("butt")) return BasicStroke.CAP_BUTT;
        if (str.equalsIgnoreCase("round")) return BasicStroke.CAP_ROUND;
        if (str.equalsIgnoreCase("square")) return BasicStroke.CAP_SQUARE;

        throw new IllegalArgumentException(String.format("Can't map %s to a line cap.", str));
    }

    public float[] getStrokeDasharray() {
        String str = parameterMap.get("stroke-dasharray");
        if (str == null) return new float[0];
        //If an odd number of values is given, then the
        //pattern is repeated twice to create the dasharray.
        String[] dashStr = str.trim().split("\\s");
        int dashArLength = (dashStr.length % 2 == 0) ? dashStr.length : 2*dashStr.length;
        float[] dash = new float[dashArLength];
        for (int i = 0; i < dashArLength; i++) {
            dash[i] = Float.parseFloat(dashStr[i % dashStr.length]);
        }

        return dash;

    }

    public float getStrokeDashoffset() {
        String str = parameterMap.get("stroke-dashoffset");
        if (str == null) return DEFAULT_STROKE_DASHOFFSET;

        return Float.parseFloat(str);
    }
}
