package org.geolatte.maprenderer.sld;

import net.opengis.se.v_1_1_0.LineSymbolizerType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * @author Karel Maesen
 *         Copyright Geovise BVBA, 2010
 */
public class TestLineSymbolizerPainter extends SLDPainterTest {

    LineSymbolizerPainter painter;
    LineSymbolizerType type;

    public LineSymbolizerType createSymbolizerType() {
        String xmlFragment =
                "<LineSymbolizer version=\"1.1.0\"" +
                        "                  xmlns=\"http://www.opengis.net/se\"" +
                        "                  xmlns:ogc=\"http://www.opengis.net/ogc\">" +
                        "<Geometry>\n" +
                        "    <ogc:PropertyName>\ncenterline\n</ogc:PropertyName>\n" +
                        "</Geometry>" +
                        "<Stroke>" +
                        "    <SvgParameter name=\"stroke\">\n#0000F\n</SvgParameter>\n" +
                        "    <SvgParameter name=\"width\">2</SvgParameter>" +
                        "</Stroke>" +
                        "<PerpendicularOffset>\n" +
                            " <ogc:Literal>\n-10\n</ogc:Literal>\n" +
                        "</PerpendicularOffset>"+
                        "</LineSymbolizer>";
        return SLD.instance().read(xmlFragment, LineSymbolizerType.class);
    }

    @Before
    public void before(){
        super.before();
        type = createSymbolizerType();
        painter = getFeatureTypeStyle().createSymbolizerPainter(type);
    }



    @Test
    public void test_get_geometry_property(){
        assertEquals("centerline", painter.getGeometryProperty());
    }

    @Test
    public void test_default_uom_is_pixel() {
        assertEquals(UOM.PIXEL, painter.getUOM());
    }

    @Test
    public void test_uom_is_foot_or_metre() {
        type.setUom("http://www.opengeospatial.org/se/units/foot");
        painter = getFeatureTypeStyle().createSymbolizerPainter(type);
        assertEquals(UOM.FOOT, painter.getUOM());
        type.setUom("http://www.opengeospatial.org/se/units/metre");
        painter = getFeatureTypeStyle().createSymbolizerPainter(type);
        assertEquals(UOM.METRE, painter.getUOM());
        
    }

    @Test
    public void test_perpendicularOffset(){
        Value<Float> perOffset = painter.getPerpendicularOffset();
        assertEquals(UOM.PIXEL, perOffset.uom());
        assertEquals(Float.valueOf(-10f), perOffset.value());
    }

    @Test
    public void test_no_geometry() {
        assertEquals("centerline", painter.getGeometryProperty());
        String xmlFragment =
                "<LineSymbolizer version=\"1.1.0\"" +
                        "                  xmlns=\"http://www.opengis.net/se\"" +
                        "                  xmlns:ogc=\"http://www.opengis.net/ogc\">" +
                        "<Stroke>" +
                        "    <SvgParameter name=\"stroke\">#0000F</SvgParameter>" +
                        "    <SvgParameter name=\"width\">2</SvgParameter>" +
                        "</Stroke>" +
                        "</LineSymbolizer>";
        LineSymbolizerType type = SLD.instance().read(xmlFragment, LineSymbolizerType.class);
        LineSymbolizerPainter painter = getFeatureTypeStyle().createSymbolizerPainter(type);
        assertNull(painter.getGeometryProperty());
    }

}
