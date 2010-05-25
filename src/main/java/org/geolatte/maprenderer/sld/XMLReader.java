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

import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.InputStream;

/**
 * An <code>XMLReader</code> can read an element of
 * a type specified by an OGC from an XML file.
 *
 * @author Karel Maesen
 * @param <T> the declared type of the xml (root) node that is to be read
 */
public interface XMLReader<T> {

    /**
     * Reads a file and returns the root node of type T
     *
     * @param in the file to read
     * @return the value object of type T in the file
     * @throws XMLReaderException thrown when the file could not be read,
     *                            or doesn't not contain a root element of type T.
     */
    public T read(File in) throws XMLReaderException;

    /**
     * Reads an InputStream and returns the root node of type T
     *
     * @param in the InputStream from which to read
     * @return the value object of type T in the file
     * @throws XMLReaderException thrown when the file could not be read,
     *                            or doesn't not contain a root element of type T.
     */
    public T read(InputStream in) throws XMLReaderException;

    /**
     * @param xmlstream the input stream
     * @return the value object of type T in the input stream
     * @throws XMLReaderException thrown when the input stream could not be read from,
     *                            or doesn't not contain a root element of type T.
     */
    public T read(XMLStreamReader xmlstream) throws XMLReaderException;

}