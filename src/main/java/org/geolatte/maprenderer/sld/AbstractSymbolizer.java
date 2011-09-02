/*
 * Copyright (c) 2011. Geovise BVBA, QMINO BVBA
 *
 * This file is part of GeoLatte Mapserver.
 *
 * GeoLatte Mapserver is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GeoLatte Mapserver is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GeoLatte Mapserver.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.geolatte.maprenderer.sld;

import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.map.Painter;

import java.awt.*;

/**
 * @author Karel Maesen, Geovise BVBA, 2010.
 *
 */
public abstract class AbstractSymbolizer {

    private UOM uom = UOM.PIXEL;
    private String geometryProperty;

    public void paint(MapGraphics graphics, Shape[] shapes) {
        throw new UnsupportedOperationException();
    }

    void setGeometryProperty(String geometryProperty) {
        this.geometryProperty = geometryProperty;
    }


    public String getGeometryProperty() {
        return geometryProperty;
    }

    public UOM getUOM() {
        return uom;
    }

    public void setUnitsOfMeasure(UOM uom) {
        this.uom = uom;
    }
}
