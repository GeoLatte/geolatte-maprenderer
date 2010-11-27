package org.geolatte.maprenderer.sld;

/**
 * @author Karel Maesen
 *         Copyright Geovise BVBA, 2010
 */
public class Value<U extends Number> {

    private final UOM uom;
    private final U value;

    private Value(U value, UOM uom){
        this.uom = uom;
        this.value = value;
    }

    public static <U extends Number> Value<U> of(U value, UOM uom){
        return new Value<U>(value, uom);
    }

    public static Value<Float> of(String valueStr, UOM defaultUom){
        valueStr = valueStr.trim().toLowerCase();
        if (valueStr.isEmpty()){
            return Value.of(0f,defaultUom);
        }
        String[] components = splitValueAndUOM(valueStr);
        UOM uom = toUOM(components[1]);
        if (uom == null) {
            uom = defaultUom;
        }
        Float value = Float.parseFloat(components[0]);
        return Value.of(value, uom);
    }

    private static UOM toUOM(String component) {
        if (component.contains("px")){
            return UOM.PIXEL;
        } else if (component.contains("m")){
            return UOM.METRE;
        } else if (component.contains("ft")){
            return UOM.FOOT;
        }
        return null;
    }

    private static String[] splitValueAndUOM(String valueString){
        StringBuffer numBuf = new StringBuffer();
        StringBuffer uomBuf  = new StringBuffer();
        boolean startedNum = false;
        boolean toEnd = false;
        for (int i = 0; i < valueString.length(); i++){
            char c = valueString.charAt(i);
            if (toEnd){
                uomBuf.append(c);
            } else if (startedNum && !Character.isLetter(c)){                
                numBuf.append(c);
            } else if (startedNum && Character.isLetter(c) ){
                toEnd = true;
                uomBuf.append(c);
            } else if (!Character.isWhitespace(c)){
                numBuf.append(Character.toString(c));
                startedNum = true;
            }
        }
        return new String[]{numBuf.toString(), uomBuf.toString()};
    }


    public UOM uom() {
        return uom;
    }

    public U value() {
        return value;
    }
}


