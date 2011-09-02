/*
 * Copyright (c) 2011. Geovise BVBA, QMINO BVBA
 *
 * This file is part of GeoLatte Mapserver.
 *
 * GeoLatte Mapserver is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GeoLatte Mapserver is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GeoLatte Mapserver.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.geolatte.test;

import org.geolatte.core.Feature;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MockFeatures implements Iterable<Feature> {

    private List<Feature> features = new ArrayList<Feature>();

    public MockFeatures(int size) {

        for (int i = 0; i < size; i++) {
            Feature feature = new MockPolygonFeature();
            features.add(feature);
        }
    }

    public Iterator<Feature> iterator() {
        return new MockFeatureIterator(this);

    }

    private static class MockFeatureIterator implements Iterator<Feature> {

        private final List<Feature> features;
        private int index = 0;

        private MockFeatureIterator(MockFeatures fi) {
            this.features = fi.features;

        }

        public boolean hasNext() {
            return index < features.size();
        }

        public Feature next() {
            return features.get(index++);
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    

}