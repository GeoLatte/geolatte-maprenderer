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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public abstract class AbstractXMLReader<T> implements XMLReader<T> {

    protected Unmarshaller unmarshaller;

    public AbstractXMLReader(JAXBContext jc) throws XMLReaderException {
        try {
            setUnmarshaller(jc.createUnmarshaller());
        } catch (Exception e) {
            throw new XMLReaderException(e);
        }
    }

    public T read(InputStream in) throws XMLReaderException {
        try {
            XMLStreamReader xmlstream = XMLInputFactory.newInstance().createXMLStreamReader(in);
            return read(xmlstream);
        } catch (Exception e) {
            throw new XMLReaderException(e);
        }
    }

    public T read(File in) throws XMLReaderException {
        try {
            return read(new FileInputStream(in));
        } catch (FileNotFoundException e) {
            throw new XMLReaderException(e);
        }
    }

    public T read(XMLStreamReader xmlstream, Class<T> declaredtype) throws XMLReaderException {
        try {
            return getUnmarshaller().unmarshal(xmlstream, declaredtype).getValue();
        } catch (Exception e) {
            throw new XMLReaderException(e);
        }
    }

    protected Unmarshaller getUnmarshaller() {
        return this.unmarshaller;
    }

    protected void setUnmarshaller(Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }


    abstract public T read(XMLStreamReader xmlstream) throws XMLReaderException;
}