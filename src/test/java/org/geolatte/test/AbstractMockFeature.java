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
