package org.geolatte.maprenderer.sld;

/**
 * @author Karel Maesen
 *         Copyright Geovise BVBA, 2010
 */
public enum UOM {

    PIXEL,
    FOOT,
    METRE;

    public static UOM fromURI(String uri){
        if (uri == null) throw new IllegalArgumentException("No null value allowed!");
        for(UOM uom : values()){
            if (uri.toUpperCase().contains(uom.toString())) return uom;
        }
        throw new IllegalArgumentException("URI " + uri + " does not identify a UOM.");
    }

}
