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

package org.geolatte.test;

import com.vividsolutions.jts.geom.*;
import org.geolatte.core.Feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: May 23, 2010
 */
//TODO Replace by Mockito
public abstract class AbstractMockFeature implements Feature {
    protected static final GeometryFactory geomFactory = new GeometryFactory();
    protected Geometry geom;
    protected Integer id;
    protected static AtomicInteger counter = new AtomicInteger(0);

    public AbstractMockFeature() {
        this.geom = generateGeom();
        this.id = counter.incrementAndGet();
    }

    protected abstract Geometry generateGeom();

    public boolean hasProperty(String s) {
        return s.equals("geometry") || s.equals("id");
    }

    public Collection<String> getProperties() {
        List<String> list = new ArrayList<String>(2);
        list.add("geometry");
        list.add("id");
        return list;
    }

    public Object getProperty(String s) {
        if (s.equals("id")) return this.id;
        if (s.equals("geometry")) return this.geom;
        return null;
    }

    public Object getId() {
        return this.id;
    }

    public Geometry getGeometry() {
        return this.geom;
    }

    public boolean hasProperty(String s, boolean b) {
        if (!b) {
            return b;
        } else {
            return hasProperty(s);
        }
    }

    public boolean hasId() {
        return this.id != null;
    }

    public boolean hasGeometry() {
        return this.geom != null;
    }
}
