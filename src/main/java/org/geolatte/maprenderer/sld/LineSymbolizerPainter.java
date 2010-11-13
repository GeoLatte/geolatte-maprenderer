package org.geolatte.maprenderer.sld;

import org.geolatte.core.Feature;
import org.geolatte.maprenderer.map.MapGraphics;

/**
 * @author Karel Maesen
 *         Copyright Geovise BVBA, 2010
 */
public class LineSymbolizerPainter extends SymbolizerPainter{

    private String geometryProperty;
    private Value<Float> perpendicularOffset = Value.of(0f, UOM.PIXEL);

    @Override
    public void paint(MapGraphics graphics, Iterable<Feature> features) {
        throw new UnsupportedOperationException();
    }

    void setGeometryProperty(String geometryProperty) {
        this.geometryProperty = geometryProperty;
    }

    public String getGeometryProperty() {
        return geometryProperty;
    }

    void setPerpendicularOffset(Value<Float> perpendicularOffset) {
        this.perpendicularOffset = perpendicularOffset;
    }

    public Value<Float> getPerpendicularOffset() {
        return perpendicularOffset;
    }
}
