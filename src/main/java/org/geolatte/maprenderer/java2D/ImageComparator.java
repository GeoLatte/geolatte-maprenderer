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

import java.awt.*;
import java.awt.image.*;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 9/10/11
 */
public class ImageComparator {


    public boolean equals(RenderedImage img1, RenderedImage img2) {
        if (img1 == img2) return true;
        if (img1 == null || img2 == null) return false;
        Raster r1 = img1.getData();
        Raster r2 = img2.getData();

        if (!sameBounds(r1, r2)) return false;
        if (!sameBands(r1, r2)) return false;

        if (!sameRasterData(r1, r2)) return false;

        return true;
    }

//Turns out testing on equality of samplemodel and colormodel is too strict.
//This is often modified by ImageIO.

//    private boolean sameSampleModel(Raster r1, Raster r2) {
//        SampleModel model1 = r1.getSampleModel();
//        SampleModel model2 = r2.getSampleModel();
//        if (model1.getDataType() != model2.getDataType()) return false;
//        return true;
//    }
//
//    private boolean sameColorModel(RenderedImage img1, RenderedImage img2) {
//        return img1.getColorModel().equals(img2.getColorModel());
//    }

    public BufferedImage difference(RenderedImage img1, RenderedImage img2) {

        ColorModel colorModel = img1.getColorModel();
        int width = img1.getWidth();
        int height = img1.getHeight();


        WritableRaster raster = colorModel.createCompatibleWritableRaster(width, height);
        BufferedImage outImg = new BufferedImage(colorModel, raster, false, null);

        // Modified - Changed to int as pixels are ints

        Raster raster1 = img1.getData();
        Raster raster2 = img2.getData();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                for(int band =0; band < raster.getNumBands(); band++){

                    int s1 = raster1.getSample(x, y, band);
                    int s2 = raster2.getSample(x, y, band);
                    int diff = Math.max(s1 , s2);
                    if (diff > 0) System.out.println("Difference at " + x + ", " + y + ": " + diff);
                    raster.setSample(x,y,band, diff);
                }

            }
        }
        return outImg;
    }


    private boolean sameBands(Raster r1, Raster r2) {
        return r1.getNumBands() == r2.getNumBands();
    }

    private boolean sameBounds(Raster r1, Raster r2) {
        Rectangle r1Bounds = r1.getBounds();
        Rectangle r2Bounds = r2.getBounds();
        return (r1Bounds.equals(r1Bounds));
    }

    private boolean sameRasterData(Raster r1, Raster r2) {
        for (int i = 0; i < r1.getNumBands(); i++) {
            if (!sameRasterBand(r1, r2, i)) return false;
        }
        return true;
    }

    private boolean sameRasterBand(Raster r1, Raster r2, int band) {
        for (int xIdx = 0; xIdx < r1.getWidth(); xIdx++) {
            for (int yIdx = 0; yIdx < r1.getHeight(); yIdx++) {
                if (r1.getSample(xIdx + r1.getMinX(), yIdx + r1.getMinY(), band) !=
                    r2.getSample(xIdx + r2.getMinX(), yIdx + r2.getMinY(), band)) {
                    return false;
                }
            }
        }
        return true;
    }


}
