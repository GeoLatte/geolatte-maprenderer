package org.geolatte.maprenderer.painters;

import org.geolatte.common.Feature;
import org.geolatte.geom.C2D;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.Point;
import org.geolatte.geom.crs.CoordinateReferenceSystems;
import org.geolatte.geom.crs.SingleCoordinateReferenceSystem;
import org.geolatte.maprenderer.java2D.AWTMapGraphics;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.map.Painter;
import org.geolatte.maprenderer.util.ImageUtils;
import org.geolatte.test.MockPointFeature;
import org.geolatte.test.TestSupport;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.geolatte.geom.builder.DSL.c;
import static org.geolatte.geom.builder.DSL.point;
import static org.geolatte.test.TestSupport.assertImageEquals;

/**
 * Created by Karel Maesen, Geovise BVBA on 06/04/2018.
 */
public class EmbeddedImagePainterTest {


    public static final SingleCoordinateReferenceSystem<C2D> CRS = CoordinateReferenceSystems.PROJECTED_2D_METER;

    Envelope<C2D> extent;
    MapGraphics mapGraphics;
    List<Feature> features = new ArrayList<>();

    @Before
    public void setUp() {
        this.extent = new Envelope<>(new C2D(0, 0), new C2D(100, 100), CRS);
        java.awt.Dimension dim = new java.awt.Dimension(512, 512);
        this.mapGraphics = new AWTMapGraphics(dim, extent);
        Map<String, Object> props = new HashMap<>();
        props.put("image", imageData);
        this.features.add(new MockPointFeature(
                point(CRS, c(50, 50)),
                props
        ));
    }


    @Test
    public void test_object_in_middle() throws IOException {


        Painter painter = new EmbeddedImagePainter(mapGraphics,
                (Feature f) -> ImageUtils.readImageFromBase64String((String) f.getProperty("image")),
                (Feature f) -> addOffset((Point<C2D>) f.getGeometry(), 10, 10),
                (Feature f) -> Math.PI / 4
        );
        painter.paint(features);
        RenderedImage img = this.mapGraphics.renderImage();
        TestSupport.writeImageToDisk(img, "embedded-image-painter-1.png", "PNG");

        //TODO -- why is this necessary here?
        //we first write the rendered file to disk, because writing to disk and then reading might cause slight
        //changes - presumably due to rounding to int pixels.
        File file = File.createTempFile("tmp", "png");
        ImageIO.write(img, "PNG", file);
        BufferedImage received = ImageIO.read(file);
        assertImageEquals("expected-embedded-image-painter-1.png", received);
    }

    private Point<C2D> addOffset(Point<C2D> pnt, double offsetX, double offsetY) {
        C2D pos = pnt.getPosition();
        C2D moved = new C2D(pos.getX() + offsetX, pos.getY() + offsetY);
        return point(pnt.getCoordinateReferenceSystem(), moved);
    }

    private static String imageData = "iVBORw0KGgoAAAANSUhEUgAAAC8AAAAxCAYAAABK+/BHAAAEdUlEQVR42u1aLVAqURTeN0MgEAwGA8FgMBgIBgJhg8FgMBgMBgPBQDAYCMzojMFgJBiIBAKBYCAQCASCgWAgEAgEA4FAMDDjefeD3TuX5f7tsuDw5p2ZM6OX3bvfvfec7/zsOs5/2VEhx0kyzTItMK0y7TAdMJ0xJU9H3lidaZGpi/t+E3SGaYXpRAAZRifeYk+3CRq71gzs7LqK+dxNgj5g2ooRsEwxfzpu4Pk1zCOsTuE/cQEvbwl0UOFPiaigEx47hH/w/j7R6SmR6xLlckQHB1EX0GCaigLeHvjhIVGxSNRuE319kVQmE6Jul+jpiejoKOwCEvGbCnb1/Z1oNqNQgutxH07G0oTCOKd+slSK6O0tPGiZ1OsLMzMvoGBDhxOjifR6FKuMxzanMNXSqJHHYSZ40CZkOiU6OzPGAV3k1O/4poCLCzCfgCsD39TauGcq/X6fkUrbqN/f3yvYMH5zc0PHx8d0cnJC+XyeTdtbNSG9D7RkSZY6V6lU+Ny3t7e0uEWvw+FwCdPLywslEomV6zBWEebnTqzf/UUy92cxS0Vr5wKrRAFfr9f5eIqd4vX1NV1dXS0tposYIIrefKpiPq5mGPCxICJ40Uywe/743t4eM98pv+eURVt/fDAY8PFOp8MXcHl5GbQxUzqddLxCQu2kAS4XbV6Ui4sLDr5cLguBdcIB3t/fr/hBNpvlC1sR5hdax/UqIPkFpZIVSbRaLQ4czjgTFgzz8X97fHxcudd1Xf77ijw/68AXcVdNeUFgd+WRfjYH7APAQkRZC/zHhw583fFqTvkFqiRLkNfXV/5wOGFQ1gKPZE4Nvut4hbE8rTVG9fHcVvHgZDK55IyxgIeo0+mRo+R35kgmQZDxH1xS+Mfa4EHVit13lMcCntXIB7NHn0UOGSuJ1BgreA3fR975HNsR/6HValV5HdIEmJSUywl1yRHfgCg7Pwxr8wDrA8ciZoa8HqD965EmYEE4qUKhwMfxdxSb74dhGzw0nU7zvKRnkdvDdPbZZqhSCcw3lmWserYZ6Hmehe+gwDH9h8JhbeXz83MpHviKCIuorXAsbW0bOsIigfLTA4T+cKXrbB7EYDqID8httCZnEWFD5TYqpxyNRvEXJxa5TaisUiY4BSVbRBWbrNIx5fMGvg+CB//DvmFSMLFmszk3FfyPtLlnW7zb5PNWlVStZgUe4FBsYAEYAxuh8MBvoFTUAhjD4oztEE0l9RNsi3udKfkNyLU1SRqAIhAhz+l4DCWeBiIrgEMw1tZlq+YatikrwHNR2x7+Lot8HQm8XfvDjd63kdCjD/Tu7o4ymcw8kIUGD+Dn59H6NtYdM8kJIMg8PDzMORthvtFo8DEI/vc7BBhbCUqYz7zjU+OLB6teJXwAThWHYB67Fngh3i4x6MyiVFTyeNxd4kj9eZazzPvuYBpVSxC+glyFpQbMMTbXn4/lzQjqgd96M7LT76T+ibeBO/8edktvwM927tuDn21+e2D46qPmdeCGgZP58ppbYI9SXF99/AX+KnfqQtkm6gAAAABJRU5ErkJggg==";


}
