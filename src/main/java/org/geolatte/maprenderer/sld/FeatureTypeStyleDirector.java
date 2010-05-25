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

import net.opengis.se.DescriptionType;
import net.opengis.se.FeatureTypeStyleType;
import net.opengis.se.RuleType;

import javax.xml.namespace.QName;

public class FeatureTypeStyleDirector implements Director<FeatureTypeStyleType, FeatureTypeStyle> {

    private BuilderFactory builderFactory;
    private FeatureTypeStyleBuilder builder;

    public FeatureTypeStyleDirector() {
    }

    public FeatureTypeStyleDirector(BuilderFactory builderFac) {
        setBuilderFactory(builderFac);
    }

    public void setBuilderFactory(BuilderFactory builderFac) {
        this.builderFactory = builderFac;
        this.builder = this.builderFactory.createFeatureTypeStylebuilder();
    }

    public void construct(FeatureTypeStyleType featureStyleType) {
        String name = featureStyleType.getName();
        this.builder.setName(name);
        QName featName = featureStyleType.getFeatureTypeName();
        if (featName != null) {
            String featureTypeLocal = featName.getLocalPart();
            String featureTypeNS = featName.getNamespaceURI();
            this.builder.setFeatureType(featureTypeNS, featureTypeLocal);
        }
        DescriptionType desc = featureStyleType.getDescription();
        if (desc != null) {
            String abst = desc.getAbstract();
            String title = featureStyleType.getDescription().getTitle();
            this.builder.setDescription(title, abst);
        }

        String version = featureStyleType.getVersion();
        this.builder.setVersion(version);

        for (Object o : featureStyleType.getRuleOrOnlineResource()) {
            if (o instanceof RuleType) {
                RuleType rt = (RuleType) o;
                RuleDirector rd = new RuleDirector(this.builderFactory);
                rd.construct(rt);
                this.builder.addRule(rd.getResult());

            }
        }
    }

    public FeatureTypeStyle getResult() {
        return this.builder.getResult();
    }


}
