/*
 * This file is part of the GeoLatte project.
 *
 *     GeoLatte is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     GeoLatte is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with GeoLatte.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright (C) 2010 - 2011 and Ownership of code is shared by:
 *  Qmino bvba - Esperantolaan 4 - 3001 Heverlee  (http://www.qmino.com)
 *  Geovise bvba - Generaal Eisenhowerlei 9 - 2140 Antwerpen (http://www.geovise.com)
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
