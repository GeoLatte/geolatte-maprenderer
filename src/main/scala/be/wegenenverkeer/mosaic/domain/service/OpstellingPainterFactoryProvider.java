package be.wegenenverkeer.mosaic.domain.service;

import be.wegenenverkeer.mosaic.application.painters.OpstellingImagePainter;
import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.Point;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.map.Painter;
import org.geolatte.maprenderer.map.PlanarFeature;
import org.geolatte.maprenderer.shape.ShapeAdapter;
import org.geolatte.mapserver.PainterFactory;
import org.geolatte.mapserver.spi.PainterFactoryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import static java.lang.String.format;
import static java.util.Arrays.asList;

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
