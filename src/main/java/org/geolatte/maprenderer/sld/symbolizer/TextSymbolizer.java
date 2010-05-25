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

package org.geolatte.maprenderer.sld.symbolizer;

import org.geolatte.core.reflection.Feature;
import org.geolatte.maprenderer.map.MapGraphics;

import java.awt.*;

public class TextSymbolizer extends AbstractSymbolizer {

    private String labelProperty = null;
    private String fontFamily = "SERIF";
    private int fontStyle = 0;
    private int fontSize = 10;

    public void setLabelProperty(String property) {
        this.labelProperty = property;

    }

    public void setFontFamily(String font) {
        this.fontFamily = font;
    }

    public void setFontStyle(int style) {
        this.fontStyle += style;
    }

    public void setFontSize(int size) {
        this.fontSize = size;
    }

    public String getLabelProperty() {
        return labelProperty;
    }

    public Font getFont() {
        return new Font(fontFamily, fontStyle, fontSize);
    }

    public void symbolize(Feature feature) {
        throw new UnsupportedOperationException();
    }

    public void setGraphics(MapGraphics graphics) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
