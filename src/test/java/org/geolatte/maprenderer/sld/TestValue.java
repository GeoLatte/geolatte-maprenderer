package org.geolatte.maprenderer.sld;

import org.junit.Test;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;

/**
 * @author Karel Maesen
 *         Copyright Geovise BVBA, 2010
 */
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

}
