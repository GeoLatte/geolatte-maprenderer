package org.geolatte.maprenderer.sld;

import org.geolatte.maprenderer.map.Painter;

/**
 * @author Karel Maesen
 *         Copyright Geovise BVBA, 2010
 */
public abstract class SymbolizerPainter implements Painter {

    private UOM uom = UOM.PIXEL;

    public UOM getUOM() {
        return uom;
    }

    public void setUnitsOfMeasure(UOM uom) {
        this.uom = uom;
    }


}
