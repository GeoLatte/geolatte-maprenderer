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

/**
 * A holder for either {@link Mark} or {@link ExternalGraphic} objects.
 *
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 9/19/11
 */
public class MarkOrExternalGraphicHolder {

    final private Object value;

    static MarkOrExternalGraphicHolder of(Object value){
        return new MarkOrExternalGraphicHolder(value);
    }

    private MarkOrExternalGraphicHolder(Object value) {
        if (!(value instanceof Mark || value instanceof ExternalGraphic)) {
            throw new IllegalArgumentException("Received " + value.getClass().getCanonicalName());
        }
        this.value = value;
    }

    public boolean isMark(){
        return this.value instanceof Mark;
    }

    public boolean isExternalGraphic(){
        return !isMark();
    }

    public Mark getMark(){
        if (! isMark()) {
            throw new IllegalStateException("This is not a Mark.");
        }
        return (Mark)value;
    }

    public ExternalGraphic getExternalGrapic(){
        if (! isExternalGraphic()) {
            throw new IllegalStateException("This is not an ExternalGraphic.");
        }
        return (ExternalGraphic)value;
    }

}
