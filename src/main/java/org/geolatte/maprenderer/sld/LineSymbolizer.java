package org.geolatte.maprenderer.sld;

import org.geolatte.core.Feature;
import org.geolatte.maprenderer.map.MapGraphics;

import java.awt.*;

/**
 * @author Karel Maesen
 *         Copyright Geovise BVBA, 2010
 */
public class LineSymbolizer extends AbstractSymbolizer {

    private Value<Float> perpendicularOffset = Value.of(0f, UOM.PIXEL);

    @Override
    public void paint(MapGraphics graphics, Shape[] shapes) {
        throw new UnsupportedOperationException();
    }

    void setPerpendicularOffset(Value<Float> perpendicularOffset) {
        this.perpendicularOffset = perpendicularOffset;
    }

    public Value<Float> getPerpendicularOffset() {
        return perpendicularOffset;
    }
}
