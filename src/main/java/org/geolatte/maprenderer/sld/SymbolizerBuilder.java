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

import org.geolatte.maprenderer.sld.symbolizer.Symbolizer;

public interface SymbolizerBuilder extends Builder<Symbolizer> {

    void setUom(String uom);

    void setName(String name);

    void setVersion(String version);

    void setDescription(String title, String abstr);

    void setType(SymbolizerType point);

    void setGeometryPropertyName(String propName);

    void setPerpendicularOffset(float d);

    void setStrokeParameter(String name, String value);

    void setFillParameter(String name, String value);

    void setFontParameter(String name, String value);

    void setLabelProperty(String content);

    enum UnitsOfMeasure {

        PIXEL, METER, FOOT;
    }

    /**
     * This class embeds information on the specs
     * this package is based on.
     *
     * @author Karel Maesen
     */
    class VersionInfo {

        public static final String OGC_SPEC="Symbology Encoding Implementation Specification";
        public static final String OGC_SPEC_VERSION = "1.1.0 (rev 4)";
        public static final String OGC_DOCUMENT_REF="OGC 05-077rv";



    }
}
