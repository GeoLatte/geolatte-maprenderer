package be.wegenenverkeer.mosaic.domain.service;

import be.wegenenverkeer.mosaic.domain.service.painters.OpstellingImagePainter;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.map.Painter;
import org.geolatte.mapserver.PainterFactory;
import org.geolatte.mapserver.spi.PainterFactoryProvider;


import static java.lang.String.format;

/**
 * Created by Karel Maesen, Geovise BVBA on 27/07/2018.
 */
public class OpstellingPainterFactoryProvider implements PainterFactoryProvider {


    @Override
    public PainterFactory painterFactory() {

        return new PainterFactory() {
            @Override
            public boolean canCreate(String ref) {
                return "opstellingPainter".equals(ref);
            }

            @Override
            public Painter mkPainter(String ref, MapGraphics mapGraphics) {
                return new OpstellingImagePainter(mapGraphics) ;
            }
        };
    }
}
