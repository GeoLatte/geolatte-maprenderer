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

import org.junit.Test;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;

public class TestValue {

    @Test
    public void test_value_from_string() {

        Value<Float> received = Value.of("10px", UOM.PIXEL);
        assertEquals(Float.valueOf(10.0f), received.value());
        assertEquals(UOM.PIXEL, received.uom());

        received = Value.of(" 10 px ", UOM.PIXEL);
        assertEquals(Float.valueOf(10.0f), received.value());
        assertEquals(UOM.PIXEL, received.uom());

        received = Value.of(" 10 ft ", UOM.PIXEL);
        assertEquals(Float.valueOf(10.0f), received.value());
        assertEquals(UOM.FOOT, received.uom());

        received = Value.of(" -10m ", UOM.PIXEL);
        assertEquals(Float.valueOf(-10.0f), received.value());
        assertEquals(UOM.METRE, received.uom());


    }

    @Test
    public void test_on_empty_string() {
        Value<Float> received = Value.of("        ", UOM.FOOT);
        assertEquals(Float.valueOf(0f), received.value());
        assertEquals(UOM.FOOT, received.uom());
    }


    @Test
    public void test_default_UOM_used() {
        Value<Float> received = Value.of("33.21", UOM.FOOT);
        assertEquals(Float.valueOf(33.21f), received.value());
        assertEquals(UOM.FOOT, received.uom());
    }

    @Test
    public void test_throws_NFE_on_non_numberic_value() {

        try {
            Value<Float> received = Value.of("px", UOM.PIXEL);
            fail();
        } catch (NumberFormatException e) {
            //OK
        }


    }

    @Test
    public void test_on_existing_uom_assumes_pixels() {
        Value<Float> received = Value.of("5 bla", UOM.PIXEL);
        assertEquals(Float.valueOf(5.0f), received.value());
        assertEquals(UOM.PIXEL, received.uom());
    }

}
