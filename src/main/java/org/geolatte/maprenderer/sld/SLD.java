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
