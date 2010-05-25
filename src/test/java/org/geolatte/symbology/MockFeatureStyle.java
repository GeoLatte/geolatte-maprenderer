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

package org.geolatte.symbology;

import org.opengis.sld.FeatureStyle;
import org.opengis.sld.Rule;
import org.opengis.util.InternationalString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: May 23, 2010
 */
public class MockFeatureStyle implements FeatureStyle {

    public String getName() {
        return "Mock Feature Style";
    }

    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    public InternationalString getTitle() {
        return null;
    }

    public void setTitle(InternationalString title) {
        throw new UnsupportedOperationException();
    }

    public InternationalString getAbstract() {
        throw new UnsupportedOperationException();
    }

    public void setAbstract(InternationalString abs) {
        throw new UnsupportedOperationException();
    }

    public String getFeatureTypeName() {
        return "Mock Feature";
    }

    public void setFeatureTypeName(String featureTypeName) {
        throw new UnsupportedOperationException();
    }

    public List<String> getSemanticTypeIdentifiers() {
        return new ArrayList<String>();
    }

    public List<Rule> getRules() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
