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
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 9/19/11
 */
public class MarkShapeFactory {

    //TODO allow users to add their own marks (plug-in system)
    public enum WellKnownMark {
        SQUARE("square", SquareMarkShape.class),
        CIRCLE("circle", CircleMarkShape.class);
        //TODO add triangle, star, cross and x


        final private String name;
        final private Class<?> markClass;

        <T extends MarkShape> Class<T> getImplementation(){
            //TODO can this be made more type-safe?
            return (Class<T>) this.markClass;
        }

        public String getName(){
            return this.name;
        }

        private <T extends MarkShape> WellKnownMark(String name, Class<T> markClass){
            this.name = name;
            this.markClass = markClass;
        }
    }

    public String[] getAvailableWellKnownNames(){
        WellKnownMark[] wellKnownMarks = WellKnownMark.values();
        String[] names = new String[wellKnownMarks.length];
        int i = 0;
        for (WellKnownMark mark : wellKnownMarks) {
            names[i++] = mark.getName();
        }
        return names;
    }

    public <T extends MarkShape> T getWellKnownMark(String name) {
        for (WellKnownMark mark : WellKnownMark.values()) {
            if (mark.getName().equalsIgnoreCase(name)) {
                return createMarkInstance(mark);
            }
        }
        throw new UnsupportedOperationException(String.format("Name %s is not known in MarkFactory.", name));
    }

    private <T extends MarkShape> T createMarkInstance(WellKnownMark mark) {
        Class<T> implementationClass = mark.getImplementation();
        try {
            return implementationClass.newInstance();
        } catch (InstantiationException e) {
           throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    public Mark getGlyphMark(String fontName, int index){
        //TODO implement GlyphMarks
        return null;
    }
}
