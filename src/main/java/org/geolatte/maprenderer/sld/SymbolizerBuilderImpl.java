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

package org.geolatte.maprenderer.sld;

import org.geolatte.maprenderer.shape.BasicScalableStroke;
import org.geolatte.maprenderer.shape.ScalableStroke;
import org.geolatte.maprenderer.sld.symbolizer.*;

import java.awt.*;

public class SymbolizerBuilderImpl implements SymbolizerBuilder {


    private Symbolizer symbolizer;
    private double strokeWidth;
    private String strokeLineJoin = "";
    private String strokeLineCap = "";
    private float perpendicularOffset = 0.0f;
    private String fillColor = "";
    private double[] strokeDashArray;
    private double strokeDashOffset;

    private void checkSymbolizer() {
        if (this.symbolizer == null) {
            throw new RuntimeException("Attempt to invoke setter before symbolizer instance is created.");
        }
    }

    public void setDescription(String title, String abstr) {
        checkSymbolizer();
        symbolizer.setTitle(title);
        symbolizer.setAbstractText(abstr);
    }

    public void setGeometryPropertyName(String propName) {
        checkSymbolizer();
        this.symbolizer.setGeometryPropertyName(propName);

    }

    public void setName(String name) {
        checkSymbolizer();
        this.symbolizer.setName(name);

    }

    public void setPerpendicularOffset(float d) {
        this.perpendicularOffset = d;
    }

    private void setFillColor(String colorStr) {
        Color color = Color.decode(colorStr);
        color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 255);
        if (this.symbolizer instanceof PolygonSymbolizer) {
            ((PolygonSymbolizer) this.symbolizer).setFillColor(color);
            return;
        }
        throw new UnsupportedOperationException();
    }

    private void setStrokeColor(String colorStr) {
        Color color = Color.decode(colorStr);
        color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 255);
        if (this.symbolizer instanceof LineSymbolizer) {
            ((LineSymbolizer) this.symbolizer).setStrokeColor(color);
            return;
        } else if (this.symbolizer instanceof PolygonSymbolizer) {
            ((PolygonSymbolizer) this.symbolizer).setStrokeColor(color);
            return;
        }
        throw new UnsupportedOperationException();
    }

    private void setStrokeOpacity(String opacityStr) {
        float opacity = Float.parseFloat(opacityStr);
        if (this.symbolizer instanceof LineSymbolizer) {
            ((LineSymbolizer) this.symbolizer).setStrokeOpacity(opacity);
            return;
        } else if (this.symbolizer instanceof PolygonSymbolizer) {
            ((PolygonSymbolizer) this.symbolizer).setStrokeOpacity(opacity);
        }
        throw new UnsupportedOperationException();
    }

    public void setStrokeParameter(String name, String value) {
        checkSymbolizer();
        try {
            if (name.equalsIgnoreCase("stroke")) {
                setStrokeColor(value);
            } else if (name.equalsIgnoreCase("stroke-opacity")) {
                setStrokeOpacity(value);
            } else if (name.equalsIgnoreCase("stroke-width")) {
                this.strokeWidth = Double.parseDouble(value);
            } else if (name.equalsIgnoreCase("stroke-linejoin")) {
                this.strokeLineJoin = value;
            } else if (name.equalsIgnoreCase("stroke-linecap")) {
                this.strokeLineCap = value;
            } else if (name.equalsIgnoreCase("stroke-dasharray")) {
                throw new UnsupportedOperationException();
            } else if (name.equalsIgnoreCase("stroke-dashoffset")) {
                this.strokeDashOffset = Double.parseDouble(value);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to set stroke parameter:" + name + " with value: " + value, e);
        }
    }

    public void setType(SymbolizerType type) {
        switch (type) {
            case LINE:
                this.symbolizer = new LineSymbolizer();
                break;
            case POLYGON:
                this.symbolizer = new PolygonSymbolizer();
                break;
            case POINT:
                this.symbolizer = new PointSymbolizer();
                break;
            case TEXT:
                this.symbolizer = new TextSymbolizer();
                break;
            default:
                throw new UnsupportedOperationException();
        }

    }

    public void setUom(String uom) {
        checkSymbolizer();
        if (uom.equalsIgnoreCase("pixel")) {
            this.symbolizer.setUom(UnitsOfMeasure.PIXEL);
        } else if (uom.equalsIgnoreCase("meter")) {
            this.symbolizer.setUom(UnitsOfMeasure.METER);
        } else if (uom.equalsIgnoreCase("foot")) {
            this.symbolizer.setUom(UnitsOfMeasure.FOOT);
        }
    }

    public void setVersion(String version) {
        checkSymbolizer();
        this.symbolizer.setVersion(version);

    }

    public Symbolizer getResult() {
        checkSymbolizer();
        if (this.symbolizer instanceof LineSymbolizer) {
            LineSymbolizer lSymb = (LineSymbolizer) this.symbolizer;
            lSymb.setStroke(createAWTStroke(1));
        } else if (this.symbolizer instanceof PolygonSymbolizer) {
            PolygonSymbolizer pSymb = (PolygonSymbolizer) this.symbolizer;
            pSymb.setStroke(createAWTStroke(1));
        }
        return this.symbolizer;
    }


    public ScalableStroke createAWTStroke(int type) {
        int cap = BasicStroke.CAP_BUTT;
        if (this.strokeLineCap.equalsIgnoreCase("round")) {
            cap = BasicStroke.CAP_ROUND;
        } else if (this.strokeLineJoin.equalsIgnoreCase("square")) {
            cap = BasicStroke.CAP_SQUARE;
        }
        int join = BasicStroke.JOIN_BEVEL;
        if (this.strokeLineJoin.equalsIgnoreCase("mitre")) {
            join = BasicStroke.JOIN_MITER;
        } else if (this.strokeLineJoin.equalsIgnoreCase("round")) {
            join = BasicStroke.JOIN_ROUND;
        }
        BasicScalableStroke stroke = new BasicScalableStroke((float) strokeWidth, cap, join);
        stroke.setPerpendicularOffset(this.perpendicularOffset);
        return stroke;
    }

    public void setFillParameter(String name, String value) {
        checkSymbolizer();
        if (name.equalsIgnoreCase("fill")) {
            setFillColor(value);
        }

    }

    public void setFontParameter(String name, String value) {
        checkSymbolizer();
        TextSymbolizer textSymbolizer;
        if (this.symbolizer instanceof TextSymbolizer) {
            textSymbolizer = (TextSymbolizer) this.symbolizer;
        } else {
            throw new UnsupportedOperationException("Method only value for TextSymbolizers");
        }

        if (name.equalsIgnoreCase("font-family")) {
            textSymbolizer.setFontFamily(value);
        } else if (name.equalsIgnoreCase("font-style")) {
            textSymbolizer.setFontStyle(parseFontStyle(value));
        } else if (name.equalsIgnoreCase("font-weight")) {
            textSymbolizer.setFontStyle(parseFontWeight(value));
        } else if (name.equalsIgnoreCase("font-size")) {
            textSymbolizer.setFontSize(parseFontSize(value));
        }

    }

    private int parseFontSize(String value) {
        //String intVal = value.replaceFirst("", "");
        return Integer.parseInt(value);
    }

    private int parseFontWeight(String weight) {
        if (weight.equalsIgnoreCase("normal")) {
            return Font.PLAIN;
        } else {
            return Font.BOLD;
        }
    }

    private int parseFontStyle(String style) {
        if (style.equalsIgnoreCase("normal")) {
            return Font.PLAIN;
        } else {
            return Font.ITALIC;
        }
    }

    public void setLabelProperty(String content) {
        checkSymbolizer();
        TextSymbolizer textSymbolizer;
        if (this.symbolizer instanceof TextSymbolizer) {
            textSymbolizer = (TextSymbolizer) this.symbolizer;
        } else {
            throw new UnsupportedOperationException("Method only value for TextSymbolizers");
        }
        textSymbolizer.setLabelProperty(content);


    }
}
