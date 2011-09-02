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

import net.opengis.se.v_1_1_0.StrokeType;
import org.geolatte.maprenderer.shape.BasicScalableStroke;

import java.awt.*;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 9/2/11
 */
public class StrokeFactory {

    public static Stroke create(StrokeType stroke) {
        verify(stroke);

        float width = 1f;
        int join = 1;
        int cap =1;
        return new BasicScalableStroke(width, join, cap);
    }

    private static void verify(StrokeType stroke) {
        if (stroke.getGraphicFill() != null
                || stroke.getGraphicStroke() != null) {
            throw new UnsupportedOperationException("Can create only solid-color strokes.");
        }
    }
}
