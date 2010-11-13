///*
// * This file is part of the GeoLatte project. This code is licenced under
// * the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
// * with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
// * Unless required by applicable law or agreed to in writing, software distributed under the License
// * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
// * implied. See the License for the specific language governing permissions and limitations under the
// * License.
// *
// * Copyright (C) 2010 - 2010 and Ownership of code is shared by:
// * Qmino bvba - Romeinsestraat 18 - 3001 Heverlee (http://www.Qmino.com)
// * Geovise bvba - Generaal Eisenhowerlei 9 - 2140 Antwerpen (http://www.geovise.com)
// */
//
//package org.geolatte.maprenderer.sld;
//
//
//
//import net.opengis.se.v_1_1_0.FeatureTypeStyleType;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//
//
//public class FeatureTypeStyleRegistry {
//
//    private Map<String, FeatureTypeStyleType> styles = new HashMap<String, FeatureTypeStyleType>();
//
//    public void configure(File styleDir) {
//        if (!styleDir.isDirectory()) {
//            throw new RuntimeException("No FeatureTypeStyle directory: " + styleDir);
//        }
//        for (File f : styleDir.listFiles()) {
//            if (!f.isFile()) continue;
//            try {
//                XMLReader<FeatureTypeStyleType> reader = OGCXMLReaderFactory.createFeatureTypeStyleReader();
//                FeatureTypeStyleType fts = reader.read(f);
//                FeatureTypeStyleDirector director = new FeatureTypeStyleDirector(factory);
//                director.construct(fts);
//                FeatureTypeStyle style = (FeatureTypeStyle) director.getResult();
//                this.styles.put(style.getFeatureTypeName().getLocalPart(), style);
//            } catch (Exception e) {
//                System.err.println("Can't read file: " + f);
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public FeatureTypeStyle getStyle(String layerName) {
//        return this.styles.get(layerName);
//    }
//
//}
