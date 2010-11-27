package org.geolatte.maprenderer.sld;

import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.map.Painter;

import java.awt.*;

/**
 * @author Karel Maesen, Geovise BVBA, 2010.
 *
 */
public abstract class AbstractSymbolizer {

    private UOM uom = UOM.PIXEL;
    private String geometryProperty;

    public void paint(MapGraphics graphics, Shape[] shapes) {
        throw new UnsupportedOperationException();
    }

    void setGeometryProperty(String geometryProperty) {
        this.geometryProperty = geometryProperty;
    }


    public String getGeometryProperty() {
        return geometryProperty;
    }

    public UOM getUOM() {
        return uom;
    }

    public void setUnitsOfMeasure(UOM uom) {
        this.uom = uom;
    }
}
