/*
 * This file is part of the GeoLatte project. This code is licenced under
 * the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the
 * License.
 *
 * Copyright (C) 2010 - 2010 and Ownership of code is shared by:
 * Qmino bvba - Romeinsestraat 18 - 3001 Heverlee (http://www.Qmino.com)
 * Geovise bvba - Generaal Eisenhowerlei 9 - 2140 Antwerpen (http://www.geovise.com)
 */

package org.geolatte.maprenderer.java2D;

import org.geolatte.maprenderer.map.MapCompositor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: maesenka
 * Date: Feb 15, 2010
 * Time: 3:22:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class JaiMapCompositor implements MapCompositor {

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
