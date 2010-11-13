package org.geolatte.maprenderer.sld;

import net.opengis.se.v_1_1_0.FeatureTypeStyleType;
import net.opengis.se.v_1_1_0.LineSymbolizerType;
import net.opengis.sld.v_1_1_0.ObjectFactory;

import javax.xml.bind.*;
import java.io.InputStream;
import java.io.StringReader;

/**
 * @author Karel Maesen
 *         Copyright Geovise BVBA, 2010
 */
public class SLD {

    private static SLD instance = new SLD();

    private JAXBContext ctxt;
    private ObjectFactory objectFactory;

    private SLD() {
        try {
            ctxt = JAXBContext.newInstance("net.opengis.sld.v_1_1_0:net.opengis.wms.v_1_3_0");
            objectFactory = new ObjectFactory();
        } catch (JAXBException e) {
            throw new IllegalStateException("Can't instantiate SLD static factory", e);
        }
    }

    public static SLD instance() {
        return instance;
    }

    FeatureTypeStyleType unmarshal(InputStream inputStream) {
        Unmarshaller unmarshaller = null;
        try {
            unmarshaller = ctxt.createUnmarshaller();
            JAXBElement<FeatureTypeStyleType> root = (JAXBElement<FeatureTypeStyleType>)unmarshaller.unmarshal(inputStream);
            return root.getValue();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
    


    public FeatureTypeStyle create(InputStream inputStream){
        FeatureTypeStyleType type = unmarshal(inputStream);
        return new FeatureTypeStyle(type);
    }

    /**
     * Creates an SLD type from an XML Fragment.
     *
     * This method is used in the SLD unit tests.
     *
     * @param xmlFragment
     * @param elementClass
     * @param <E>
     * @return
     */
    <E> E read(String xmlFragment, Class<E> elementClass) {
        StringReader reader = new StringReader(xmlFragment);
        try {
            Unmarshaller unmarshaller = ctxt.createUnmarshaller();
            JAXBElement<E> element = (JAXBElement<E>)unmarshaller.unmarshal(reader);
            return element.getValue();
        }catch(JAXBException e){
            throw new RuntimeException(e);
        }        
    }
}
