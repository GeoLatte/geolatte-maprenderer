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

package org.geolatte.maprenderer.sld;

import net.opengis.se.v_1_1_0.AnchorPointType;
import net.opengis.se.v_1_1_0.DisplacementType;
import net.opengis.se.v_1_1_0.FeatureTypeStyleType;
import net.opengis.se.v_1_1_0.ParameterValueType;
import net.opengis.sld.v_1_1_0.ObjectFactory;
import org.geolatte.maprenderer.util.JAXBHelper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.awt.geom.Point2D;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;

/**
 * A utility class for parsing SLD documents.
 *
 */
public class SLD {

    private static SLD instance = new SLD();

    private JAXBContext ctxt;
    private ObjectFactory objectFactory;

    private SLD() {
        try {
            ctxt = JAXBContext.newInstance("net.opengis.sld.v_1_1_0:net.opengis.wms.v_1_3_0");
            objectFactory = new ObjectFactory();
        } catch (JAXBException e) {
            throw new IllegalStateException("Can't instantiate SLD static factory", e);
        }
    }

    public static SLD instance() {
        return instance;
    }

    FeatureTypeStyleType unmarshal(InputStream inputStream) {
        Unmarshaller unmarshaller = null;
        try {
            unmarshaller = ctxt.createUnmarshaller();
            JAXBElement<FeatureTypeStyleType> root = (JAXBElement<FeatureTypeStyleType>) unmarshaller.unmarshal(inputStream);
            return root.getValue();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }


    public FeatureTypeStyle create(InputStream inputStream) {
        FeatureTypeStyleType type = unmarshal(inputStream);
        return new FeatureTypeStyle(type);
    }

    /**
     * Creates an SLD type from an XML Fragment.
     * <p/>
     * This method is used in the SLD unit tests.
     *
     * @param xmlFragment
     * @param elementClass
     * @param <E>
     * @return
     */
    public <E> E read(String xmlFragment, Class<E> elementClass) {
        StringReader reader = new StringReader(xmlFragment);
        try {
            Unmarshaller unmarshaller = ctxt.createUnmarshaller();
            JAXBElement<E> element = (JAXBElement<E>) unmarshaller.unmarshal(reader);
            return element.getValue();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Extracts the value from a ParameterValueType element as String.
     *
     * <p>This implementation extracts all text from the element content. It does
     * not currently parse or interpret this content as an OGC expression. </p>
     *
     * @param parameterValueType
     * @return
     */
    public String extractParameterValue(ParameterValueType parameterValueType) {
        //TODO -- parse the OGC expression (if present).
        if (parameterValueType == null) {
            return null;
        }
        java.util.List<Serializable> content = parameterValueType.getContent();
        if (content == null || content.isEmpty()) {
            return null;
        }
        return JAXBHelper.extractValueToString(content);
    }

    /**
     * Extracts the displacement as a <code>Point2D</code> from an Displacement-element.
     *
     * @param displacementType
     * @return
     */
    public Point2D readDisplacement(DisplacementType displacementType) {
        Point2D defaultDisplacement = new Point2D.Float(0f, 0f);
        if (displacementType == null) return defaultDisplacement;
        String dXStr = extractParameterValue(displacementType.getDisplacementX());
        String dYStr = extractParameterValue(displacementType.getDisplacementY());
        if (dXStr == null || dYStr == null) return defaultDisplacement;
        float dX = Float.parseFloat(dXStr);
        float dY = Float.parseFloat(dYStr);
        return new Point2D.Float(dX, dY);
    }


    public Point2D readAnchorPoint(AnchorPointType anchorPoint) {
        Point2D defaultDisplacement = new Point2D.Float(0.5f, 0.5f);
        if (anchorPoint == null) return defaultDisplacement;
        String aXStr = extractParameterValue(anchorPoint.getAnchorPointX());
        String aYStr = extractParameterValue(anchorPoint.getAnchorPointY());
        if (aXStr == null || aYStr == null) return defaultDisplacement;
        float aX = Float.parseFloat(aXStr);
        float aY = Float.parseFloat(aYStr);
        return new Point2D.Float(aX, aY);
    }
}
