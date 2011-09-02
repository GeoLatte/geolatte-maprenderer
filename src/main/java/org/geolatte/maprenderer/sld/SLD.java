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

import net.opengis.se.v_1_1_0.FeatureTypeStyleType;
import net.opengis.sld.v_1_1_0.ObjectFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.StringReader;

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
}
