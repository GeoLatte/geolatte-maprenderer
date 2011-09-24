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

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.w3c.dom.svg.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Converts an SVG document to an ARGB PNG image of the specified size.
 *
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 9/16/11
 */
public class SVGTranscoder {


    /**
     * Transcodes the specified <code>SVGDocument.</code>
     *
     * @param svg the input SVG Document
     * @param width the width of the output image
     * @param height the height of the output image
     * @return
     */
    public BufferedImage transcode(SVGDocument svg, int width, int height) {
        PNGTranscoder transcoder = prepareTranscoder(width, height);
        TranscoderInput input = new TranscoderInput(svg);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        TranscoderOutput output = new TranscoderOutput(stream);
        try {
            transcoder.transcode(input, output);
            return ImageIO.read(new ByteArrayInputStream(stream.toByteArray()));
        } catch (TranscoderException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private PNGTranscoder prepareTranscoder(int width, int height) {
        PNGTranscoder transcoder = new PNGTranscoder();
        transcoder.addTranscodingHint(ImageTranscoder.KEY_WIDTH, Float.valueOf(width));
        transcoder.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, Float.valueOf(height));
        return transcoder;
    }

}
