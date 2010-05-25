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

import net.opengis.se.FeatureTypeStyleType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamReader;

public class OGCXMLReaderFactory {


    private static final String OGC_SCHEMA_PACKAGES = "net.opengis.context"
            + ":net.opengis.se"
            + ":net.opengis.ogc"
            + ":net.opengis.gml"
            + ":net.opengis.sld";


    private static final JAXBContext OGCContext;

    static {

        try {
            OGCContext = JAXBContext.newInstance(OGC_SCHEMA_PACKAGES);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

    }

    public static XMLReader<FeatureTypeStyleType> createFeatureTypeStyleReader() throws XMLReaderException {
        return new AbstractXMLReader<FeatureTypeStyleType>(OGCContext) {

            @Override
            public FeatureTypeStyleType read(XMLStreamReader xmlstream) throws XMLReaderException {
                return this.read(xmlstream, FeatureTypeStyleType.class);
            }

        };
    }


}
