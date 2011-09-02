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

package org.geolatte.maprenderer.sld.symbolizer;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: maesenka
 * Date: Jan 25, 2010
 * Time: 7:25:24 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractMarkGraphic implements Graphic {


    private float size;


    public abstract Shape generateMarkShape(double x, double y, double size);


}
