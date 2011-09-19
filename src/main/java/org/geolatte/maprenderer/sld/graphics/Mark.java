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

package org.geolatte.maprenderer.sld.graphics;

import net.opengis.se.v_1_1_0.MarkType;
import net.opengis.se.v_1_1_0.SvgParameterType;
import org.geolatte.maprenderer.sld.SvgParameters;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines a shape which has coloring applied to it.
 *
 * <p>This implementation only supports marks with a well-known name.</p>
 * <p>See SE, § 11.3.2 </p>
 *
 */
public class Mark {

    final private String name;
    final private SvgParameters parameters;
    private boolean hasFill = false;
    private boolean hasStroke = false;

    public Mark(String name, SvgParameters svgParams) {
        this.name = name;
        this.parameters = svgParams;
    }

    public Mark(MarkType type){
        verify(type);
        this.name = type.getWellKnownName();
        List<SvgParameterType> parameters = collectParameters(type);
        this.parameters = SvgParameters.from(parameters);
    }

    public String getWellKnownName() {
        return name;
    }

    public SvgParameters getSvgParameters() {
        return this.parameters;
    }


    public boolean hasStroke() {
        return this.hasStroke;
    }

    public boolean hasFill() {
        return this.hasFill;
    }



    private List<SvgParameterType> collectParameters(MarkType type) {
        List<SvgParameterType> params = new ArrayList<SvgParameterType>();
        if (type.getFill() != null){
            params.addAll(type.getFill().getSvgParameter());
            hasFill = true;
        }
        if (type.getStroke() != null) {
            params.addAll(type.getStroke().getSvgParameter());
            hasStroke = true;
        }
        return params;
    }

    private void verify(MarkType type) {
        if (type.getWellKnownName() == null ||
                type.getWellKnownName().isEmpty()) {
            throw new UnsupportedOperationException("Only Well-known marks are supported");
        }
    }
}
