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


import org.geolatte.core.Feature;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.map.Painter;

import java.util.Collections;
import java.util.List;

public class FeatureTypeStylePainter implements Painter {


    private final List<Rule> rules;

    FeatureTypeStylePainter(List<Rule> rules){
        this.rules = rules;
    }

    @Override
    public void paint(MapGraphics graphics, Iterable<Feature> features) {

    }

    /**
     * Returns the rules in the order defined by the SLD document.
     * @return
     */
    protected List<Rule> getRules() {
        return rules;
    }
}
