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

public interface FeatureTypeStyleBuilder extends Builder<FeatureTypeStyle> {

    /**
     * Sets the name of the FeatureType style
     *
     * @param name
     */
    public void setName(String name);

    /**
     * Sets the namespace and localname of the feature type
     *
     * @param featureTypeNS    the feature type namespace URI
     * @param featureTypeLocal the local name of the feature type
     */
    public void setFeatureType(String featureTypeNS, String featureTypeLocal);


    /**
     * Sets the description for the style
     *
     * @param title the title
     * @param abst  the abstract
     */
    public void setDescription(String title, String abst);

    /**
     * Sets the version for the style
     *
     * @param version
     */
    public void setVersion(String version);

    public void addRule(Object result);


}
