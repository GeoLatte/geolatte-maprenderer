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

import org.geolatte.maprenderer.sld.symbolizer.Rule;

import javax.xml.namespace.QName;

public class FeatureTypeStyleBuilderImpl implements FeatureTypeStyleBuilder {

    FeatureTypeStyle style = new FeatureTypeStyle();

    public void addRule(Object result) {
        if (result != null) {
            Rule rule = (Rule) result;
            style.getRules().add(rule);
        }
    }

    public void setDescription(String title, String abst) {
        style.setTitle(title);
        style.setAbstractText(abst);
    }

    public void setFeatureType(String featureTypeNS, String featureTypeLocal) {
        QName featureTypeName;
        if (featureTypeNS == null) {
            featureTypeName = new QName(featureTypeLocal);
        } else {
            featureTypeName = new QName(featureTypeNS, featureTypeLocal);
        }
        style.setFeatureTypeName(featureTypeName);
    }

    public void setName(String name) {
        this.style.setName(name);

    }

    public void setVersion(String version) {
        this.style.setVersion(version);
    }

    public FeatureTypeStyle getResult() {
        return this.style;
    }

}
