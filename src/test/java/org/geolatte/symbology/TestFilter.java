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

import com.vividsolutions.jts.geom.Geometry;
import net.opengis.ogc.FilterType;
import net.opengis.se.FeatureTypeStyleType;
import net.opengis.se.RuleType;
import org.geolatte.core.Feature;
import org.geolatte.maprenderer.sld.*;
import org.geolatte.maprenderer.sld.filter.Filter;

import java.io.File;
import java.util.Collection;

public class TestFilter {

    public static void main(String[] args) {

        File f = new File(
                "/Users/maesenka/workspaces/java2D/SimpleGIS/src/test/resources/test_sld_1.xml");
        try {
            XMLReader<FeatureTypeStyleType> reader = OGCXMLReaderFactory
                    .createFeatureTypeStyleReader();
            FeatureTypeStyleType fts = reader.read(f);
            RuleType rt = (RuleType) fts.getRuleOrOnlineResource().get(0);
            FilterType ft = rt.getFilter();
//			parser.construct(ft);
//			List<String> exprlist = (List<String>)parser.getResult();
//			
//			int i = 0;
//			for (String atom : exprlist ){
//				System.out.println(i++ + ":" + atom);
//			}
//			
            BuilderFactory factory = new BuilderFactoryImpl();
            FilterDirector parser = new FilterDirector(factory);
            parser.construct(ft);
            Filter filter = (Filter) parser.getResult();

            System.out.println(filter.toString());

            Feature feat = new TestFeature(101f, 4);
            System.out.println(filter.evaluate(feat));

            feat = new TestFeature(99f, 3);
            System.out.println(filter.evaluate(feat));


        } catch (XMLReaderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    private static class TestFeature implements Feature {

        private float depth;
        private int numlanes;


        public TestFeature(float depth, int numlanes) {
            System.out.printf("Creating feature with %e depth and %d lanes", new Object[]{depth, numlanes});
            System.out.println();
            this.depth = depth;
            this.numlanes = numlanes;
        }

        public Geometry getGeometry() {
            // TODO Auto-generated method stub
            return null;
        }

        public boolean hasId() {
            return true;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public boolean hasGeometry() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public boolean hasProperty(String s) {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public boolean hasProperty(String s, boolean b) {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Collection<String> getProperties() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Object getProperty(String propertyName) {
            if (propertyName.equalsIgnoreCase("DEPTH"))
                return depth;
            if (propertyName.equalsIgnoreCase("num_lanes"))
                return numlanes;
            return null;
        }

        public Object getId() {
            return new Long(1);
        }


    }
}

