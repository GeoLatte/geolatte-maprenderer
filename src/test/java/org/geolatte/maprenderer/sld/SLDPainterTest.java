package org.geolatte.maprenderer.sld;

import net.opengis.se.v_1_1_0.FeatureTypeStyleType;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.InputStream;

/**
 * @author Karel Maesen
 *         Copyright Geovise BVBA, 2010
 */
public class SLDPainterTest {

    static FeatureTypeStyleType sldRoot;

    FeatureTypeStyle featureTypeStyle;
    FeatureTypeStylePainter painter;
    LineSymbolizerPainter lsPainter;


    @BeforeClass
    public static void beforeClass(){
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("test-sld.xml");
        sldRoot = SLD.instance().unmarshal(in);
    }

    public void before(){
        featureTypeStyle = new FeatureTypeStyle(sldRoot);
        painter = featureTypeStyle.painter();
    }


    /**
     * Returns the {@FeatureTypeStylePainter} associated with test-sld.xml
     * @return
     */
    FeatureTypeStylePainter getFeatureTypeStylePainter(){
        return painter;
    }

    /**
     * Returns the FeatureTypeStyle created form test-sld.xml
     */
    public FeatureTypeStyle getFeatureTypeStyle(){
        return featureTypeStyle;        
    }

}
