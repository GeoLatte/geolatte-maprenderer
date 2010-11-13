package org.geolatte.maprenderer.sld;

import net.opengis.se.v_1_1_0.FeatureTypeStyleType;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertNotNull;

/**
 * @author Karel Maesen
 *         Copyright Geovise BVBA, 2010
 */
public class TestSLD {

    private static final String TEST_SLD_FILE = "test-sld.xml";
    @Test
    public void test_unmarshal_featuretypestyletype(){        
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("TEST_SLD_FILE");
        FeatureTypeStyleType style = SLD.instance().unmarshal(in);
        assertNotNull(style);
    }

    @Test
    public void test_create_FeatureTypeStyle(){
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("TEST_SLD_FILE");
        FeatureTypeStyle style = SLD.instance().create(in);
    }
}
