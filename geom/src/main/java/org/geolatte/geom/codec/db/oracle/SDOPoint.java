/*
 * This file is part of Hibernate Spatial, an extension to the
 *  hibernate ORM solution for spatial (geographic) data.
 *
 *  Copyright © 2007-2012 Geovise BVBA
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.geolatte.geom.codec.db.oracle;

import java.sql.SQLException;
import java.sql.Struct;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: Jul 1, 2010
 */
class SDOPoint {
    public Double x;

    public Double y;

    public Double z = null;

    public SDOPoint(Double x, Double y) {
        this(x, y, null);
    }

    public SDOPoint(Double x, Double y, Double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public SDOPoint(Double[] ordinates){
        this(ordinates[0], ordinates[1], ordinates.length > 2 ? ordinates[2] : null);
    }

    public SDOPoint(Struct struct) {
        try {
            final Object[] data = struct.getAttributes();
            this.x = ((Number) data[0]).doubleValue();
            this.y = ((Number) data[1]).doubleValue();

            if (data[2] != null) {
                this.z = ((Number) data[2]).doubleValue();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String toString() {
        final StringBuilder stb = new StringBuilder();
        stb.append("(").append(x).append(",").append(y).append(",").append(z).append(")");
        return stb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SDOPoint sdoPoint = (SDOPoint) o;

        if (!x.equals(sdoPoint.x)) return false;
        if (!y.equals(sdoPoint.y)) return false;
        return z != null ? z.equals(sdoPoint.z) : sdoPoint.z == null;

    }

    @Override
    public int hashCode() {
        int result = x.hashCode();
        result = 31 * result + y.hashCode();
        result = 31 * result + (z != null ? z.hashCode() : 0);
        return result;
    }
}
