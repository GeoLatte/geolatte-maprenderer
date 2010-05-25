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

import net.opengis.ogc.LiteralType;
import net.opengis.ogc.PropertyNameType;
import net.opengis.se.*;
import net.opengis.se.SymbolizerType;
import org.geolatte.maprenderer.sld.symbolizer.Symbolizer;

import javax.xml.bind.JAXBElement;
import java.io.Serializable;
import java.util.List;

public class SymbolizerDirector implements Director<SymbolizerType, Symbolizer> {

    private BuilderFactory builderFactory;
    private SymbolizerBuilder builder;


    public SymbolizerDirector(BuilderFactory builderFactory) {
        this.builderFactory = builderFactory;
    }

    public void construct(SymbolizerType symbolizer) {
        this.builder = this.builderFactory.createSymbolizerBuilder();

        if (symbolizer instanceof LineSymbolizerType) {
            constructLineSymbolizer((LineSymbolizerType) symbolizer);
        } else if (symbolizer instanceof PolygonSymbolizerType) {
            constructPolygonSymbolizer((PolygonSymbolizerType) symbolizer);
        } else if (symbolizer instanceof PointSymbolizerType) {
            constructPointSymbolizer((PointSymbolizerType) symbolizer);
        } else if (symbolizer instanceof TextSymbolizerType) {
            constructTextSymbolizer((TextSymbolizerType) symbolizer);
        } else {
            throw new UnsupportedOperationException("Symbolizer type " + symbolizer.getClass().getCanonicalName());
        }


        String uom = getUom(symbolizer);
        if (uom != null) {
            this.builder.setUom(uom);
        }

        String name = symbolizer.getName();
        if (name != null) {
            this.builder.setName(name);
        }

        String version = symbolizer.getVersion();
        if (version != null) {
            this.builder.setVersion(version);
        }

        DescriptionType dt = symbolizer.getDescription();
        if (dt != null) {
            this.builder.setDescription(dt.getTitle(), dt.getAbstract());
        }

    }

    private void setGeometry(GeometryType geom) {
        if (geom == null || geom.getPropertyName() == null) {
            return;
        }

        String propName = geom.getPropertyName().getContent();
        if (propName != null) {
            this.builder.setGeometryPropertyName(propName);
        }

    }

    private String getParameterValue(ParameterValueType pt) {
        if (pt == null || pt.getContent() == null || pt.getContent().isEmpty()) {
            return null;
        }
        return (String) unpackValue(pt.getContent().get(0));
    }

    private String unpackValue(Object o) {
        if (o instanceof String) {
            return (String) o;
        }
        if (o instanceof JAXBElement) {
            JAXBElement el = (JAXBElement) o;
            Object v = el.getValue();
            return unpackValue(v);
        }

        if (o instanceof LiteralType) {
            LiteralType literal = (LiteralType) o;
            if (!literal.getContent().isEmpty()) {
                Object v = literal.getContent().get(0);
                return unpackValue(v);
            }
        }
        return "";
    }

    private void constructPointSymbolizer(PointSymbolizerType symbolizer) {
        this.builder.setType(org.geolatte.maprenderer.sld.SymbolizerType.POINT);
        setGeometry(symbolizer.getGeometry());
        //TODO == complete this
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    private void constructTextSymbolizer(TextSymbolizerType symbolizer) {
        this.builder.setType(org.geolatte.maprenderer.sld.SymbolizerType.TEXT);

        //set the label placement point
        setGeometry(symbolizer.getGeometry());

        //set the font
        FontType font = symbolizer.getFont();
        constructFontSVGParams(font.getSvgParameter());

        //set the label content property
        List<Serializable> labelElems = symbolizer.getLabel().getContent();
        for (Serializable ser : labelElems) {
            if (!(ser instanceof JAXBElement)) {
                continue;
            }
            JAXBElement elem = (JAXBElement) ser;
            if (elem.getDeclaredType() == PropertyNameType.class) {
                PropertyNameType property = (PropertyNameType) elem.getValue();
                this.builder.setLabelProperty(property.getContent());
                continue;
            }
        }

        // TODO -- treat label placement
        LabelPlacementType placement = symbolizer.getLabelPlacement();

    }

    private void constructPolygonSymbolizer(PolygonSymbolizerType symbolizer) {
        this.builder.setType(org.geolatte.maprenderer.sld.SymbolizerType.POLYGON);
        setGeometry(symbolizer.getGeometry());
        FillType fill = symbolizer.getFill();
        if (fill != null && fill.getSvgParameter() != null) {
            constructFillSVGParams(fill.getSvgParameter());
        }
        //TODO --support displacement and
        // perpendicular offset

        StrokeType stroke = symbolizer.getStroke();
        if (stroke != null && stroke.getSvgParameter() != null) {
            constructStrokeSVGParams(stroke.getSvgParameter());
        }
    }

    private void constructLineSymbolizer(LineSymbolizerType symbolizer) {
        this.builder.setType(org.geolatte.maprenderer.sld.SymbolizerType.LINE);
        setGeometry(symbolizer.getGeometry());
        String offset = getParameterValue(symbolizer.getPerpendicularOffset());
        if (offset != null) {
            this.builder.setPerpendicularOffset(Float.parseFloat(offset));
        }
        StrokeType stroke = symbolizer.getStroke();
        //TODO -- support GraphicFill and GraphicStroke
        if (stroke != null && stroke.getSvgParameter() != null) {
            constructStrokeSVGParams(stroke.getSvgParameter());
        }
    }

    private void constructStrokeSVGParams(List<SvgParameterType> parameters) {
        for (SvgParameterType p : parameters) {
            if (!p.getContent().isEmpty()) {
                String value = unpackValue(p.getContent().get(0));
                this.builder.setStrokeParameter(p.getName(), value);
            }
        }
    }

    private void constructFillSVGParams(List<SvgParameterType> parameters) {
        for (SvgParameterType p : parameters) {
            if (!p.getContent().isEmpty()) {
                String value = unpackValue(p.getContent().get(0));
                this.builder.setFillParameter(p.getName(), value);
            }
        }
    }

    private void constructFontSVGParams(List<SvgParameterType> parameters) {
        for (SvgParameterType p : parameters) {
            if (!p.getContent().isEmpty()) {
                String value = unpackValue(p.getContent().get(0));
                this.builder.setFontParameter(p.getName(), value);
            }
        }
    }

    private String getUom(SymbolizerType s) {
        String uom = s.getUom();
        if (uom.contains("pixel")) {
            return "pixel";
        } else if (uom.contains("meter")) {
            return "meter";
        } else if (uom.contains("foot")) {
            return "foot";
        } else {
            return uom;
        }
    }

    public Symbolizer getResult() {
        return this.builder.getResult();
    }

    public void setBuilderFactory(BuilderFactory builderFactory) {
        this.builderFactory = builderFactory;

    }


}
