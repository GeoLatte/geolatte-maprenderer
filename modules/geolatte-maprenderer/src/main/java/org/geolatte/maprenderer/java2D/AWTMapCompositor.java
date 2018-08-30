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

package org.geolatte.maprenderer.java2D;

import org.geolatte.maprenderer.map.MapCompositor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.List;

public class AWTMapCompositor implements MapCompositor {

    public RenderedImage overlay(List<RenderedImage> images) {

        if (images == null || images.isEmpty())
            throw new IllegalArgumentException("Empty image list ");

        RenderedImage img = images.get(0);

        ColorModel cm = img.getColorModel();
        WritableRaster raster = cm.createCompatibleWritableRaster(img.getWidth(), img.getHeight());
        BufferedImage result = new BufferedImage(cm, raster, true, null);
        Graphics2D g = (Graphics2D) result.getGraphics();
        g.setComposite(AlphaComposite.SrcOver);
        for (RenderedImage image : images) {
            g.drawRenderedImage(image, null);
        }
        return result;
    }

}
