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

package org.geolatte.maprenderer.sld.symbolizer;

import org.geolatte.maprenderer.sld.filter.SLDRuleFilter;

import java.util.ArrayList;
import java.util.List;

public class Rule {

    private String name;
    private String title;
    private String abstractText;
    private SLDRuleFilter filter;
    private Double minScaleDenominator;
    private Double maxScaleDenominator;
    private final List<Symbolizer> symbolizers = new ArrayList<Symbolizer>();

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SLDRuleFilter getFilter() {
        return filter;
    }

    public void setFilter(SLDRuleFilter filter) {
        this.filter = filter;
    }

    public Double getMaxScaleDenominator() {
        return maxScaleDenominator;
    }

    public void setMaxScaleDenominator(Double maxScaleDenominator) {
        this.maxScaleDenominator = maxScaleDenominator;
    }

    public Double getMinScaleDenominator() {
        return minScaleDenominator;
    }

    public void setMinScaleDenominator(Double minScaleDenominator) {
        this.minScaleDenominator = minScaleDenominator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Symbolizer> getSymbolizers() {
        return this.symbolizers;
    }

}
