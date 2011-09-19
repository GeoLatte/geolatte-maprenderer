/*
 * This file is part of the GeoLatte project.
 *
 *     GeoLatte is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     GeoLatte is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with GeoLatte.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright (C) 2010 - 2011 and Ownership of code is shared by:
 *  Qmino bvba - Esperantolaan 4 - 3001 Heverlee  (http://www.qmino.com)
 *  Geovise bvba - Generaal Eisenhowerlei 9 - 2140 Antwerpen (http://www.geovise.com)
 */

package org.geolatte.maprenderer.sld.graphics;

import net.opengis.se.v_1_1_0.*;
import org.geolatte.maprenderer.sld.SLD;
import org.geolatte.maprenderer.sld.SvgParameters;

import java.awt.geom.Point2D;
import java.util.*;

/**
 * A Graphic symbol with an inherent shape, color and possibly size.
 *
 * <p>See SE, § 11.3.2</p>
 *
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 9/12/11
 */
public class Graphic {

    private static final float DEFAULT_OPACITY = 1.0f;
    private static final float DEFAULT_SIZE = 16f;
    private static final float DEFAULT_ROTATION = 0f;

    private final List<MarkOrExternalGraphicHolder> sources;
    private final float opacity;
    private final float size;
    private final float rotation;
    private final Point2D displacement;
    private final Point2D anchorPoint;

     //TODO -- improve reporting of XML parsing errors (e.g. formatting errors)

    public Graphic(GraphicType type) {
        sources = new ArrayList<MarkOrExternalGraphicHolder>();
        readSources(type.getExternalGraphicOrMark());
        opacity = readOpacity(type.getOpacity());
        size = readSize(type.getSize());
        rotation = readRotation(type.getRotation());
        displacement = readDisplacement(type.getDisplacement());
        anchorPoint = readAnchorPoint(type.getAnchorPoint());
    }

    /**
     * Returns the sources for this graphic.
     *
     * <p>The list returned as an unmodifiable view. A source is either a <code>Mark</code> or an <code>ExternalGraphic</code>.</p>
     * @return
     */
    public List<MarkOrExternalGraphicHolder> getSources() {
        return Collections.unmodifiableList(sources);
    }

    public float getOpacity() {
        return this.opacity;
    }

    public float getSize() {
        return this.size;
    }

    public float getRotation(){
        return this.rotation;
    }

    public Point2D getDisplacement() {
        return displacement;
    }

    public Point2D getAnchorPoint() {
        return anchorPoint;
    }

    private void readSources(List<Object> objects) {
        if (objects == null || objects.isEmpty()) {
            addDefaultSource();
            return;
        }

        for (Object o : objects) {
            Object value = null;
            if (o instanceof ExternalGraphicType) {
                value = new ExternalGraphic((ExternalGraphicType) o);
            } else if (o instanceof MarkType) {
                //TODO -- add support for Marks
                throw new UnsupportedOperationException("No support for Marks");
            } else {
                throw new IllegalStateException(String.format("Element %s not supported.", o.getClass().getName()));
            }
            sources.add(MarkOrExternalGraphicHolder.of(value));
        }
    }

    private void addDefaultSource() {
        sources.add(MarkOrExternalGraphicHolder.of(createDefaultMark()));
    }

    private Mark createDefaultMark(){
        Map<String, String> svgParams = new HashMap<String, String>();
        svgParams.put(SvgParameters.FILL, "#808080"); //50% grey fill
        svgParams.put(SvgParameters.STROKE, "#000000"); //black outline
        return new Mark("square", SvgParameters.from(svgParams));
    }

    private float readOpacity(ParameterValueType opacity) {
        if (opacity == null) return DEFAULT_OPACITY;
        String value = SLD.instance().extractParameterValue(opacity);
        return Float.parseFloat(value);
    }

    private float readSize(ParameterValueType size) {
        if (size == null) return DEFAULT_SIZE;
        String value = SLD.instance().extractParameterValue(size);
        return Float.parseFloat(value);
    }

    private float readRotation(ParameterValueType rotation) {
        if (rotation == null) return DEFAULT_ROTATION;
        String value = SLD.instance().extractParameterValue(rotation);
        return Float.parseFloat(value);
    }

    private Point2D readDisplacement(DisplacementType displacement) {
        return SLD.instance().readDisplacement(displacement);
    }

    private Point2D readAnchorPoint(AnchorPointType anchorPoint) {
        return SLD.instance().readAnchorPoint(anchorPoint);
    }
}
