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

import java.awt.*;
import java.awt.image.RenderedImage;


/**
 * The {@link MapRenderer} is a canvas upon which
 * spatial data is rendered. The content of the map,
 * and its styling is determined by the {@link MapContext}.
 * The scale, size and graphic properties are determined by the
 * {@link MapGraphics}.
 *
 * @author Karel Maesen
 */
public interface MapRenderer {

    public void setMapContext(MapContext context);

    public MapContext getMapContext();

    public RenderedImage render(Dimension dim, SpatialExtent extent);


}