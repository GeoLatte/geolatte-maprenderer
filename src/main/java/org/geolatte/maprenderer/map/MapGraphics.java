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

package org.geolatte.maprenderer.map;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.crs.CrsId;

import java.awt.*;
import java.awt.image.RenderedImage;

/**
 * A specialized <code>Graphics2D</code> for rendering spatial data.
 */
public abstract class MapGraphics extends Graphics2D {

//    TODO : add method to set a DPI "hint" == intended resolution

    abstract public Dimension getDimension();

    abstract public RenderedImage renderImage();

    abstract public CrsId getSpatialReference();

    abstract public double getMapUnitsPerPixel();

    abstract public Envelope<C2D> getExtent();

}
