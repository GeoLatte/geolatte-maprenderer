package be.wegenenverkeer.mosaic.domain.service.featuredeserializers;

import org.geolatte.mapserver.features.FeatureDeserializer;
import org.geolatte.mapserver.rxhttp.FeatureDeserializerFactory;

public class OpstellingFeatureDeserializerFactory implements FeatureDeserializerFactory {

    @Override
    public FeatureDeserializer featureDeserializer() {
        return new OpstellingFeatureDeserializer();
    }

}
