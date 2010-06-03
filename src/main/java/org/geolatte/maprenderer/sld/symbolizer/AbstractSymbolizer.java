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

import org.geolatte.maprenderer.sld.SymbolizerBuilder;


public abstract class AbstractSymbolizer implements Symbolizer {


    private String name;
    private String title;
    private String abstractText;
    private SymbolizerBuilder.UnitsOfMeasure uom = SymbolizerBuilder.UnitsOfMeasure.PIXEL;
    private String version;
    private String geometryPropertyName;


    public String getTitle() {
        return title;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SymbolizerBuilder.UnitsOfMeasure getUom() {
        return uom;
    }

    public void setUom(SymbolizerBuilder.UnitsOfMeasure uom) {
        this.uom = uom;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGeometryPropertyName() {
        return geometryPropertyName;
    }

    public void setGeometryPropertyName(String geometryPropertyName) {
        this.geometryPropertyName = geometryPropertyName;
    }

}