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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class FeatureTypeStyleRegistry {

    private final static Logger LOGGER = LoggerFactory.getLogger(FeatureTypeStyleRegistry.class);
    private Map<String, FeatureTypeStyle> styles = new HashMap<String, FeatureTypeStyle>();

    public void configure(File styleDir) {
        if (!styleDir.isDirectory()) {
            throw new RuntimeException("No FeatureTypeStyle directory: " + styleDir);
        }
        configureFiles(styleDir);
    }

    private void configureFiles(File styleDir) {
        for (File f : styleDir.listFiles()) {
            if (!f.isFile()) continue;
            configureFile(f);
        }
    }

    private void configureFile(File f) {
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            FeatureTypeStyle style = SLD.instance().create(in);
            this.styles.put(style.getName(), style);
        } catch (Exception e) {
            LOGGER.warn("Failure reading SLD from " + f.getAbsolutePath());
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                //Nothing to be done.
            }
        }
    }

    public FeatureTypeStyle getStyle(String layerName) {
        return this.styles.get(layerName);
    }

}
