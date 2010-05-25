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

package org.geolatte.render;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: adnsgis
 * Date: Dec 18, 2009
 * Time: 5:53:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestSimpleImage {


    public static void main(String[] args) {

        ColorModel colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), true, true, Transparency.BITMASK, DataBuffer.TYPE_BYTE);
//        ColorModel colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        WritableRaster raster = colorModel.createCompatibleWritableRaster(256, 256);
        BufferedImage img = new BufferedImage(colorModel, raster, false, null);
//        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = (Graphics2D) img.getGraphics();
        g2.setComposite(AlphaComposite.SrcOver);
        Color backgroundColor = new Color(1.0f, 1.0f, 1.0f, 0.0f);//Color.WHITE;
        g2.setBackground(backgroundColor);
        g2.clearRect(0, 0, 256, 256);     //THIS IS REQUIRED  when setting background color !!
        g2.setColor(Color.RED);
        g2.fillRect(50, 50, 150, 150);
        g2.setColor(Color.BLACK);
        g2.fillRect(100, 100, 10, 10);

        RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        hints.put(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_DITHERING,
                RenderingHints.VALUE_DITHER_ENABLE);
        hints.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


//        ColorModel colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), true, true, Transparency.BITMASK, DataBuffer.TYPE_USHORT);
//
//
//        ImageLayout layout = new ImageLayout(0, 0, 256, 256, null, colorModel);
//        hints.put(JAI.KEY_IMAGE_LAYOUT, layout);

        AffineTransform identity = new AffineTransform();
        identity.setToIdentity();
//        RenderContext ctxt = new RenderContext(identity, hints);
//        RenderedImage img = g2.createRendering(ctxt);
        try {
            ImageIO.write(img, "PNG", new File("/tmp/img/test" + ".png"));
            ImageIO.write(img, "GIF", new File("/tmp/img/test" + ".gif"));
            ImageIO.write(img, "JPEG", new File("/tmp/img/test" + ".jpg"));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }


}
