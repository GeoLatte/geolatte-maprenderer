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

import org.geolatte.maprenderer.shape.PerpendicularOffsetStroke;

import java.awt.*;

/**
 * A factory that creates <code>ScalableStroke</code>s from an SLD stroke specification (the <Stroke>-element).
 * <p>
 * This implementation is currently limited to SLD stroke specifications with:
 * </p>
 * <ul>
 * <li>SVG parameters with only literal values</li>
 * <li>with no GraphicFill or GraphicStroke elements</li>
 * </ul>
 *
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 9/2/11
 */
public class StrokeFactory {

    public Stroke create(SvgParameters svgParameters, Value<Float> perpendicularOffset) {
        if (perpendicularOffset == null || Math.abs(perpendicularOffset.value()) < Math.ulp(1f)) {
            return create(svgParameters);
        }
        float width = svgParameters.getStrokeWidth();
        int join = svgParameters.getStrokeLinejoin();
        int cap = svgParameters.getStrokeLinecap();
        float[] dashArray = svgParameters.getStrokeDasharray();
        float dashOffset = svgParameters.getStrokeDashoffset();
        return new PerpendicularOffsetStroke(width, perpendicularOffset, join, cap, dashArray, dashOffset);
    }

    public Stroke create(SvgParameters svgParameters) {
        return new BasicStroke(svgParameters.getStrokeWidth(),
                svgParameters.getStrokeLinecap(),
                svgParameters.getStrokeLinejoin(),
                10f,
                svgParameters.getStrokeDasharray(),
                svgParameters.getStrokeDashoffset());
    }


}
